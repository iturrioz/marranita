package net.iturrioz.marranita.domain;

import android.support.annotation.Nullable;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Jokalarie {

    private final String JSON_ID = "id";
    private final String JSON_PUNTUK = "puntuk";
    private final String JSON_KARTAK = "kartak";
    private final String JSON_ESKATUTAKOK = "eskatutakok";
    private final String JSON_BAZAK = "bazak";
    private final String JSON_JOKATUTAKOA = "jokatutakoa";

    final String id;

    private int puntuk;
    private Karta[] kartak = new Karta[0];
    private int eskatutakok = -1;
    private int bazak = 0;
    private Karta jokatutakoa;

    public Jokalarie(final String id, final int puntuk) {
        this.id = id;
        this.puntuk = puntuk;
    }

    public Jokalarie(final String json) throws JSONException {
        final JSONObject obj = new JSONObject(json);
        this.id = obj.getString(JSON_ID);
        puntuk = obj.getInt(JSON_PUNTUK);
        final JSONArray jsonArray = obj.getJSONArray(JSON_KARTAK);
        final Karta[] kartak = new Karta[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            kartak[i] = new Karta(jsonArray.getString(i));
        }
        this.kartak = kartak;
        eskatutakok = obj.getInt(JSON_ESKATUTAKOK);
        bazak = obj.getInt(JSON_BAZAK);
        final String jokatutakoaJson = obj.optString(JSON_JOKATUTAKOA, null);
        jokatutakoa = jokatutakoaJson != null ? new Karta(jokatutakoaJson) : null;
    }

    public String getJsonString() throws JSONException {
        final JSONObject retVal = new JSONObject();
        retVal.put(JSON_ID, id);
        retVal.put(JSON_PUNTUK, puntuk);
        final List<String> kartakJson = new ArrayList<String>();
        for (Karta karta : kartak) {
            kartakJson.add(karta.getJsonString());
        }
        final JSONArray jsonArray = new JSONArray(kartakJson);
        retVal.put(JSON_KARTAK, jsonArray);
        retVal.put(JSON_ESKATUTAKOK, eskatutakok);
        retVal.put(JSON_BAZAK, bazak);
        retVal.put(JSON_JOKATUTAKOA, jokatutakoa != null ? jokatutakoa.getJsonString() : null);
        return retVal.toString();
    }

    public int getPuntuk() {
        return puntuk;
    }

    public String getId() {
        return id;
    }

    public Karta[] getKartak() {
        return kartak;
    }

    public void setKartak(Karta[] kartak) {
        this.kartak = kartak;
        bazak = 0;
        eskatutakok = -1;
        jokatutakoa = null;
    }

    public Karta getJokatutakoa() {
        return jokatutakoa;
    }

    public int getEskatutakok() {
        return eskatutakok;
    }

    public void setEskatutakok(final int eskatutakok) {
        this.eskatutakok = eskatutakok;
    }

    public void kartaJokatu(final int KartaIndex) {
        final Karta jokatutakoKarta = kartak[KartaIndex];
        jokatutakoa = jokatutakoKarta;

        kartak = Collections2.filter(Arrays.asList(kartak), new Predicate<Karta>() {
            @Override
            public boolean apply(@Nullable final Karta karta) {
                return !jokatutakoKarta.equals(karta);
            }
        }).toArray(new Karta[0]);
    }

    public void bazaGehitu() {
        this.bazak += 1;
    }

    public void jokatutakoaKendu () {
        jokatutakoa = null;
    }

    public void puntukGehitu(final boolean isUrrek) {
        final int puntuk;
        if (bazak == eskatutakok) {
            puntuk = 10 + eskatutakok * 5;
        } else {
            puntuk = -5 * Math.abs(eskatutakok - bazak);
        }
        this.puntuk += puntuk * (isUrrek ? 2 : 1);
    }

    public String getBazaTextue() {
        return eskatutakok == -1 ? "" : bazak + " / " + eskatutakok;
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof Jokalarie && equals((Jokalarie)o);
    }

    public boolean equals(final Jokalarie jokalarie) {
        return jokalarie != null &&
               puntuk == jokalarie.puntuk &&
               Arrays.equals(kartak, jokalarie.kartak) &&
               eskatutakok == jokalarie.eskatutakok &&
               bazak == jokalarie.bazak &&
               ((jokatutakoa != null && jokatutakoa.equals(jokalarie.jokatutakoa)) ||
                       (jokatutakoa == null && jokatutakoa == jokalarie.jokatutakoa));
    }
}
