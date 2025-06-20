package domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import enums.InvestmentType;

public class InvestmentHandler {

	private List<Investment> investments;

	public InvestmentHandler() {
		investments = new ArrayList<>();
	}

	public void addInvestment(String name, LocalDate startDate, BigDecimal initialValue, Currency currency,
			InvestmentType investmentType) {
		investments.add(new Investment(name, startDate, initialValue, currency, investmentType));
	}

	public Investment getInvestmentById(String id) {
		return investments.stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
	}

	public List<Investment> getInvestments() {
		return investments;
	}

	public void setInvestments(List<Investment> investments) {
		this.investments = investments;
	}

	public void addNote(String id, String note) {
		getInvestmentById(id).addNote(note);
	}

}
