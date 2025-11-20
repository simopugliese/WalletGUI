package com.simonepugliese.walletgui;

import com.simonepugliese.Core.WalletManager;
import com.simonepugliese.Security.DecryptionFailedException;
import com.simonepugliese.WalletFactory;
import com.simonepugliese.walletgui.controllers.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class ScreenNavigator {

    private final Stage stage;
    private WalletManager manager;

    public ScreenNavigator(Stage stage) {
        this.stage = stage;
    }

    public void showLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simonepugliese/walletgui/login.fxml"));
            Parent root = loader.load();

            LoginController controller = loader.getController();
            controller.setNavigator(this);

            stage.setScene(new Scene(root));
        } catch (IOException e) {
            mostraErrore("errore", "impossibile caricare login.fxml");
            System.err.println("Errore fatale: impossibile caricare login.fxml");
            e.printStackTrace();
        }
    }

    public void attemptLogin(String password) {
        try {
            this.manager = WalletFactory.createWalletManager(password);
            manager.loadAllEntrySummaries();
            showHome();
        } catch (DecryptionFailedException e) {
            mostraErrore("Password Errata", "La password inserita non è corretta o il database è corrotto.");
        } catch (Exception e) {
            mostraErrore("Errore di Avvio", "Impossibile inizializzare il wallet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simonepugliese/walletgui/home.fxml"));
            Parent root = loader.load();

            HomeController controller = loader.getController();

            controller.setNavigator(this);
            controller.setManager(this.manager);

            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("Wallet - Home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mostra il dettaglio di una specifica entry.
     * @param entryId L'ID univoco dell'entry da visualizzare.
     */
    public void showEntryDetail(String entryId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simonepugliese/walletgui/entry_detail.fxml"));
            Parent root = loader.load();

            EntryDetailController controller = loader.getController();
            controller.setNavigator(this);
            controller.setManager(this.manager);

            // Passiamo l'ID: il controller caricherà i dati decifrati
            controller.loadEntryData(entryId);

            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("Wallet - Dettaglio");
        } catch (IOException e) {
            e.printStackTrace();
            mostraErrore("Errore Navigazione", "Impossibile caricare il dettaglio entry.");
        }
    }

    /**
     * Torna alla Home (usato dal pulsante "Indietro" nel dettaglio).
     */
    public void goBackToHome() {
        showHome();
    }

    public void closeManager() {
        if (manager != null) {
            manager.close();
            System.out.println("WalletManager chiuso e password azzerata.");
        }
    }

    private void mostraErrore(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(titolo);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}