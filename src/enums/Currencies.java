package enums;

public enum Currencies {

	EUR("Euro", "€"), USD("US Dollar", "$"), GBP("British Pound", "£"), JPY("Japanese Yen", "¥");

	private final String fullName;
	private final String symbol;

	Currencies(String fullname, String symbol) {
		this.fullName = fullname;
		this.symbol = symbol;
	}

	public String getFullName() {
		return this.fullName;
	}

	public String getSymbol() {
		return this.symbol;
	}

}
