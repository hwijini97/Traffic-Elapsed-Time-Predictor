import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * 각 영업소로부터 모든 기상 관측소까지의 거리를 측정하여 가장 가까운 관측소와 매핑시키고 csv파일로 저장
 * @author 손휘진
 */
public class NearestObserverByTG {
	public static void main(String[] args){
		NearestObserverByTG n = new NearestObserverByTG();
		/* 영업소 코드별 좌표 불러와서 ArrayList에 저장 */
		ArrayList<LocationByCode> locationByTG = n.getLocationByTG();
		/* 날씨 데이터에 존재하는 관측소 코드를 ArrayList에 저장 */
		ArrayList<Integer> observerList = n.getObserverList();
		/* 관측소 코드별 좌표 불러와서 ArrayList에 저장 */
		ArrayList<LocationByCode> locationByObserver = n.getLocationByObserver(observerList);
		/* 각 영업소에 대해 가장 가까운 관측소를 ArrayList에 저장 */
		ArrayList<Mapping> mappingList = n.getNearestObserverByTG(locationByTG, locationByObserver);
		/* csv파일로 저장 */
		n.save(mappingList);
	}

	/* 영업소 위도 경도 */
	private ArrayList<LocationByCode> getLocationByTG(){
		try{
			ArrayList<LocationByCode> location;
			String inputFolder = "D:\\Capstone Data\\";
			String locationFileName = "LocationOfTG"; // 영업소 위도 경도 파일 이름
			String line;

			File locationFile = new File(inputFolder + locationFileName + ".csv");
			BufferedReader bufferedReader_location = new BufferedReader(new FileReader(locationFile));
			location = new ArrayList<LocationByCode>(); // (영업소 번호)를 (위도, 경도)로 매핑

			while((line = bufferedReader_location.readLine()) != null){
				String[] stringArray = line.split(",");
				int code = Integer.parseInt(stringArray[0]);
				float latitude = Float.parseFloat(stringArray[2]); // 위도
				float longitude = Float.parseFloat(stringArray[1]); // 경도

				location.add(new LocationByCode(code, latitude, longitude));
			}
			bufferedReader_location.close();

			return location;
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/* 날씨 데이터에 포함된 관측소 리스트 */
	private ArrayList<Integer> getObserverList(){
		try{
			ArrayList<Integer> list;
			String inputFolder = "D:\\Capstone Data\\weatherData\\";
			String weatherFileName = "weatherData"; // 날씨 데이터 파일 이름

			File weatherFile = new File(inputFolder + weatherFileName + ".csv");
			BufferedReader bufferedReader_weather = new BufferedReader(new FileReader(weatherFile));
			list = new ArrayList<Integer>();

			String line;
			while((line = bufferedReader_weather.readLine()) != null){
				String[] stringArray = line.split(",");
				int code = Integer.parseInt(stringArray[0]);
				if(!list.contains(code)){
					list.add(code);
				}
			}
			bufferedReader_weather.close();

			return list;
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/* 관측소 위도 경도 */
	private ArrayList<LocationByCode> getLocationByObserver(ArrayList<Integer> observerList){
		try{
			ArrayList<LocationByCode> location;
			String inputFolder = "D:\\Capstone Data\\";
			String locationFileName = "LocationOfObserver"; // 관측소 위도 경도 파일 이름

			File locationFile = new File(inputFolder + locationFileName + ".csv");
			BufferedReader bufferedReader_location = new BufferedReader(new FileReader(locationFile));
			location = new ArrayList<LocationByCode>(); // (관측소 번호)를 (위도, 경도)로 매핑

			String line = bufferedReader_location.readLine(); // 첫 줄 건너뜀
			while((line = bufferedReader_location.readLine()) != null){
				String[] stringArray = line.split(",");
				if(stringArray.length > 5){
					int code = Integer.parseInt(stringArray[0]);
					if(observerList.contains(code)){
						float latitude = Float.parseFloat(stringArray[5]); // 위도
						float longitude = Float.parseFloat(stringArray[6]); // 경도

						location.add(new LocationByCode(code, latitude, longitude));
					}
				}
			}
			bufferedReader_location.close();

			return location;
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/* 각 영업소에 대해 가장 가까운 관측소가 어디인지 계산하고 HashMap에 저장하여 반환 */
	private ArrayList<Mapping> getNearestObserverByTG(ArrayList<LocationByCode> codeList, ArrayList<LocationByCode> observerList){
		ArrayList<Mapping> mappingList = new ArrayList<Mapping>(); // 순서대로 영업소 코드, 관측소 코드

		for(int i=0; i<codeList.size(); i++){
			LocationByCode nearestObserver = observerList.get(0);
			// 원래 거리는 루트를 씌워야하는데 비교만 할 것이므로 제곱된 상태로 계산
			float minDistance = (codeList.get(i).latitude - observerList.get(0).latitude) *
					(codeList.get(i).latitude - observerList.get(0).latitude) + 
					(codeList.get(i).longitude - observerList.get(0).longitude) *
					(codeList.get(i).longitude - observerList.get(0).longitude); 

			for(int j=1; j<observerList.size(); j++){
				float distance = (codeList.get(i).latitude - observerList.get(j).latitude) *
						(codeList.get(i).latitude - observerList.get(j).latitude) + 
						(codeList.get(i).longitude - observerList.get(j).longitude) *
						(codeList.get(i).longitude - observerList.get(j).longitude); 
				if(distance < minDistance){
					nearestObserver = observerList.get(j);
					minDistance = distance;
				}
			}
			mappingList.add(new Mapping(codeList.get(i).code, nearestObserver.code));
		}
		return mappingList;
	}

	/* 매핑 결과를 csv 파일로 저장 */
	private void save(ArrayList<Mapping> mappingList){
		try{
			String outputFileName = "NearestObserverByTG";
			String outputFolder = "D:\\Capstone Data\\";
			
			File fileForWrite = new File(outputFolder + outputFileName + ".csv");
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileForWrite));

			for(int i=0; i<mappingList.size(); i++){
				bufferedWriter.write(mappingList.get(i).toString());
				bufferedWriter.newLine();
			}
			bufferedWriter.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}

/**
 * 코드별 위도, 경도 저장하는 클래스
 * @author 손휘진
 */
class LocationByCode{
	int code;
	float latitude, longitude;
	public LocationByCode(int code, float latitude, float longitude){
		this.code = code;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	public String toString(){
		return code + ":(" + latitude + ", " + longitude + ")";
	}
}

/**
 * 영업소 코드와 관측소 코드를 묶어서 저장하는 클래스
 * @author 손휘진
 */
class Mapping{
	int codeTG, codeObserver;
	public Mapping(int codeTG, int codeObserver){
		this.codeTG = codeTG;
		this.codeObserver = codeObserver;
	}

	@Override
	public String toString(){
		return codeTG + "," + codeObserver;
	}
}
