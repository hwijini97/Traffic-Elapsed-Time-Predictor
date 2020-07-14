import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * ���ο� ���� �ð��뺰 ��� �ҿ�ð��� �����ϴ� Ŭ����
 * @author ������
 */
class AverageTimeOfRoad{
	String road = "";
	int[] elapsedTimeArray; // elapsedTimeArray[i] = i �ð������� ��� �ҿ� �ð�
	public AverageTimeOfRoad(String road, int[] elapsedTimeArray){
		this.road = road;
		this.elapsedTimeArray = elapsedTimeArray;
	}
}

/**
 * ���ο� ���� �ð��뺰 ��� �ҿ�ð��� ����ؼ� ���Ϸ� ����
 * @author ������
 */
public class CalculateAverageElapsedTime {
	public static void main(String[] args){
		ArrayList<AverageTimeOfRoad> list = new ArrayList<AverageTimeOfRoad>();
		try{
			String inputDirectory = "D:\\Capstone Data\\inputs\\";
			File dirFile = new File(inputDirectory);
			File[] fileList = dirFile.listFiles();
			String line = "";
			for(File file : fileList) {
				if(file.isFile()) {
					int[] count = new int[24];
					int[] sum = new int[24];
					BufferedReader reader = new BufferedReader(new FileReader(file));
					boolean initialization = false; // �ʱ�ȭ ���� -> ��� �ִ� �ð��밡 ���� �� �����Ƿ� ù ��° ���� �����ͷ� ��� ���θ� �ʱ�ȭ��
					while((line = reader.readLine()) != null){
						String[] stringArray = line.split(",");
						if(!initialization){ // �ʱ�ȭ ���� ���� �����̸�
							initialization = true;
							for(int i=0; i<24; i++){
								sum[i] = Integer.parseInt(stringArray[6]);
								count[i] = 1;
							}
						} else{
							if(count[0] > 50 && count[8] > 50 && count[16] > 50) // ����� �����Ͱ� ���̸�
								break;
							sum[Integer.parseInt(stringArray[4])] += Integer.parseInt(stringArray[6]);
							count[Integer.parseInt(stringArray[4])] += 1;
						}
					}
					reader.close();

					int[] averageArray = new int[24];
					for(int i=0; i<24; i++){
						averageArray[i] = sum[i]/count[i];
					}

					list.add(new AverageTimeOfRoad(file.getName().substring(0, 7), averageArray));
				}
			}

			String outputDirectory = "D:\\Capstone Data\\averageElapsedTime\\";

			for(int i=0; i<list.size(); i++){
				BufferedWriter writer = new BufferedWriter(new FileWriter(outputDirectory + list.get(i).road + ".csv"));
				for(int k=0; k<24; k++){
					writer.write(k + "," + list.get(i).elapsedTimeArray[k]);
					writer.newLine();
				}
				writer.close();
				System.out.println("Done!");
			}

		} catch(Exception e){
			e.printStackTrace();
		}
	}
}