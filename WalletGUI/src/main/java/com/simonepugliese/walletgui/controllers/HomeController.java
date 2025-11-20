package com.simonepugliese.walletgui.controllers;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXListView;
import com.simonepugliese.Core.WalletManager;
import com.simonepugliese.Model.Category;
import com.simonepugliese.Model.Entry;
import com.simonepugliese.walletgui.ScreenNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;
import java.util.stream.Collectors;

public class HomeController {

    @FXML private JFXDrawersStack drawersStack;
    @FXML private JFXDrawer categoryDrawer;
    @FXML private JFXHamburger hamburgerButton;
    @FXML private Label titleLabel;
    @FXML private JFXListView<String> categoryListView;
    @FXML private AnchorPane contentPane;

    private ScreenNavigator navigator;
    private WalletManager manager;

    @FXML
    public void initialize() {
        hamburgerButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> drawersStack.toggle(categoryDrawer));

        categoryListView.getItems().addAll(
                "Tutte le Entry", "Login", "Carte", "Wi-Fi", "Note Sicure", "App", "Documenti"
        );

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
        String currentCat = categoryListView.getSelectionModel().getSelectedItem();
        if(currentCat != null) loadEntriesForCategory(currentCat);
    }

    @FXML
    private void handleNewEntry() {
        if (navigator != null) {
            // Passa null per indicare "Nuova creazione"
            navigator.showEntryEditor(null);
        }
    }

    private void handleCategoryChange(String categoryUiName) {
        if (categoryUiName == null) return;
        titleLabel.setText(categoryUiName);
        loadEntriesForCategory(categoryUiName);
        if (categoryDrawer.isOpened()) drawersStack.toggle(categoryDrawer);
    }

    private void loadEntriesForCategory(String categoryUiName) {
        if (manager == null) return;
        contentPane.getChildren().clear();

        Category targetCategory = mapUiStringToCategory(categoryUiName);
        boolean showAll = categoryUiName.equals("Tutte le Entry");

        List<Entry> allSummaries = manager.loadAllEntrySummaries();

        List<Entry> filteredEntries = allSummaries.stream()
                .filter(e -> showAll || e.getCategory() == targetCategory)
                .toList();

        JFXListView<Entry> entriesListView = new JFXListView<>();
        entriesListView.getItems().addAll(filteredEntries);

        entriesListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Entry item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox container = new VBox(3);
                    Label lblTitle = new Label(item.getDescription());
                    lblTitle.setFont(Font.font("System", javafx.scene.text.FontWeight.BOLD, 14));
                    Label lblCat = new Label(item.getCategory().name());
                    lblCat.setTextFill(Color.GRAY);
                    lblCat.setFont(Font.font(10));
                    container.getChildren().addAll(lblTitle, lblCat);
                    setGraphic(container);
                }
            }
        });

        entriesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && navigator != null) {
                navigator.showEntryDetail(newVal.getId());
            }
        });

        AnchorPane.setTopAnchor(entriesListView, 0.0);
        AnchorPane.setBottomAnchor(entriesListView, 0.0);
        AnchorPane.setLeftAnchor(entriesListView, 0.0);
        AnchorPane.setRightAnchor(entriesListView, 0.0);
        contentPane.getChildren().add(entriesListView);
    }

    private Category mapUiStringToCategory(String uiName) {
        return switch (uiName) {
            case "Login" -> Category.LOGIN;
            case "Carte" -> Category.CREDIT_CARD;
            case "Wi-Fi" -> Category.WIFI;
            case "Note Sicure" -> Category.SECURE_NOTE;
            case "App" -> Category.APP;
            case "Documenti" -> Category.IDENTITY;
            default -> Category.GENERIC;
        };
    }
}