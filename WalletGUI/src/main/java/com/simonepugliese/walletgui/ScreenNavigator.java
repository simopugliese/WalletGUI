package com.simonepugliese.walletgui;

import com.simonepugliese.Core.WalletManager;
import com.simonepugliese.Model.Entry;
import com.simonepugliese.Security.DecryptionFailedException;
import com.simonepugliese.WalletFactory;
import com.simonepugliese.walletgui.controllers.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

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

            stage.setScene(createScene(root));
        } catch (IOException e) {
            mostraErrore("Errore Fatale", "Impossibile caricare login.fxml");
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

            stage.setScene(createScene(root, 800, 600));
            stage.setTitle("Wallet - Home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showEntryDetail(String entryId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simonepugliese/walletgui/entry_detail.fxml"));
            Parent root = loader.load();

            EntryDetailController controller = loader.getController();
            controller.setNavigator(this);
            controller.setManager(this.manager);
            controller.loadEntryData(entryId);

            stage.setScene(createScene(root, 800, 600));
            stage.setTitle("Wallet - Dettaglio");
        } catch (IOException e) {
            e.printStackTrace();
            mostraErrore("Errore Navigazione", "Impossibile caricare il dettaglio entry.");
        }
    }

    public void showEntryEditor(Entry entryToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simonepugliese/walletgui/entry_editor.fxml"));
            Parent root = loader.load();

            EntryEditorController controller = loader.getController();
            controller.setNavigator(this);
            controller.setManager(this.manager);
            controller.initEditor(entryToEdit);

            stage.setScene(createScene(root, 800, 600));
            stage.setTitle(entryToEdit == null ? "Nuova Entry" : "Modifica Entry");
        } catch (IOException e) {
            e.printStackTrace();
            mostraErrore("Errore Navigazione", "Impossibile caricare l'editor.");
        }
    }

    public void goBackToHome() {
        showHome();
    }

    public void closeManager() {
        if (manager != null) {
            manager.close();
            System.out.println("WalletManager chiuso.");
        }
    }

    // --- Metodi Helper per il CSS e la Scena ---

    private Scene createScene(Parent root) {
        Scene scene = new Scene(root);
        applyStyle(scene);
        return scene;
    }

    private Scene createScene(Parent root, double width, double height) {
        Scene scene = new Scene(root, width, height);
        applyStyle(scene);
        return scene;
    }

    private void applyStyle(Scene scene) {
        // Usa percorso relativo: cerca "style.css" nello stesso package di ScreenNavigator
        URL cssUrl = getClass().getResource("style.css");

        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
            System.out.println("✅ CSS caricato: " + cssUrl.toExternalForm());
        } else {
            System.err.println("❌ ERRORE: style.css non trovato! Verifica che sia in src/main/resources/com/simonepugliese/walletgui/");
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