package com.simonepugliese.walletgui.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.simonepugliese.Core.WalletManager;
import com.simonepugliese.Model.Entry;
import com.simonepugliese.Model.Field;
import com.simonepugliese.Model.FieldType;
import com.simonepugliese.Security.DecryptionFailedException;
import com.simonepugliese.walletgui.ScreenNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class EntryDetailController {

    @FXML
    private Label titleLabel;
    @FXML
    private VBox fieldsContainer;

    private ScreenNavigator navigator;
    private WalletManager manager;
    private Entry currentEntry;

    public void setNavigator(ScreenNavigator navigator) {
        this.navigator = navigator;
    }

    public void setManager(WalletManager manager) {
        this.manager = manager;
    }

    /**
     * Carica l'entry dal DB, la decifra e popola la UI.
     */
    public void loadEntryData(String entryId) {
        if (manager == null) return;

        try {
            // 1. Carica e Decifra
            Optional<Entry> entryOpt = manager.loadAndDecryptEntry(entryId);

            if (entryOpt.isEmpty()) {
                mostraErrore("Non trovata", "L'entry richiesta non esiste più.");
                handleBack();
                return;
            }

            this.currentEntry = entryOpt.get();

            // 2. Imposta Intestazione
            titleLabel.setText(currentEntry.getDescription());

            // 3. Genera Campi Dinamici
            renderFields();

        } catch (DecryptionFailedException e) {
            mostraErrore("Errore Decrittazione", "Impossibile decifrare i dati. Database corrotto o errore interno.");
        } catch (Exception e) {
            mostraErrore("Errore", "Si è verificato un errore imprevisto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void renderFields() {
        fieldsContainer.getChildren().clear();

        // Mostra la categoria come primo elemento informativo
        Label catLabel = new Label("Categoria: " + currentEntry.getCategory());
        catLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");
        fieldsContainer.getChildren().add(catLabel);

        // Itera sulla mappa dei campi
        currentEntry.getFields().forEach((fieldName, field) -> {
            VBox fieldBox = createFieldWidget(fieldName, field);
            fieldsContainer.getChildren().add(fieldBox);
        });
    }

    private VBox createFieldWidget(String fieldName, Field field) {
        VBox container = new VBox(5);

        // Etichetta del campo (es. "Username")
        Label lblName = new Label(fieldName);
        lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        HBox valueContainer = new HBox(10);

        // Controllo per il valore (differenziato per tipo)
        if (field.getType() == FieldType.PASSWORD) {
            JFXPasswordField pwdField = new JFXPasswordField();
            pwdField.setText(field.getValue());
            pwdField.setEditable(false); // Solo lettura
            pwdField.setPrefWidth(300);
            valueContainer.getChildren().add(pwdField);
        } else {
            JFXTextField txtField = new JFXTextField();
            txtField.setText(field.getValue());
            txtField.setEditable(false);
            txtField.setPrefWidth(300);
            valueContainer.getChildren().add(txtField);
        }

        // Bottone "Copia"
        JFXButton copyBtn = new JFXButton("Copia");
        copyBtn.setStyle("-fx-background-color: #E0E0E0;");
        copyBtn.setOnAction(e -> copyToClipboard(field.getValue()));

        valueContainer.getChildren().add(copyBtn);

        container.getChildren().addAll(lblName, valueContainer);
        return container;
    }

    private void copyToClipboard(String text) {
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        Clipboard.getSystemClipboard().setContent(content);
        // Opzionale: mostrare un piccolo tooltip o notifica "Copiato!"
    }

    @FXML
    private void handleBack() {
        if (navigator != null) {
            navigator.goBackToHome();
        }
    }

    private void mostraErrore(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}