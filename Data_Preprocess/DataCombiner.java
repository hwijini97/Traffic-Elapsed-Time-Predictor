import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * 머신러닝 학습에 사용할 최종 데이터를 만드는 파일
 * 영업소 간 이동 소요시간 데이터와 지점별 기상 데이터를 결합해서 최종 데이터를 생성함
 * 1. LocationOfTG.csv 파일 불러와서 각 영업소의 위도와 경도 데이터를 메모리에 저장
 * 2. NearestObserverByTG.csv 파일 불러와서 각 영업소와 가장 가까운 기상 관측소의 코드를 메모리에 저장 + 영업소 코드 리스트에 저장
 * 3. 위의 파일 다시 불러와서 영업소 리스트 배열에 저장
 * 4. weatherData.csv 파일 불러와서 기상 관측 데이터를 메모리에 저장
 * 5. 날짜별 평일/휴일 데이터 메모리에 저장 : YY/MM/DD(일의 자리 수면 십의 자리 수에 0 포함하지 않음) -> 0(평일) or 1(휴일)
 * 6. EssentialTGList.txt 파일 불러와서 필요한 TG 리스트를 메모리에 저장
 * 7. 출발지_목적지.csv 형태로 저장된 영업소 간 이동 소요 시간 데이터를 불러와서 
 *    각 파일에 대해 평일or휴일,년,월,일,시간,출발지위도,출발지경도,도착지위도,도착지경도,강수량,소요시간 형태로 저장
 * @author 손휘진
 */
public class DataCombiner {
	static HashMap<Integer, Coordinate> locationOfTG;
	static HashMap<Integer, Integer> nearestObserverByTG;
	static int[] TGList;
	static HashMap<Integer, HashMap<String, Float>> weatherData;
	static HashMap<String, WeekdayInfo> holidayData;
	static ArrayList<Integer> essentialTGList;

	public static void main(String[] args){
		DataCombiner combiner = new DataCombiner();

		locationOfTG = combiner.getLocationOfTG();
		System.out.println("1. 영업소 위치 데이터 불러오기 완료");
		nearestObserverByTG = combiner.getNearestObserverByTG();
		System.out.println("2. 각 영업소와 가장 가까운 기상 관측소 데이터 불러오기 완료");
		TGList = combiner.getTG(nearestObserverByTG.size());
		System.out.println("3. 영업소 리스트 불러오기 완료");
		weatherData = combiner.getWeatherData(); // 없는 날짜 및 시간대도 있음 -> 강수량 0으로 처리
		System.out.println("4. 기상 데이터 불러오기 완료");
		holidayData = combiner.getHolidayData();
		System.out.println("5. 날짜별 평일/휴일 데이터 불러오기 완료");
		essentialTGList = combiner.getEssentialTGList();
		System.out.println("6. 필요한 톨게이트 리스트 불러오기 완료");

		combiner.combine();
		System.out.println("7. 최종 데이터 생성 완료");
	}

