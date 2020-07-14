import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.StringTokenizer;

/**
 * 데이터 필터
 * 공공 데이터 포털에서 제공한 raw data에서 필요없는 값들을 제거하고, 적절한 형태로 변형한 뒤 csv확장자로 변환하여 저장
 * 필요 없는 값들 : 소요 시간이 -1인 데이터(null), 차선 번호가 1이 아닌 값들
 * 적절한 형태 : 년,월,일,시간대,출발 TG,도착 TG,소요 시간
 * @author 손휘진
 */
public class TrafficDataFilter {
	private String inputFolder = "D:\\Capstone Data\\trafficInput\\";
	private String outputFolder = "D:\\Capstone Data\\trafficOutput\\";
	
	public static void main(String[] args){
		TrafficDataFilter df = new TrafficDataFilter();
		df.filterAndSave("201801");
		df.filterAndSave("201802");
		df.filterAndSave("201803");
		df.filterAndSave("201804");
		df.filterAndSave("201805");
		df.filterAndSave("201806");
		df.filterAndSave("201807");
		df.filterAndSave("201808");
		df.filterAndSave("201809");
		df.filterAndSave("201810");
		df.filterAndSave("201811");
		df.filterAndSave("201812");
	}
	
	private void filterAndSave(String fileName){
		try{
			File fileForRead = new File(inputFolder + fileName + ".txt");
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileForRead));
			
			File fileForWrite = new File(outputFolder + fileName + ".csv");
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileForWrite));

			String line = "";
			boolean skip;

			while((line = bufferedReader.readLine()) != null){
				skip = false;
				line = line.replace(" ", ""); // 공백 제거
				StringTokenizer st = new StringTokenizer(line, "|"); // raw파일은 값들이 |문자로 구분되어 있음
				String [] array = new String[7];

				for(int i=0; st.hasMoreElements(); i++){
					if(i==0){
						String temp = st.nextToken();
						array[0] = temp.substring(0, 4);
						array[1] = temp.substring(4, 6);
						array[2] = temp.substring(6, 8);
						i = 2;
					} else if(i==6){
						if(!st.nextToken().equals("1")){ // 차선 번호가 1이 아닌 경우 -> 데이터 버림
							skip = true;
							break;
						}
					} else if(i==7){
						array[6] = st.nextToken();
					} else{
						array[i] = st.nextToken();
					}
				}

				if(!skip && !array[6].equals("-1")){ // 소요 시간이 -1이 아닌 데이터만 취급함
					String result = "";
					for(int j=0; j < array.length ; j++){
						result = result + array[j];
						if(j+1 != array.length)
							result = result + ",";
					}
					bufferedWriter.write(result);
					bufferedWriter.newLine();
				}
			}

			bufferedReader.close();
			bufferedWriter.close();
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
