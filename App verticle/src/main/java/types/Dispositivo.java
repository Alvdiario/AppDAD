package types;

public class Dispositivo {
	private int iddispositivo;
	private String ip;
	private String nombre;
	private int idusuario;
	private Long initialtimestamp;
	
	
	
	
	
	public Dispositivo() {
		super();
	}





	public Dispositivo(int iddispositivo, String ip, String nombre, int idusuario, Long initialtimestamp) {
		super();
		this.iddispositivo = iddispositivo;
		this.ip = ip;
		this.nombre = nombre;
		this.idusuario = idusuario;
		this.initialtimestamp = initialtimestamp;
	}





	public int getIddispositivo() {
		return iddispositivo;
	}





	public void setIddispositivo(int iddispositivo) {
		this.iddispositivo = iddispositivo;
	}





	public String getIp() {
		return ip;
	}





	public void setIp(String ip) {
		this.ip = ip;
	}





	public String getNombre() {
		return nombre;
	}





	public void setNombre(String nombre) {
		this.nombre = nombre;
	}





	public int getIdusuario() {
		return idusuario;
	}





	public void setIdusuario(int idusuario) {
		this.idusuario = idusuario;
	}





	public Long getInitialtimestamp() {
		return initialtimestamp;
	}





	public void setInitialtimestamp(Long initialtimestamp) {
		this.initialtimestamp = initialtimestamp;
	}





	@Override
	public String toString() {
		return "Dispositivo [iddispositivo=" + iddispositivo + ", ip=" + ip + ", nombre=" + nombre + ", idusuario="
				+ idusuario + ", initialtimestamp=" + initialtimestamp + "]";
	}





	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + iddispositivo;
		result = prime * result + idusuario;
		result = prime * result + ((initialtimestamp == null) ? 0 : initialtimestamp.hashCode());
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
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
		Dispositivo other = (Dispositivo) obj;
		if (iddispositivo != other.iddispositivo)
			return false;
		if (idusuario != other.idusuario)
			return false;
		if (initialtimestamp == null) {
			if (other.initialtimestamp != null)
				return false;
		} else if (!initialtimestamp.equals(other.initialtimestamp))
			return false;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;
		return true;
	}

	
	
}