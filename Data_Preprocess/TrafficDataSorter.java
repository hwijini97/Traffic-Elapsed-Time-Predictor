import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

/**
 * ������ ���� �� "�����_������.csv" ���Ϸ� ����
 * @author ������
 */
public class TrafficDataSorter {
	private String inputFolder = "D:\\Capstone Data\\trafficOutput\\";
	private String outputFolder = "D:\\Capstone Data\\trafficData\\";

	public static void main(String[] args){
		TrafficDataSorter sorter = new TrafficDataSorter();
		
		long start = System.currentTimeMillis();

		sorter.sortAndSave("201801");
		sorter.sortAndSave("201802");
		sorter.sortAndSave("201803");
		sorter.sortAndSave("201804");
		sorter.sortAndSave("201805");
		sorter.sortAndSave("201806");
		sorter.sortAndSave("201807");
		sorter.sortAndSave("201808");
		sorter.sortAndSave("201809");
		sorter.sortAndSave("201810");
		sorter.sortAndSave("201811");
		sorter.sortAndSave("201812");
		
		long end = System.currentTimeMillis();
		System.out.println("���� �ð� : " + (end - start)/1000.0 + "��");
	}

	private void sortAndSave(String fileName){
		ArrayList<TrafficData> list = sort(fileName);
		save(list);
		System.out.println(fileName + " Done!");
	}

	// �Է� �����͸� ����� - ������ - �� - �� - �� - �ð��� ������ ����
	private ArrayList<TrafficData> sort(String fileName){
		ArrayList<TrafficData> list = new ArrayList<TrafficData>();

		try{
			File fileForRead = new File(inputFolder + fileName + ".csv");
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileForRead));

			String line = "";

			while((line = bufferedReader.readLine()) != null){
				StringTokenizer st = new StringTokenizer(line, ","); // csv������ ������ ,���ڷ� ���еǾ� ����

				String year = st.nextToken();
				String month = st.nextToken();
				String day = st.nextToken();
				String hour = st.nextToken();
				String departure = st.nextToken();
				String arrival = st.nextToken();
				String elapsedTime = st.nextToken();

				list.add(new TrafficData(
						Integer.parseInt(year),
						Integer.parseInt(month), 
						Integer.parseInt(day), 
						Integer.parseInt(hour), 
						Integer.parseInt(departure), 
						Integer.parseInt(arrival), 
						Integer.parseInt(elapsedTime)));
			}
			Collections.sort(list);
			bufferedReader.close();

		} catch(Exception e){
			e.printStackTrace();
		}

		return list;
	}

	// ���ĵ� �����͸� "�����_������.csv" ���·� ����
	private void save(ArrayList<TrafficData> list){
		int firstIndex = 0;
		try{
			for(int i=0; i<list.size()-1; i++){
				if(list.get(i).departure != list.get(i+1).departure || 
						list.get(i).arrival != list.get(i+1).arrival){
					String originalData = "";
					String fileName = list.get(i).departure + "_" + list.get(i).arrival;
					File fileForWrite = new File(outputFolder + fileName + ".csv");

					if(fileForWrite.exists()){ // �̹� �����ϴ� ������ ��� -> �����
						String line = "";
						BufferedReader bufferedReader = new BufferedReader(new FileReader(fileForWrite));
						
						while((line = bufferedReader.readLine()) != null){
							originalData += line;
							originalData += "\n";
						}
						
						bufferedReader.close();
					}
					
					BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileForWrite));
					if(!originalData.equals(""))
						bufferedWriter.write(originalData);

					for(int j=firstIndex; j<i+1; j++){
						String result = list.get(j).year + ","
								+ list.get(j).month + ","
								+ list.get(j).day + ","
								+ list.get(j).hour + ","
								+ list.get(j).departure + ","
								+ list.get(j).arrival + ","
								+ list.get(j).elapsedTime;
						bufferedWriter.write(result);
						bufferedWriter.newLine();
					}
					bufferedWriter.close();
					firstIndex = i+1;
				}
			}

			String originalData = "";
			String fileName = list.get(list.size()-1).departure + "_" + list.get(list.size()-1).arrival;
			File fileForWrite = new File(outputFolder + fileName + ".csv");

			if(fileForWrite.exists()){ // �̹� �����ϴ� ������ ��� -> �����
				String line = "";
				BufferedReader bufferedReader = new BufferedReader(new FileReader(fileForWrite));
				
				while((line = bufferedReader.readLine()) != null){
					originalData += line;
					originalData += "\n";
				}
				
				bufferedReader.close();
			}
			
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileForWrite));
			if(!originalData.equals(""))
				bufferedWriter.write(originalData);
			
			for(int j=firstIndex; j<list.size(); j++){
				String result = list.get(j).year + ","
						+ list.get(j).month + ","
						+ list.get(j).day + ","
						+ list.get(j).hour + ","
						+ list.get(j).departure + ","
						+ list.get(j).arrival + ","
						+ list.get(j).elapsedTime;
				bufferedWriter.write(result);
				bufferedWriter.newLine();
			}
			bufferedWriter.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}