import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * �ӽŷ��� �н��� ����� ���� �����͸� ����� ����
 * ������ �� �̵� �ҿ�ð� �����Ϳ� ������ ��� �����͸� �����ؼ� ���� �����͸� ������
 * 1. LocationOfTG.csv ���� �ҷ��ͼ� �� �������� ������ �浵 �����͸� �޸𸮿� ����
 * 2. NearestObserverByTG.csv ���� �ҷ��ͼ� �� �����ҿ� ���� ����� ��� �������� �ڵ带 �޸𸮿� ���� + ������ �ڵ� ����Ʈ�� ����
 * 3. ���� ���� �ٽ� �ҷ��ͼ� ������ ����Ʈ �迭�� ����
 * 4. weatherData.csv ���� �ҷ��ͼ� ��� ���� �����͸� �޸𸮿� ����
 * 5. ��¥�� ����/���� ������ �޸𸮿� ���� : YY/MM/DD(���� �ڸ� ���� ���� �ڸ� ���� 0 �������� ����) -> 0(����) or 1(����)
 * 6. EssentialTGList.txt ���� �ҷ��ͼ� �ʿ��� TG ����Ʈ�� �޸𸮿� ����
 * 7. �����_������.csv ���·� ����� ������ �� �̵� �ҿ� �ð� �����͸� �ҷ��ͼ� 
 *    �� ���Ͽ� ���� ����or����,��,��,��,�ð�,���������,������浵,����������,�������浵,������,�ҿ�ð� ���·� ����
 * @author ������
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
		System.out.println("1. ������ ��ġ ������ �ҷ����� �Ϸ�");
		nearestObserverByTG = combiner.getNearestObserverByTG();
		System.out.println("2. �� �����ҿ� ���� ����� ��� ������ ������ �ҷ����� �Ϸ�");
		TGList = combiner.getTG(nearestObserverByTG.size());
		System.out.println("3. ������ ����Ʈ �ҷ����� �Ϸ�");
		weatherData = combiner.getWeatherData(); // ���� ��¥ �� �ð��뵵 ���� -> ������ 0���� ó��
		System.out.println("4. ��� ������ �ҷ����� �Ϸ�");
		holidayData = combiner.getHolidayData();
		System.out.println("5. ��¥�� ����/���� ������ �ҷ����� �Ϸ�");
		essentialTGList = combiner.getEssentialTGList();
		System.out.println("6. �ʿ��� �����Ʈ ����Ʈ �ҷ����� �Ϸ�");

		combiner.combine();
		System.out.println("7. ���� ������ ���� �Ϸ�");
	}

	private HashMap<Integer, Coordinate> getLocationOfTG(){
		try{
			HashMap<Integer, Coordinate> map;
			String inputFolder = "D:\\Capstone Data\\";
			String locationFileName = "LocationOfTG"; // ������ ���� �浵 ���� �̸�
			String line;

			File locationFile = new File(inputFolder + locationFileName + ".csv");
			BufferedReader bufferedReader_location = new BufferedReader(new FileReader(locationFile));
			map = new HashMap<Integer, Coordinate>(); // (������ ��ȣ)�� (����, �浵)�� ����

			while((line = bufferedReader_location.readLine()) != null){
				String[] stringArray = line.split(",");
				int code = Integer.parseInt(stringArray[0]);
				float latitude = Float.parseFloat(stringArray[2]); // ����
				float longitude = Float.parseFloat(stringArray[1]); // �浵

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
			String mappingFileName = "NearestObserverByTG"; // �� �����ҷκ��� ���� ����� ��� ������ ���ε� ���� �̸�
			String line;

			File mappingFile = new File(inputFolder + mappingFileName + ".csv");
			BufferedReader bufferedReader = new BufferedReader(new FileReader(mappingFile));

			int[] TG = new int[size];

			for(int i=0; i<size; i++){
				line = bufferedReader.readLine();
				String[] stringArray = line.split(",");
				TG[i] = Integer.parseInt(stringArray[0]); // ������ �ڵ�
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
			String mappingFileName = "NearestObserverByTG"; // �� �����ҷκ��� ���� ����� ��� ������ ���ε� ���� �̸�
			String line;

			File mappingFile = new File(inputFolder + mappingFileName + ".csv");
			BufferedReader bufferedReader = new BufferedReader(new FileReader(mappingFile));
			map = new HashMap<Integer, Integer>(); // ������ �ڵ带�� ��� ������ �ڵ�� ����

			while((line = bufferedReader.readLine()) != null){
				String[] stringArray = line.split(",");
				int codeTG = Integer.parseInt(stringArray[0]); // ������ �ڵ�
				int codeObserver = Integer.parseInt(stringArray[1]); // ��� ������ �ڵ�

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
					if(innerMap.size() == 0){ // ������ ù�κ��� ���
						lastCode = code;
					} else{ // �ڵ尡 �ٲ�� ��� ���� �ڵ��� ������ map�� put��
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
				String date = st.nextToken(); // ��¥
				int weekDay = Integer.parseInt(st.nextToken()); // ����
				int holiday = Integer.parseInt(st.nextToken()); // ������ ����

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

							System.out.println(TGList[i] + "_" + TGList[j] + " �Ϸ�!");
						}
					} catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
	}
}