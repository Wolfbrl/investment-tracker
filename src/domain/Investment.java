package domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import enums.InvestmentType;

public class Investment implements Comparable<Investment> {

	private String name;
	private LocalDate startDate;
	private BigDecimal amount;
	private Currency currency;
	private InvestmentType type;
	private String note;
	private final String id;

	public Investment(String name, LocalDate startDate, BigDecimal amount, Currency currency, InvestmentType type) {
		setName(name);
		setStartDate(startDate);
		setAmount(amount);
		setCurrency(currency);
		setType(type);

		this.id = String.format("INV-%s-%s", type.name(), UUID.randomUUID().toString());

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public InvestmentType getType() {
		return type;
	}

	public void setType(InvestmentType type) {
		this.type = type;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return String.format("[%s] %s %s - %s %s (%s)", id, startDate, name, amount, currency, type);
	}

	@Override
	public int compareTo(Investment o) {
		int datecompare = this.startDate.compareTo(o.startDate);
		return datecompare != 0 ? datecompare : name.compareTo(o.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Investment other = (Investment) obj;
		return Objects.equals(id, other.id);
	}

}
