import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * �н��� ���� �����_������.csv ���ϸ� ���� �����ؼ� output ������ �������ִ� Ŭ����
 * @author ������
 */
public class EssentialRoadCopier {
	public static void main(String[] args){
		EssentialRoadCopier copier = new EssentialRoadCopier();
		ArrayList<String> essentialTG = copier.loadEssentialTGList();
		copier.saveEssentialRoad(essentialTG);
		System.out.println("Done");
	}

	/**
	 * �ʿ��� TG�� ����Ʈ�� �ҷ���
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
	 * �ʿ��� road ������ outputFolder�� ������
	 * @param list �ʿ��� road ����Ʈ
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
	 * ������ �����Ѵ�.
	 * @param inFileName ������ ����
	 * @param outFileName ����� ����
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