	private HashMap<Integer, Coordinate> getLocationOfTG(){
		try{
			HashMap<Integer, Coordinate> map;
			String inputFolder = "D:\\Capstone Data\\";
			String locationFileName = "LocationOfTG"; // 영업소 위도 경도 파일 이름
			String line;

			File locationFile = new File(inputFolder + locationFileName + ".csv");
			BufferedReader bufferedReader_location = new BufferedReader(new FileReader(locationFile));
			map = new HashMap<Integer, Coordinate>(); // (영업소 번호)를 (위도, 경도)로 매핑

			while((line = bufferedReader_location.readLine()) != null){
				String[] stringArray = line.split(",");
				int code = Integer.parseInt(stringArray[0]);
				float latitude = Float.parseFloat(stringArray[2]); // 위도
				float longitude = Float.parseFloat(stringArray[1]); // 경도

				map.put(code, new Coordinate(latitude, longitude));
			}
			bufferedReader_location.close();

			return map;
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	private int[] getTG(int size){
		try{
			String inputFolder = "D:\\Capstone Data\\";
			String mappingFileName = "NearestObserverByTG"; // 각 영업소로부터 가장 가까운 기상 관측소 매핑된 파일 이름
			String line;

			File mappingFile = new File(inputFolder + mappingFileName + ".csv");
			BufferedReader bufferedReader = new BufferedReader(new FileReader(mappingFile));

			int[] TG = new int[size];

			for(int i=0; i<size; i++){
				line = bufferedReader.readLine();
				String[] stringArray = line.split(",");
				TG[i] = Integer.parseInt(stringArray[0]); // 영업소 코드
			}

			bufferedReader.close();

			return TG;
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	private HashMap<Integer, Integer> getNearestObserverByTG(){
		try{
			HashMap<Integer, Integer> map;
			String inputFolder = "D:\\Capstone Data\\";
			String mappingFileName = "NearestObserverByTG"; // 각 영업소로부터 가장 가까운 기상 관측소 매핑된 파일 이름
			String line;

			File mappingFile = new File(inputFolder + mappingFileName + ".csv");
			BufferedReader bufferedReader = new BufferedReader(new FileReader(mappingFile));
			map = new HashMap<Integer, Integer>(); // 영업소 코드를를 기상 관측소 코드로 매핑

			while((line = bufferedReader.readLine()) != null){
				String[] stringArray = line.split(",");
				int codeTG = Integer.parseInt(stringArray[0]); // 영업소 코드
				int codeObserver = Integer.parseInt(stringArray[1]); // 기상 관측소 코드

				map.put(codeTG, codeObserver);
			}
			bufferedReader.close();

			return map;
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	private HashMap<Integer, HashMap<String, Float>> getWeatherData() {
		HashMap<Integer, HashMap<String, Float>> map = new HashMap<Integer, HashMap<String, Float>>();
		HashMap<String, Float> innerMap = new HashMap<String, Float>();
		try{
			File fileForRead;
			String inputFolder = "D:\\Capstone Data\\weatherData\\";
			String fileName = "weatherData";
			String line;
			String lastCode = "";
			fileForRead = new File(inputFolder + fileName + ".csv");
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileForRead));

			while((line = bufferedReader.readLine()) != null){
				StringTokenizer st = new StringTokenizer(line, ",");
				String code = st.nextToken();
				String year = st.nextToken();
				String month = st.nextToken();
				String day = st.nextToken();
				String hour = st.nextToken();
				String rain = st.nextToken();

				if(!code.equals(lastCode)){
					if(innerMap.size() == 0){ // 파일의 첫부분인 경우
						lastCode = code;
					} else{ // 코드가 바뀌는 경우 이전 코드의 정보를 map에 put함
						map.put(Integer.parseInt(lastCode), innerMap);
						lastCode = code;
						innerMap = new HashMap<String, Float>();
					}
				}
				String timeString = year + "/" + month + "/" + day + "/" + hour;
				innerMap.put(timeString, Float.parseFloat(rain));
			}
			bufferedReader.close();

			return map;
		} catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}

	class WeekdayInfo{
		int weekDay, holiday;
		public WeekdayInfo(int weekday, int holiday){
			this.weekDay = weekday;
			this.holiday = holiday;
		}
	}

	private HashMap<String, WeekdayInfo> getHolidayData() {
		HashMap<String, WeekdayInfo> map = new HashMap<String, WeekdayInfo>();
		try{
			File fileForRead;
			String inputFolder = "D:\\Capstone Data\\";
			String fileName = "Holiday";
			String line;
			fileForRead = new File(inputFolder + fileName + ".csv");
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileForRead));

			while((line = bufferedReader.readLine()) != null){
				StringTokenizer st = new StringTokenizer(line, ",");
				String date = st.nextToken(); // 날짜
				int weekDay = Integer.parseInt(st.nextToken()); // 요일
				int holiday = Integer.parseInt(st.nextToken()); // 공휴일 여부

				map.put(date, new WeekdayInfo(weekDay, holiday));
			}
			bufferedReader.close();

			return map;
		} catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}

	private ArrayList<Integer> getEssentialTGList() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		try{
			File fileForRead;
			String inputFolder = "D:\\Capstone Data\\";
			String fileName = "essentialTGList";
			String line;
			fileForRead = new File(inputFolder + fileName + ".txt");
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileForRead));

			while((line = bufferedReader.readLine()) != null){
				list.add(Integer.parseInt(line));
			}
			bufferedReader.close();

			return list;
		} catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}

	private void combine() {
		boolean error;
		String inputFolder = "D:\\Capstone Data\\trafficData\\";
		String outputFolder = "D:\\Capstone Data\\dataForTrain2\\";
		String line;

		for(int i=0; i<TGList.length; i++){
			for(int j=0; j<TGList.length; j++){
				error = false;
				int TG1 = TGList[i];
				int TG2 = TGList[j];
				if(essentialTGList.contains(TG1) && essentialTGList.contains(TG2)){
					String fileName = TG1 + "_" + TG2;

					try{
						File fileForInput = new File(inputFolder + fileName + ".csv");
						if(fileForInput.exists()){
							BufferedReader bufferedReader = new BufferedReader(new FileReader(fileForInput));
							ArrayList<CombinedData> list = new ArrayList<CombinedData>();

							while((line = bufferedReader.readLine()) != null){
								String[] stringArray = line.split(",");
								int year = Integer.parseInt(stringArray[0]);
								int month = Integer.parseInt(stringArray[1]);
								int day = Integer.parseInt(stringArray[2]);
								int hour = Integer.parseInt(stringArray[3]);
								int departure = Integer.parseInt(stringArray[4]);
								int arrival = Integer.parseInt(stringArray[5]);
								int elapsedTime = Integer.parseInt(stringArray[6]);

								WeekdayInfo weekdayInfo = holidayData.get("" + year%100 + "/" + month + "/" + day);
								int holiday = weekdayInfo.holiday;
								int weekday = weekdayInfo.weekDay;
								float dLatitude = locationOfTG.get(departure).latitude;
								float dLongitude = locationOfTG.get(departure).longitude;
								float aLatitude = locationOfTG.get(arrival).latitude;
								float aLongitude = locationOfTG.get(arrival).longitude;

								float lastRain1 = 0;
								float lastRain2 = 0;

								int observer1 = nearestObserverByTG.get(departure);
								int observer2 = nearestObserverByTG.get(arrival);

								String timeString = year + "/" + month + "/" + day + "/" + hour;
								float rain1 = weatherData.containsKey(observer1) ? 
										weatherData.get(observer1).containsKey(timeString) ? 
												weatherData.get(observer1).get(timeString) : lastRain1 : -1;
								lastRain1 = rain1;
								float rain2 = weatherData.containsKey(observer2) ? 
										weatherData.get(observer2).containsKey(timeString) ? 
												weatherData.get(observer2).get(timeString) : lastRain2 : -1;
								lastRain2 = rain2;

								if(rain1 == -1 || rain2 == -1){
									error = true;
									break;
								}

								float rain = (rain1 + rain2) / 2;

								list.add(new CombinedData(holiday, weekday, year, month, day, hour, 
										dLatitude, dLongitude, aLatitude, aLongitude, elapsedTime, rain));
							}
							if(error)
								break;
							bufferedReader.close();

							File fileForWrite = new File(outputFolder + fileName + ".csv");

							BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileForWrite));

							for(int k=0; k<list.size(); k++){
								bufferedWriter.write(list.get(k).toString());
								bufferedWriter.newLine();
							}
							bufferedWriter.close();

							System.out.println(TGList[i] + "_" + TGList[j] + " 완료!");
						}
					} catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
	}
}