

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connexion {
	private static final String url = "jdbc:mysql://localhost:3306/recipe";
	private static final String username = "root";
	private static final String password = "";

	private static Connection conn = null;

	public static Connection getConnection() throws SQLException {
		if (conn == null || conn.isClosed()) {
			try {
				conn = DriverManager.getConnection(url, username, password);
			} catch (SQLException e) {
				throw new SQLException("Erreur lors de la connexion à la base de données", e);
			}
		}
		return conn;
	}

	public static void closeConnection() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				System.err.println(
						"Erreur lors de la fermeture de la connexion à la base de données : " + e.getMessage());
			}
		}
	}
}