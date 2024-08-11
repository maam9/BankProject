package bankprojekt.oberflaeche;

import bankprojekt.verarbeitung.GesperrtException;
import bankprojekt.verarbeitung.Konto;
import bankprojekt.verarbeitung.Kunde;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static javafx.beans.binding.Bindings.createStringBinding;

/**
 * Eine Oberfläche für ein einzelnes Konto. Man kann einzahlen
 * und abheben und sperren und die Adresse des Kontoinhabers
 * ändern
 *
 * @author Doro
 */
public class KontoOberflaeche extends BorderPane {
    @FXML
    private Text ueberschrift;
    @FXML
    private GridPane anzeige;
    private Text txtNummer;
    /**
     * Anzeige der Kontonummer
     */
    @FXML
    private Text nummer;
    private Text txtStand;
    /**
     * Anzeige des Kontostandes
     */
    @FXML
    private Text stand;
    private Text txtGesperrt;
    /**
     * Anzeige und Änderung des Gesperrt-Zustandes
     */
    @FXML
    private CheckBox gesperrt;
    private Text txtAdresse;
    /**
     * Anzeige und Änderung der Adresse des Kontoinhabers
     */
    @FXML
    private TextArea adresse;
    /**
     * Anzeige von Meldungen über Kontoaktionen
     */
    @FXML
    private Text meldung;
    private HBox aktionen;
    /**
     * Auswahl des Betrags für eine Kontoaktion
     */
    @FXML
    private TextField betrag;
    /**
     * löst eine Einzahlung aus
     */
    @FXML
    private Button einzahlen;
    /**
     * löst eine Abhebung aus
     */
    @FXML
    private Button abheben;

    private Konto konto;
    private Kunde kunde;

    /**
     * Erstellt die Oberfläche für ein Konto.
     *
     * @param konto Das Konto, das angezeigt und bearbeitet werden soll.
     * @param kunde Der Inhaber des Kontos.
     */
    public KontoOberflaeche(Konto konto, Kunde kunde) {
        ueberschrift = new Text("Ein Konto verändern");
        ueberschrift.setFont(new Font("Sans Serif", 25));
        BorderPane.setAlignment(ueberschrift, Pos.CENTER);
        this.setTop(ueberschrift);

        anzeige = new GridPane();
        anzeige.setPadding(new Insets(20));
        anzeige.setVgap(10);
        anzeige.setAlignment(Pos.CENTER);

        txtNummer = new Text("Kontonummer:");
        txtNummer.setFont(new Font("Sans Serif", 15));
        anzeige.add(txtNummer, 0, 0);
        nummer = new Text();
        nummer.setFont(new Font("Sans Serif", 15));
        GridPane.setHalignment(nummer, HPos.RIGHT);
        anzeige.add(nummer, 1, 0);

        txtStand = new Text("Kontostand:");
        txtStand.setFont(new Font("Sans Serif", 15));
        anzeige.add(txtStand, 0, 1);
        stand = new Text();
        stand.setFont(new Font("Sans Serif", 15));
        GridPane.setHalignment(stand, HPos.RIGHT);
        anzeige.add(stand, 1, 1);

        txtGesperrt = new Text("Gesperrt: ");
        txtGesperrt.setFont(new Font("Sans Serif", 15));
        anzeige.add(txtGesperrt, 0, 2);
        gesperrt = new CheckBox();
        GridPane.setHalignment(gesperrt, HPos.RIGHT);
        anzeige.add(gesperrt, 1, 2);

        txtAdresse = new Text("Adresse: ");
        txtAdresse.setFont(new Font("Sans Serif", 15));
        anzeige.add(txtAdresse, 0, 3);
        adresse = new TextArea();
        adresse.setPrefColumnCount(25);
        adresse.setPrefRowCount(2);
        GridPane.setHalignment(adresse, HPos.RIGHT);
        anzeige.add(adresse, 1, 3);

        meldung = new Text("Willkommen lieber Benutzer");
        meldung.setFont(new Font("Sans Serif", 15));
        meldung.setFill(Color.RED);
        anzeige.add(meldung, 0, 4, 2, 1);

        this.setCenter(anzeige);

        aktionen = new HBox();
        aktionen.setSpacing(10);
        aktionen.setAlignment(Pos.CENTER);

        betrag = new TextField("100.00");
        aktionen.getChildren().add(betrag);
        einzahlen = new Button("Einzahlen");
        aktionen.getChildren().add(einzahlen);
        abheben = new Button("Abheben");
        aktionen.getChildren().add(abheben);

        this.setBottom(aktionen);

        this.konto = konto;
        this.kunde = kunde;
        // Kontonummer anzeigen

        // Kontostand anzeigen und Farbanpassung je nach Wert
        stand.textProperty().bind(createStringBinding(
                () -> String.format("%.2f €", konto.getKontostand()),
                konto.kontostandProperty()));
        stand.fillProperty().bind(Bindings.createObjectBinding(
                () -> konto.getKontostand() >= 0 ? Color.GREEN : Color.RED,
                konto.kontostandProperty()));

        // Gesperrt Zustand und Möglichkeit zur Änderung
        gesperrt.selectedProperty().bindBidirectional(konto.gesperrtProperty());

        // Adresse anzeigen und Änderung ermöglichen
        adresse.textProperty().bindBidirectional(kunde.adresseProperty());

        // Ereignishandler für Einzahlungen und Abhebungen
        einzahlen.setOnAction(e -> handleEinzahlung());
        abheben.setOnAction(e -> handleAbhebung());

        stand = new Text();
        GridPane.setHalignment(stand, HPos.RIGHT);
        // Annahme: 'anzeige' ist eine Instanz von GridPane
        // anzeige.add(new Text("Kontostand:"), 0, 1);
        // anzeige.add(stand, 1, 1);
    }

