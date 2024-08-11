package bankprojekt.Observer;

import bankprojekt.verarbeitung.Konto;

public interface KontoObserver {
    void update(Konto konto);
}
