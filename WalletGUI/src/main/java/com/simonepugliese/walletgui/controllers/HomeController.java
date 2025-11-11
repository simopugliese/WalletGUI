package com.simonepugliese.walletgui.controllers;

import com.jfoenix.controls.JFXListView;
import com.simonepugliese.Core.WalletManager;
import com.simonepugliese.walletgui.ScreenNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class HomeController {

    @FXML
    private JFXListView<String> categoryListView;

    @FXML
    private AnchorPane contentPane;

    private ScreenNavigator navigator;
    private WalletManager manager;

    @FXML
    public void initialize() {
        categoryListView.getItems().addAll("Tutte le Entry", "Password", "Note Sicure", "Carte");
        categoryListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> handleCategoryChange(newValue)
        );

        categoryListView.getSelectionModel().selectFirst();
    }

    public void setNavigator(ScreenNavigator navigator) {
        this.navigator = navigator;
    }

    public void setManager(WalletManager manager) {
        this.manager = manager;
        loadEntriesForCategory(categoryListView.getSelectionModel().getSelectedItem());
    }

    private void handleCategoryChange(String category) {
        if (category == null) return;
        System.out.println("Categoria selezionata: " + category);
        loadEntriesForCategory(category);
    }

    private void loadEntriesForCategory(String category) {
        if (manager == null) {
            return; //TODO: Manager non ancora pronto
        }

        contentPane.getChildren().clear();

        // ESEMPIO: In futuro, qui caricherai una TableView con le entry filtrate
        // Per ora, mostriamo solo una label
        Label contentLabel = new Label("Contenuto per: " + category);
        AnchorPane.setTopAnchor(contentLabel, 10.0);
        AnchorPane.setLeftAnchor(contentLabel, 10.0);
        contentPane.getChildren().add(contentLabel);

        // Logica futura:
        // List<EntrySummary> entries = manager.loadAllEntrySummaries();
        // ... filtra 'entries' in base a 'category' ...
        // ... popola la TableView ...
    }
}