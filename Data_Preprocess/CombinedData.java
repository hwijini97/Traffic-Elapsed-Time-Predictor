/**
 * @author 손휘진 
 * input으로 들어오는 영업소 간 교통 소요시간 csv 파일의 각 line의 포맷에 맞게 데이터를 저장하기 위한 용도
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

	/* 원래는 평일or휴일,년,월,일,시간,출발지위도,출발지경도,도착지위도,도착지경도,강수량,소요시간 형태로 하려 했는데
	 * 학습에 필요한 정보는 평일or휴일,시간,강수량,소요시간 뿐이라서 간략화함
	 * 다른 데이터도 포함시키려면 toString() 반환값 바꿔주면 됨
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