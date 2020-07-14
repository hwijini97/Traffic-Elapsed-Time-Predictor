/**
 * @author 손휘진 
 * input으로 들어오는 영업소 간 교통 소요시간 csv 파일의 각 line의 포맷에 맞게 데이터를 저장하기 위한 용도
 */
public class TrafficData implements Comparable<TrafficData>{
	public int year, month, day, hour, departure, arrival, elapsedTime;

	public TrafficData(int year, int month, int day, int hour, int departure, int arrival, int elapsedTime){
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.departure = departure;
		this.arrival = arrival;
		this.elapsedTime = elapsedTime;
	}

	public void printData(){
		System.out.println("year : " + year + 
				", month : " + month + 
				", day : " + day + 
				", hour : " + hour + 
				", departure : " + departure + 
				", arrival : " + arrival + 
				", elapsedTime : " + elapsedTime);
	}

	@Override
	public int compareTo(TrafficData o) { // 출발지 - 목적지 - 년 - 월 - 일 - 시간대 순으로 비교
		if(this.departure == o.departure){
			if(this.arrival == o.arrival){
				if(this.year == o.year){
					if(this.month == o.month){
						if(this.day == o.day){
							if(this.hour == o.hour){
								return 0;
							} else return this.hour - o.hour;
						} else return this.day - o.day;
					} else return this.month - o.month;
				} else return this.year - o.year;
			} else return this.arrival - o.arrival;
		} else return this.departure - o.departure;
	}
}