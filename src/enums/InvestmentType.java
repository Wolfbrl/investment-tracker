package enums;

public enum InvestmentType {

	STOCK("Stock"), Crypto("Cryptocurrency"), BOND("Bond"), ETF("Exchange-Traded Fund");

	private final String displayName;

	InvestmentType(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

}
