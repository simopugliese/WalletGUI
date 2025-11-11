// File: simopugliese/walletgui/WalletGUI/src/main/java/com/simonepugliese/walletgui/ScreenNavigator.java
package com.simonepugliese.walletgui;

import com.simonepugliese.Core.WalletManager;
import com.simonepugliese.Security.DecryptionFailedException;
import com.simonepugliese.WalletFactory;
import com.simonepugliese.walletgui.controllers.*; // Assicura che HelloController sia importato
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Gestisce la navigazione tra le diverse schermate (Scene/FXML)
 * all'interno della finestra principale (Stage).
 *
 * FILE CORRETTO: Path FXML aggiornati.
 */
public class ScreenNavigator {

    private final Stage stage; // La finestra principale (Stage)
    private WalletManager manager; // Il backend, creato *solo* dopo un login valido

    /**
     * Costruttore. Richiede lo Stage principale su cui operare.
     * @param stage La finestra principale dell'applicazione.
     */
    public ScreenNavigator(Stage stage) {
        this.stage = stage;
    }

    /**
     * Mostra la schermata di login iniziale.
     */
    public void showLogin() {
        try {
            // 1. Carica l'FXML del login
            // *** CORREZIONE: Path aggiornato da /gui/views/ a /walletgui/ ***
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simonepugliese/walletgui/login.fxml"));
            Parent root = loader.load();

            // 2. "Inietta" questo navigator nel controller del login
            // Questo permette a LoginController di chiamare attemptLogin()
            LoginController controller = loader.getController();
            controller.setNavigator(this);

            // 3. Imposta la scena sul login
            stage.setScene(new Scene(root, 400, 300)); // Dimensioni adatte al login
            stage.setTitle("Wallet - Login");

        } catch (IOException e) {
            System.err.println("Errore fatale: impossibile caricare login.fxml");
            e.printStackTrace();
        }
    }

    /**
     * Metodo chiamato da LoginController.
     * Tenta di autenticare l'utente creando il WalletManager.
     * @param password La password inserita dall'utente.
     */
    public void attemptLogin(String password) {
        try {
            // 1. Tenta di creare il WalletManager usando la tua Factory.
            this.manager = WalletFactory.createWalletManager(password);

            // Prova di caricamento: se la password è errata, questo lancerà
            // DecryptionFailedException
            manager.loadAllEntrySummaries(); // Tentativo di lettura

            // 2. Se ha successo (nessuna eccezione), naviga alla home
            showHome();

        } catch (DecryptionFailedException e) {
            // 3. Password errata!
            mostraErrore("Password Errata", "La password inserita non è corretta o il database è corrotto.");

        } catch (Exception e) {
            // 4. Altro errore (es. DB non trovato, file .db corrotto)
            mostraErrore("Errore di Avvio", "Impossibile inizializzare il wallet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Mostra la schermata Home (dopo un login corretto).
     */
    private void showHome() {
        try {
            // *** CORREZIONE: Path aggiornato a 'hello-view.fxml' (l'unico FXML "home" disponibile) ***
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simonepugliese/walletgui/hello-view.fxml"));
            Parent root = loader.load();

            // *** CORREZIONE: Modificato da HomeController a HelloController ***
            // Nota: HelloController al momento non fa nulla. Per la vera
            // implementazione, dovrai creare un home.fxml e un HomeController
            // che accettino il Navigator e il Manager.
            HelloController controller = loader.getController();

            // Le righe seguenti non funzionano perché HelloController
            // non ha i metodi setNavigator() e setManager().
            // controller.setNavigator(this);
            // controller.setManager(this.manager); // Passa il backend!

            stage.setScene(new Scene(root, 800, 600)); // Dimensioni per la home
            stage.setTitle("Wallet - Home");
        } catch (IOException e) {
            System.err.println("Errore fatale: impossibile caricare home.fxml (hello-view.fxml)");
            e.printStackTrace();
        }
    }

    /* * Qui aggiungerai altri metodi per la navigazione:
     *
     * public void showEntryDetail(Entry entry) { ... }
     * public void showAddEntry() { ... }
     * public void showEditEntry(Entry entry) { ... }
     * public void goBackToHome() { showHome(); }
     */

    /**
     * Chiamato da WalletApplication quando l'app si chiude.
     * Si assicura di azzerare la password in memoria.
     */
    public void closeManager() {
        if (manager != null) {
            manager.close();
            System.out.println("WalletManager chiuso e password azzerata.");
        }
    }

    /**
     * Helper per mostrare un popup di errore.
     */
    private void mostraErrore(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(titolo);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}