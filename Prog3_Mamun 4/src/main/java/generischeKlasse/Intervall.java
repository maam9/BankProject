package generischeKlasse;

// intervall klassse
public class Intervall<T extends Comparable<T>> {
    // untere grenze des intervalls
    private T untereGrenze;
    // obere grenze des intervalls
    private T obereGrenze;

    // konstruktur
    public Intervall(T untereGrenze, T obereGrenze) {
        this.untereGrenze = untereGrenze;
        this.obereGrenze = obereGrenze;
    }

    // prüft ob das intervall leer ist

    /**
     * @return
     */
    public boolean isLeer() {
        return obereGrenze.compareTo(untereGrenze) < 0;
    }

    // prüft ob wert im intervall enthalten ist
    public <T1> boolean enthaelt(T1 wert) {
        try {
            if (wert instanceof Comparable) {
                Comparable compWert = (Comparable) wert;
                return compWert.compareTo(untereGrenze) >= 0 && compWert.compareTo(obereGrenze) <= 0;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    // bildet ein schnittmengen-intervall mit einem anderen intervall eines typs T2
    public <T2 extends Comparable<T2>> Intervall<T> schnitt(Intervall<T2> anderes) {
        try {
            Comparable<? super T2> castUntere = (Comparable<? super T2>) untereGrenze;
            Comparable<? super T2> castObere = (Comparable<? super T2>) obereGrenze;

            T2 neueUntereGrenze = anderes.untereGrenze;
            T2 neueObereGrenze = anderes.obereGrenze;

            if (castUntere.compareTo(neueUntereGrenze) <= 0 && castObere.compareTo(neueObereGrenze) >= 0) {
                return this;
            }
        } catch (ClassCastException e) {
            return null;
        }
        return null;
    }
}
