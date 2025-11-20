package com.simonepugliese.walletgui.controllers;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXListView;
import com.simonepugliese.Core.WalletManager;
import com.simonepugliese.walletgui.ScreenNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class HomeController {

    @FXML
    private JFXDrawersStack drawersStack;
    @FXML
    private JFXDrawer categoryDrawer;
    @FXML
    private JFXHamburger hamburgerButton;
    @FXML
    private Label titleLabel;
    @FXML
    private JFXListView<String> categoryListView;
    @FXML
    private AnchorPane contentPane;

    private ScreenNavigator navigator;
    private WalletManager manager;

    @FXML
    public void initialize() {
        hamburgerButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            categoryDrawer.open();

            //drawersStack.toggle(categoryDrawer);
        });

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

        titleLabel.setText(category);
        loadEntriesForCategory(category);
        if (categoryDrawer.isOpened()) {
            drawersStack.toggle(categoryDrawer, false);
        }
    }

    private void loadEntriesForCategory(String category) {
        if (manager == null) {
            return;
        }

        contentPane.getChildren().clear();

        Label contentLabel = new Label("Contenuto per: " + category);
        AnchorPane.setTopAnchor(contentLabel, 10.0);
        AnchorPane.setLeftAnchor(contentLabel, 10.0);
        contentPane.getChildren().add(contentLabel);

        // Logica futura:
        // List<EntrySummary> entries = manager.loadAllEntrySummaries();
        // ... filtra 'entries' in base a 'category' ...
        // ... carica entry_list.fxml ...
    }
}