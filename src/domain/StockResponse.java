package domain;

import java.math.BigDecimal;
import java.util.List;

public class StockResponse {

	private List<String> Symbol;
	private List<BigDecimal> Mid;
	private List<String> Date;

	public List<String> getSymbol() {
		return Symbol;
	}

	public void setSymbol(List<String> symbol) {
		this.Symbol = symbol;
	}

	public List<BigDecimal> getMid() {
		return Mid;
	}

	public void setMid(List<BigDecimal> mid) {
		this.Mid = mid;
	}

	public List<String> getDate() {
		return Date;
	}

	public void setDate(List<String> date) {
		this.Date = date;
	}

}
