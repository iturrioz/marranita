package net.iturrioz.marranita.domain;

import net.iturrioz.marranita.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Eskue {

    private final String JSON_TXANDA = "txanda";
    private final String JSON_TRIUNFOA = "triunfoa";
    private final String JSON_JOKALARIA = "jokalaria";

    private final int txanda;
    private final Karta triunfoa;
    private final String jokalaria;

    public Eskue(int txanda, final Karta triunfoa, final String jokalaria) {
        this.txanda = txanda;
        this.triunfoa = triunfoa;
        this.jokalaria = jokalaria;
    }

    public Eskue(final String json) throws JSONException {
        final JSONObject obj = new JSONObject(json);
        this.txanda = obj.getInt(JSON_TXANDA);
        this.triunfoa = new Karta(obj.getString(JSON_TRIUNFOA));
        this.jokalaria = obj.getString(JSON_JOKALARIA);
    }

    public Karta getTriunfoa() {
        return triunfoa;
    }

    public int getTxanda() {
        return txanda;
    }

    public boolean isUrrek() {
        return triunfoa.getKolorea() == Karta.Kolorea.URREA;
    }

    public String getJsonString() throws JSONException {
        final JSONObject retVal = new JSONObject();
        retVal.put(JSON_TXANDA, txanda);
        retVal.put(JSON_TRIUNFOA, triunfoa.getJsonString());
        retVal.put(JSON_JOKALARIA, jokalaria);
        return retVal.toString();
    }

    public static final int[] kartaKopurua = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 8, 8, 8, 7, 6, 5, 4, 3, 2, 1};
    static Karta triunfoaAukeratu(final int txanda, final Karta hurrengoa) {
        final Karta triunfoa;
        switch (txanda) {
            case 7: triunfoa = new Karta(10, Karta.Kolorea.URREA);
                break;
            case 8: triunfoa = new Karta(10, Karta.Kolorea.KOPA);
                break;
            case 9: triunfoa = new Karta(10, Karta.Kolorea.EZPATA);
                break;
            case 10: triunfoa = new Karta(10, Karta.Kolorea.BASTOA);
                break;
            default:
                triunfoa = hurrengoa;
        }
        return triunfoa;
    }

    public static final int[] kartaIdk = new int[] {
        R.id.karta_0, R.id.karta_1, R.id.karta_2, R.id.karta_3, R.id.karta_4, R.id.karta_5, R.id.karta_6, R.id.karta_7
    };
    public static final int[] eskatuIdk = new int[] {
            R.id.eskatu_0, R.id.eskatu_1, R.id.eskatu_2, R.id.eskatu_3, R.id.eskatu_4, R.id.eskatu_5, R.id.eskatu_6,
            R.id.eskatu_7, R.id.eskatu_8
    };

    @Override
    public boolean equals(final Object o) {
        return o instanceof Eskue && equals((Eskue) o);
    }

    public boolean equals(final Eskue eskue) {
        return eskue != null &&
               txanda == eskue.txanda &&
               triunfoa.equals(eskue.triunfoa) &&
               jokalaria.equals(eskue.jokalaria);
    }
}
