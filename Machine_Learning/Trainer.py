﻿# 데이터셋 학습에 사용되는 파일, 한 시퀀스당 24개의 입력 -> 다음 24시간 동안의 데이터 출력하도록 학습됨
import tensorflow as tf
import numpy as np
import matplotlib.pyplot as plt
import os
from datetime import datetime


# 너무 작거나 너무 큰 값이 학습을 방해하는 것을 방지하고자 정규화한다
# lst가 양수라는 가정하에 최소값과 최대값을 이용하여 0~1사이의 값으로 변환
# Min-Max scaling
def min_max_scaling(lst):
    lst_np = np.asarray(lst)
    return (lst_np - lst_np.min()) / (lst_np.max() - lst_np.min() + 1e-7)  # 1e-7은 0으로 나누는 오류 예방차원


# 정규화된 값을 원래의 값으로 되돌린다
# 정규화하기 이전의 org_lst값과 되돌리고 싶은 lst를 입력하면 역정규화된 값을 리턴한다
def reverse_min_max_scaling(org_lst, lst):
    org_lst_np = np.asarray(org_lst)
    lst_np = np.asarray(lst)
    return (lst_np * (org_lst_np.max() - org_lst_np.min() + 1e-7)) + org_lst_np.min()


def start_learning(directory, file_name):
    tf.reset_default_graph()

    xy = np.genfromtxt(directory + file_name + '.csv', delimiter=',')

    seq_length = 24  # 학습용 데이터 시퀀스의 길이
    data_dim = 6  # 학습용 데이터의 개수(월, 일, 휴일 여부, 요일 코드, 시간대, 통행 소요 시간)
    hidden_dim = 8  # hidden dimension
    output_dim = 24  # 출력 데이터 개수
    learning_rate = 0.015  # 학습 속도
    iterations = 800  # 반복 학습 횟수

    # 원래의 통행 소요 시간 데이터 -> 역정규화에 사용됨
    original_elapsed_time = xy[:, 5]

    # 학습에 사용될 데이터 정규화
    norm_month = min_max_scaling(xy[:, 0])
    norm_day = min_max_scaling(xy[:, 1])
    norm_holiday = min_max_scaling(xy[:, 2])
    norm_weekday = min_max_scaling(xy[:, 3])
    norm_time = min_max_scaling(xy[:, 4])
    norm_elapsed_time = min_max_scaling(xy[:, 5])

    # 학습에 사용될 정규화된 데이터를 하나의 2차원 리스트에 넣고 np array로 변환
    x = []
    for i in range(0, len(norm_holiday)):
        x.append((norm_month[i], norm_day[i], norm_holiday[i], norm_weekday[i], norm_time[i], norm_elapsed_time[i]))
    x = np.array(x)

    # 학습의 목표로 사용될 정규화된 데이터(통행 소요 시간)를 리스트에 넣고 np array로 변환
    y = []
    for i in range(0, len(norm_elapsed_time) - 24):
        y.append([norm_elapsed_time[i], norm_elapsed_time[i + 1], norm_elapsed_time[i + 2], norm_elapsed_time[i + 3], norm_elapsed_time[i + 4],
                  norm_elapsed_time[i + 5], norm_elapsed_time[i + 6], norm_elapsed_time[i + 7], norm_elapsed_time[i + 8], norm_elapsed_time[i + 9],
                  norm_elapsed_time[i + 10], norm_elapsed_time[i + 11], norm_elapsed_time[i + 12], norm_elapsed_time[i + 13], norm_elapsed_time[i + 14],
                  norm_elapsed_time[i + 15], norm_elapsed_time[i + 16], norm_elapsed_time[i + 17], norm_elapsed_time[i + 18], norm_elapsed_time[i + 19],
                  norm_elapsed_time[i + 20], norm_elapsed_time[i + 21], norm_elapsed_time[i + 22], norm_elapsed_time[i + 23]])
    y = np.array(y)

    dataX = []
    dataY = []

    # 각 데이터를 sequence length에 맞게 잘라내서 dataX랑 dataY에 넣음
    # dataX는 월, 일, 휴일 여부, 요일 코드, 시간대, 강수량, 통행 소요 시간
    # dataY는 통행 소요 시간
    for i in range(0, len(y) - seq_length):
        _x = x[i:i + seq_length]
        _y = y[i + seq_length]
        # print(_x, "->", _y)
        dataX.append(_x)
        dataY.append(_y)

    # 학습용 데이터 : 70%, 테스트용 데이터 : 30%
    train_size = int(len(dataY) * 0.7)
    test_size = len(dataY) - train_size
    trainX, testX = np.array(dataX[0:train_size]), np.array(dataX[train_size:])
    trainY, testY = np.array(dataY[0:train_size]), np.array(dataY[train_size:])

    # input과 output의 형태 및 시퀀스와 입력 개수 정의
    X = tf.placeholder(tf.float32, [None, seq_length, data_dim])
    Y = tf.placeholder(tf.float32, [None, 24])

    # cell 정의, 활성화 함수로 tanh 사용
    cell = tf.contrib.rnn.BasicLSTMCell(num_units=hidden_dim, state_is_tuple=True, activation=tf.nn.tanh)
    # LSTM 네트워크 생성
    outputs, _states = tf.nn.dynamic_rnn(cell, X, dtype=tf.float32)

    # Y_pred에 train을 통해 계산된 최종 output Y가 들어가고 이 값이 testY와 비교됨
    Y_pred = tf.contrib.layers.fully_connected(outputs[:, -1], output_dim, activation_fn=None)

    # cost/loss 계산 -> sum of the squares
    loss = tf.reduce_sum(tf.square(Y_pred - Y))

    # optimizer : AdamOptimizer 사용
    optimizer = tf.train.AdamOptimizer(learning_rate)
    train = optimizer.minimize(loss)

    # train 진행 평가척도로 RMSE 사용 -> loss를 줄이는 방향으로 학습
    targets = tf.placeholder(tf.float32, [None, 24])
    predictions = tf.placeholder(tf.float32, [None, 24])
    rmse = tf.sqrt(tf.reduce_mean(tf.square(targets - predictions)))

    # 훈련된 모델 저장에 사용되는 객체 생성
    saver = tf.train.Saver()

    with tf.Session() as sess:
        init = tf.global_variables_initializer()
        sess.run(init)
        test_predict = -1
        rmse_val = -1

        for i in range(iterations):
            _, step_loss = sess.run([train, loss], feed_dict={X: trainX, Y: trainY})
            test_predict = sess.run(Y_pred, feed_dict={X: testX})
            rmse_val = sess.run(rmse, feed_dict={targets: testY, predictions: test_predict})
            # print("[step: {}] loss: {}, RMSE: {}".format(i, step_loss, rmse_val))
        # print("min: ", np.min(original_elapsed_time), "max: ", np.max(original_elapsed_time), "average: ", np.mean(original_elapsed_time))
        saver.save(sess, 'models/' + file_name + '.ckpt')
        now = datetime.now()
        # print('%s-%s-%s, %s:%s:%s, %s Done! RMSE = %s' % (now.year, now.month, now.day, now.hour, now.minute, now.second, file_name, rmse_val))
        print('%s Done! RMSE = %s' % (file_name, rmse_val))


        """
        # 학습 결과 그래프 생성
        print(reverse_min_max_scaling(original_elapsed_time, testY))
        plt.plot(reverse_min_max_scaling(original_elapsed_time, testY), "b")
        plt.plot(reverse_min_max_scaling(original_elapsed_time, test_predict), "r")
        plt.xlabel("Time Period")
        plt.ylabel("Elapsed Time")
        plt.show()

        print(testY[0])
        print(test_predict[0])
        """

        """
        # 원래의 값과 예측 값 출력
        for j in range(0, len(testY)):
            print("원래 값 : ", reverse_min_max_scaling(original_elapsed_time, testY[j]), "\t예측 값 : ", reverse_min_max_scaling(original_elapsed_time, test_predict[j]))
        """


tf.set_random_seed(777)  # reproductivity를 위한 random seed

folder = 'inputs\\'

file_list = os.listdir(folder)
for step in range(0, len(file_list)):
    start_learning(folder, file_list[step][:-4])
