package bankprojekt.verarbeitung;

import bankprojekt.Observer.KontoObserver;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * stellt ein allgemeines Bank-Konto dar
 */
public abstract class Konto implements Comparable<Konto> {
    /**
     * die Kontonummer
     */
    private final long nummer;
    private BooleanProperty imPlus = new SimpleBooleanProperty();
    private List<KontoObserver> observers = new ArrayList<>();
    /**
     * der Kontoinhaber
     */
    private Kunde inhaber;
    /**
     * der aktuelle Kontostand
     */

    /**
     * setzt den aktuellen Kontostand
     *
     * @param kontostand neuer Kontostand
     */
    private ReadOnlyDoubleWrapper kontostand = new ReadOnlyDoubleWrapper();

    /**
     * Waehrung
     */
    private Waehrung waehrung;
    private double dispo;
    /**
     * Wenn das Konto gesperrt ist (gesperrt = true), können keine Aktionen daran mehr vorgenommen werden,
     * die zum Schaden des Kontoinhabers wären (abheben, Inhaberwechsel)
     */
    private BooleanProperty gesperrt = new SimpleBooleanProperty();

    /**
     * Setzt die beiden Eigenschaften kontoinhaber und kontonummer auf die angegebenen Werte,
     * der anfängliche Kontostand wird auf 0 gesetzt.
     *
     * @param inhaber     der Inhaber
     * @param kontonummer die gewünschte Kontonummer
     * @throws IllegalArgumentException wenn der inhaber null ist
     */
    public Konto(Kunde inhaber, long kontonummer, Waehrung waehrung) {
        if (inhaber == null)
            throw new IllegalArgumentException("Inhaber darf nicht null sein!");
        this.inhaber = inhaber;
        this.nummer = kontonummer;
        this.waehrung = waehrung;
        this.dispo = 0;
        this.kontostand.set(0);
        imPlus.set(kontostand.get() >= 0);
        this.gesperrt.set(false);
    }

    /**
     * setzt alle Eigenschaften des Kontos auf Standardwerte
     */
    public Konto() {
        this(Kunde.MUSTERMANN, 1234567, Waehrung.EUR);
    }

