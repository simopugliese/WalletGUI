package com.simonepugliese.walletgui.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.simonepugliese.walletgui.ScreenNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;

public class LoginController {

    private static final String DATABASE_PATH = "wallet.db";

    @FXML
    private VBox rootPane;

    @FXML
    private JFXPasswordField passwordField;

    @FXML
    private JFXButton loginButton;

    @FXML
    private Label titleLabel;

    @FXML
    private Label verifyPasswordLabel;

    @FXML
    private JFXPasswordField verifyPasswordField;

    private ScreenNavigator navigator;
    private boolean isFirstTimeSetup = false;

    @FXML
    public void initialize() {
        File dbFile = new File(DATABASE_PATH);
        boolean walletExists = dbFile.exists() && !dbFile.isDirectory();

        setupForFirstTime(!walletExists);

        passwordField.requestFocus();

        rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Stage stage = (Stage) newScene.getWindow();
                if (stage != null) {
                    stage.setTitle(isFirstTimeSetup ? "Wallet - Creazione" : "Wallet - Login");
                }
            }
        });
    }

    private void setupForFirstTime(boolean firstTime) {
        this.isFirstTimeSetup = firstTime;

        verifyPasswordLabel.setVisible(firstTime);
        verifyPasswordLabel.setManaged(firstTime);
        verifyPasswordField.setVisible(firstTime);
        verifyPasswordField.setManaged(firstTime);

        if (firstTime) {
            titleLabel.setText("Crea Nuovo Wallet");
            loginButton.setText("Crea");
        } else {
            titleLabel.setText("Wallet Login");
            loginButton.setText("Accedi");
        }
    }

    public void setNavigator(ScreenNavigator navigator) {
        this.navigator = navigator;
    }

    @FXML
    private void handleLogin() {
        String password = passwordField.getText();

        if (isFirstTimeSetup) {
            String verifyPassword = verifyPasswordField.getText();

            if (password == null || password.isBlank()) {
                System.out.println("La password non può essere vuota");
                // TODO: Mostrare errore all'utente (es. con un Alert o Label)
                return;
            }
            if (!password.equals(verifyPassword)) {
                System.out.println("Le password non coincidono");
                // TODO: Mostrare errore all'utente
                return;
            }
            // Se arrivi qui, le password coincidono e non sono vuote

        } else {
            if (password == null || password.isBlank()) {
                System.out.println("Password vuota");
                // TODO: Mostrare errore
                return;
            }
        }

        if (navigator != null) {
            navigator.attemptLogin(password);
        } else {
            System.err.println("Errore: ScreenNavigator non impostato!");
        }
    }
}