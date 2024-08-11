package bankprojekt.oberflaeche;

import bankprojekt.verarbeitung.GesperrtException;
import bankprojekt.verarbeitung.Girokonto;
import bankprojekt.verarbeitung.Kunde;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.LocalDate;

/**
 * Der KontoController ist eine JavaFX-Anwendung zur Verwaltung eines Girokontos.
 * Er erstellt ein Konto-Modell, eine Oberfläche und verbindet beide.
 * Ereignishandler für Einzahlungen und Abhebungen werden ebenfalls eingerichtet.
 *
 * @autor Doro
 */
public class KontoController extends Application {
    private KontoOberflaeche kontoOberflaeche;
    private Girokonto girokonto;

    /**
     * Der Einstiegspunkt für die JavaFX-Anwendung.
     *
     * @param args die Befehlszeilenargumente
     */

    /**
     * Diese Methode wird beim Start der Anwendung aufgerufen.
     * Sie erstellt das Konto-Modell, die Oberfläche und verbindet beide.
     *
     * @param primaryStage die primäre Bühne für diese Anwendung
     */
    @Override
    public void start(Stage primaryStage) {
        // Konto-Modell erstellen
        Kunde kunde = new Kunde("Max", "Mustermann", "Musterstraße 123", LocalDate.of(1990, 1, 1));
        girokonto = new Girokonto(kunde, 99887766, 500);

        // Oberfläche erstellen und mit dem Modell verknüpfen
        kontoOberflaeche = new KontoOberflaeche(girokonto, kunde);
        kontoOberflaeche.getKontostand().textProperty().bind(girokonto.kontostandProperty().asString("%.2f €"));
        kontoOberflaeche.getGesperrt().selectedProperty().bindBidirectional(girokonto.gesperrtProperty());
        kontoOberflaeche.getAdresse().textProperty().bindBidirectional(kunde.adresseProperty());

        // Ereignishandler für die Buttons einrichten
        kontoOberflaeche.getEinzahlen().setOnAction(e -> einzahlen());
        kontoOberflaeche.getAbheben().setOnAction(e -> abheben());

        // Scene und Stage einrichten
        Scene scene = new Scene(kontoOberflaeche, 600, 400);
        primaryStage.setTitle("Konto Verwaltung");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Handhabt die Einzahlung eines Betrags auf das Girokonto.
     * Setzt eine Meldung, wenn die Einzahlung erfolgreich war oder ein Fehler aufgetreten ist.
     */
    private void einzahlen() {
        try {
            double betrag = Double.parseDouble(kontoOberflaeche.getBetrag().getText());
            girokonto.einzahlen(betrag);
            kontoOberflaeche.getMeldung().setText("Einzahlung erfolgreich.");
        } catch (IllegalArgumentException e) {
            kontoOberflaeche.getMeldung().setText("Fehler: " + e.getMessage());
        }
    }

    /**
     * Handhabt die Abhebung eines Betrags vom Girokonto.
     * Setzt eine Meldung, wenn die Abhebung erfolgreich war oder ein Fehler aufgetreten ist.
     */
    private void abheben() {
        try {
            double betrag = Double.parseDouble(kontoOberflaeche.getBetrag().getText());
            girokonto.abheben(betrag);
            kontoOberflaeche.getMeldung().setText("Abhebung erfolgreich.");
        } catch (GesperrtException | IllegalArgumentException e) {
            kontoOberflaeche.getMeldung().setText("Fehler: " + e.getMessage());
        }
    }
}
