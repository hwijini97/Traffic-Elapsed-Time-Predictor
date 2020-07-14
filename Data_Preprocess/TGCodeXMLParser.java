import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * ���������� ���п��� ������ �ڵ庰 ����(����, �浵, ������ �̸� ��)�� XML ���·� �ҷ��ͼ� �����
 * ��µ� ������ ���۾��� ���� ��ġ ������ ���� ���� �����ϰ� CodeXMLFilter.java�� �Է����� �����
 * ó���� ���������� ���� ������ ���� Ű�� �Է��ؾ� �� 
 * @author ������
 */
public class TGCodeXMLParser {
	public static void main(String[] args){
		TGCodeXMLParser p = new TGCodeXMLParser();
		
		Scanner s = new Scanner(System.in);
		System.out.println("Enter Service Key");
		
		String serviceKey = s.nextLine();
		s.close();
		String format = "xml";
		String url = "http://data.ex.co.kr/exopenapi/locationinfo/locationinfoUnit?"
				+"serviceKey="+serviceKey
				+"&type="+format
				+"&unitCode[]=*"
				+"&numOfRows=90"
				+"&pageNo=";
		for(int i=1; i<=6; i++){
			System.out.println(p.getXML(url+i));
		}
	}

	private String getXML(String url){
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection)obj.openConnection();
			con.setRequestMethod("GET");

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF8"));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			return response.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}