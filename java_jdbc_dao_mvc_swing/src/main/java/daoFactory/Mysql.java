package daoFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import dao.concrete.MysqlUserDao;
import dao.interfaces.UserDao;

public class Mysql extends DaoFactory {

	//private static String url = "jdbc:mysql://127.0.0.1:3306/";   // Banco  de dados oficial
    String url = "jdbc:mysql://localhost:3306/java_mvc_test_db";    //Banco de dados teste
	private static String database = "mailsystem";
	private static String driver = "com.mysql.cj.jdbc.Driver";
	private static String user = "root";
	private static String password = "@GSkpx87";

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
