package com.simonepugliese.walletgui;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private ScreenNavigator navigator;

    @Override
    public void start(Stage stage) throws IOException {
        navigator = new ScreenNavigator(stage);
        navigator.showLogin();
        stage.show();
    }

    @Override
    public void stop() {
        if (navigator != null) {
            navigator.closeManager();
        }
    }
}