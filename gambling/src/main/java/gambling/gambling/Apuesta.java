package gambling.gambling;

public abstract class Apuesta {
	protected int id;
	protected String fechaApuesta;
	protected String combinacion;
	protected double precio;
	protected double ganado;
	protected int sorteo;
	protected Jugador jugador;

	// Constructor
	public Apuesta(int id, String fechaApuesta, String combinacion, double precio, double ganado, int sorteo,
			Jugador jugador) {
		this.id = id;
		this.fechaApuesta = fechaApuesta;
		this.combinacion = combinacion;

		this.precio = precio;
		this.ganado = ganado;
		this.sorteo = sorteo;
		this.jugador = jugador;
	}

	public Apuesta(String fechaApuesta, String combinacion, double precio, double ganado, int sorteo, Jugador jugador) {
		this.fechaApuesta = fechaApuesta;
		this.combinacion = combinacion;

		this.precio = precio;
		this.ganado = ganado;
		this.sorteo = sorteo;
		this.jugador = jugador;
	}

	public Apuesta() {

	}

	// Getters y setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFechaApuesta() {
		return fechaApuesta;
	}

	public void setFechaApuesta(String fechaApuesta) {
		this.fechaApuesta = fechaApuesta;
	}

	public String getCombinacion() {
		return combinacion;
	}

	public void setCombinacion(String combinacion) {
		this.combinacion = combinacion;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public double getGanado() {
		return ganado;
	}

	public void setGanado(double ganado) {
		this.ganado = ganado;
	}

	public int getSorteo() {
		return sorteo;
	}

	public void setSorteo(int sorteo) {
		this.sorteo = sorteo;
	}

	public Jugador getJugador() {
		return jugador;
	}

	public void setJugador(Jugador jugador) {
		this.jugador = jugador;
	}

	@Override
	public String toString() {
		return "Apuesta [id=" + id + ", fechaApuesta=" + fechaApuesta + ", combinacion=" + combinacion + ", precio="
				+ precio + ", ganado=" + ganado + ", sorteo=" + sorteo + ", jugador=" + jugador + "]";
	}

}
