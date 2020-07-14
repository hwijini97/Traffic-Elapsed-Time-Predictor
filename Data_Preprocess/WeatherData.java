/**
 * @author 손휘진
 * 날씨 데이터 저장하기 위한 클래스
 */
public class WeatherData implements Comparable<WeatherData>{
	int code, year, month, day, hour;
	float rain;
	public WeatherData(int number, int year, int month, int day, int hour, float rain){
		this.code = number;
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.rain = rain;
	}

	@Override
	public String toString(){
		return code + ","
				+ year + ","
				+ month + ","
				+ day + ","
				+ hour + ","
				+ rain;
	}

	@Override
	public int compareTo(WeatherData o) {
		if(this.code == o.code){
			if(this.year == o.year){
				if(this.month == o.month){
					if(this.day == o.day){
						if(this.hour == o.hour){
							return 0;
						} else return this.hour - o.hour;
					} else return this.day - o.day;
				} else return this.month - o.month;
			} else return this.year - o.year;
		} else return this.code - o.code;
	}
}