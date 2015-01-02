package net.iturrioz.marranita;

import android.view.View;
import android.widget.ImageView;

import net.iturrioz.marranita.domain.Eskue;
import net.iturrioz.marranita.domain.Jokalarie;
import net.iturrioz.marranita.domain.Karta;
import net.iturrioz.marranita.domain.Partida;

import java.util.ArrayList;
import java.util.Arrays;

public class TapeteaInput {

    final MarranitaActivity activity;

    TapeteaInput(final MarranitaActivity activity) {
        this.activity = activity;
    }

    private View findViewById(final int id) {
        return activity.findViewById(id);
    }

    private void disableEskatu() {
        for (int i = 0; i < 9; i++) {
            findViewById(Eskue.eskatuIdk[i]).setEnabled(false);
        }
    }

    private void disableJokatu() {
        for (int i = 0; i < 8; i++) {
            findViewById(Eskue.kartaIdk[i]).setEnabled(false);
        }
    }

    void setKartaListeners() {
        for (int i = 0; i < 8; i++) {
            final int kartaZenbakie = i;
            final View view = findViewById(Eskue.kartaIdk[kartaZenbakie]);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    disableJokatu();
                    final int nerePosizioa = activity.getNerePosizioa();
                    final Jokalarie ni = activity.partida.getJokalarik()[nerePosizioa];
                    ni.kartaJokatu(kartaZenbakie);
                    activity.jokatuTxandaBukatu(nerePosizioa);
                }
            });
            view.setEnabled(false);
        }

        for (int i = 0; i < 9; i++) {
            final int eskatutakok = i;
            final View view = findViewById(Eskue.eskatuIdk[eskatutakok]);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    disableEskatu();
                    findViewById(R.id.eskatu_layout).setVisibility(View.GONE);
                    final int nerePosizioa = activity.getNerePosizioa();
                    final Jokalarie ni = activity.partida.getJokalarik()[nerePosizioa];
                    ni.setEskatutakok(eskatutakok);
                    activity.txandaBukatu(null);
                }
            });
            view.setEnabled(false);
        }
    }

    public void setListeners(final ArrayList<String> participantIds, final String currentPlayerId) {
        final int nerePosizioa = participantIds.indexOf(currentPlayerId);
        final Jokalarie ni = activity.partida.getJokalarik()[nerePosizioa];
        if (ni.getEskatutakok() < 0) {
            findViewById(R.id.eskatu_layout).setVisibility(View.VISIBLE);
            setEskatuListeners(ni);
        } else {
            setJokatuListeners(ni);
        }
    }

    private void setEskatuListeners(final Jokalarie ni) {
        final Partida partida = activity.partida;
        final int kartaKopurua = Eskue.kartaKopurua[partida.getEskue().getTxanda()];
        int ezin = kartaKopurua;
        for (int i = 0; i < 5; i++) {
            final Jokalarie jokalarie = partida.getJokalarik()[i];
            if (jokalarie != ni) {
                final int bazak = jokalarie.getEskatutakok();
                if (bazak < 0) {
                    ezin = 1000;
                } else {
                    ezin -= bazak;
                }
            }
        }
        for (int i = 0; i <= 8; i++) {
            final View view = findViewById(Eskue.eskatuIdk[i]);
            if (i <= kartaKopurua && i != ezin) {
                view.setVisibility(View.VISIBLE);
                view.setEnabled(true);
            } else {
                view.setVisibility(View.GONE);
                view.setEnabled(false);
            }
        }
    }

    private void setJokatuListeners(final Jokalarie ni) {
        final Karta[] kartak = ni.getKartak();
        final Karta[] jokatzeko = activity.partida.getJokatzeko(ni);
        for (int i = 0; i < 8; i++) {
            final ImageView imageView = (ImageView) findViewById(Eskue.kartaIdk[i]);
            if (i < kartak.length) {
                imageView.setVisibility(View.VISIBLE);
                if (Arrays.asList(jokatzeko).contains(kartak[i])) {
                    //noinspection deprecation
                    imageView.setAlpha(255);
                    imageView.setEnabled(true);
                } else {
                    //noinspection deprecation
                    imageView.setAlpha(128);
                    imageView.setEnabled(false);
                }
            }
        }
    }
}
