package bankprojekt.verwaltung;

import bankprojekt.verarbeitung.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Bank {
    /**
     * eine attribute bankleitzahl
     */
    private long bankleitzahl;

    /**
     * diese Hashmap wird die kontoliste gesammelt
     */
    private HashMap<Long, Konto> kontenliste;

    /**
     * @param bankleitzahl konstruktur von der Klasse Bank
     */
    public Bank(long bankleitzahl) {
        this.bankleitzahl = bankleitzahl;
        this.kontenliste = new HashMap<>();
    }

    /**
     * gibt zurük die Bankleitzahl
     *
     * @return
     */
    public long getBankleitzahl() {
        return this.bankleitzahl;
    }

    /**
     * @param inhaber Erstellt eine girokonto
     * @return die kontonummer
     */
    public long girokontoErstellen(Kunde inhaber) {
        long kontonummer = generiereNeueKontonummer();
        Girokonto neuesKonto = new Girokonto(inhaber, kontonummer, 500);
        kontenliste.put(kontonummer, neuesKonto);
        return kontonummer;
    }

    /**
     * @param inhaber Erstellt eine Sparbuch
     *                Erstellen wir eine neues Objekte von Sparbuch ist  neuesSparbuch
     *                tragen wir in die kontenliste
     * @return die kontonummer von dem sparbuch
     */
    public long sparbuchErstellen(Kunde inhaber) {
        long kontonummer = generiereNeueKontonummer();
        Sparbuch neuesSparbuch = new Sparbuch(inhaber, kontonummer);
        kontenliste.put(kontonummer, neuesSparbuch);
        return kontonummer;
    }

    /**
     * Generieren wir eine neue Konto  Nummer
     *
     * @return kontonummer
     */
    private long generiereNeueKontonummer() {
        long kontonummer = 0;
        for (Long nummer : kontenliste.keySet()) {
            if (nummer > kontonummer) {
                kontonummer = nummer;
            }
        }
        return kontonummer + 1;
    }

    /**
     * bekommen wir alle Konto mit diese Methode
     *
     * @return die liste von Konten
     */

    public String getAlleKonten() {
        StringBuilder auflistung = new StringBuilder();
        for (Long kontonummer : kontenliste.keySet()) {
            Konto konto = kontenliste.get(kontonummer);
            auflistung.append("Kontonummer: ")
                    .append(kontonummer)
                    .append(", Kontostand: ")
                    .append(konto.getKontostand())
                    .append(" EUR\n");
        }
        return auflistung.toString();
    }

    public List<Long> getAlleKontonummern() {
        return new ArrayList<>(kontenliste.keySet());
    }

    /**
     * @param von
     * @param betrag
     * @return
     * @throws GesperrtException
     */
    public boolean geldAbheben(long von, double betrag) throws GesperrtException {
        Konto konto = kontenliste.get(von);
        if (konto != null && konto.getKontostand() >= betrag) {
            konto.abheben(betrag);
            return true;
        }
        return false;
    }

    /**
     * @param auf
     * @param betrag
     */
    public void geldEinzahlen(long auf, double betrag) {
        Konto konto = kontenliste.get(auf);
        if (konto != null) {
            konto.einzahlen(betrag);
        }
    }

    /**
     * @param nummer
     * @return
     */

    public boolean kontoLoeschen(long nummer) {
        if (kontenliste.containsKey(nummer)) {
            kontenliste.remove(nummer);
            return true;
        }
        return false;
    }

    /**
     * Löscht ein Konto mit einer gegebenen Kontonummer.
     *
     * @param nummer die Kontonummer des zu löschenden Kontos
     * @return true, wenn das Konto erfolgreich gelöscht wurde, sonst false
     */

    public double getKontostand(long nummer) {
        Konto konto = kontenliste.get(nummer);
        if (konto != null) {
            return konto.getKontostand();
        }
        return 0.0;
    }

    /**
     * Führt eine Überweisung von einem Konto auf ein anderes Konto durch.
     *
     * @param vonKontonr       die Kontonummer des sendenden Kontos
     * @param nachKontonr      die Kontonummer des empfangenden Kontos
     * @param betrag           der zu überweisende Betrag
     * @param verwendungszweck der Verwendungszweck der Überweisung
     * @return true, wenn die Überweisung erfolgreich war, sonst false
     */
    public boolean geldUeberweisen(long vonKontonr, long nachKontonr, double betrag, String verwendungszweck) {
        if (vonKontonr == nachKontonr) {
            return false; // Überweisung auf dasselbe Konto ist nicht erlaubt
        }

        Konto vonKonto = kontenliste.get(vonKontonr);
        Konto nachKonto = kontenliste.get(nachKontonr);

        if (vonKonto == null || nachKonto == null) {
            return false; // Mindestens ein Konto existiert nicht
        }

        if (vonKonto.isGesperrt() || nachKonto.isGesperrt()) {
            return false; // Überweisung nicht möglich, da mindestens ein Konto gesperrt ist
        }

        try {
            // Abheben des Betrags vom Ursprungskonto
            if (!vonKonto.abheben(betrag)) {
                return false; // Nicht genügend Guthaben oder Konto gesperrt
            }
            // Einzahlen des Betrags auf das Zielkonto
            nachKonto.einzahlen(betrag);
            return true; // Überweisung erfolgreich
        } catch (GesperrtException e) {
            // Fange die GesperrtException, falls die Konten während der Operation gesperrt wurden
            return false;
        }
    }


    /**
     * Fügt ein gegebenes Konto in die Kontenliste der Bank ein und liefert die
     * dabei von der Bank vergebene Kontonummer zurück.
     *
     * @param k das Konto, das eingefügt werden soll
     * @return die Kontonummer des eingefügten Kontos
     */
    public long mockEinfuegen(Konto k) {
        long kontonummer = generiereNeueKontonummer();
        kontenliste.put(kontonummer, k);
        return kontonummer;
    }

}



