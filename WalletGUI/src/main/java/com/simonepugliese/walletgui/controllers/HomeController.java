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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;
import java.util.stream.Collectors;

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
        // Gestione menu laterale
        hamburgerButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            drawersStack.toggle(categoryDrawer);
        });

        // Popolamento categorie (Stringhe UI)
        categoryListView.getItems().addAll(
                "Tutte le Entry",
                "Login",          // Mappa a Category.LOGIN
                "Carte",          // Mappa a Category.CREDIT_CARD
                "Wi-Fi",          // Mappa a Category.WIFI
                "Note Sicure",    // Mappa a Category.SECURE_NOTE
                "App",            // Mappa a Category.APP
                "Documenti"       // Mappa a Category.IDENTITY
        );

        // Listener cambio categoria
        categoryListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> handleCategoryChange(newValue)
        );

        // Selezione di default
        categoryListView.getSelectionModel().selectFirst();
    }

    public void setNavigator(ScreenNavigator navigator) {
        this.navigator = navigator;
    }

    public void setManager(WalletManager manager) {
        this.manager = manager;
        // Ricarica la vista iniziale se il manager arriva dopo l'init
        String currentCat = categoryListView.getSelectionModel().getSelectedItem();
        if(currentCat != null) {
            loadEntriesForCategory(currentCat);
        }
    }

    private void handleCategoryChange(String categoryUiName) {
        if (categoryUiName == null) return;

        titleLabel.setText(categoryUiName);
        loadEntriesForCategory(categoryUiName);

        // Chiudi il drawer dopo la selezione
        if (categoryDrawer.isOpened()) {
            drawersStack.toggle(categoryDrawer);
        }
    }

    private void loadEntriesForCategory(String categoryUiName) {
        if (manager == null) return;

        contentPane.getChildren().clear();

        // 1. Converti nome UI -> Enum Category (o null per "Tutte")
        Category targetCategory = mapUiStringToCategory(categoryUiName);
        boolean showAll = categoryUiName.equals("Tutte le Entry");

        // 2. Carica i sommari dal backend
        List<Entry> allSummaries = manager.loadAllEntrySummaries();

        // 3. Filtra la lista
        List<Entry> filteredEntries = allSummaries.stream()
                .filter(e -> showAll || e.getCategory() == targetCategory)
                .collect(Collectors.toList());

        // 4. Crea la ListView per le Entry
        JFXListView<Entry> entriesListView = new JFXListView<>();
        entriesListView.getItems().addAll(filteredEntries);

        // 5. Custom Cell Factory per mostrare Titolo e Categoria
        entriesListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Entry item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Layout semplice: Titolo in grassetto, Categoria piccola sotto
                    VBox container = new VBox(3);
                    Label lblTitle = new Label(item.getDescription());
                    lblTitle.setFont(Font.font("System", javafx.scene.text.FontWeight.BOLD, 14));

                    Label lblCat = new Label(item.getCategory().name()); // Si potrebbe mappare in IT
                    lblCat.setTextFill(Color.GRAY);
                    lblCat.setFont(Font.font(10));

                    container.getChildren().addAll(lblTitle, lblCat);
                    setGraphic(container);
                }
            }
        });

        // 6. Gestione click: vai al dettaglio
        entriesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && navigator != null) {
                navigator.showEntryDetail(newVal.getId());
            }
        });

        // Ancoraggio per riempire lo spazio
        AnchorPane.setTopAnchor(entriesListView, 0.0);
        AnchorPane.setBottomAnchor(entriesListView, 0.0);
        AnchorPane.setLeftAnchor(entriesListView, 0.0);
        AnchorPane.setRightAnchor(entriesListView, 0.0);

        contentPane.getChildren().add(entriesListView);
    }

    /**
     * Mappa le stringhe della UI agli Enum del backend.
     */
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