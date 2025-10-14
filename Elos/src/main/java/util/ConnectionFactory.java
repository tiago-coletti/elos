package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
	
	// Definição de variáveis de conexão com o banco de dados
	private static final String DRIVER = "com.mysql.cj.jdbc.Driver"; // Driver JDBC para MySQL
	private static final String URL = "jdbc:mysql://127.0.0.1:3306/elos?useTimezone=true&serverTimezone=UTC";
	private static final String USER = "root"; // Usuário do banco
	private static final String PASSWORD = "tiago0424"; // Senha do banco

	// Método para estabelecer a conexão com o banco de dados
	public static Connection conectar() throws SQLException {
		Connection con = null;
		try {
			// Carrega o driver JDBC
			Class.forName(DRIVER);
			// Estabelece a conexão com o banco
			con = DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (Exception e) {
			throw new SQLException("Erro ao conectar ao banco de dados", e);
		}
		return con; // Retorna a conexão (pode ser nula em caso de erro)
	}
}
