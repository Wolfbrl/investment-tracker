package domain;

import java.math.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import enums.*;

public class Investment implements Comparable<Investment> {

	private String name;
	private LocalDate startDate;

	private Currencies currency;
	private InvestmentType type;
	private String note = "";
	private final String id;
	private BigDecimal initialValue;
	private BigDecimal currentValue;
	private String user;
	private BigDecimal startPrice;

	private List<ValuePoint> valueHistory;

	public Investment(String id, String name, LocalDate startDate, BigDecimal startPrice, BigDecimal initialValue,
			BigDecimal currentValue, Currencies currency, InvestmentType type, String user, String note) {
		this.id = id;
		setName(name);
		setStartDate(startDate);
		setStartPrice(startPrice);
		setInitialValue(initialValue);
		setCurrentValue(currentValue);
		setCurrency(currency);
		setType(type);
		setUser(user);
		setNote(note);

//		this.id = String.format("INV-%s-%s", type.name(), UUID.randomUUID().toString());

	}

	public BigDecimal getStartPrice() {
		return startPrice;
	}

	public void setStartPrice(BigDecimal startPrice) {
		if (startPrice.doubleValue() < 0) {
			throw new IllegalArgumentException("Start price can't be less than 0...");
		}
		this.startPrice = startPrice;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public BigDecimal getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(BigDecimal initialValue) {
		if (initialValue.doubleValue() <= 0) {
			throw new IllegalArgumentException("amount cannot be less than or 0");
		}
		this.initialValue = initialValue;
	}

	public BigDecimal getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(BigDecimal currentValue) {
		if (currentValue.doubleValue() < 0) {
			throw new IllegalArgumentException("current value cannot be less than 0");
		}
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

	public Currencies getCurrency() {
		return currency;
	}

	public void setCurrency(Currencies currency) {
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
		if (note.length() > 100) {
			throw new IllegalArgumentException("The note can only have a maximum of 100 characters");
		}
		this.note = note;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return String.format("[%s] %s %s %s - %s (%s), Initial: %s, Current: %s", id, user, startDate, name, currency,
				type, initialValue, currentValue);
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

	public List<ValuePoint> getSimulatedHistory() {
		List<ValuePoint> result = new ArrayList<>();
		LocalDate today = LocalDate.now();
		LocalDate start = getStartDate();
		long days = ChronoUnit.DAYS.between(start, today);

		// Als start en today gelijk zijn, voeg enkel 1 punt toe
		if (days <= 0) {
			result.add(new ValuePoint(start, currentValue)); // of initialValue
			return result;
		}

		// Normaal verloop
		BigDecimal delta = getProfitOrLoss().divide(BigDecimal.valueOf(days), RoundingMode.HALF_UP);
		BigDecimal value = getInitialValue();

		for (int i = 0; i <= days; i++) {
			LocalDate date = start.plusDays(i);
			result.add(new ValuePoint(date, value));
			value = value.add(delta);
		}

		return result;
	}

}
