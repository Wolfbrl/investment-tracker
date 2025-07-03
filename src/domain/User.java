package domain;

import java.util.Objects;

public class User implements Comparable<User> {

	private String username;
//	private String email;
	private String password;
	private String salt;

	public User(String username, String password, String salt) {

		setUsername(username);
		setPassword(password);
		this.salt = salt;

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
		return String.format("User %s with password %s and salt %s", this.username, this.password, this.salt);
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
