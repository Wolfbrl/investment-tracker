package domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import enums.*;

public class InvestmentHandler {

	private List<Investment> allInvestments;

	public InvestmentHandler() {
		allInvestments = new ArrayList<>();
	}

	public void addInvestment(String name, LocalDate startDate, BigDecimal initialValue, Currencies currency,
			InvestmentType investmentType, User user) {
		Investment newInvestment = new Investment(name, startDate, initialValue, currency, investmentType, user);
		allInvestments.add(newInvestment);
		user.addInvestment(newInvestment);
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

	public void addNote(String id, String note) {
		getInvestmentById(id).addNote(note);
	}

}
