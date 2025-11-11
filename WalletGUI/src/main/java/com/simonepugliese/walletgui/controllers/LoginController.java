package com.simonepugliese.walletgui.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.simonepugliese.walletgui.ScreenNavigator;
import javafx.fxml.FXML;

public class LoginController {

    @FXML
    private JFXPasswordField passwordField;

    @FXML
    private JFXButton loginButton;

    private ScreenNavigator navigator;

    @FXML
    public void initialize() {
        // Possiamo pre-impostare il focus sul campo password
        passwordField.requestFocus();
    }

    public void setNavigator(ScreenNavigator navigator) {
        this.navigator = navigator;
    }

    @FXML
    private void handleLogin() {
        String password = passwordField.getText();

        // Semplice validazione (poi miglioreremo con ValidatorFX)
        if (password == null || password.isBlank()) {
            System.out.println("Password vuota");
            return;
        }

        if (navigator != null) {
            navigator.attemptLogin(password);
        } else {
            System.err.println("Errore: ScreenNavigator non impostato!");
        }
    }
}