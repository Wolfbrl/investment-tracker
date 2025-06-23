package domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import enums.*;
import utils.PasswordUtils;

public class InvestmentHandler {

	private List<Investment> allInvestments;

	public InvestmentHandler() {
		allInvestments = new ArrayList<>();
	}

	public void removeInvestment(String id) {
		Investment investment = getInvestmentById(id);
		User user = investment.getUser();
		allInvestments.remove(investment);
		user.removeInvestment(investment);
	}

	public Investment getInvestmentById(String id) {
		return allInvestments.stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
	}

	public List<Investment> getInvestments() {
		return allInvestments;
	}

	public void setInvestments(List<Investment> investments) {
		this.allInvestments = investments;
	}

	public void setNote(String id, String note) {
		getInvestmentById(id).setNote(note);
	}

	public void addInvestment(String id, String name, LocalDate startDate, BigDecimal initialValue,
			BigDecimal currentValue, Currencies currency, InvestmentType investmentType, User user, String note) {
		Investment newInvestment = new Investment(id, name, startDate, initialValue, currentValue, currency,
				investmentType, user, note);
		allInvestments.add(newInvestment);
		user.addInvestment(newInvestment);

	}

	public void createUser(String username, String password) {
		String salt = PasswordUtils.generateSalt();
		String hashedpassword = PasswordUtils.hashPassword(password, salt);
		User user = new User(username, hashedpassword, salt);

	}

}
