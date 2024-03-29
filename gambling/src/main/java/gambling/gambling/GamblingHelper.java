package gambling.gambling;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 
 * @author Carlos & Manuel
 *
 */
public class GamblingHelper {

	/**
	 * establece la conexion con la base de datos
	 * 
	 * @return
	 * @throws Exception
	 */
	public Connection crearConexion() throws Exception {
		Connection conexion = null;
		try {
			Properties props = new Properties();
			props.load(new FileReader("files/db.properties"));
			String driver = props.getProperty("database.driver");
			String url = props.getProperty("database.url");
			String usuario = props.getProperty("database.user");
			String pass = props.getProperty("database.password");

			Class.forName(driver);
			conexion = DriverManager.getConnection(url, usuario, pass);
			conexion.setAutoCommit(false);
			System.out.println("conexion creada");
		} catch (IOException | SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			throw e;
		}
		return conexion;
	}

	/**
	 * Cierra la conexion con la base de datos
	 * 
	 * @param conex
	 * @throws SQLException
	 */
	public void cerrarConexion(Connection conex) throws SQLException {

		try {
			if (conex != null)
				conex.close();
			System.out.println("conexion cerrada");
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}

	}

	/**
	 * 
	 * consulta para recoger todas las apuestas de la base de datos
	 * 
	 * @param conexion
	 * @return
	 * @throws Exception
	 */
	public List<Apuesta> seleccionarApuestas(Connection conexion) throws Exception {
		List<Apuesta> apuestas = new ArrayList<>();
		String sql = "select * from apuesta";
		Statement sentencia = null;
		ResultSet resultado = null;
		try {
			sentencia = conexion.createStatement();
			resultado = sentencia.executeQuery(sql);

			while (resultado.next()) {

				int idApuesta = resultado.getInt("id");
				String fechaApuesta = resultado.getString("fecha_apuesta");
				String combinacion = resultado.getString("combinacion");
				double precio = resultado.getDouble("precio");
				double ganado = resultado.getDouble("ganado");

				int sorteo = resultado.getInt("id_sorteo");
				String mail = resultado.getString("correo_jugador");
				Jugador jugador = jugadorPorMail(conexion, mail);

				String tipo = resultado.getString("tipo");
				switch (tipo) {
				case "LOTERIA":
					int reintegro = resultado.getInt("reintegro");
					Loteria loteria = new Loteria(idApuesta, fechaApuesta, combinacion, precio, ganado, sorteo, jugador,
							reintegro);
					apuestas.add(loteria);
					break;
				case "QUINIELA":
					Quiniela quiniela = new Quiniela(idApuesta, fechaApuesta, combinacion, precio, ganado, sorteo,
							jugador);
					apuestas.add(quiniela);
					break;
				case "GORDO":
					int nclave = resultado.getInt("num_clave");
					Gordo gordo = new Gordo(idApuesta, fechaApuesta, combinacion, precio, ganado, sorteo, jugador,
							nclave);

					apuestas.add(gordo);
					break;
				case "PRIMITIVA":

					int complementario = resultado.getInt("complementario");
					reintegro = resultado.getInt("reintegro");
					Primitiva primitiva = new Primitiva(idApuesta, fechaApuesta, combinacion, precio, ganado, sorteo,
							jugador, complementario, reintegro);
					apuestas.add(primitiva);
					break;
				case "EUROMILLON":
					String estrellas = resultado.getString("estrellas");
					Euromillon euromillon = new Euromillon(idApuesta, fechaApuesta, combinacion, precio, ganado, sorteo,
							jugador, estrellas);
					apuestas.add(euromillon);
					break;
				default:
					break;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (resultado != null)
				resultado.close();
			if (sentencia != null)
				sentencia.close();
		}

		return apuestas;
	}

	/**
	 * Recoge el jugador de la base datos que contenga el correo electronico pasado
	 * por parametro
	 * 
	 * @param conexion
	 * @param mail
	 * @return
	 * @throws SQLException
	 */
	private Jugador jugadorPorMail(Connection conexion, String mail) throws SQLException {
		Jugador jugador = new Jugador();
		String sql = "select * from jugador where correo_electronico=?";
		PreparedStatement sentencia = null;
		ResultSet resultado = null;
		try {
			sentencia = conexion.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			sentencia.setString(1, mail);

			resultado = sentencia.executeQuery();

			while (resultado.next()) {
				jugador.setCorreoElectronico(resultado.getString("correo_electronico"));
				jugador.setContrasena(resultado.getString("contraseña"));
				jugador.setDni(resultado.getString("dni"));
				jugador.setTelefono(resultado.getString("telefono"));
				jugador.setDinero(resultado.getDouble("dinero"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (resultado != null)
				resultado.close();
			if (sentencia != null)
				sentencia.close();
		}

		return jugador;
	}

	/**
	 * selecciona todas las apuestas del tipo pasado por parametro
	 * 
	 * @param conexion
	 * @param tipo
	 * @return
	 * @throws Exception
	 */
	public List<Apuesta> seleccionarApuestas(Connection conexion, String tipo) throws Exception {
		List<Apuesta> apuestas = new ArrayList<>();
		String sql = "select * from apuesta where tipo=?";
		PreparedStatement sentencia = null;
		ResultSet resultado = null;
		try {
			sentencia = conexion.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			sentencia.setString(0, tipo.toUpperCase());

			resultado = sentencia.executeQuery();

			while (resultado.next()) {

				int idApuesta = resultado.getInt("id");
				String fechaApuesta = resultado.getString("fecha_apuesta");
				String combinacion = resultado.getString("combinacion");
				double precio = resultado.getDouble("precio");
				double ganado = resultado.getDouble("ganado");

				int sorteo = resultado.getInt("id_sorteo");
				Jugador jugador = jugadorPorMail(conexion, resultado.getString("correo_jugador"));

				switch (tipo) {
				case "LOTERIA":
					int reintegro = resultado.getInt("reintegro");
					Loteria loteria = new Loteria(idApuesta, fechaApuesta, combinacion, precio, ganado, sorteo, jugador,
							reintegro);
					apuestas.add(loteria);
					break;
				case "QUINIELA":
					Quiniela quiniela = new Quiniela(idApuesta, fechaApuesta, combinacion, precio, ganado, sorteo,
							jugador);
					apuestas.add(quiniela);
					break;
				case "GORDO":
					int nclave = resultado.getInt("num_clave");
					Gordo gordo = new Gordo(idApuesta, fechaApuesta, combinacion, precio, ganado, sorteo, jugador,
							nclave);

					apuestas.add(gordo);
					break;
				case "PRIMITIVA":

					int complementario = resultado.getInt("complementario");
					reintegro = resultado.getInt("reintegro");
					Primitiva primitiva = new Primitiva(idApuesta, fechaApuesta, combinacion, precio, ganado, sorteo,
							jugador, complementario, reintegro);
					apuestas.add(primitiva);
					break;
				case "EUROMILLON":
					String estrellas = resultado.getString("estrellas");
					Euromillon euromillon = new Euromillon(idApuesta, fechaApuesta, combinacion, precio, ganado, sorteo,
							jugador, estrellas);
					apuestas.add(euromillon);
					break;
				default:
					break;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (resultado != null)
				resultado.close();
			if (sentencia != null)
				sentencia.close();
		}

		return apuestas;
	}

	/**
	 * 
	 * inserta una apuesta de cualquier tipo en la base de datos sentencia u otra
	 * 
	 * @param conex
	 * @param apuesta
	 * @throws SQLException
	 */
	private void insertarApuesta(Connection conex, Apuesta apuesta) throws SQLException {

		String sql = null;
		PreparedStatement sentencia = null;
		ResultSet genKeys = null;
		try {

			if (apuesta instanceof Loteria) {
				sql = "Insert into apuesta (`fecha_apuesta`,`combinacion`,`tipo`,`precio`,`ganado`,`reintegro`,`id_sorteo`,`correo_jugador`) VALUES(?,?,?,?,?,?,?,?) ";
				sentencia = conex.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
				sentencia.setString(3, "LOTERIA");
				sentencia.setInt(6, ((Loteria) apuesta).getReintegro());
				sentencia.setInt(7, apuesta.getSorteo());
				sentencia.setString(8, apuesta.getJugador().getCorreoElectronico());

			} else if (apuesta instanceof Gordo) {
				sql = "Insert into apuesta (`fecha_apuesta`,`combinacion`,`tipo`,`precio`,`ganado`,`num_clave`,`id_sorteo`,`correo_jugador`) VALUES(?,?,?,?,?,?,?,?) ";
				sentencia = conex.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
				sentencia.setString(3, "GORDO");
				sentencia.setInt(6, ((Gordo) apuesta).getnClave());
				sentencia.setInt(7, apuesta.getSorteo());
				sentencia.setString(8, apuesta.getJugador().getCorreoElectronico());

			} else if (apuesta instanceof Primitiva) {
				sql = "Insert into apuesta (`fecha_apuesta`,`combinacion`,`tipo`,`precio`,`ganado`,`reintegro`,`complementario`,`id_sorteo`,`correo_jugador`) VALUES(?,?,?,?,?,?,?,?,?) ";
				sentencia = conex.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
				sentencia.setString(3, "PRIMITIVA");

				sentencia.setInt(6, ((Primitiva) apuesta).getReintegro());
				sentencia.setInt(7, ((Primitiva) apuesta).getComplementario());
				sentencia.setInt(8, apuesta.getSorteo());
				sentencia.setString(9, apuesta.getJugador().getCorreoElectronico());

			} else if (apuesta instanceof Euromillon) {
				sql = "Insert into apuesta (`fecha_apuesta`,`combinacion`,`tipo`,`precio`,`ganado`,`estrellas`,`id_sorteo`,`correo_jugador`) VALUES(?,?,?,?,?,?,?,?) ";
				sentencia = conex.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
				sentencia.setString(3, "EUROMILLON");
				sentencia.setString(6, ((Euromillon) apuesta).getEstrellas());
				sentencia.setInt(7, apuesta.getSorteo());
				sentencia.setString(8, apuesta.getJugador().getCorreoElectronico());

			} else if (apuesta instanceof Quiniela) {

				sql = "Insert into apuesta (`fecha_apuesta`,`combinacion`,`tipo`,`precio`,`ganado`,`id_sorteo`,`correo_jugador`) VALUES(?,?,?,?,?,?,?) ";
				sentencia = conex.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
				sentencia.setString(3, "QUINIELA");

				sentencia.setInt(6, apuesta.getSorteo());
				sentencia.setString(7, apuesta.getJugador().getCorreoElectronico());
			}

			sentencia.setString(1, apuesta.getFechaApuesta());
			sentencia.setString(2, apuesta.getCombinacion());
			sentencia.setDouble(4, apuesta.getPrecio());
			sentencia.setDouble(5, apuesta.getGanado());

			if (sentencia.executeUpdate() > 0) {
				genKeys = sentencia.getGeneratedKeys();

				while (genKeys.next()) {
					int id = genKeys.getInt(1);
					apuesta.setId(id);
				}
			}

			conex.commit();
			System.out.println("apuesta añadida");

		} catch (SQLException e) {
			conex.rollback();
			e.printStackTrace();
			throw e;
		} finally {
			if (genKeys != null)
				genKeys.close();
			if (sentencia != null)
				sentencia.close();
		}

	}

	/**
	 * Inserta un sorteo en la base de datos, ademas insertará las apuestas que este
	 * sorteo tenga si es que las tiene
	 * 
	 * @param connection
	 * @param sorteo
	 * @throws SQLException
	 */
	public void insertarSorteo(Connection connection, Sorteo sorteo) throws SQLException {
		String sql = "INSERT INTO sorteo (fecha_apertura, fecha_cierre, fecha_hora_celebracion, resultado, tipo) "
				+ "VALUES (?, ?, ?, ?, ?)";

		try {
			PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, sorteo.getFechaApertura());
			statement.setString(2, sorteo.getFechaCierre());
			statement.setString(3, sorteo.getFechaHoraCelebracion());
			statement.setString(4, sorteo.getResultado());
			statement.setString(5, sorteo.getTipo());

			if (statement.executeUpdate() > 0) {
				ResultSet generatedKeys = statement.getGeneratedKeys();
				if (generatedKeys.next()) {
					int id = generatedKeys.getInt(1);
					sorteo.setId(id);
				}
			}
			List<Apuesta> apuestas = sorteo.getApuestas();
			if (apuestas != null) {
				for (Apuesta apuesta : apuestas) {
					apuesta.setSorteo(sorteo.getId());
					insertarApuesta(connection, apuesta);
				}
			}

			connection.commit();
			System.out.println("SORTEO AÑADIDO");
		} catch (SQLException e) {
			connection.rollback();
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Inserta un jugador en la base de datos
	 * 
	 * @param connection
	 * @param jugador
	 * @throws SQLException
	 */
	public void insertarJugador(Connection connection, Jugador jugador) throws SQLException {
		String sql = "INSERT INTO jugador (correo_electronico, contraseña, dni, telefono, dinero) "
				+ "VALUES (?, ?, ?, ?, ?)";

		try {
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, jugador.getCorreoElectronico());
			statement.setString(2, jugador.getContrasena());
			statement.setString(3, jugador.getDni());
			statement.setString(4, jugador.getTelefono());
			statement.setDouble(5, jugador.getDinero());

			statement.executeUpdate();
			connection.commit();
			System.out.println("JUGADOR AGREGADO");

		} catch (Exception e) {
			connection.rollback();
			e.printStackTrace();
		}
	}

	/**
	 * Devuelve las apuestas realizadas por el jugador especificado ordenadas por
	 * fecha de mas reciente a antigua
	 * 
	 * @param connection
	 * @param jugador
	 * @return
	 * @throws SQLException
	 */
	public List<Apuesta> buscarApuestasPorJugador(Connection connection, Jugador jugador) throws SQLException {
		String sql = "SELECT * FROM apuesta WHERE correo_jugador = ? ORDER BY fecha_apuesta DESC";
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		List<Apuesta> apuestas = new ArrayList<>();

		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, jugador.getDni());
			resultSet = statement.executeQuery();

			while (resultSet.next()) {
				Apuesta apuesta = crearApuestaDesdeResultSet(connection, resultSet);
				apuestas.add(apuesta);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
		}

		return apuestas;
	}

	/**
	 * devuelve todas las apuestas que tenga un sorteo
	 * 
	 * @param connection
	 * @param sorteo
	 * @return
	 * @throws SQLException
	 */
	public List<Apuesta> buscarApuestasPorSorteo(Connection connection, Sorteo sorteo) throws SQLException {
		String sql = "SELECT * FROM apuesta WHERE id_sorteo = ? ORDER BY fecha_apuesta";
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		List<Apuesta> apuestas = new ArrayList<>();

		try {
			statement = connection.prepareStatement(sql);
			statement.setInt(1, sorteo.getId());
			resultSet = statement.executeQuery();

			while (resultSet.next()) {
				Apuesta apuesta = crearApuestaDesdeResultSet(connection, resultSet);
				apuestas.add(apuesta);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
		}

		return apuestas;
	}

	/**
	 * metodo privado para crear en java las apuestas recibidas en la base de datos
	 * 
	 * @param connection
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	private Apuesta crearApuestaDesdeResultSet(Connection connection, ResultSet resultSet) throws SQLException {
		// Obtener los valores de las columnas del ResultSet
		int id = resultSet.getInt("id");
		String fechaApuesta = resultSet.getString("fecha_apuesta");
		String combinacion = resultSet.getString("combinacion");
		double precio = resultSet.getDouble("precio");
		double ganado = resultSet.getDouble("ganado");
		int sorteo = resultSet.getInt("id_sorteo");
		String mail = resultSet.getString("correo_jugador");

		Jugador jugador = obtenerJugadorPorMail(mail, connection);

		String tipo = resultSet.getString("tipo");
		Apuesta apuesta = null;

		switch (tipo) {
		case "LOTERIA":
			int reintegro = resultSet.getInt("reintegro");
			apuesta = new Loteria(id, fechaApuesta, combinacion, precio, ganado, sorteo, jugador, reintegro);
			break;
		case "GORDO":
			int numClave = resultSet.getInt("num_clave");
			apuesta = new Gordo(id, fechaApuesta, combinacion, precio, ganado, sorteo, jugador, numClave);
			break;
		case "PRIMITIVA":
			int reintegroP = resultSet.getInt("reintegro");
			int complementario = resultSet.getInt("complementario");
			apuesta = new Primitiva(id, fechaApuesta, combinacion, precio, ganado, sorteo, jugador, reintegroP,
					complementario);
			break;
		case "EUROMILLON":
			String estrellas = resultSet.getString("estrellas");
			apuesta = new Euromillon(id, fechaApuesta, combinacion, precio, ganado, sorteo, jugador, estrellas);
			break;
		case "QUINIELA":
			apuesta = new Quiniela(id, fechaApuesta, combinacion, precio, ganado, sorteo, jugador);
			break;
		}

		return apuesta;
	}

	/**
	 * devuelve todos los sorteos de la base de datos
	 * 
	 * @param conex
	 * @return
	 */
	public List<Sorteo> ObtenerSorteos(Connection conex) {
		List<Sorteo> sorteos = new ArrayList<>();
		String sql = "SELECT * FROM sorteo ";

		Statement statement = null;
		ResultSet resultSet = null;

		try {

			statement = conex.createStatement();
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				Sorteo sorteo;
				int id = resultSet.getInt("id");
				String fechaApertura = resultSet.getString("fecha_apertura");
				String fechaCierre = resultSet.getString("fecha_cierre");
				String fechaHoraCelebracion = resultSet.getString("fecha_hora_celebracion");
				String resultado = resultSet.getString("resultado");
				String tipo = resultSet.getString("tipo");
				sorteo = new Sorteo(id, fechaApertura, fechaCierre, fechaHoraCelebracion, resultado, tipo, null);
				List<Apuesta> apuestas = buscarApuestasPorSorteo(conex, sorteo);
				sorteo.setApuestas(apuestas);
				sorteos.add(sorteo);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return sorteos;
	}

	/**
	 * metodo para sacar un jugador entero a traves de su email
	 * 
	 * @param mail
	 * @param connection
	 * @return
	 */
	public Jugador obtenerJugadorPorMail(String mail, Connection connection) {
		String sql = "SELECT * FROM jugador WHERE correo_electronico = ?";
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		Jugador jugador = null;

		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, mail);
			resultSet = statement.executeQuery();

			if (resultSet.next()) {
				// Obtener los valores de las columnas del ResultSet
				String correoElectronico = resultSet.getString("correo_electronico");
				String contrasena = resultSet.getString("contraseña");
				String telefono = resultSet.getString("telefono");
				double dinero = resultSet.getDouble("dinero");
				String jugadorDni = resultSet.getString("dni");

				// Crear el objeto Jugador
				jugador = new Jugador(correoElectronico, contrasena, jugadorDni, telefono, dinero);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return jugador;
	}

}
