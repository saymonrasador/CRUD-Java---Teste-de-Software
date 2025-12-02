package daoFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import dao.concrete.MysqlUserDao;
import dao.interfaces.UserDao;

public class Mysql extends DaoFactory {

	// String url = "jdbc:mysql://localhost:3306/java_mvc_test_db"; //Banco de dados
	// teste
	private static String host = System.getenv().getOrDefault("MYSQL_HOST", "127.0.0.1");
	private static String port = System.getenv().getOrDefault("MYSQL_PORT", "3306");
	private static String database = System.getenv().getOrDefault("MYSQL_DATABASE", "mailsystem");
	private static String user = System.getenv().getOrDefault("MYSQL_USER", "root");
	private static String password = System.getenv().getOrDefault("MYSQL_PASSWORD", "@GSkpx87");

	public Connection openConnection() {
		try {
			Class.forName(driver);
			Connection connection = DriverManager.getConnection(url, user, password);
			return connection;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			System.err.println(
					"Não foi possével salvar os dados! O Banco de dados não estão respondendo!");
		}
		return null;
	}

	@Override
	public UserDao getUserDao() {
		return new MysqlUserDao();
	}
}
