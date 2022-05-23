package types;

public class Usuario {
private int idusuario;
private String nombre;
private Long tlf_usuario;
private String correo_usuario;
private Long tlf_emergencia;
private String modelo_moto;





public Usuario() {
	super();
}





public Usuario(int idusuario, String nombre, Long tlf_usuario, String correo_usuario, Long tlf_emergencia,
		String modelo_moto) {
	super();
	this.idusuario = idusuario;
	this.nombre = nombre;
	this.tlf_usuario = tlf_usuario;
	this.correo_usuario = correo_usuario;
	this.tlf_emergencia = tlf_emergencia;
	this.modelo_moto = modelo_moto;
}





public int getIdusuario() {
	return idusuario;
}





public void setIdusuario(int idusuario) {
	this.idusuario = idusuario;
}





public String getNombre() {
	return nombre;
}





public void setNombre(String nombre) {
	this.nombre = nombre;
}





public Long getTlf_usuario() {
	return tlf_usuario;
}





public void setTlf_usuario(Long tlf_usuario) {
	this.tlf_usuario = tlf_usuario;
}





public String getCorreo_usuario() {
	return correo_usuario;
}





public void setCorreo_usuario(String correo_usuario) {
	this.correo_usuario = correo_usuario;
}





public Long getTlf_emergencia() {
	return tlf_emergencia;
}





public void setTlf_emergencia(Long tlf_emergencia) {
	this.tlf_emergencia = tlf_emergencia;
}





public String getModelo_moto() {
	return modelo_moto;
}





public void setModelo_moto(String modelo_moto) {
	this.modelo_moto = modelo_moto;
}





@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((correo_usuario == null) ? 0 : correo_usuario.hashCode());
	result = prime * result + idusuario;
	result = prime * result + ((modelo_moto == null) ? 0 : modelo_moto.hashCode());
	result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
	result = prime * result + ((tlf_emergencia == null) ? 0 : tlf_emergencia.hashCode());
	result = prime * result + ((tlf_usuario == null) ? 0 : tlf_usuario.hashCode());
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
	Usuario other = (Usuario) obj;
	if (correo_usuario == null) {
		if (other.correo_usuario != null)
			return false;
	} else if (!correo_usuario.equals(other.correo_usuario))
		return false;
	if (idusuario != other.idusuario)
		return false;
	if (modelo_moto == null) {
		if (other.modelo_moto != null)
			return false;
	} else if (!modelo_moto.equals(other.modelo_moto))
		return false;
	if (nombre == null) {
		if (other.nombre != null)
			return false;
	} else if (!nombre.equals(other.nombre))
		return false;
	if (tlf_emergencia == null) {
		if (other.tlf_emergencia != null)
			return false;
	} else if (!tlf_emergencia.equals(other.tlf_emergencia))
		return false;
	if (tlf_usuario == null) {
		if (other.tlf_usuario != null)
			return false;
	} else if (!tlf_usuario.equals(other.tlf_usuario))
		return false;
	return true;
}





@Override
public String toString() {
	return "Usuario [idusuario=" + idusuario + ", nombre=" + nombre + ", tlf_usuario=" + tlf_usuario
			+ ", correo_usuario=" + correo_usuario + ", tlf_emergencia=" + tlf_emergencia + ", modelo_moto="
			+ modelo_moto + "]";
}





}