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
import javafx.scene.layout.VBox;

public class EntryEditorController {

    @FXML private Label headerLabel;
    @FXML private JFXTextField descriptionField;
    @FXML private JFXComboBox<Category> categoryComboBox;
    @FXML private VBox fieldsContainer;

    private ScreenNavigator navigator;
    private WalletManager manager;

    // Se entry è null, siamo in modalità creazione, altrimenti modifica
    private Entry currentEntry;

    public void setNavigator(ScreenNavigator navigator) {
        this.navigator = navigator;
    }

    public void setManager(WalletManager manager) {
        this.manager = manager;
    }

    public void initEditor(Entry entryToEdit) {
        this.currentEntry = entryToEdit;
        categoryComboBox.getItems().addAll(Category.values());

        if (currentEntry == null) {
            // CREAZIONE
            headerLabel.setText("Nuova Entry");
            categoryComboBox.getSelectionModel().select(Category.GENERIC);
            // Aggiungi un paio di campi vuoti di default per comodità
            addUiRow("", "", FieldType.TEXT, false);
            addUiRow("", "", FieldType.PASSWORD, true);
        } else {
            // MODIFICA (predisposizione futura)
            headerLabel.setText("Modifica Entry");
            descriptionField.setText(currentEntry.getDescription());
            categoryComboBox.getSelectionModel().select(currentEntry.getCategory());
            // Carica i campi esistenti
            currentEntry.getFields().forEach((name, field) -> {
                addUiRow(name, field.getValue(), field.getType(), field.isSensitive());
            });
        }
    }

    @FXML
    private void addFieldRow() {
        addUiRow("", "", FieldType.TEXT, false);
    }

    /**
     * Crea graficamente una riga per inserire un campo (Nome | Valore | Tipo | Sensibile | X).
     */
    private void addUiRow(String name, String value, FieldType type, boolean isSensitive) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #F5F5F5; -fx-padding: 10; -fx-background-radius: 5;");

        // 1. Nome del campo (Chiave)
        JFXTextField nameField = new JFXTextField(name);
        nameField.setPromptText("Nome Campo (es. User)");
        nameField.setPrefWidth(150);

        // 2. Valore
        JFXTextField valueField = new JFXTextField(value);
        valueField.setPromptText("Valore");
        valueField.setPrefWidth(200);

        // 3. Tipo (Combo)
        JFXComboBox<FieldType> typeCombo = new JFXComboBox<>();
        typeCombo.getItems().addAll(FieldType.values());
        typeCombo.getSelectionModel().select(type != null ? type : FieldType.TEXT);
        typeCombo.setPrefWidth(100);

        // 4. Sensibile (Checkbox)
        JFXCheckBox sensitiveCheck = new JFXCheckBox("Sensibile");
        sensitiveCheck.setSelected(isSensitive);

        // 5. Bottone Rimuovi
        JFXButton removeBtn = new JFXButton("X");
        removeBtn.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        removeBtn.setOnAction(e -> fieldsContainer.getChildren().remove(row));

        // Assemblaggio
        row.getChildren().addAll(nameField, valueField, typeCombo, sensitiveCheck, removeBtn);

        // Tagghiamo i nodi per recuperarli dopo nel salvataggio in modo sicuro
        nameField.setUserData("name");
        valueField.setUserData("value");
        typeCombo.setUserData("type");
        sensitiveCheck.setUserData("sensitive");

        fieldsContainer.getChildren().add(row);
    }

    @FXML
    private void handleSave() {
        if (descriptionField.getText() == null || descriptionField.getText().isBlank()) {
            mostraErrore("Campo obbligatorio", "Inserisci un nome per l'entry.");
            return;
        }

        Category cat = categoryComboBox.getSelectionModel().getSelectedItem();
        if (cat == null) cat = Category.GENERIC;

        // Creazione nuova Entry
        Entry newEntry = new Entry(descriptionField.getText(), cat);

        // Iteriamo sulle righe della UI per estrarre i dati
        for (Node node : fieldsContainer.getChildren()) {
            if (node instanceof HBox row) {
                String fieldName = null;
                String fieldValue = null;
                FieldType fieldType = FieldType.TEXT;
                boolean fieldSensitive = false;

                for (Node child : row.getChildren()) {
                    if ("name".equals(child.getUserData())) {
                        fieldName = ((JFXTextField) child).getText();
                    } else if ("value".equals(child.getUserData())) {
                        fieldValue = ((JFXTextField) child).getText();
                    } else if ("type".equals(child.getUserData())) {
                        fieldType = ((JFXComboBox<FieldType>) child).getSelectionModel().getSelectedItem();
                    } else if ("sensitive".equals(child.getUserData())) {
                        fieldSensitive = ((JFXCheckBox) child).isSelected();
                    }
                }

                // Validazione minima: il campo deve avere un nome per essere salvato
                if (fieldName != null && !fieldName.isBlank()) {
                    // Se value è null metti stringa vuota per evitare problemi
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
            mostraErrore("Errore Salvataggio", "Impossibile salvare: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        navigator.goBackToHome();
    }

    private void mostraErrore(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}