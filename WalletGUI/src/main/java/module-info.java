module com.simonepugliese.walletgui {
    // --- Dipendenze JavaFX ---
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;

    requires com.jfoenix;

    requires com.simonepugliese.wallet;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;

    opens com.simonepugliese.walletgui to javafx.fxml, javafx.graphics;

    exports com.simonepugliese.walletgui;
    exports com.simonepugliese.walletgui.controllers;
    opens com.simonepugliese.walletgui.controllers to javafx.fxml, javafx.graphics;
}