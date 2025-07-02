package domain;

import java.math.BigDecimal;
import java.util.*;

public class User implements Comparable<User> {

	private List<Investment> investments;
	private String username;
//	private String email;
	private String password;
	private String salt;

	public User(String username, String password, String salt) {
		investments = new ArrayList<>();
		setUsername(username);
		setPassword(password);
		this.salt = salt;

	}

	public List<Investment> getInvestments() {
		return investments;
	}

	public int getNumberOfInvestments() {
		return investments.size();
	}

	public void setInvestments(List<Investment> investments) {
		this.investments = investments;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		if (username == null || username.isBlank()) {
			throw new IllegalArgumentException("Please fill in your username");
		}
		if (username.length() <= 4) {
			throw new IllegalArgumentException("Your username must contain at least 5 characters");
		}
		this.username = username;

	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {

		this.password = password;
	}

	@Override
	public String toString() {
		return String.format("User %s with password %s and salt %s has made %d investments", this.username,
				this.password, this.salt, this.investments.size());
	}

	public void addInvestment(Investment investment) {
		investments.add(investment);
	}

	public void removeInvestment(Investment investment) {
		investments.remove(investment);
	}

	public BigDecimal getTotalProfitOrLoss() {
		return investments.stream().map(x -> x.getProfitOrLoss()).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	@Override
	public int hashCode() {
		return Objects.hash(username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(username, other.username);
	}

	@Override
	public int compareTo(User o) {
		return username.compareTo(o.username);
	}

	public String getSalt() {
		return this.salt;
	}

}
