package net.iturrioz.marranita.domain;

import android.test.AndroidTestCase;

import org.json.JSONException;

public class EskueTest extends AndroidTestCase {

    public void testJokalariePersist() throws JSONException {
        final Eskue testEskue = new Eskue(0, new Karta(3, Karta.Kolorea.KOPA), "test_jokalarie");

        final String json = testEskue.getJsonString();
        final Eskue loaded = new Eskue(json);

        assertEquals(testEskue, loaded);
    }
}
