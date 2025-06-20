package domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import enums.InvestmentType;

public class Investment implements Comparable<Investment> {

	private String name;
	private LocalDate startDate;

	private Currency currency;
	private InvestmentType type;
	private String note;
	private final String id;
	private BigDecimal initialValue;
	private BigDecimal currentValue;

	public Investment(String name, LocalDate startDate, BigDecimal initialValue, Currency currency,
			InvestmentType type) {
		setName(name);
		setStartDate(startDate);
		setInitialValue(initialValue);
		setCurrentValue(initialValue);
		setCurrency(currency);
		setType(type);
		this.id = String.format("INV-%s-%s", type.name(), UUID.randomUUID().toString());

	}

	public BigDecimal getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(BigDecimal initialValue) {
		this.initialValue = initialValue;
	}

	public BigDecimal getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(BigDecimal currentValue) {
		this.currentValue = currentValue;
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

	public void addNote(String note) {
		if (note.length() > 150) {
			throw new IllegalArgumentException("The note can only have a maximum of 150 characters");
		}
		this.note = note;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return String.format("[%s] %s %s - %s (%s), Initial: %s, Current: %s", id, startDate, name, currency, type,
				initialValue, currentValue);
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

	public BigDecimal getProfitOrLoss() {
		return currentValue.subtract(initialValue);
	}

	public void updateCurrentValueUsingPercentage(BigDecimal percentage) {
		BigDecimal multipier = BigDecimal.ONE.add(percentage.divide(BigDecimal.valueOf(100)));
		this.currentValue = this.currentValue.multiply(multipier);
	}

	public void updateCurrentValueUsingAmount(BigDecimal amount) {
		this.currentValue = this.currentValue.add(amount);
	}

}
