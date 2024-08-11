package bankprojekt.verwaltung;

import bankprojekt.verarbeitung.GesperrtException;
import bankprojekt.verarbeitung.Konto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Testklasse für die Bank-Klasse, die mithilfe von Mockito Mocks erstellt,
 * um die Funktionalität der Bank unabhängig von der genauen Implementierung der Konten zu testen.
 */

class BankTest {


    private Bank bank;


    private long kontoNummer1;
    private long kontoNummer2;
    private long kontoNummer3;


    @Mock
    private Konto mockKonto1;

    @Mock
    private Konto mockKonto2;

    private Konto mockKonto3;


    /**
     * Initialisiert die mocks und fügt sie im Bank ein. beim jede durchlauf
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bank = new Bank(2424553);
        kontoNummer1 = bank.mockEinfuegen(mockKonto1);
        kontoNummer2 = bank.mockEinfuegen(mockKonto2);
        kontoNummer3 = bank.mockEinfuegen(mockKonto3);


    }

    /**
     * Testet die erfolgreiche Überweisung von Geld zwischen zwei Konten.
     *
     * @throws GesperrtException wenn das Konto gesperrt ist
     */
    @Test
    void geldUeberweisen_TransferSuccessful() throws GesperrtException {
        // Arrange
        when(mockKonto1.abheben(anyDouble())).thenReturn(true);
        doNothing().when(mockKonto2).einzahlen(anyDouble());

        // Act
        boolean transferSuccess = bank.geldUeberweisen(kontoNummer1, kontoNummer2, 100.0, "Überweisung");

        // Assert
        assertTrue(transferSuccess);
        verify(mockKonto1, times(1)).abheben(100.0);
        verify(mockKonto2, times(1)).einzahlen(100.0);
    }

    /**
     * Testet die unerfolgreiche Überweisung von Geld zwischen zwei Konten.
     *
     * @throws GesperrtException wenn das Konto gesperrt ist
     */
    @Test
    void geldUeberweisen_TransferUnsuccessful() throws GesperrtException {
        // Arrange
        when(mockKonto1.abheben(anyDouble())).thenReturn(false);

        // Act
        boolean transferSuccess = bank.geldUeberweisen(kontoNummer1, kontoNummer2, 100.0, "Überweisung");

        // Assert
        assertFalse(transferSuccess);
        verify(mockKonto1, times(1)).abheben(100.0);
        verify(mockKonto2, never()).einzahlen(anyDouble());
    }

    /**
     * Testet die Überweisung von Geld zwischen zwei Konten, trotzt wenn eine falsche kontonummer  gibt oder existieren nicht
     *
     * @throws GesperrtException wenn das Konto gesperrt ist
     */

    @Test
    void geldUeberweisen_NonExistentAccount() throws GesperrtException {
        // Arrange
        when(mockKonto1.abheben(anyDouble())).thenReturn(true);

        // Act
        boolean transferSuccess = bank.geldUeberweisen(kontoNummer1, 999, 100.0, "Überweisung");

        // Assert
        assertFalse(transferSuccess);
        verify(mockKonto1, never()).abheben(100.0);  // sicherstellen, dass abheben nicht aufgerufen wird
        verify(mockKonto2, never()).einzahlen(anyDouble());
    }

    /**
     * Testet die Überweisung von Geld zwischen zwei Konten. wenn es ist mehr als aktuelle kontostand
     *
     * @throws GesperrtException wenn das Konto gesperrt ist
     */
    @Test
    void geldUeberweisen_NotEnoughFunds() throws GesperrtException {
        // Arrange
        double konto1Kontostand = 500.0;
        double ueberweisungsBetrag = 1000.0;

        when(mockKonto1.abheben(ueberweisungsBetrag)).thenReturn(false);
        when(mockKonto1.getKontostand()).thenReturn(konto1Kontostand);

        // Act
        boolean transferSuccess = bank.geldUeberweisen(kontoNummer1, kontoNummer2, ueberweisungsBetrag, "Überweisung");

        // Assert
        assertFalse(transferSuccess, "Die Überweisung sollte fehlschlagen, wenn das Konto nicht genügend Guthaben hat.");
        verify(mockKonto1, times(1)).abheben(ueberweisungsBetrag);
        verify(mockKonto2, never()).einzahlen(anyDouble());
    }

    /**
     * Testet das Löschen eines Kontos.
     */
    @Test
    void kontoLoeschen_AccountDeleted() {
        // Act
        boolean deletionSuccess = bank.kontoLoeschen(kontoNummer3);

        // Assert
        assertTrue(deletionSuccess);
    }

    /**
     * Testet das Löschen eines Kontos von der liste . falls es eine falsche nummer gibt
     */

    @Test
    void kontoLoeschen_NonExistentAccount() {
        // Act
        boolean deletionSuccess = bank.kontoLoeschen(1234);

        // Assert
        assertFalse(deletionSuccess);
    }


}
