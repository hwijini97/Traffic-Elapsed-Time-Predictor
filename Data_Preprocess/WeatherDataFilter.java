import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

/**
 * @author ������
 * ���� �����͸� ������ ���·� �����Ͽ� �ϳ��� ���Ϸ� �����Ѵ�.
 * ������ ���� ������ : 1~3��, 4~6��, 7~9��, 10~12������ �� 4���� ���Ϸ� �Ǿ� ����
 * 
 * 1. ������ 1��ġ ���� �����Ϳ��� �ʿ��� ������(���� ��ȣ, ��¥, ������)�� �����Ͽ� �޸�(list ����)�� ����
 * 2. �޸𸮿� ����� �� ������ �������� (�ڵ�,��,��,��,�ð�,������) ���·� �ϳ��� ���Ͽ� ������ ����
 */
public class WeatherDataFilter {
	private String inputFolder = "D:\\Capstone Data\\weatherInput\\";
	private String outputFolder = "D:\\Capstone Data\\weatherData\\";
	private String outputFileName = "weatherData"; // ����� ���� �̸�
	private ArrayList<WeatherData> list; // 1��ġ ���� ������ ���� ����Ʈ

	public static void main(String[] args){
		WeatherDataFilter wdf = new WeatherDataFilter();
		wdf.filter();
	}

	private void filter(){
		try{
			/* 1��ġ ������ �޸𸮷� �ҷ����� �κ� */
			list = new ArrayList<WeatherData>();

			File fileForRead;
			BufferedReader bufferedReader_weather = null;
			String line;
			String lastCode = "";

			for(int fileName=1; fileName<=4; fileName++){
				fileForRead = new File(inputFolder + fileName + ".csv");
				bufferedReader_weather = new BufferedReader(new FileReader(fileForRead));
				bufferedReader_weather.readLine(); // ù�� �ǳʶ�
				while((line = bufferedReader_weather.readLine()) != null){
					StringTokenizer st = new StringTokenizer(line, ","); // csv������ ������ ,���ڷ� ���еǾ� ����
					String code = st.nextToken();
					String rawDate = st.nextToken();
					String rain = st.nextToken();
					String year = rawDate.substring(0, 4);
					String month = rawDate.substring(5, 7);
					String day = rawDate.substring(8, 10);
					String hour = rawDate.length() == 15 ? rawDate.substring(11, 12) : rawDate.substring(11, 13);
					if(!code.equals(lastCode)){ // ���� ������ ���� ������ ������ ���� ������ ���� ������ �Ѿ�� ��� -> �� �տ� 0���� �����͸� �߰�����
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

			/* �ڵ�,��,��,��,�ð� ������ ���� */
			Collections.sort(list);

			/* 
			 * 1��ġ �����͸� �ϳ��� ���Ϸ� �����ϴ� �κ�
			 * ������ ���� : �ڵ�,��,��,��,�ð�,������
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