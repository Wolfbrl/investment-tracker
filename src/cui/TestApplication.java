package cui;

import java.util.List;

import domain.*;

public class TestApplication {

	private InvestmentHandler handler;

	public TestApplication(InvestmentHandler investmenthandler) {
		this.handler = investmenthandler;
	}

	public void runTest() {

		// USERTEST

//		handler.createUser("Wolfbrl", "goegoe");

		List<User> users = handler.giveAllUsers();

		System.out.println(users.get(0).toString());

//		System.out.printf("the password goegoe is %s",
//				PasswordUtils.verifyPassword("goegoe", users.get(0).getPassword(), users.get(0).getSalt()));

		// INVESTMENT TEST
//
//		handler.addInvestment("id12345", "BTC", LocalDate.now(), BigDecimal.valueOf(15), BigDecimal.valueOf(20),
//				Currencies.EUR, InvestmentType.Crypto, users.get(0), "test");

		User user = handler.giveAllInvestments().get(0).getUser();

	}

}
