module investmenttracker {
	requires java.sql;
	requires transitive javafx.graphics;

	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.base;

	exports main;
	exports domain;
	exports enums;
	exports gui;
}