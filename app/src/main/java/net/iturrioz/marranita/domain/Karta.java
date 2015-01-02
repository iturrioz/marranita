package net.iturrioz.marranita.domain;

import net.iturrioz.marranita.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Karta {

    private final String JSON_BALIOA = "balioa";
    private final String JSON_KOLOREA = "kolorea";

    public enum Kolorea {
        URREA,
        KOPA,
        EZPATA,
        BASTOA
    }

    private final int balioa;
    private final Kolorea kolorea;

    public Karta(final int balioa, final Kolorea kolorea) {
        this.balioa = balioa;
        this.kolorea = kolorea;
    }

    public Karta(final String json) throws JSONException {
        final JSONObject obj = new JSONObject(json);
        this.balioa = obj.getInt(JSON_BALIOA);
        this.kolorea = Kolorea.valueOf(obj.getString(JSON_KOLOREA));
    }

    public int getBalioa() {
        return balioa;
    }

    public Kolorea getKolorea() {
        return kolorea;
    }

    public String getKoloreaName() {
        return kolorea.name();
    }

    public String getJsonString() throws JSONException {
        final JSONObject retVal = new JSONObject();
        retVal.put(JSON_BALIOA, balioa);
        retVal.put(JSON_KOLOREA, kolorea.name());
        return retVal.toString();
    }

    final static int[] urrek = new int[] {
            R.drawable.oros_2s,
            R.drawable.oros_4s,
            R.drawable.oros_5s,
            R.drawable.oros_6s,
            R.drawable.oros_7s,
            R.drawable.oros_10s,
            R.drawable.oros_11s,
            R.drawable.oros_12s,
            R.drawable.oros_3s,
            R.drawable.oros_1s,
            R.drawable.oros_triunfo
    };

    final static int[] kopak = new int[]{
            R.drawable.copas_2s,
            R.drawable.copas_4s,
            R.drawable.copas_5s,
            R.drawable.copas_6s,
            R.drawable.copas_7s,
            R.drawable.copas_10s,
            R.drawable.copas_11s,
            R.drawable.copas_12s,
            R.drawable.copas_3s,
            R.drawable.copas_1s,
            R.drawable.copas_triunfo
    };

    final static int[] ezpatak = new int[]{
            R.drawable.espadas_2s,
            R.drawable.espadas_4s,
            R.drawable.espadas_5s,
            R.drawable.espadas_6s,
            R.drawable.espadas_7s,
            R.drawable.espadas_10s,
            R.drawable.espadas_11s,
            R.drawable.espadas_12s,
            R.drawable.espadas_3s,
            R.drawable.espadas_1s,
            R.drawable.espadas_triunfo
    };

    final static int[] bastok = new int[]{
            R.drawable.bastos_2s,
            R.drawable.bastos_4s,
            R.drawable.bastos_5s,
            R.drawable.bastos_6s,
            R.drawable.bastos_7s,
            R.drawable.bastos_10s,
            R.drawable.bastos_11s,
            R.drawable.bastos_12s,
            R.drawable.bastos_3s,
            R.drawable.bastos_1s,
            R.drawable.bastos_triunfo
    };

    public static int getKartaDrawableId(final Karta karta) {
        return getKartaIdArray(karta.getKolorea())[karta.getBalioa()];
    }

    public static int[] getKartaIdArray(final Kolorea kolorea) {
        final int[] idArray;
        switch (kolorea) {
            case URREA:
                idArray = urrek;
                break;
            case KOPA:
                idArray = kopak;
                break;
            case EZPATA:
                idArray = ezpatak;
                break;
            case BASTOA:
                idArray = bastok;
                break;
            default:
                throw new IllegalStateException("Wrong Kolorea");
        }
        return idArray;

    }

    public static List<Karta> getBaraja() {
        final List<Karta> kartak = new ArrayList<Karta>();
        for (Kolorea kolorea : Kolorea.values()) {
            for (int i = 0; i < 10; i++) {
                kartak.add(new Karta(i, kolorea));
            }
        }
        Collections.shuffle(kartak);
        return kartak;
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof Karta && equals((Karta) o);
    }

    public boolean equals(final Karta karta) {
        return karta != null &&
               balioa == karta.balioa &&
               kolorea == karta.kolorea;
    }
}
