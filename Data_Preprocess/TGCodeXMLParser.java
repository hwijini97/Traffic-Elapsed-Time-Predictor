import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * 공공데이터 포털에서 영업소 코드별 정보(위도, 경도, 영업소 이름 등)를 XML 형태로 불러와서 출력함
 * 출력된 내용은 수작업을 통해 위치 정보가 없는 값을 제거하고 CodeXMLFilter.java의 입력으로 사용함
 * 처음에 공공데이터 포털 계정의 서비스 키를 입력해야 함 
 * @author 손휘진
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