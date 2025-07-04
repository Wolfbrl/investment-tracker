package domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ValuePoint {

	private LocalDate date;
	private BigDecimal value;

	public ValuePoint(LocalDate date, BigDecimal value) {

		this.date = date;
		this.value = value;

	}

	public LocalDate getDate() {
		return this.date;
	}

	public BigDecimal getValue() {
		return this.value;
	}

}
