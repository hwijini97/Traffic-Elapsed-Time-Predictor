import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 학습에 사용될 출발지_목적지.csv 파일만 따로 복사해서 output 폴더에 복사해주는 클래스
 * @author 손휘진
 */
public class EssentialRoadCopier {
	public static void main(String[] args){
		EssentialRoadCopier copier = new EssentialRoadCopier();
		ArrayList<String> essentialTG = copier.loadEssentialTGList();
		copier.saveEssentialRoad(essentialTG);
		System.out.println("Done");
	}

	/**
	 * 필요한 TG의 리스트를 불러옴
	 */
	private ArrayList<String> loadEssentialTGList(){
		ArrayList<String> essentialRoad = new ArrayList<String>();
		try{
			File essentialTGListFile;
			String inputFolder = "D:\\Capstone Data\\";
			String fileName = "essentialRoadList";
			String road;
			essentialTGListFile = new File(inputFolder + fileName + ".txt");
			BufferedReader bufferedReader = new BufferedReader(new FileReader(essentialTGListFile));

			while((road = bufferedReader.readLine()) != null){
				essentialRoad.add(road);
			}
			bufferedReader.close();
		} catch(Exception e){
			e.printStackTrace();
		}

		return essentialRoad;
	}

	/**
	 * 필요한 road 파일을 outputFolder에 복사함
	 * @param list 필요한 road 리스트
	 */
	private void saveEssentialRoad(ArrayList<String> list) {
		String inputFolder = "D:\\Capstone Data\\inputs\\";
		String outputFolder = "D:\\Capstone Data\\outputs\\";
		try{
			for(int i=0; i<list.size(); i++){
				String roadName = list.get(i);
				File f = new File(inputFolder + roadName + ".csv");

				if(f.exists()){
					copyFile(inputFolder + roadName + ".csv", outputFolder + roadName + ".csv");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 파일을 복사한다.
	 * @param inFileName 복사할 파일
	 * @param outFileName 복사될 파일
	 */
	private void copyFile(String inFileName, String outFileName) {
		try {
			FileInputStream fis = new FileInputStream(inFileName);
			FileOutputStream fos = new FileOutputStream(outFileName);

			int data = 0;
			while((data=fis.read())!=-1) {
				fos.write(data);
			}
			fis.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
