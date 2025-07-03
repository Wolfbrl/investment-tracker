package domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import enums.*;
import utils.PasswordUtils;

public class InvestmentHandler {

	private UserRepository repo;

	public InvestmentHandler() {
		repo = new UserRepository();
	}

	public void removeInvestment(String id) {
		Investment investment = getInvestmentById(id);
		repo.removeInvestment(investment);

	}

	public Investment getInvestmentById(String id) {
		return repo.giveAllInvestments().stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
	}

	public List<Investment> giveAllInvestments() {
		return repo.giveAllInvestments();
	}

	public void setNote(String id, String note) {
		getInvestmentById(id).setNote(note);
	}

	public void addInvestment(String id, String name, LocalDate startDate, BigDecimal initialValue,
			BigDecimal currentValue, Currencies currency, InvestmentType investmentType, User user, String note) {
		Investment newInvestment = new Investment(id, name, startDate, initialValue, currentValue, currency,
				investmentType, user, note);
		repo.saveInvestment(newInvestment);

	}

	public void createUser(String username, String password) {
		String salt = PasswordUtils.generateSalt();
		String hashedpassword = PasswordUtils.hashPassword(password, salt);
		User user = new User(username, hashedpassword, salt);
		repo.saveUser(user);

	}

	public List<User> giveAllUsers() {
		return repo.giveAllUsers();
	}

	public boolean doesUserExist(String username) {
		return repo.doesUserExist(username);
	}

}
