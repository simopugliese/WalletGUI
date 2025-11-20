package com.simonepugliese.walletgui.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.simonepugliese.Core.WalletManager;
import com.simonepugliese.Model.Category;
import com.simonepugliese.Model.Entry;
import com.simonepugliese.Model.Field;
import com.simonepugliese.Model.FieldType;
import com.simonepugliese.walletgui.ScreenNavigator;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class EntryEditorController {

    @FXML private Label headerLabel;
    @FXML private JFXTextField descriptionField;
    @FXML private JFXComboBox<Category> categoryComboBox;
    @FXML private VBox fieldsContainer;

    private ScreenNavigator navigator;
    private WalletManager manager;
    private Entry currentEntry;

    public void setNavigator(ScreenNavigator navigator) { this.navigator = navigator; }
    public void setManager(WalletManager manager) { this.manager = manager; }

    public void initEditor(Entry entryToEdit) {
        this.currentEntry = entryToEdit;
        categoryComboBox.getItems().addAll(Category.values());

        if (currentEntry == null) {
            headerLabel.setText("Nuova Entry");
            categoryComboBox.getSelectionModel().select(Category.GENERIC);
            addUiRow("", "", FieldType.TEXT, false);
            addUiRow("", "", FieldType.PASSWORD, true);
        } else {
            headerLabel.setText("Modifica Entry");
            descriptionField.setText(currentEntry.getDescription());
            categoryComboBox.getSelectionModel().select(currentEntry.getCategory());
            currentEntry.getFields().forEach((name, field) -> {
                addUiRow(name, field.getValue(), field.getType(), field.isSensitive());
            });
        }
    }

    @FXML
    private void addFieldRow() {
        addUiRow("", "", FieldType.TEXT, false);
    }

    private void addUiRow(String name, String value, FieldType type, boolean isSensitive) {
        HBox row = new HBox(15); // Spaziatura aumentata
        row.setAlignment(Pos.CENTER_LEFT);

        // Assegna classe CSS invece di stile inline
        row.getStyleClass().add("field-row");

        JFXTextField nameField = new JFXTextField(name);
        nameField.setPromptText("Nome Campo");
        nameField.setLabelFloat(true);
        nameField.setPrefWidth(180);

        JFXTextField valueField = new JFXTextField(value);
        valueField.setPromptText("Valore");
        valueField.setLabelFloat(true);
        HBox.setHgrow(valueField, Priority.ALWAYS); // Si espande

        JFXComboBox<FieldType> typeCombo = new JFXComboBox<>();
        typeCombo.getItems().addAll(FieldType.values());
        typeCombo.getSelectionModel().select(type != null ? type : FieldType.TEXT);
        typeCombo.setPrefWidth(120);

        JFXCheckBox sensitiveCheck = new JFXCheckBox("Sensibile");
        sensitiveCheck.setSelected(isSensitive);

        JFXButton removeBtn = new JFXButton("Elimina");
        // Usa classe CSS per il bottone rosso
        removeBtn.getStyleClass().add("button-danger");
        removeBtn.setOnAction(e -> fieldsContainer.getChildren().remove(row));

        row.getChildren().addAll(nameField, valueField, typeCombo, sensitiveCheck, removeBtn);

        nameField.setUserData("name");
        valueField.setUserData("value");
        typeCombo.setUserData("type");
        sensitiveCheck.setUserData("sensitive");

        fieldsContainer.getChildren().add(row);
    }

    @FXML
    private void handleSave() {
        if (descriptionField.getText() == null || descriptionField.getText().isBlank()) {
            mostraErrore("Campo mancante", "Inserisci un nome per l'entry.");
            return;
        }

        Category cat = categoryComboBox.getSelectionModel().getSelectedItem();
        if (cat == null) cat = Category.GENERIC;

        Entry newEntry = new Entry(descriptionField.getText(), cat);

        for (Node node : fieldsContainer.getChildren()) {
            if (node instanceof HBox row) {
                String fieldName = null;
                String fieldValue = null;
                FieldType fieldType = FieldType.TEXT;
                boolean fieldSensitive = false;

                for (Node child : row.getChildren()) {
                    if ("name".equals(child.getUserData())) fieldName = ((JFXTextField) child).getText();
                    else if ("value".equals(child.getUserData())) fieldValue = ((JFXTextField) child).getText();
                    else if ("type".equals(child.getUserData())) fieldType = ((JFXComboBox<FieldType>) child).getSelectionModel().getSelectedItem();
                    else if ("sensitive".equals(child.getUserData())) fieldSensitive = ((JFXCheckBox) child).isSelected();
                }

                if (fieldName != null && !fieldName.isBlank()) {
                    if (fieldValue == null) fieldValue = "";
                    newEntry.putField(fieldName, new Field(fieldValue, fieldType, fieldSensitive));
                }
            }
        }

        try {
            manager.saveEntry(newEntry);
            navigator.goBackToHome();
        } catch (Exception e) {
            e.printStackTrace();
            mostraErrore("Errore Salvataggio", e.getMessage());
        }
    }

    @FXML
    private void handleCancel() { navigator.goBackToHome(); }

    private void mostraErrore(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}