package types;




public class Gps {
	private Integer idsensor_valor_mpu6050;
	private Integer idsensor;
	private String localizacion;
	
	@Override
	public String toString() {
		return "SensorValue [idsensor_valor_mpu6050=" + idsensor_valor_mpu6050 + ", idsensor=" + idsensor
				+ ", Localizacion" + localizacion +  "]";
	}



	public Gps() {
		super();
	}

	public Gps(Integer idsensor_valor_mpu6050, Integer idsensor, String localizacion) {
		super();
		this.idsensor_valor_mpu6050 = idsensor_valor_mpu6050;
		this.idsensor = idsensor;
		this.localizacion =localizacion;
		
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

	public String getLocalizacion() {
		return localizacion;
	}

	public void setLocalizacion(String localizacion) {
		this.localizacion = localizacion;
	}

	
	



}