package net.iturrioz.marranita.domain;

import android.test.AndroidTestCase;

import org.json.JSONException;

import java.util.Arrays;

import static net.iturrioz.marranita.domain.TestSupport.getKartak;

public class JokalarieTest extends AndroidTestCase {

    private Jokalarie testJokalarie;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        testJokalarie = new Jokalarie("id", 5);
    }

    public void testJokalariePersist() throws JSONException {

        final String json = testJokalarie.getJsonString();
        final Jokalarie loaded = new Jokalarie(json);

        assertEquals(testJokalarie, loaded);
    }

    public void testSetKartak() {
        final Karta[] expectedKartak = getKartak();
        testJokalarie.setKartak(expectedKartak);
        assertEquals(expectedKartak, testJokalarie.getKartak());
    }

    public void testKartaJokatu() {
        final Karta[] initialKartak = getKartak();
        testJokalarie.setKartak(initialKartak);
        final Karta[] expectedKartak = Arrays.copyOfRange(initialKartak, 0, 2);
        testJokalarie.kartaJokatu(2);
        testJokalarie.kartaJokatu(2);
        final Karta[] finalKartak = testJokalarie.getKartak();

        assertEquals(2, finalKartak.length);
        assertTrue(Arrays.equals(expectedKartak, finalKartak));
    }

    public void testPuntuaketa() {
        // Bi ondo
        testJokalarie.setEskatutakok(2);
        testJokalarie.bazaGehitu();
        testJokalarie.bazaGehitu();
        testJokalarie.puntukGehitu(false);
        assertEquals(25, testJokalarie.getPuntuk());

        // Hiru ondo urrek
        testJokalarie.setKartak(new Karta[0]);
        testJokalarie.setEskatutakok(3);
        testJokalarie.bazaGehitu();
        testJokalarie.bazaGehitu();
        testJokalarie.bazaGehitu();
        testJokalarie.puntukGehitu(true);
        assertEquals(75, testJokalarie.getPuntuk());

        // Bat gaizki
        testJokalarie.setKartak(new Karta[0]);
        testJokalarie.setEskatutakok(3);
        testJokalarie.bazaGehitu();
        testJokalarie.bazaGehitu();
        testJokalarie.puntukGehitu(false);
        assertEquals(70, testJokalarie.getPuntuk());

        // Lau gaizki urrek
        testJokalarie.setKartak(new Karta[0]);
        testJokalarie.setEskatutakok(8);
        testJokalarie.bazaGehitu();
        testJokalarie.bazaGehitu();
        testJokalarie.bazaGehitu();
        testJokalarie.bazaGehitu();
        testJokalarie.puntukGehitu(true);
        assertEquals(30, testJokalarie.getPuntuk());
    }
}
