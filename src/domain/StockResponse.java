package domain;

import java.util.List;

public class StockResponse {

	private List<String> Symbol;
	private List<Double> Mid;
	private List<String> Date;

	public List<String> getSymbol() {
		return Symbol;
	}

	public void setSymbol(List<String> symbol) {
		this.Symbol = symbol;
	}

	public List<Double> getMid() {
		return Mid;
	}

	public void setMid(List<Double> mid) {
		this.Mid = mid;
	}

	public List<String> getDate() {
		return Date;
	}

	public void setDate(List<String> date) {
		this.Date = date;
	}

}
