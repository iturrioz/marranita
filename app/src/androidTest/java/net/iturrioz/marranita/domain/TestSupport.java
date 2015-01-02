package net.iturrioz.marranita.domain;

import java.util.ArrayList;

import static net.iturrioz.marranita.domain.Partida.maptoJokalarieArray;

public class TestSupport {

    static ArrayList<String> getJokalarik() {
        final ArrayList<String> jokalarik = new ArrayList<String>();
        jokalarik.add("P1");
        jokalarik.add("P2");
        jokalarik.add("P3");
        jokalarik.add("P4");
        jokalarik.add("P5");
        return jokalarik;
    }

    static Karta[] getKartak() {
        return new Karta[]{
                new Karta(7, Karta.Kolorea.URREA),
                new Karta(4, Karta.Kolorea.EZPATA),
                new Karta(7, Karta.Kolorea.EZPATA),
                new Karta(3, Karta.Kolorea.BASTOA),
        };
    }

    private static Karta[] getJokatzekoKartak() {
        return new Karta[]{
                new Karta(5, Karta.Kolorea.URREA),
                new Karta(3, Karta.Kolorea.EZPATA),
                new Karta(8, Karta.Kolorea.BASTOA),
                new Karta(5, Karta.Kolorea.EZPATA),
                new Karta(3, Karta.Kolorea.KOPA)
        };
    }

    static Jokalarie[] getJokalarikWithKartak() {
        final Jokalarie[] jokalarik = maptoJokalarieArray(getJokalarik());
        final Karta[] jokatzekoKartak = getJokatzekoKartak();
        for (int i=0; i<5; i++) {
            jokalarik[i].setKartak(new Karta[]{jokatzekoKartak[i]});
        }
        return jokalarik;
    }
}