    /**
     * Getter für das Text-Objekt, das den Kontostand anzeigt.
     *
     * @return Das Text-Objekt, das den Kontostand anzeigt.
     */
    public Text getKontostand() {
        return stand;
    }

    /**
     * Handhabt die Einzahlung eines Betrags auf das Konto.
     */
    private void handleEinzahlung() {
        try {
            double amount = Double.parseDouble(betrag.getText());
            konto.einzahlen(amount);
            meldung.setText("Einzahlung erfolgreich!");
            meldung.setFill(Color.GREEN);
        } catch (NumberFormatException e) {
            meldung.setText("Ungültiger Betrag!");
            meldung.setFill(Color.RED);
        }
    }

    /**
     * Handhabt die Abhebung eines Betrags vom Konto.
     */
    private void handleAbhebung() {
        try {
            double amount = Double.parseDouble(betrag.getText());
            konto.abheben(amount);
            meldung.setText("Abhebung erfolgreich!");
            meldung.setFill(Color.GREEN);
        } catch (NumberFormatException e) {
            meldung.setText("Ungültiger Betrag!");
            meldung.setFill(Color.RED);
        } catch (GesperrtException e) {
            meldung.setText("Konto gesperrt!");
            meldung.setFill(Color.RED);
        }
    }

    /**
     * Getter für die Anzeige der Kontonummer.
     *
     * @return Das Text-Objekt, das die Kontonummer anzeigt.
     */
    public Text getNummer() {
        return nummer;
    }

    /**
     * Getter für die Anzeige des Kontostands.
     *
     * @return Das Text-Objekt, das den Kontostand anzeigt.
     */
    public Text getStand() {
        return stand;
    }

    /**
     * Getter für das CheckBox-Objekt, das den Gesperrt-Zustand anzeigt.
     *
     * @return Das CheckBox-Objekt, das den Gesperrt-Zustand anzeigt.
     */
    public CheckBox getGesperrt() {
        return gesperrt;
    }

    /**
     * Getter für das TextArea-Objekt, das die Adresse des Kontoinhabers anzeigt.
     *
     * @return Das TextArea-Objekt, das die Adresse des Kontoinhabers anzeigt.
     */
    public TextArea getAdresse() {
        return adresse;
    }

    /**
     * Getter für das TextField-Objekt, das den Betrag für eine Kontoaktion eingibt.
     *
     * @return Das TextField-Objekt, das den Betrag für eine Kontoaktion eingibt.
     */
    public TextField getBetrag() {
        return betrag;
    }

    /**
     * Getter für den Button, der eine Einzahlung auslöst.
     *
     * @return Der Button, der eine Einzahlung auslöst.
     */
    public Button getEinzahlen() {
        return einzahlen;
    }

    /**
     * Getter für den Button, der eine Abhebung auslöst.
     *
     * @return Der Button, der eine Abhebung auslöst.
     */
    public Button getAbheben() {
        return abheben;
    }


    public Text getMeldung() {
        return meldung;
    }
}