import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

/**
 * @author 손휘진
 * 날씨 데이터를 적절한 형태로 변형하여 하나의 파일로 저장한다.
 * 원래의 날씨 데이터 : 1~3월, 4~6월, 7~9월, 10~12월으로 총 4개의 파일로 되어 있음
 * 
 * 1. 원래의 1년치 날씨 데이터에서 필요한 데이터(지점 번호, 날짜, 강수량)만 추출하여 메모리(list 변수)에 저장
 * 2. 메모리에 저장된 두 내용을 바탕으로 (코드,년,월,일,시간,강수량) 형태로 하나의 파일에 데이터 저장
 */
public class WeatherDataFilter {
	private String inputFolder = "D:\\Capstone Data\\weatherInput\\";
	private String outputFolder = "D:\\Capstone Data\\weatherData\\";
	private String outputFileName = "weatherData"; // 결과물 파일 이름
	private ArrayList<WeatherData> list; // 1년치 날씨 데이터 담을 리스트

	public static void main(String[] args){
		WeatherDataFilter wdf = new WeatherDataFilter();
		wdf.filter();
	}

	private void filter(){
		try{
			/* 1년치 데이터 메모리로 불러오는 부분 */
			list = new ArrayList<WeatherData>();

			File fileForRead;
			BufferedReader bufferedReader_weather = null;
			String line;
			String lastCode = "";

			for(int fileName=1; fileName<=4; fileName++){
				fileForRead = new File(inputFolder + fileName + ".csv");
				bufferedReader_weather = new BufferedReader(new FileReader(fileForRead));
				bufferedReader_weather.readLine(); // 첫줄 건너뜀
				while((line = bufferedReader_weather.readLine()) != null){
					StringTokenizer st = new StringTokenizer(line, ","); // csv파일은 값들이 ,문자로 구분되어 있음
					String code = st.nextToken();
					String rawDate = st.nextToken();
					String rain = st.nextToken();
					String year = rawDate.substring(0, 4);
					String month = rawDate.substring(5, 7);
					String day = rawDate.substring(8, 10);
					String hour = rawDate.length() == 15 ? rawDate.substring(11, 12) : rawDate.substring(11, 13);
					if(!code.equals(lastCode)){ // 이전 지점에 대한 정보가 끝나고 다음 지점에 대한 정보로 넘어가는 경우 -> 맨 앞에 0시의 데이터를 추가해줌
						lastCode = code;
						list.add(new WeatherData(
								Integer.parseInt(code),
								Integer.parseInt(year),
								Integer.parseInt(month),
								Integer.parseInt(day),
								0,
								Float.parseFloat(rain)));
					}
					list.add(new WeatherData(
							Integer.parseInt(code),
							Integer.parseInt(year),
							Integer.parseInt(month),
							Integer.parseInt(day),
							Integer.parseInt(hour),
							Float.parseFloat(rain)));
				}
			}
			bufferedReader_weather.close();

			/* 코드,년,월,일,시간 순으로 정렬 */
			Collections.sort(list);

			/* 
			 * 1년치 데이터를 하나의 파일로 저장하는 부분
			 * 데이터 포맷 : 코드,년,월,일,시간,강수량
			 */
			File fileForWrite = new File(outputFolder + outputFileName + ".csv");
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileForWrite));

			for(int i=0; i<list.size(); i++){
				bufferedWriter.write(list.get(i).toString());
				bufferedWriter.newLine();
			}
			bufferedWriter.close();
			
			System.out.println(list.size());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}