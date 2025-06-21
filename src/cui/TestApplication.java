package cui;

import java.math.BigDecimal;
import java.time.LocalDate;

import domain.*;
import enums.*;

public class TestApplication {

	private InvestmentHandler investmenthandler;

	public TestApplication(InvestmentHandler investmenthandler) {
		this.investmenthandler = investmenthandler;
	}

	public void runTest() {
		System.out.println("==== Investment Tracker Test ====");

		// 1. Registering a user
		User user = new User("wolfie", "securepassword123");
		System.out.println("Registered user: " + user);

		// 2. Making investments
		investmenthandler.addInvestment("Apple Stock", LocalDate.of(2024, 6, 1), new BigDecimal("500"), Currencies.USD,
				InvestmentType.STOCK, user);
		investmenthandler.addInvestment("Bitcoin", LocalDate.of(2024, 6, 10), new BigDecimal("1000"), Currencies.USD,
				InvestmentType.Crypto, user);

		// Print user's investments
		System.out.println("\nUser's Investments After Creation:");
		for (Investment inv : user.getInvestments()) {
			System.out.println(inv);
		}

		// 3. Editing an investment (add note and update value)
		Investment bitcoin = user.getInvestments().get(1);
		investmenthandler.addNote(bitcoin.getId(), "Holding for long term.");
		bitcoin.updateCurrentValueUsingPercentage(new BigDecimal("-10")); // +10%
		System.out.println("\nUpdated Bitcoin investment:");
		System.out.println(bitcoin);
		System.out.println("Note: " + bitcoin.getNote());

		// 4. Remove an investment (manually from user list)
		Investment toRemove = user.getInvestments().get(0);
		user.getInvestments().remove(toRemove);
		System.out.println("\nRemoved one investment. Remaining investments:");
		for (Investment inv : user.getInvestments()) {
			System.out.println(inv);
		}

		// 5. Summary of all investments in the system
		System.out.println("\nAll Investments (System-wide):");
		for (Investment inv : investmenthandler.getInvestments()) {
			System.out.println(inv);
		}

		// 6. Profit/Loss
		System.out.println("\nProfit or Loss per Investment:");
		for (Investment inv : user.getInvestments()) {
			BigDecimal result = inv.getProfitOrLoss();
			System.out.println(inv.getName() + ": " + result + " " + inv.getCurrency().getSymbol());
		}

	}

}
