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
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class EntryDetailController {

    @FXML private Label titleLabel;
    @FXML private VBox fieldsContainer;

    private ScreenNavigator navigator;
    private WalletManager manager;
    private Entry currentEntry;

    public void setNavigator(ScreenNavigator navigator) { this.navigator = navigator; }
    public void setManager(WalletManager manager) { this.manager = manager; }

    public void loadEntryData(String entryId) {
        if (manager == null) return;
        try {
            Optional<Entry> entryOpt = manager.loadAndDecryptEntry(entryId);
            if (entryOpt.isEmpty()) {
                mostraErrore("Non trovata", "Entry non esistente.");
                handleBack();
                return;
            }
            this.currentEntry = entryOpt.get();
            titleLabel.setText(currentEntry.getDescription());
            renderFields();
        } catch (DecryptionFailedException e) {
            mostraErrore("Errore", "Password errata o DB corrotto.");
        } catch (Exception e) {
            mostraErrore("Errore", e.getMessage());
            e.printStackTrace();
        }
    }

    private void renderFields() {
        fieldsContainer.getChildren().clear();

        Label catLabel = new Label(currentEntry.getCategory().name());
        // Usiamo una classe CSS per l'etichetta piccola
        catLabel.getStyleClass().add("label-small");
        // Aggiungiamo margine sotto
        VBox.setMargin(catLabel, new javafx.geometry.Insets(0, 0, 10, 0));
        fieldsContainer.getChildren().add(catLabel);

        currentEntry.getFields().forEach((fieldName, field) -> {
            VBox fieldBox = createFieldWidget(fieldName, field);
            // Aggiungiamo la classe card per ogni campo per renderlo carino
            fieldBox.getStyleClass().add("field-row");
            fieldsContainer.getChildren().add(fieldBox);
        });
    }

    private VBox createFieldWidget(String fieldName, Field field) {
        VBox container = new VBox(5);

        Label lblName = new Label(fieldName);
        lblName.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;"); // O classe CSS dedicata

        HBox valueContainer = new HBox(10);
        valueContainer.setAlignment(Pos.CENTER_LEFT);

        if (field.getType() == FieldType.PASSWORD) {
            JFXPasswordField pwdField = new JFXPasswordField();
            pwdField.setText(field.getValue());
            pwdField.setEditable(false);
            HBox.setHgrow(pwdField, Priority.ALWAYS);
            valueContainer.getChildren().add(pwdField);
        } else {
            JFXTextField txtField = new JFXTextField();
            txtField.setText(field.getValue());
            txtField.setEditable(false);
            HBox.setHgrow(txtField, Priority.ALWAYS);
            valueContainer.getChildren().add(txtField);
        }

        JFXButton copyBtn = new JFXButton("Copia");
        // Classe CSS per bottone grigio
        copyBtn.getStyleClass().add("button-secondary");
        copyBtn.setOnAction(e -> copyToClipboard(field.getValue()));

        valueContainer.getChildren().add(copyBtn);

        container.getChildren().addAll(lblName, valueContainer);
        return container;
    }

    private void copyToClipboard(String text) {
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        Clipboard.getSystemClipboard().setContent(content);
    }

    @FXML private void handleBack() {
        if (navigator != null) navigator.goBackToHome();
    }

    private void mostraErrore(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}