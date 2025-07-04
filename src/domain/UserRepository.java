package domain;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import database.Database;
import enums.*;

public class UserRepository {

	// 1. Sla één user op
	public void saveUser(User user) {
		try (Connection conn = Database.connect()) {
			String sql = "INSERT INTO user (username, pwd, salt) VALUES (?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, user.getUsername());
			ps.setString(2, user.getPassword());
			ps.setString(3, user.getSalt());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 2. Sla één investment op
	public void saveInvestment(Investment investment) {
		try (Connection conn = Database.connect()) {
			String sql = "INSERT INTO investment (id, user, name, startDate, type, currency, initialValue, currentValue, profitLoss, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, investment.getId());
			ps.setString(2, investment.getUser());
			ps.setString(3, investment.getName());
			ps.setString(4, investment.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
			ps.setString(5, investment.getType().name());
			ps.setString(6, investment.getCurrency().name());
			ps.setBigDecimal(7, investment.getInitialValue());
			ps.setBigDecimal(8, investment.getCurrentValue());
			ps.setBigDecimal(9, investment.getProfitOrLoss());
			ps.setString(10, investment.getNote());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 3. Sla een lijst van users op
	public void saveUsers(List<User> users) {
		for (User user : users) {
			saveUser(user);
		}
	}

	// 4. Sla een lijst van investments op
	public void saveInvestments(List<Investment> investments) {
		for (Investment investment : investments) {
			saveInvestment(investment);
		}
	}

	public List<User> giveAllUsers() {
		List<User> users = new ArrayList<>();

		String sql = "SELECT username, pwd, salt FROM user";

		try (Connection conn = Database.connect();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				String username = rs.getString("username");
				String passwordHash = rs.getString("pwd");
				String salt = rs.getString("salt");

				User user = new User(username, passwordHash, salt);

				users.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return users;
	}

	public List<Investment> giveAllInvestments() {
		List<Investment> investments = new ArrayList<>();
		String sql = "SELECT * FROM investment";

		try (Connection conn = Database.connect();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				String id = rs.getString("id");
				String username = rs.getString("user");
				String name = rs.getString("name");
				LocalDate startDate = LocalDate.parse(rs.getString("startDate"));
				InvestmentType type = InvestmentType.valueOf(rs.getString("type"));
				Currencies currency = Currencies.valueOf(rs.getString("currency"));
				BigDecimal initialValue = rs.getBigDecimal("initialValue");
				BigDecimal currentValue = rs.getBigDecimal("currentValue");
				String note = rs.getString("note");

				User user = giveAllUsers().stream().filter(x -> x.getUsername().equals(username)).findFirst()
						.orElse(null);

				Investment investment = new Investment(id, name, startDate, initialValue, currentValue, currency, type,
						user.getUsername(), note);
				investments.add(investment);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return investments;
	}

	public void removeInvestment(Investment investment) {
		// TODO Auto-generated method stub

	}

	public boolean doesUserExist(String username) {
		String sql = "SELECT 1 FROM user WHERE username = ?";

		try (Connection conn = Database.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();

			return rs.next();

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean doesInvestmentExist(String invID) {
		String sql = "SELECT 1 FROM investment WHERE id = ?";

		try (Connection conn = Database.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, invID);
			ResultSet rs = ps.executeQuery();

			return rs.next();

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
