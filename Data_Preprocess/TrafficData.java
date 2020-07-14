/**
 * @author ������ 
 * input���� ������ ������ �� ���� �ҿ�ð� csv ������ �� line�� ���˿� �°� �����͸� �����ϱ� ���� �뵵
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
	public int compareTo(TrafficData o) { // ����� - ������ - �� - �� - �� - �ð��� ������ ��
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