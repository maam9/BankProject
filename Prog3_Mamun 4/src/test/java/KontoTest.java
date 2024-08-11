import bankprojekt.Observer.KontoObserver;
import bankprojekt.verarbeitung.GesperrtException;
import bankprojekt.verarbeitung.Girokonto;
import bankprojekt.verarbeitung.Kunde;
import bankprojekt.verarbeitung.Waehrung;
import org.decimal4j.util.DoubleRounder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KontoTest {
    private Girokonto konto;
    private KontoObserver mockObserver;

    @BeforeEach
    void setUp() {
        mockObserver = mock(KontoObserver.class);
        // Initialisiere Konto mit EUR als Währung und verwende den vorgegebenen Musterkunden.
        konto = new Girokonto(Kunde.MUSTERMANN, 123456, 5000);
        konto.einzahlen(1000);
        konto.addObserver(mockObserver);
        System.out.println("Konto erstellt und 1000 EUR eingezahlt. Aktueller Kontostand: " + konto.getKontostand());
    }

    @Test
    public void testNotifyObserversOnDeposit() throws GesperrtException {
        konto.einzahlen(500, Waehrung.EUR);
        verify(mockObserver, times(1)).update(konto);
    }

    @Test
    public void testNotifyObserversOnWithdrawal() throws GesperrtException {
        konto.abheben(200, Waehrung.EUR);
        verify(mockObserver, times(1)).update(konto);
    }

    @Test
    void testWaehrungswechselESCUDO() {
        double erwarteterKontostand = DoubleRounder.round(konto.getKontostand() * 109.8269, 2);

        // Sicherstellen, dass die Währung nicht null ist.
        assertNotNull(konto.getAktuelleWaehrung(), "Die Währung sollte initial nicht null sein.");
        System.out.println("Aktuelle Währung: " + konto.getAktuelleWaehrung());

        // Durchführung des Währungswechsels.
        konto.waehrungswechsel(Waehrung.ESCUDO);
        System.out.println("Währungswechsel durchgeführt. Aktuelle Währung: " + konto.getAktuelleWaehrung());
        assertEquals(Waehrung.ESCUDO, konto.getAktuelleWaehrung(), "Die Währung sollte nach dem Wechsel ESCUDO sein.");

        // Prüfung des Kontostands nach der Umrechnung.
        assertEquals(erwarteterKontostand, konto.getKontostand(), "Der umgerechnete Kontostand sollte dem erwarteten Wert in ESCUDO entsprechen.");
    }

    @Test
    void testWaehrungswechselDobra() {
        double erwarteterKontostand = DoubleRounder.round(konto.getKontostand() * 24304.7429, 2);

        // Sicherstellen, dass die Währung nicht null ist.
        assertNotNull(konto.getAktuelleWaehrung(), "Die Währung sollte initial nicht null sein.");
        System.out.println("Aktuelle Währung: " + konto.getAktuelleWaehrung());

        // Durchführung des Währungswechsels.
        konto.waehrungswechsel(Waehrung.Dobra);
        System.out.println("Währungswechsel durchgeführt. Aktuelle Währung: " + konto.getAktuelleWaehrung());
        assertEquals(Waehrung.Dobra, konto.getAktuelleWaehrung(), "Die Währung sollte nach dem Wechsel Dobra sein.");

        // Prüfung des Kontostands nach der Umrechnung.
        assertEquals(erwarteterKontostand, konto.getKontostand(), "Der umgerechnete Kontostand sollte dem erwarteten Wert in Dobra entsprechen.");
    }

    @Test
    void testWaehrungswechselFrancs() {
        double erwarteterKontostand = DoubleRounder.round(konto.getKontostand() * 490.92, 2);

        // Sicherstellen, dass die Währung nicht null ist.
        assertNotNull(konto.getAktuelleWaehrung(), "Die Währung sollte initial nicht null sein.");
        System.out.println("Aktuelle Währung: " + konto.getAktuelleWaehrung());

        // Durchführung des Währungswechsels.
        konto.waehrungswechsel(Waehrung.Francs);
        System.out.println("Währungswechsel durchgeführt. Aktuelle Währung: " + konto.getAktuelleWaehrung());
        assertEquals(Waehrung.Francs, konto.getAktuelleWaehrung(), "Die Währung sollte nach dem Wechsel Francs sein.");

        // Prüfung des Kontostands nach der Umrechnung.
        assertEquals(erwarteterKontostand, konto.getKontostand(), "Der umgerechnete Kontostand sollte dem erwarteten Wert in Francs entsprechen.");
    }


    @Test
    void testAbhebenErfolgreich() throws GesperrtException {
        assertTrue(konto.abheben(100));
        assertEquals(900, konto.getKontostand());
    }

    @Test
    void testAbhebenGesperrtesKonto() {
        konto.sperren();
        assertThrows(GesperrtException.class, () -> konto.abheben(50));
    }

    @Test
    void testAbhebenUngueltigerBetrag() {
        assertThrows(IllegalArgumentException.class, () -> konto.abheben(-100));
    }

    @Test
    void testAbhebenUeberDispo() throws GesperrtException {
        System.out.println("kontostand: " + konto.getKontostand());
        assertFalse(konto.abheben(6001));
    }
}
