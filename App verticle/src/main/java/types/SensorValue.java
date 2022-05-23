package types;

public class SensorValue {
	private int idsensor_valor_mpu6050;
	private int idsensor;
	private int a_value_x;
	private int a_value_y;
	private int a_value_z;
	private int g_value_x;
	private int g_value_y;
	private int g_value_z;
	private Long timestamp;
	@Override
	public String toString() {
		return "SensorValue [idsensor_valor_mpu6050=" + idsensor_valor_mpu6050 + ", idsensor=" + idsensor
				+ ", a_value_x=" + a_value_x + ", a_value_y=" + a_value_y + ", a_value_z=" + a_value_z + ", g_value_x="
				+ g_value_x + ", g_value_y=" + g_value_y + ", g_value_z=" + g_value_z + ", timestamp=" + timestamp
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + a_value_x;
		result = prime * result + a_value_y;
		result = prime * result + a_value_z;
		result = prime * result + g_value_x;
		result = prime * result + g_value_y;
		result = prime * result + g_value_z;
		result = prime * result + idsensor;
		result = prime * result + idsensor_valor_mpu6050;
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SensorValue other = (SensorValue) obj;
		if (a_value_x != other.a_value_x)
			return false;
		if (a_value_y != other.a_value_y)
			return false;
		if (a_value_z != other.a_value_z)
			return false;
		if (g_value_x != other.g_value_x)
			return false;
		if (g_value_y != other.g_value_y)
			return false;
		if (g_value_z != other.g_value_z)
			return false;
		if (idsensor != other.idsensor)
			return false;
		if (idsensor_valor_mpu6050 != other.idsensor_valor_mpu6050)
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}

	public int getIdsensor_valor_mpu6050() {
		return idsensor_valor_mpu6050;
	}

	public void setIdsensor_valor_mpu6050(int idsensor_valor_mpu6050) {
		this.idsensor_valor_mpu6050 = idsensor_valor_mpu6050;
	}

	public int getIdsensor() {
		return idsensor;
	}

	public void setIdsensor(int idsensor) {
		this.idsensor = idsensor;
	}

	public int getA_value_x() {
		return a_value_x;
	}

	public void setA_value_x(int a_value_x) {
		this.a_value_x = a_value_x;
	}

	public int getA_value_y() {
		return a_value_y;
	}

	public void setA_value_y(int a_value_y) {
		this.a_value_y = a_value_y;
	}

	public int getA_value_z() {
		return a_value_z;
	}

	public void setA_value_z(int a_value_z) {
		this.a_value_z = a_value_z;
	}

	public int getG_value_x() {
		return g_value_x;
	}

	public void setG_value_x(int g_value_x) {
		this.g_value_x = g_value_x;
	}

	public int getG_value_y() {
		return g_value_y;
	}

	public void setG_value_y(int g_value_y) {
		this.g_value_y = g_value_y;
	}

	public int getG_value_z() {
		return g_value_z;
	}

	public void setG_value_z(int g_value_z) {
		this.g_value_z = g_value_z;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public SensorValue() {
		super();
	}

	public SensorValue(int idsensor_valor_mpu6050, int idsensor, int a_value_x, int a_value_y, int a_value_z,
			int g_value_x, int g_value_y, int g_value_z ,Long timestamp) {
		super();
		this.idsensor_valor_mpu6050 = idsensor_valor_mpu6050;
		this.idsensor = idsensor;
		this.a_value_x = a_value_x;
		this.a_value_y = a_value_y;
		this.a_value_z = a_value_z;
		this.g_value_x = g_value_x;
		this.g_value_y = g_value_y;
		this.g_value_z = g_value_z;
		this.timestamp = timestamp;
	}



}
