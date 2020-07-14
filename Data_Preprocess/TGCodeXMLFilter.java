import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * 영업소 코드별 위치 XML 파일에서 영업소 코드, 위도, 경도만 추출하고 정렬한 뒤 csv파일로 저장
 * @author 손휘진
 */
public class TGCodeXMLFilter {
	public static void main(String[] args){
		try{
			String inputFolder = "D:\\Capstone Data\\";
			File xmlFile = new File(inputFolder + "TGCodeXML.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document document = dBuilder.parse(xmlFile);
			
			ArrayList<InformationOfCode> infoList = new ArrayList<InformationOfCode>();
			NodeList unitCodeList = document.getDocumentElement().getElementsByTagName("unitCode");
			NodeList xValueList = document.getDocumentElement().getElementsByTagName("xValue");
			NodeList yValueList = document.getDocumentElement().getElementsByTagName("yValue");
			for(int i=0; i<unitCodeList.getLength(); i++){
				infoList.add(new InformationOfCode(
						Integer.parseInt(unitCodeList.item(i).getTextContent().replace(" ", "")),
						xValueList.item(i).getTextContent().replace(" ", ""),
						yValueList.item(i).getTextContent().replace(" ", "")));
			}
			
			Collections.sort(infoList);
			
			String outputFileName = "LocationOfTG";
			File fileForWrite = new File(outputFileName + ".csv");
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileForWrite));

			for(int i=0; i<infoList.size(); i++){
				bufferedWriter.write(infoList.get(i).toString());
				bufferedWriter.newLine();
			}
			bufferedWriter.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}

/**
 * 영업소별 위도와 경도를 저장하는 클래스
 * @author 손휘진
 */
class InformationOfCode implements Comparable<InformationOfCode>{
	int unitCode;
	String latitude, longitude;
	public InformationOfCode(int unitCode, String latitude, String longitude){
		this.unitCode = unitCode;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	public String toString(){
		return unitCode + "," + latitude + "," + longitude;
	}

	@Override
	public int compareTo(InformationOfCode o) {
		return this.unitCode - o.unitCode;
	}
}