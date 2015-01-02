package net.iturrioz.marranita.domain;

import android.test.AndroidTestCase;

import java.util.Arrays;

import static net.iturrioz.marranita.domain.TestSupport.getJokalarikWithKartak;
import static net.iturrioz.marranita.domain.TestSupport.getKartak;

public class PartidaGetJokatzekoTest extends AndroidTestCase {

    private Karta[] nereKartak;
    private Jokalarie[] jokalarik;
    private Eskue eskue;
    private Partida partida;

    private Karta[] expected;
    private Karta[] jokatzeko;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        nereKartak = getKartak();
    }

    private Karta[] getJokatzeko(final int id,
                                 final Karta.Kolorea kolorea,
                                 final int... jokatuta) {
        // Setup
        jokalarik = getJokalarikWithKartak();
        eskue = new Eskue(0,new Karta(0, kolorea), jokalarik[id].getId());
        partida = new Partida(jokalarik, 0, eskue);

        // Banatu kartak
        jokalarik[id].setKartak(nereKartak);

        // Nere aurrekok jokatu
        for (final int jokatu : jokatuta) {
            jokalarik[jokatu].kartaJokatu(0);
        }

        // Jokatzeko kartak aukeratu
        return partida.getJokatzeko(jokalarik[id]);
    }

    //////////////////////////////
    // 1. Kolorea triunfoa bada //
    //////////////////////////////
    public void testTriunfoHaundigokFiltratu() throws Exception {
        jokatzeko = getJokatzeko(0, Karta.Kolorea.EZPATA, 1, 2, 3, 4);
        expected = new Karta[]{nereKartak[2]};
        assertTrue(Arrays.equals(expected, jokatzeko));

        jokatzeko = getJokatzeko(2, Karta.Kolorea.EZPATA, 1);
        expected = new Karta[]{nereKartak[1], nereKartak[2]};
        assertTrue(Arrays.equals(expected, jokatzeko));
    }

    public void testBestelaTriunfokFiltratu() throws Exception {
        jokatzeko = getJokatzeko(3, Karta.Kolorea.BASTOA, 2);
        expected = new Karta[]{nereKartak[3]};
        assertTrue(Arrays.equals(expected, jokatzeko));

        jokatzeko = getJokatzeko(0, Karta.Kolorea.BASTOA, 2, 3, 4);
        expected = new Karta[]{nereKartak[3]};
        assertTrue(Arrays.equals(expected, jokatzeko));

        jokatzeko = getJokatzeko(1, Karta.Kolorea.BASTOA, 2, 3, 4, 0);
        expected = new Karta[]{nereKartak[3]};
        assertTrue(Arrays.equals(expected, jokatzeko));
    }

    public void testTriunfoikGabeEdozeinKarta() throws Exception {
        jokatzeko = getJokatzeko(0, Karta.Kolorea.KOPA, 4);
        expected = nereKartak;
        assertTrue(Arrays.equals(expected, jokatzeko));
    }

    ///////////////////////////////////
    // 2. Kolorea triunfoa ez bada   //
    //   2.1 kolorekoik euki ezkeo   //
    //      2.1.1 triunfoik ez badao //
    ///////////////////////////////////
    public void testKolorekoHaundigokFiltratu() throws Exception {
        jokatzeko = getJokatzeko(1, Karta.Kolorea.BASTOA, 0);
        expected = new Karta[]{nereKartak[0]};
        assertTrue(Arrays.equals(expected, jokatzeko));

        jokatzeko = getJokatzeko(3, Karta.Kolorea.BASTOA, 0, 1, 2);
        expected = new Karta[]{nereKartak[0]};
        assertTrue(Arrays.equals(expected, jokatzeko));
    }

    public void testKolorekokFiltratu() throws Exception {
        jokatzeko = getJokatzeko(3, Karta.Kolorea.EZPATA, 2);
        expected = new Karta[]{nereKartak[3]};
        assertTrue(Arrays.equals(expected, jokatzeko));

        jokatzeko = getJokatzeko(1, Karta.Kolorea.EZPATA, 2, 3, 4, 0);
        expected = new Karta[]{nereKartak[3]};
        assertTrue(Arrays.equals(expected, jokatzeko));
    }
    /////////////////////////////////////////
    //      2.1.2 triunfoik badao          //
    //        2.1.2.1 kolorekok euki ezkeo //
    /////////////////////////////////////////
    public void testKolorekoEdozeinFiltratu() throws Exception {
        jokatzeko = getJokatzeko(2, Karta.Kolorea.EZPATA, 0, 1);
        expected = new Karta[]{nereKartak[0]};
        assertTrue(Arrays.equals(expected, jokatzeko));

        jokatzeko = getJokatzeko(1, Karta.Kolorea.EZPATA, 2, 3, 4, 0);
        expected = new Karta[]{nereKartak[3]};
        assertTrue(Arrays.equals(expected, jokatzeko));

        jokatzeko = getJokatzeko(0, Karta.Kolorea.KOPA, 1, 2, 3, 4);
        expected = new Karta[]{nereKartak[1], nereKartak[2]};
        assertTrue(Arrays.equals(expected, jokatzeko));

        jokatzeko = getJokatzeko(3, Karta.Kolorea.BASTOA, 1, 2);
        expected = new Karta[]{nereKartak[1], nereKartak[2]};
        assertTrue(Arrays.equals(expected, jokatzeko));
    }

    /////////////////////////////////////////
    //        2.1.2.2 kolorekoik euki gabe //
    /////////////////////////////////////////
    public void testKolorekoikEdoTriunfoHaundigoikGabe() throws Exception {
        jokatzeko = getJokatzeko(3, Karta.Kolorea.BASTOA, 4, 0, 1, 2);
        expected = nereKartak;
        assertTrue(Arrays.equals(expected, jokatzeko));
    }

    /////////////////////////////////////
    //   2.2 kolorekoik euki gabe      //
    //     2.2.1 triunfoik ez badao:    //
    //       2.2.1.1 triunfok filtratu //
    /////////////////////////////////////
    public void testKolorekoikGabeTriunfokFiltratu() throws Exception {
        jokatzeko = getJokatzeko(2, Karta.Kolorea.BASTOA, 4, 0, 1);
        expected = new Karta[]{nereKartak[3]};
        assertTrue(Arrays.equals(expected, jokatzeko));

        jokatzeko = getJokatzeko(1, Karta.Kolorea.EZPATA, 4, 0);
        expected = new Karta[]{nereKartak[1], nereKartak[2]};
        assertTrue(Arrays.equals(expected, jokatzeko));

        jokatzeko = getJokatzeko(0, Karta.Kolorea.URREA, 4);
        expected = new Karta[]{nereKartak[0]};
        assertTrue(Arrays.equals(expected, jokatzeko));
    }

    /////////////////////////////////////
    //       2.2.1.2 edozein karta //
    /////////////////////////////////////
    public void testKolorekoikEdoTriunfoikGabe() throws Exception {
        nereKartak = new Karta[]{nereKartak[1], nereKartak[2], nereKartak[3]};
        jokatzeko = getJokatzeko(0, Karta.Kolorea.URREA, 4);
        expected = nereKartak;
        assertTrue(Arrays.equals(expected, jokatzeko));

        jokatzeko = getJokatzeko(3, Karta.Kolorea.URREA, 4, 0, 1, 2);
        expected = nereKartak;
        assertTrue(Arrays.equals(expected, jokatzeko));
    }

    //////////////////////////////////////////////
    //     2.2.2 triunfoik badao:               //
    //       2.2.2.1 triunfo haundigok filtratu //
    //////////////////////////////////////////////
    public void testKolorekoikGabeTriunfoHaundigokFiltratu() throws Exception {
        jokatzeko = getJokatzeko(1, Karta.Kolorea.URREA, 4, 0);
        expected = new Karta[]{nereKartak[0]};
        assertTrue(Arrays.equals(expected, jokatzeko));

        jokatzeko = getJokatzeko(3, Karta.Kolorea.EZPATA, 4, 0, 1, 2);
        expected = new Karta[]{nereKartak[1], nereKartak[2]};
        assertTrue(Arrays.equals(expected, jokatzeko));
    }
}
