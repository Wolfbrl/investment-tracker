module investmenttracker {
	requires java.sql;
	requires transitive javafx.graphics;

	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.base;
	requires com.google.gson;
	requires java.desktop;
	requires javafx.swing;

	opens domain to com.google.gson;

	exports main;
	exports domain;
	exports enums;
	exports gui;
}