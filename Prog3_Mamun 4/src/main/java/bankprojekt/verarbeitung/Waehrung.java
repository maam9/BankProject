package bankprojekt.verarbeitung;

import org.decimal4j.util.DoubleRounder;

public enum Waehrung {
    EUR(1),
    ESCUDO(109.8269),
    Dobra(24304.7429),
    Francs(490.92);

    private final double umrechnungskurs;

    /**
     *
     * @param umrechnungskurs
     */
    Waehrung(double umrechnungskurs) {
        this.umrechnungskurs=umrechnungskurs;
    }


    /**
     * @param betrag
     * @return
     */
    public double euroInWaehrungUmrechnen(double betrag) {
        return DoubleRounder.round(betrag * this.umrechnungskurs, 2);

    }


    /**
     * @param betrag
     * @return
     */
    public double waehrungInEuroUmrechnen(double betrag){
        return DoubleRounder.round(betrag / this.umrechnungskurs, 2);
    }


}
