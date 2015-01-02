package net.iturrioz.marranita.domain;

import android.test.AndroidTestCase;

import org.json.JSONException;

import java.util.List;

public class KartaTest extends AndroidTestCase {

    private final int TEST_BALIOA = 3;
    private final Karta TEST_KARTA = new Karta(TEST_BALIOA, Karta.Kolorea.BASTOA);

    public void testKartaPersist() throws JSONException {
        final String json = TEST_KARTA.getJsonString();
        final Karta loaded = new Karta(json);

        assertEquals(TEST_KARTA, loaded);
    }

    public void testKartaDrawableId() {
        assertEquals(Karta.bastok[TEST_BALIOA], Karta.getKartaDrawableId(TEST_KARTA));
    }

    public void testKartaIdArray() {
        assertEquals(Karta.urrek, Karta.getKartaIdArray(Karta.Kolorea.URREA));
        assertEquals(Karta.kopak, Karta.getKartaIdArray(Karta.Kolorea.KOPA));
        assertEquals(Karta.ezpatak, Karta.getKartaIdArray(Karta.Kolorea.EZPATA));
        assertEquals(Karta.bastok, Karta.getKartaIdArray(Karta.Kolorea.BASTOA));
    }

    public void testGetBaraja() {
        final List<Karta> kartak1 = Karta.getBaraja();
        final List<Karta> kartak2 = Karta.getBaraja();
        assertEquals(40, kartak1.size());

        boolean difference = false;
        for (int i=0; i<40; i++) {
            difference = difference || !kartak1.get(0).equals(kartak2.get(0));
        }
        assertTrue(difference);
    }
}