    public void addObserver(KontoObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(KontoObserver observer) {
        observers.remove(observer);
    }

    protected void notifyObservers() {
        for (KontoObserver observer : observers) {
            observer.update(this);
        }
    }

    /**
     * Methode zum Abheben in einer bestimmten Währung
     */
    public boolean abheben(double betrag, Waehrung w) throws GesperrtException {
        if (this.gesperrt.get()) {
            throw new GesperrtException(this.nummer);
        }

        if (betrag < 0 || Double.isNaN(betrag) || Double.isInfinite(betrag)) {
            throw new IllegalArgumentException("Betrag ungültig");
        }
        /**
         Umrechnung des Betrags in Euro
         */

        double betragInEuro = w.waehrungInEuroUmrechnen(betrag);

        if (this.kontostand.get() - betragInEuro >= -dispo) {
            kontostand.set(kontostand.get() - betrag);
            notifyObservers();

            return true;
        } else {
            return false;
        }
    }

    /**
     * Methode zum Einzahlen in einer bestimmten Währung
     */

    public void einzahlen(double betrag, Waehrung w) throws GesperrtException {
        if (this.gesperrt.get()) {
            throw new GesperrtException(this.nummer);
        }

        if (betrag < 0 || Double.isNaN(betrag) || Double.isInfinite(betrag)) {
            throw new IllegalArgumentException("Betrag ungültig");
        }

        // Umrechnung des Betrags in Euro
        double betragInEuro = w.waehrungInEuroUmrechnen(betrag);
        kontostand.set(kontostand.get() + betrag);
        notifyObservers();

    }

    /**
     * Methode zur Abfrage der aktuellen Währung
     */

    public Waehrung getAktuelleWaehrung() {
        return this.waehrung;
    }

    /**
     * Neue Methode zum Wechseln der Währung
     */

    public void waehrungswechsel(Waehrung neu) {
        if (this.waehrung != neu) {
            // Aktualisieren der Währung
            this.waehrung = neu;

            // Umrechnung des Kontostands und des Dispos in die neue Währung
            double kontostandInEuro = Waehrung.EUR.waehrungInEuroUmrechnen(this.kontostand.get());
            double dispoInEuro = Waehrung.EUR.waehrungInEuroUmrechnen(this.dispo);

            // Umrechnung des zwischengespeicherten Kontostands und des Dispos in die neue Währung
            double neuerKontostand = neu.euroInWaehrungUmrechnen(kontostandInEuro);
            double neuerDispo = neu.euroInWaehrungUmrechnen(dispoInEuro);

            // Zurückumrechnung in die neue Währung
            this.kontostand.set(neuerKontostand);
            this.dispo = neuerDispo;
        }
    }

    /**
     * liefert den Kontoinhaber zurück
     *
     * @return der Inhaber
     */
    public final Kunde getInhaber() {
        return this.inhaber;
    }

    /**
     * setzt den Kontoinhaber
     *
     * @param kinh neuer Kontoinhaber
     * @throws GesperrtException        wenn das Konto gesperrt ist
     * @throws IllegalArgumentException wenn kinh null ist
     */
    public final void setInhaber(Kunde kinh) throws GesperrtException {
        if (kinh == null)
            throw new IllegalArgumentException("Der Inhaber darf nicht null sein!");
        if (this.gesperrt.get())
            throw new GesperrtException(this.nummer);
        this.inhaber = kinh;

    }

    public boolean isImPlus() {
        return imPlus.get();
    }

    public BooleanProperty imPlusProperty() {
        return imPlus;
    }

    public ReadOnlyDoubleProperty kontostandProperty() {
        return kontostand.getReadOnlyProperty();
    }

    /**
     * liefert den aktuellen Kontostand
     *
     * @return Kontostand
     */
    public final double getKontostand() {
        return this.kontostand.get();
    }

    protected void setKontostand(double kontostand) {
        this.kontostand.set(kontostand);
    }

    /**
     * liefert die Kontonummer zurück
     *
     * @return Kontonummer
     */
    public final long getKontonummer() {
        return nummer;
    }

    /**
     * liefert zurück, ob das Konto gesperrt ist oder nicht
     *
     * @return true, wenn das Konto gesperrt ist
     */
    public final boolean isGesperrt() {
        return gesperrt.get();
    }

    public BooleanProperty gesperrtProperty() {
        return gesperrt;
    }

    /**
     * Erhöht den Kontostand um den eingezahlten Betrag.
     *
     * @param betrag double
     * @throws IllegalArgumentException wenn der betrag negativ ist
     */
    public void einzahlen(double betrag) {
        if (betrag < 0 || !Double.isFinite(betrag)) {
            throw new IllegalArgumentException("Falscher Betrag");
        }
        setKontostand(getKontostand() + betrag);
    }

    @Override
    public String toString() {
        String ausgabe;
        ausgabe = "Kontonummer: " + this.getKontonummerFormatiert()
                + System.getProperty("line.separator");
        ausgabe += "Inhaber: " + this.inhaber;
        ausgabe += "Aktueller Kontostand: " + getKontostandFormatiert() + " ";
        ausgabe += this.getGesperrtText() + System.getProperty("line.separator");
        return ausgabe;
    }

    /**
     * Mit dieser Methode wird der geforderte Betrag vom Konto abgehoben, wenn es nicht gesperrt ist
     * und die speziellen Abheberegeln des jeweiligen Kontotyps die Abhebung erlauben
     *
     * @param betrag double
     * @return true, wenn die Abhebung geklappt hat,
     * false, wenn sie abgelehnt wurde
     * @throws GesperrtException        wenn das Konto gesperrt ist
     * @throws IllegalArgumentException wenn der betrag negativ oder unendlich oder NaN ist
     */
    public abstract boolean abheben(double betrag)
            throws GesperrtException;

    /**
     * sperrt das Konto, Aktionen zum Schaden des Benutzers sind nicht mehr möglich.
     */
    public final void sperren() {
        this.gesperrt.set(true);
    }

    /**
     * entsperrt das Konto, alle Kontoaktionen sind wieder möglich.
     */
    public final void entsperren() {
        this.gesperrt.set(false);
    }


    /**
     * liefert eine String-Ausgabe, wenn das Konto gesperrt ist
     *
     * @return "GESPERRT", wenn das Konto gesperrt ist, ansonsten ""
     */
    public final String getGesperrtText() {
        if (this.gesperrt.get()) {
            return "GESPERRT";
        } else {
            return "";
        }
    }

    /**
     * liefert die ordentlich formatierte Kontonummer
     *
     * @return auf 10 Stellen formatierte Kontonummer
     */
    public String getKontonummerFormatiert() {
        return String.format("%10d", this.nummer);
    }

    /**
     * liefert den ordentlich formatierten Kontostand
     *
     * @return formatierter Kontostand mit 2 Nachkommastellen und Währungssymbol
     */
    public String getKontostandFormatiert() {
        return String.format("%10.2f €", this.getKontostand());
    }

    /**
     * Vergleich von this mit other; Zwei Konten gelten als gleich,
     * wen sie die gleiche Kontonummer haben
     *
     * @param other das Vergleichskonto
     * @return true, wenn beide Konten die gleiche Nummer haben
     */
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (this.getClass() != other.getClass())
            return false;
        if (this.nummer == ((Konto) other).nummer)
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {
        return 31 + (int) (this.nummer ^ (this.nummer >>> 32));
    }

    /**
     * @param other the object to be compared.
     * @return
     */
    @Override
    public int compareTo(Konto other) {
        if (other.getKontonummer() > this.getKontonummer())
            return -1;
        if (other.getKontonummer() < this.getKontonummer())
            return 1;
        return 0;
    }
}
