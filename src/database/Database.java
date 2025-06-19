package database;

import java.sql.*;

public class Database {
	public static Connection connect() {
		String url = "jdbc:sqlite:data/investment_tracker.db"; // in je projectmap

		try {
			return DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.out.println("Connectie mislukt: " + e.getMessage());
			return null;
		}
	}
}
