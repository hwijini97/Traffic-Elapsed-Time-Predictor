import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

/**
 * 데이터 정렬 후 "출발지_목적지.csv" 파일로 저장
 * @author 손휘진
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
		System.out.println("실행 시간 : " + (end - start)/1000.0 + "초");
	}

	private void sortAndSave(String fileName){
		ArrayList<TrafficData> list = sort(fileName);
		save(list);
		System.out.println(fileName + " Done!");
	}

	// 입력 데이터를 출발지 - 목적지 - 년 - 월 - 일 - 시간대 순으로 정렬
	private ArrayList<TrafficData> sort(String fileName){
		ArrayList<TrafficData> list = new ArrayList<TrafficData>();

		try{
			File fileForRead = new File(inputFolder + fileName + ".csv");
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileForRead));

			String line = "";

			while((line = bufferedReader.readLine()) != null){
				StringTokenizer st = new StringTokenizer(line, ","); // csv파일은 값들이 ,문자로 구분되어 있음

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

	// 정렬된 데이터를 "출발지_목적지.csv" 형태로 저장
	private void save(ArrayList<TrafficData> list){
		int firstIndex = 0;
		try{
			for(int i=0; i<list.size()-1; i++){
				if(list.get(i).departure != list.get(i+1).departure || 
						list.get(i).arrival != list.get(i+1).arrival){
					String originalData = "";
					String fileName = list.get(i).departure + "_" + list.get(i).arrival;
					File fileForWrite = new File(outputFolder + fileName + ".csv");

					if(fileForWrite.exists()){ // 이미 존재하는 파일일 경우 -> 덮어쓰기
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

			if(fileForWrite.exists()){ // 이미 존재하는 파일일 경우 -> 덮어쓰기
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