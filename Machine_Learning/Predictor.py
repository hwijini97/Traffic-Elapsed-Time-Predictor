import tensorflow as tf
import numpy as np
import csv
import os
import requests
import xml.etree.ElementTree as elemTree
import datetime
from datetime import datetime as dt
from calendar import monthrange
import time

road_directory_name = 'inputs'  # 머신러닝 학습에 사용되는 2018년 1월 ~ 2019년 10월 교통 소요 시간 데이터들이 저장되어 있는 폴더
model_directory_name = 'models'  # 머신러닝 학습의 결과 모델들이 저장되어 있는 폴더
predict_directory_name = 'predict_inputs/'  # API를 통해 실시간 영업소 간 통행 소요 시간 데이터를 xml 형태로 받아와서 csv 파일로 저장할 폴더, 예측의 입력으로 사용됨
result_directory_name = 'data'  # 모델을 통해 예측된 결과들이 저장되는 폴더

holiday_data = [[[0 for _ in range(32)] for _ in range(13)] for _ in range(50)]  # holiday_data[YYYY-2000][MM][DD}


# 입력한 시간으로부터 한 시간 전의 년, 월, 일, 시간을 반환함
def get_previous_hour(year, month, day, hour):
    if hour > 0:
        return year, month, day, hour-1
    else:
        if day > 1:
            return year, month, day-1, 23
        else:
            if month > 1:
                month = month - 1
                day = monthrange(year, month)[1]  # 해당 달의 마지막 날짜
                return year, month-1, day, 23
            else:
                return year-1, 12, 31, 23


# 입력한 날짜로부터 하루 전날의 년, 월, 일을 반환함
def get_previous_day(year, month, day):
    if day > 1:
        return year, month, day-1
    else:
        if month > 1:
            month = month - 1
            day = monthrange(year, month)[1]  # 해당 달의 마지막 날짜
            return year, month, day
        else:
            return year-1, 12, 31


# 입력한 년, 월의 주말 및 공휴일 데이터를 불러옴
def get_holiday_data(year, month):  # YYYYMM
    # 주말 여부
    for day in range(1, monthrange(year, month)[1] + 1):
        if datetime.date(year, month, day).weekday() == 5 or datetime.date(year, month, day).weekday() == 6:
            holiday_data[year-2000][month][day] = True
        else:
            holiday_data[year-2000][month][day] = False

    if month < 10:
        month_str = '0' + str(month)
    else:
        month_str = str(month)

    # 공휴일 여부
    response = requests.get(url='http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo?solYear=' + str(year) + '&solMonth=' + month_str + '&ServiceKey=npWZKZVSx1tedwTzyXQpLSMKC2X%2FAqYPD2W%2FAmtP66iQv0OjUqOrHrwogq4Dgd1prz%2BPk6E4rcv2iaKpYVDLrw%3D%3D', timeout=600)
    xml_str = response.content.decode('utf-8')

    # xml 문자열 처리
    tree = elemTree.fromstring(xml_str).find('body').find('items')

    for data in tree.findall('./item'):
        date = data.find('./locdate').text  # YYYYMMDD
        holiday_data[year-2000][int(date[4:6])][int(date[6:])] = True


# 입력한 년, 월, 일의 휴일코드를 반환함
# 휴일 = 주말 또는 공휴일
# 휴일이 아니면 0, 휴일 첫째날이면 1, 둘째날이면 2, ...
def get_holiday(year, month, day):
    if holiday_data[year-2000][month][day] is False:
        return 0
    elif holiday_data[year-2000][month][day] is True:
        year, month, day = get_previous_day(year, month, day)
        return get_holiday(year, month, day) + 1
    else:
        return -1


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


