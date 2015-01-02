package net.iturrioz.marranita.domain;

import android.test.AndroidTestCase;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static net.iturrioz.marranita.domain.TestSupport.getJokalarik;
import static net.iturrioz.marranita.domain.TestSupport.getJokalarikWithKartak;

public class PartidaTest extends AndroidTestCase {

    public void testPartidaPersist() throws UnsupportedEncodingException, JSONException {
        final ArrayList<String> jokalarik = getJokalarik();
        final Partida partida = new Partida(jokalarik);

        final byte[] persisted = partida.persist();

        final Partida loaded = Partida.unpersist(persisted, jokalarik);

        assertTrue(loaded.equals(partida));
    }

    public void testBanatuKartak() {
        final ArrayList<String> jokalarik = getJokalarik();
        Partida partida = new Partida(jokalarik);

        // One less because the first one is done in the new Partida(jokalarik);
        for (int i=1; i<Eskue.kartaKopurua.length; i++) {
            partida = partida.banatuKartak();
            assertNotNull("Failed in the " + i + " iteration", partida);
            assertEquals(i, partida.getEskue().getTxanda());
            for (final Jokalarie jokalarie : partida.getJokalarik()) {
                assertEquals(Eskue.kartaKopurua[i], jokalarie.getKartak().length);
            }
        }
        assertNull(partida.banatuKartak());
    }

    public void testBazaIrabazlea() {
        final Jokalarie[] jokalarik = getJokalarikWithKartak();
        for (int i=0; i<0; i++) {
            assertEquals(jokalarik[0].getJokatutakoa(), jokatuBaza(jokalarik, Karta.Kolorea.URREA, i));
            assertEquals(jokalarik[4].getJokatutakoa(), jokatuBaza(jokalarik, Karta.Kolorea.KOPA, i));
            assertEquals(jokalarik[3].getJokatutakoa(), jokatuBaza(jokalarik, Karta.Kolorea.EZPATA, i));
            assertEquals(jokalarik[2].getJokatutakoa(), jokatuBaza(jokalarik, Karta.Kolorea.BASTOA, i));
        }
    }

    private Jokalarie jokatuBaza(final Jokalarie[] jokalarik, final Karta.Kolorea kolorea, final int id) {
        final Eskue eskue = new Eskue(0,new Karta(0, Karta.Kolorea.EZPATA), jokalarik[0].getId());
        final Partida partida = new Partida(jokalarik, 0, eskue);
        return partida.bazaIrabazlea(jokalarik[id]);
    }
}
