package com.simonepugliese.walletgui.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.simonepugliese.walletgui.ScreenNavigator; // Il nostro 'direttore d'orchestra'
import javafx.fxml.FXML;

public class LoginController {

    // --- Collegamenti FXML ---
    // Questi nomi DEVONO corrispondere agli 'fx:id' nell'FXML

    @FXML
    private JFXPasswordField passwordField;

    @FXML
    private JFXButton loginButton;

    // --- Logica interna ---

    private ScreenNavigator navigator;

    /**
     * Metodo di inizializzazione standard di JavaFX.
     * Chiamato automaticamente dopo il caricamento dell'FXML.
     */
    @FXML
    public void initialize() {
        // Possiamo pre-impostare il focus sul campo password
        passwordField.requestFocus();
    }

    /**
     * Questo metodo viene 'iniettato' dallo ScreenNavigator
     * per dare al controller un modo di comunicare con l'esterno.
     */
    public void setNavigator(ScreenNavigator navigator) {
        this.navigator = navigator;
    }

    /**
     * Questo metodo è collegato all'evento 'onAction'
     * del bottone e del campo password nell'FXML.
     */
    @FXML
    private void handleLogin() {
        String password = passwordField.getText();

        // Semplice validazione (poi miglioreremo con ValidatorFX)
        if (password == null || password.isBlank()) {
            // Qui potresti mostrare un errore nel campo
            System.out.println("Password vuota");
            return;
        }

        if (navigator != null) {
            // Deleghiamo il TENTATIVO di login al navigator.
            // Questo controller non sa (e non deve sapere)
            // cosa succede dopo.
            navigator.attemptLogin(password); //prova a fare il login
        } else {
            System.err.println("Errore: ScreenNavigator non impostato!");
        }
    }
}