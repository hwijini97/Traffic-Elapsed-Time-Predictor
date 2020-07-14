/**
 * @author 손휘진
 * float 형태의 x, y 좌표를 저장하는 클래스
 */
public class Coordinate{
	float latitude;
	float longitude;
	public Coordinate(float latitude, float longitude){
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	public String toString(){
		return "(" + latitude + ", " + longitude + ")";
	}
}