def predict(name):
    tf.reset_default_graph()  # tf를 초기화함, tf를 재사용하기 위해 사용

    seq_length = 24  # 최근 24개의 데이터를 입력으로 넣음
    data_dim = 6  # 월, 일, 휴일 여부, 요일 코드, 시간대, 소요 시간
    hidden_dim = 8  # 예측 과정에서 8개의 레이어를 사용
    output_dim = 24  # 미래 24시간 동안의 데이터 출력

    X = tf.placeholder(tf.float32, [None, seq_length, data_dim])

    cell = tf.contrib.rnn.BasicLSTMCell(num_units=hidden_dim, state_is_tuple=True, activation=tf.nn.tanh)
    outputs, _state = tf.nn.dynamic_rnn(cell, X, dtype=tf.float32)

    Y_predict = tf.contrib.layers.fully_connected(outputs[:, -1], output_dim, activation_fn=None)

    saver = tf.train.Saver()

    with tf.Session() as sess:
        saver.restore(sess, model_directory_name + '/' + name + '.ckpt')

        xy = np.genfromtxt(predict_directory_name + name + '.csv', delimiter=',')
        original_time = xy[:, 4]
        original_elapsed_time = xy[:, 5]

        # 예측에 사용될 데이터 정규화
        norm_month = min_max_scaling(xy[:, 0])
        norm_day = min_max_scaling(xy[:, 1])
        norm_holiday = min_max_scaling(xy[:, 2])
        norm_weekday = min_max_scaling(xy[:, 3])
        norm_time = min_max_scaling(xy[:, 4])
        norm_elapsed_time = min_max_scaling(xy[:, 5])

        factorX = []
        for i in range(0, len(norm_month)):
            factorX.append((norm_month[i], norm_day[i], norm_holiday[i], norm_weekday[i], norm_time[i], norm_elapsed_time[i]))
        factorX = np.array(factorX)

        factorX = factorX.reshape(1, 24, 6)
        test_predict = sess.run(Y_predict, feed_dict={X: factorX})

        data = []
        for i in range(0, output_dim):
            congestion = (reverse_min_max_scaling(original_elapsed_time, test_predict[0][i]) - sum(original_elapsed_time)/len(original_elapsed_time))/(reverse_min_max_scaling(original_elapsed_time, test_predict[0][i]) + sum(original_elapsed_time)/len(original_elapsed_time))
            t = (int(original_time[output_dim - 1]) + 1 + i) % 24
            data.append((t, int(round(reverse_min_max_scaling(original_elapsed_time, test_predict[0][i]))), round(congestion, 2)))

        with open(result_directory_name + '/' + name + '.csv', 'w', newline='') as f:
            wr = csv.writer(f)
            for row in data:
                wr.writerow(row)


