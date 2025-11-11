// File: simopugliese/walletgui/WalletGUI/src/main/java/com/simonepugliese/walletgui/HelloApplication.java
package com.simonepugliese.walletgui;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe di avvio principale dell'applicazione (Entry Point).
 * Modificata per lanciare lo ScreenNavigator invece di hello-view.
 */
public class HelloApplication extends Application {

    private ScreenNavigator navigator;

    @Override
    public void start(Stage stage) throws IOException {
        // 1. Crea il navigator (il "regista")
        navigator = new ScreenNavigator(stage);

        // 2. Mostra la prima schermata (login)
        navigator.showLogin();

        // 3. Mostra lo stage
        stage.show();
    }

    /**
     * Metodo chiamato da JavaFX quando l'app si chiude.
     * Ci assicuriamo di chiudere il manager per azzerare la password.
     */
    @Override
    public void stop() {
        if (navigator != null) {
            navigator.closeManager();
        }
    }
}