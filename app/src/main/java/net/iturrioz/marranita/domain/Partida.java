package net.iturrioz.marranita.domain;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.*;

public class Partida {

    public static final String TAG = "EBTurn";

    private static final String JSON_JOKALARIK = "jokalarik";
    private static final String JSON_ESKUE = "eskue";
    private static final String JSON_AURRENEKOA = "EBTurn";
    private static final String JSON_FINISHED = "finished";

    private final Jokalarie[] jokalarik;
    private Eskue eskue;

    private final int aurrenekoa;

    private boolean finished = false;

    public Partida(final List<String> jokalariArrayList) {
        this(jokalariArrayList,new Random().nextInt(5));
    }

    public Partida(final List<String> jokalariArrayList, final int aurrenekoa) {
        this(maptoJokalarieArray(jokalariArrayList), aurrenekoa);
    }

    public Partida(final Jokalarie[] jokalarik, final int aurrenekoa) {
        this.jokalarik = jokalarik;
        this.aurrenekoa = aurrenekoa;
        banatuKartak(0);
    }

    Partida(final Jokalarie[] jokalarik, final int aurrenekoa, final Eskue eskue) {
        this.jokalarik = jokalarik;
        this.aurrenekoa = aurrenekoa;
        this.eskue = eskue;
    }


    public Jokalarie[] getJokalarik() {
        return jokalarik;
    }

    public Eskue getEskue() {
        return eskue;
    }

    public int getAurrenekoa() {
        return aurrenekoa;
    }

    // TODO: check this in MarranitaActivity
    public boolean isFinished() {
        return finished;
    }

    public Partida banatuKartak() {
        final int txanda = eskue.getTxanda() + 1;
        if (txanda == Eskue.kartaKopurua.length) {
            return null;
        }
        return banatuKartak(txanda);
    }

    private Partida banatuKartak(final int txanda) {
        final int kartaKopurua = Eskue.kartaKopurua[txanda];
        final List<Karta> list = Karta.getBaraja();
        final Karta[] baraja =  list.toArray(new Karta[list.size()]);
        final Karta hurrengoa = kartaKopurua != 8 ? baraja[kartaKopurua * 5] : null;
        for (int i = 0; i < 5; i++) {
            final List<Karta> kartak = new ArrayList<Karta>();
            final int lehenKarta = i * kartaKopurua;
            final List<Karta> kartaList = Arrays.asList(baraja);
            final List<Karta> kartaSubList = kartaList.subList(lehenKarta, lehenKarta + kartaKopurua);
            kartak.addAll(kartaSubList);
            final Karta[] array =  kartak.toArray(new Karta[kartak.size()]);
            jokalarik[i].setKartak(array);
        }
        final Jokalarie jokalarie = jokalarik[(txanda + aurrenekoa) % 5];
        eskue = new Eskue(txanda, Eskue.triunfoaAukeratu(txanda, hurrengoa),jokalarie.id);
        return this;
    }