def get_traffic_data(key, departure, arrival):
    # 실시간 영업소 간 통행 소요 시간 데이터 받아오기
    params = {'key': key,
              'type': 'xml',
              'iStartUnitCode': departure,
              'iEndUnitCode': arrival,
              'iStartEndStdTypeCode': '2',
              'sumTmUnitTypeCode': '3',
              'numOfRows': '100',
              'pageNo': '1'}
    response = requests.get(url='http://data.ex.co.kr/openapi/trtm/realUnitTrtm', params=params, timeout=600)
    xml_str = response.content.decode('utf-8')

    # xml 문자열 처리
    input_list = []
    tree = elemTree.fromstring(xml_str)

    # xml 데이터를 리스트에 삽입
    for data in tree.findall('./realUnitTrtmVO'):
        date = data.find('./stdDate').text  # YYYYMMDD
        year = int(date[0:4])  # YYYY
        month = int(date[4:6])  # MM
        day = int(date[6:8])  # DD
        weekday = (datetime.date(year, month, day).weekday() + 1) % 7 + 1  # 엑셀에서 제공하는 weekday 타입 맞추기(1:일, 2:월, ..., 7:토)
        holiday = get_holiday(year, month, day)
        time = int(data.find('./stdTime').text[0:2])
        elapsed_time = round(float(data.find('./timeAvg').text)*60)  # 분 단위의 데이터를 초 단위로 바꾸기

        input_list.append((month, day, weekday, holiday, time, elapsed_time))

    file_name = str(departure) + "_" + str(arrival)
    average_list = np.genfromtxt('averageElapsedTime/' + file_name + '.csv', delimiter=',')
    elapsed_time = average_list[:, 1]  # 과거 시간대별 평균 소요시간

    year = dt.now().year
    month = dt.now().month
    day = dt.now().day
    hour = dt.now().hour

    for i in range(0, 24):
        year, month, day, hour = get_previous_hour(year, month, day, hour)
        exist = False  # 해당 month, day, hour 값을 갖는 데이터가 존재하는지 여부
        for elem in input_list:
            if elem[0] == month and elem[1] == day and elem[4] == hour:
                exist = True
                break
        if exist is False:  # 비어있는 데이터의 경우 -> 과거 평균 데이터를 입력으로 넣음
            weekday = (datetime.date(year, month, day).weekday() + 1) % 7 + 1
            holiday = get_holiday(year, month, day)
            input_list.append((month, day, weekday, holiday, hour, elapsed_time[hour]))

    # 리스트 시간 순서대로 정렬
    for i in range(0, len(input_list)):
        for j in range(i+1, len(input_list)):
            if input_list[i][0] > input_list[j][0]:
                temp = input_list[j]
                input_list[j] = input_list[i]
                input_list[i] = temp
            elif input_list[i][1] > input_list[j][1]:
                temp = input_list[j]
                input_list[j] = input_list[i]
                input_list[i] = temp
            elif input_list[i][4] > input_list[j][4]:
                temp = input_list[j]
                input_list[j] = input_list[i]
                input_list[i] = temp

    # 중복된 시간 있으면 앞의 것 제거
    i = 0
    while True:
        if i >= len(input_list)-1:
            break
        if input_list[i][4] == input_list[i+1][4]:
            input_list.remove(input_list[i])
            i = 0
        else:
            i = i + 1

    with open(predict_directory_name + '/' + file_name + '.csv', 'w', newline='') as f:
        wr = csv.writer(f)
        for row in input_list[len(input_list)-24:len(input_list)]:  # 데이터 최신순 24개 넣음
            wr.writerow(row)

    return True


def main_function(key):
    get_holiday_data(dt.now().year, dt.now().month)
    if dt.now().day == 1:
        year, month, _ = get_previous_day(dt.now().year, dt.now().month, dt.now().day)
        get_holiday_data(year, month)
    file_list = os.listdir(road_directory_name)
    while True:
        exception_occurred = False
        start_time = time.time()
        for step in range(0, len(file_list)):
            file_name = file_list[step][0:-4]
            request_result = False
            try:
                request_result = get_traffic_data(key, file_name[0:3], file_name[4:7])
            except requests.exceptions.ReadTimeout:
                exception_occurred = True
                print("request timeout exception 발생, 30초 뒤 재요청")
                time.sleep(30)
            except requests.exceptions.ConnectionError:
                exception_occurred = True
                print("Connection aborted exception 발생, 30초 뒤 재요청")
                time.sleep(30)
            except ConnectionResetError:
                exception_occurred = True
                print("ConnectionResetError 발생, 30초 뒤 재요청")
                time.sleep(30)
            except Exception:
                exception_occurred = True
                print("Exception 발생, 30초 뒤 재요청")
                time.sleep(30)

            if exception_occurred:
                break

            if request_result:  # 정상적으로 데이터를 받아온 경우 -> 머신러닝으로 예측
                predict(file_name)
                print("step : " + str(step) + ", " + file_name + " -> 예측 성공")

        if exception_occurred is False:
            end_time = time.time()
            now = dt.now()
            print('\n%s-%s-%s, %s:%s:%s, 모든 도로 예측에 걸린 시간 : %s초\n\n' % (now.year, now.month, now.day, now.hour, now.minute, now.second, str(round(end_time - start_time))))
            time.sleep(300)  # 5분 주기로 실시간 데이터를 받아옴


main_function('여기에 서비스키 입력하세요.')
