package generischeKlasse;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class HauptProgramm {
    public static void main(String[] args) {
        // intervall von strings
        Intervall<String> stringIntervall = new Intervall<>("Apfel", "Birne");
        System.out.println("string intervall ist leer: " + stringIntervall.isLeer());
        System.out.println("string intervall enthält 'banane': " + stringIntervall.enthaelt("Banane"));

        // intervall von date
        Calendar cal = Calendar.getInstance();
        cal.set(2000, Calendar.JANUARY, 11);
        Date start = cal.getTime();
        cal.set(2001, Calendar.MAY, 3);
        Date ende = cal.getTime();
        Intervall<Date> dateIntervall1 = new Intervall<>(start, ende);

        cal.set(2000, Calendar.MARCH, 1);
        Date start2 = cal.getTime();
        cal.set(2000, Calendar.DECEMBER, 31);
        Date ende2 = cal.getTime();
        Intervall<Date> dateIntervall2 = new Intervall<>(start2, ende2);

        // schnittintervall von date
        Intervall<Date> schnittDate = dateIntervall1.schnitt(dateIntervall2);
        if (schnittDate != null) {
            System.out.println("schnittintervall von date ist nicht leer");
        } else {
            System.out.println("schnittintervall von date ist leer");
        }

        // intervall von time
        Time timeStart = new Time(start.getTime());
        Time timeEnd = new Time(ende.getTime());
        //  Intervall<Time> timeIntervall = new Intervall<>(timeStart, timeEnd);

        // timestamp überprüfung
        Timestamp timestamp = new Timestamp(new Date().getTime());
        // System.out.println("timestamp ist im time-intervall: " + timeIntervall.enthaelt(timestamp));

        // fehlversuche
        try {
            //   Intervall<Object> falsch = new Intervall<>(new Object(), new Object());
            System.out.println("intervall<object> erstellt ohne fehler");
        } catch (Exception e) {
            System.out.println("erwarteter fehler bei intervall<object>: " + e.getMessage());
        }

        try {
            System.out.println("intervall<string> enthält 3.5: " + stringIntervall.enthaelt(3.5));
        } catch (Exception e) {
            System.out.println("erwarteter fehler bei intervall<string> und enthaelt(3.5): " + e.getMessage());
        }

        try {
            Intervall<Double> zahlen = new Intervall<>(1.2, 3.4);
            Intervall<String> texte = new Intervall<>("a", "b");
            zahlen.schnitt(texte);
            System.out.println("intervall<double> mit intervall<string> geschnitten ohne fehler");
        } catch (Exception e) {
            System.out.println("erwarteter fehler beim versuch intervall<double> mit intervall<string> zu schneiden: " + e.getMessage());
        }
    }
}
