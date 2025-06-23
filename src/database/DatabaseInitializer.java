package database;

import java.sql.*;

public class DatabaseInitializer {

	public static void initialize() {
		try (Connection conn = Database.connect(); Statement stmt = conn.createStatement()) {
			if (conn != null) {
				System.out.println("Connectie succesvol!");
			}

			String createUserTable = """
					CREATE TABLE IF NOT EXISTS user (
					    username TEXT PRIMARY KEY,
					    pwd TEXT NOT NULL,
					    salt TEXT NOT NULL,
					    numberofinvestments INTEGER DEFAULT 0
					);
					""";

			String createInvestmentTable = """
					CREATE TABLE IF NOT EXISTS investment (
					    id TEXT PRIMARY KEY ,
					    user TEXT NOT NULL,
					    name TEXT NOT NULL,
					    startDate TEXT,
					    type TEXT,
					    currency TEXT,
					    initialValue REAL,
					    currentValue REAL,
					    profitLoss REAL,
					    note TEXT,
					    FOREIGN KEY (user) REFERENCES user(username)
					);
					""";

			stmt.execute(createUserTable);
			stmt.execute(createInvestmentTable);

			System.out.println("Database-initialisation completed");

		} catch (SQLException e) {
			System.err.print("Database can't initialize: " + e.getMessage());
		}

	}

}