    // This is the byte array we will write out to the TBMP API.
    public byte[] persist() {
        final JSONObject retVal = new JSONObject();
        try {
            final List<String> jokalarikJson = new ArrayList<String>();
            for (Jokalarie jokalarie : jokalarik) {
                jokalarikJson.add(jokalarie.getJsonString());
            }
            final JSONArray jsonArray = new JSONArray(jokalarikJson);
            retVal.put(JSON_JOKALARIK, jsonArray);
            retVal.put(JSON_ESKUE, eskue.getJsonString());
            retVal.put(JSON_AURRENEKOA, aurrenekoa);
            retVal.put(JSON_FINISHED, finished);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String st = retVal.toString();

        Log.e(TAG, "==== PERSISTING\n" + st);

        return st.getBytes(Charset.forName("UTF-16"));
    }

    // Creates a new instance of Partida.
    static public Partida unpersist(final byte[] byteArray, final List<String> jokalariArrayList) {

        if (byteArray == null) {
            Log.d(TAG, "Empty array---possible bug.");
            return new Partida(jokalariArrayList);
        }

        final String st;
        try {
            st = new String(byteArray, "UTF-16");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }

        Log.d(TAG, "====UNPERSIST \n" + st);

        Partida retVal = null;

        try {
            final JSONObject obj = new JSONObject(st);
            final JSONArray jsonArray = obj.getJSONArray(JSON_JOKALARIK);
            final Jokalarie[] jokalarik = new Jokalarie[5];
            for (int i = 0; i < 5; i++) {
                jokalarik[i] = new Jokalarie(jsonArray.getString(i));
            }
            final Eskue eskue = new Eskue(obj.getString(JSON_ESKUE));
            final int aurrenekoa = obj.getInt(JSON_AURRENEKOA);
            final ArrayList<Jokalarie> jokalarieArrayList = new ArrayList<Jokalarie>(Arrays.asList(jokalarik));
            retVal = new Partida(jokalarieArrayList.toArray(new Jokalarie[5]),aurrenekoa, eskue);
            retVal.finished = obj.getBoolean(JSON_FINISHED);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return retVal;
    }

    static Jokalarie[] maptoJokalarieArray(final List<String> jokalariArrayList) {
        final Jokalarie[] jokalarik = new Jokalarie[5];
        for (int i = 0; i < 5; i++) {
            jokalarik[i] = new Jokalarie(jokalariArrayList.get(i), 0);
        }
        return jokalarik;
    }

    public Karta[] getJokatzeko(final Jokalarie ni) {
        Karta aurrenekoa = null;
        for (Jokalarie jokalarie : jokalarik) {
            if (jokalarie.getJokatutakoa() != null){
                aurrenekoa = jokalarie.getJokatutakoa();
            }
        }
        if (aurrenekoa == null) {
            return ni.getKartak();
        }

        for (int i = 0; i < 5; i++) {
            if (jokalarik[i].getJokatutakoa() == null) {
                final int hurrengoa = i < 4 ? i + 1 : 0;
                if (jokalarik[hurrengoa].getJokatutakoa() != null) {
                    aurrenekoa = jokalarik[hurrengoa].getJokatutakoa();
                }
            }
        }

        final Karta.Kolorea kolorea = aurrenekoa.getKolorea();
        final Karta.Kolorea triunfoa = getEskue().getTriunfoa().getKolorea();

        final List<Karta> mahaikoTriunfok = new ArrayList<Karta>();
        for (Jokalarie jokalarie : jokalarik) {
            if (jokalarie.getJokatutakoa() != null &&
                jokalarie.getJokatutakoa().getKolorea() == eskue.getTriunfoa().getKolorea()){
                    mahaikoTriunfok.add(jokalarie.getJokatutakoa());
            }
        }

        final List<Karta> kolorekok = new ArrayList<Karta>();
        final List<Karta> triunfok = new ArrayList<Karta>();
        for (Karta karta : ni.getKartak()) {
            if (karta.getKolorea() == kolorea) {
                kolorekok.add(karta);
            }
            if (karta.getKolorea() == triunfoa) {
                triunfok.add(karta);
            }
        }

        Karta haundina = aurrenekoa;
        for (Jokalarie jokalarie : jokalarik) {
            haundina = getHaundina(kolorea, triunfoa, haundina, jokalarie.getJokatutakoa());
        }


        /*
            Kolorea triunfoa bada
                triunfo haundigok filtratu
                bestela triunfok filtratu
                bestela edozein karta
            bestela
                kolorekoik euki ezkeo
                    triunfoik ez badao:
                        haundigok filtratu
                        bestela koloreko edozein
                    bestela koloreko edozein
                bestela
                    triunfoik ez badao:
                        triunfok filtratu
                        bestela edozein karta
                    bestela
                        triunfo haundigok filtratu
                        bestela edozein karta

         */
        // Kolorea triunfoa bada
        if (kolorea == triunfoa) {
            // triunfo haundigok filtratu
            final List<Karta> triunfoHaundigok = new ArrayList<Karta>();
            for (Karta nereTriunfoa : triunfok) {
                final Karta bitanHaundina = getHaundina(kolorea, triunfoa, haundina, nereTriunfoa);
                if (bitanHaundina != haundina) {
                    triunfoHaundigok.add(nereTriunfoa);
                }
            }
            // triunfo haundigok filtratu
            if (!triunfoHaundigok.isEmpty()) {
                return triunfoHaundigok.toArray(new Karta[triunfoHaundigok.size()]);
            } else {
                // bestela triunfok filtratu
                if (!triunfok.isEmpty()) {
                    return triunfok.toArray(new Karta[triunfok.size()]);
                } else {
                    // bestela edozein karta
                    return ni.getKartak();
                }
            }
        } else {
            // kolorekoik euki ezkeo
            if (!kolorekok.isEmpty()) {
                // triunfoik ez badao
                if (mahaikoTriunfok.isEmpty()) {
                    final List<Karta> kartak = new ArrayList<Karta>();
                    for (Karta kolorekoa : kolorekok) {
                        final Karta bitanHaundina = getHaundina(kolorea, triunfoa, haundina, kolorekoa);
                        if (bitanHaundina != haundina) {
                            kartak.add(kolorekoa);
                        }
                    }
                    // haundigok filtratu
                    if (!kartak.isEmpty()) {
                        return kartak.toArray(new Karta[kartak.size()]);
                    } else {
                        return kolorekok.toArray(new Karta[kolorekok.size()]);
                    }
                } else {
                    // bestela koloreko edozein
                    return kolorekok.toArray(new Karta[kolorekok.size()]);
                }
            } else {
                // bestela
                // triunfoik ez badao
                if (mahaikoTriunfok.isEmpty()) {
                    // triunfok filtratu
                    if (!triunfok.isEmpty()) {
                        return triunfok.toArray(new Karta[triunfok.size()]);
                    } else {
                        // bestela edozein karta
                        return ni.getKartak();
                    }
                } else {
                    final List<Karta> triunfoHaundigok = new ArrayList<Karta>();
                    for (Karta nereTriunfoa : triunfok) {
                        final Karta bitanHaundina = getHaundina(kolorea, triunfoa, haundina, nereTriunfoa);
                        if (bitanHaundina != haundina) {
                            triunfoHaundigok.add(nereTriunfoa);
                        }
                    }
                    // triunfo haundigok filtratu
                    if (!triunfoHaundigok.isEmpty()) {
                        return triunfoHaundigok.toArray(new Karta[triunfoHaundigok.size()]);
                    } else {
                        return ni.getKartak();
                    }
                }
            }
        }
    }

    private static Karta getHaundina(final Karta.Kolorea kolorea, final Karta.Kolorea triunfoa,
                              final Karta haundina, final Karta hurrengoa) {
        if (hurrengoa == null) return haundina;
        if (haundina.getKolorea() == kolorea) {
            if (hurrengoa.getKolorea() == triunfoa) {
                if (kolorea == triunfoa) {
                    return hurrengoa.getBalioa() > haundina.getBalioa() ? hurrengoa : haundina;
                } else {
                    return hurrengoa;
                }
            } else {
                if (hurrengoa.getKolorea() == kolorea && hurrengoa.getBalioa() > haundina.getBalioa()) {
                    return hurrengoa;
                }
            }
        } else {
            if (hurrengoa.getKolorea() == triunfoa && hurrengoa.getBalioa() > haundina.getBalioa()) {
                return hurrengoa;
            }
        }
        return haundina;
    }

    public Jokalarie bazaIrabazlea(final Jokalarie aurrenekoa) {
        Jokalarie bazaIrabazlea = aurrenekoa;
        for (Jokalarie jokalarie : getJokalarik()) {
            final Karta bitanHaundina = getHaundina(aurrenekoa.getJokatutakoa().getKolorea(),
                    eskue.getTriunfoa().getKolorea(),
                    bazaIrabazlea.getJokatutakoa(),
                    jokalarie.getJokatutakoa());
            if (bitanHaundina != bazaIrabazlea.getJokatutakoa()) {
                bazaIrabazlea = jokalarie;
            }
        }
        return bazaIrabazlea;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Partida && equals((Partida)o);
    }

    public boolean equals(final Partida partida) {
        return Arrays.equals(jokalarik, partida.jokalarik) &&
               eskue.equals(partida.eskue) &&
               aurrenekoa == partida.aurrenekoa &&
               finished == partida.finished;
    }
}
