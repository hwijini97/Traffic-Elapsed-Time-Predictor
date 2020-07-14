/**
 * @author ������ 
 * input���� ������ ������ �� ���� �ҿ�ð� csv ������ �� line�� ���˿� �°� �����͸� �����ϱ� ���� �뵵
 */
@SuppressWarnings("unused")
public class CombinedData{
	private int holiday, weekday, year, month, day, hour, elapsedTime;
	private float dLatitude, dLongitude, aLatitude, aLongitude, rain; 

	public CombinedData(int holiday, int weekday, int year, int month, int day, int hour, 
			float dLatitude, float dLongitude, float aLatitude, float aLongitude, int elapsedTime, float rain){
		this.holiday = holiday;
		this.weekday = weekday;
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.dLatitude = dLatitude;
		this.dLongitude = dLongitude;
		this.aLatitude = aLatitude;
		this.aLongitude = aLongitude;
		this.elapsedTime = elapsedTime;
		this.rain = rain;
	}

	/* ������ ����or����,��,��,��,�ð�,���������,������浵,����������,�������浵,������,�ҿ�ð� ���·� �Ϸ� �ߴµ�
	 * �н��� �ʿ��� ������ ����or����,�ð�,������,�ҿ�ð� ���̶� ����ȭ��
	 * �ٸ� �����͵� ���Խ�Ű���� toString() ��ȯ�� �ٲ��ָ� ��
	 */
	@Override
	public String toString(){
		return month +
				"," + day +
				"," + holiday +
				"," + weekday +
				"," + hour + 
				"," + rain +
				"," + elapsedTime;
	}
}