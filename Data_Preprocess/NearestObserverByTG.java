import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * �� �����ҷκ��� ��� ��� �����ұ����� �Ÿ��� �����Ͽ� ���� ����� �����ҿ� ���ν�Ű�� csv���Ϸ� ����
 * @author ������
 */
public class NearestObserverByTG {
	public static void main(String[] args){
		NearestObserverByTG n = new NearestObserverByTG();
		/* ������ �ڵ庰 ��ǥ �ҷ��ͼ� ArrayList�� ���� */
		ArrayList<LocationByCode> locationByTG = n.getLocationByTG();
		/* ���� �����Ϳ� �����ϴ� ������ �ڵ带 ArrayList�� ���� */
		ArrayList<Integer> observerList = n.getObserverList();
		/* ������ �ڵ庰 ��ǥ �ҷ��ͼ� ArrayList�� ���� */
		ArrayList<LocationByCode> locationByObserver = n.getLocationByObserver(observerList);
		/* �� �����ҿ� ���� ���� ����� �����Ҹ� ArrayList�� ���� */
		ArrayList<Mapping> mappingList = n.getNearestObserverByTG(locationByTG, locationByObserver);
		/* csv���Ϸ� ���� */
		n.save(mappingList);
	}

	/* ������ ���� �浵 */
	private ArrayList<LocationByCode> getLocationByTG(){
		try{
			ArrayList<LocationByCode> location;
			String inputFolder = "D:\\Capstone Data\\";
			String locationFileName = "LocationOfTG"; // ������ ���� �浵 ���� �̸�
			String line;

			File locationFile = new File(inputFolder + locationFileName + ".csv");
			BufferedReader bufferedReader_location = new BufferedReader(new FileReader(locationFile));
			location = new ArrayList<LocationByCode>(); // (������ ��ȣ)�� (����, �浵)�� ����

			while((line = bufferedReader_location.readLine()) != null){
				String[] stringArray = line.split(",");
				int code = Integer.parseInt(stringArray[0]);
				float latitude = Float.parseFloat(stringArray[2]); // ����
				float longitude = Float.parseFloat(stringArray[1]); // �浵

				location.add(new LocationByCode(code, latitude, longitude));
			}
			bufferedReader_location.close();

			return location;
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/* ���� �����Ϳ� ���Ե� ������ ����Ʈ */
	private ArrayList<Integer> getObserverList(){
		try{
			ArrayList<Integer> list;
			String inputFolder = "D:\\Capstone Data\\weatherData\\";
			String weatherFileName = "weatherData"; // ���� ������ ���� �̸�

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

	/* ������ ���� �浵 */
	private ArrayList<LocationByCode> getLocationByObserver(ArrayList<Integer> observerList){
		try{
			ArrayList<LocationByCode> location;
			String inputFolder = "D:\\Capstone Data\\";
			String locationFileName = "LocationOfObserver"; // ������ ���� �浵 ���� �̸�

			File locationFile = new File(inputFolder + locationFileName + ".csv");
			BufferedReader bufferedReader_location = new BufferedReader(new FileReader(locationFile));
			location = new ArrayList<LocationByCode>(); // (������ ��ȣ)�� (����, �浵)�� ����

			String line = bufferedReader_location.readLine(); // ù �� �ǳʶ�
			while((line = bufferedReader_location.readLine()) != null){
				String[] stringArray = line.split(",");
				if(stringArray.length > 5){
					int code = Integer.parseInt(stringArray[0]);
					if(observerList.contains(code)){
						float latitude = Float.parseFloat(stringArray[5]); // ����
						float longitude = Float.parseFloat(stringArray[6]); // �浵

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

	/* �� �����ҿ� ���� ���� ����� �����Ұ� ������� ����ϰ� HashMap�� �����Ͽ� ��ȯ */
	private ArrayList<Mapping> getNearestObserverByTG(ArrayList<LocationByCode> codeList, ArrayList<LocationByCode> observerList){
		ArrayList<Mapping> mappingList = new ArrayList<Mapping>(); // ������� ������ �ڵ�, ������ �ڵ�

		for(int i=0; i<codeList.size(); i++){
			LocationByCode nearestObserver = observerList.get(0);
			// ���� �Ÿ��� ��Ʈ�� �������ϴµ� �񱳸� �� ���̹Ƿ� ������ ���·� ���
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

	/* ���� ����� csv ���Ϸ� ���� */
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
 * �ڵ庰 ����, �浵 �����ϴ� Ŭ����
 * @author ������
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
 * ������ �ڵ�� ������ �ڵ带 ��� �����ϴ� Ŭ����
 * @author ������
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
