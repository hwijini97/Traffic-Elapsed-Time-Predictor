/**
 * @author ������
 * float ������ x, y ��ǥ�� �����ϴ� Ŭ����
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