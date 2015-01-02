package net.iturrioz.marranita;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.iturrioz.marranita.domain.Eskue;
import net.iturrioz.marranita.domain.Jokalarie;
import net.iturrioz.marranita.domain.Karta;
import net.iturrioz.marranita.domain.Partida;

public class TapeteaUI {

    final MarranitaActivity activity;

    TapeteaUI(final MarranitaActivity activity) {
        this.activity = activity;
    }

    private View findViewById(final int id) {
        return activity.findViewById(id);
    }


    // Switch to gameplay view.
    public void setGameplayUI() {
        activity.setViewVisibility();

        final Partida partida = activity.partida;
        final Jokalarie[] jokalarik = partida.getJokalarik();
        final Eskue eskue = partida.getEskue();

        final int nerePosizioa = activity.getNerePosizioa();

        setApuntatu(jokalarik[0], R.id.izena_1, R.id.puntuk_1, R.id.bazak_1, mahaikoTokie(0, nerePosizioa));
        setApuntatu(jokalarik[1], R.id.izena_2, R.id.puntuk_2, R.id.bazak_2, mahaikoTokie(1, nerePosizioa));
        setApuntatu(jokalarik[2], R.id.izena_3, R.id.puntuk_3, R.id.bazak_3, mahaikoTokie(2, nerePosizioa));
        setApuntatu(jokalarik[3], R.id.izena_4, R.id.puntuk_4, R.id.bazak_4, mahaikoTokie(3, nerePosizioa));
        setApuntatu(jokalarik[4], R.id.izena_5, R.id.puntuk_5, R.id.bazak_5, mahaikoTokie(4, nerePosizioa));

        final Karta triunfoa = eskue.getTriunfoa();
        if (triunfoa != null) {
            setTextViewText(R.id.triunfo_textue, triunfoa.getKoloreaName());
            setImageViewDrawable(R.id.triunfo_irudie, Karta.getKartaDrawableId(triunfoa));
        }

        final Karta[] kartak = jokalarik[nerePosizioa].getKartak();
        final int[] kartaIdak = new int[] {R.id.karta_0, R.id.karta_1, R.id.karta_2, R.id.karta_3,
                R.id.karta_4, R.id.karta_5, R.id.karta_6, R.id.karta_7};
        for (int i = 0; i < 8; i++) {
            if (i < kartak.length) {
                setImageViewDrawable(kartaIdak[i], Karta.getKartaDrawableId(kartak[i]));
            } else {
                findViewById(kartaIdak[i]).setVisibility(View.GONE);
            }
        }
    }

    private int mahaikoTokie(final int jokalarie, final int nerePosizioa) {
        final int kenketa = jokalarie - nerePosizioa;
        final int id;
        switch (kenketa) {
            case -4: id = R.id.karta_eskuin_beheran;
                break;
            case -3: id = R.id.karta_eskuin_goran;
                break;
            case -2: id = R.id.karta_ezker_goran;
                break;
            case -1: id = R.id.karta_ezker_beheran;
                break;
            case 0: id = R.id.karta_erdin_beheran;
                break;
            case 1: id = R.id.karta_eskuin_beheran;
                break;
            case 2: id = R.id.karta_eskuin_goran;
                break;
            case 3: id = R.id.karta_ezker_goran;
                break;
            case 4: id = R.id.karta_ezker_beheran;
                break;
            default: id = R.id.karta_erdin_beheran;
                break;
        }
        return id;
    }

    private void setApuntatu(final Jokalarie jokalarie,
                             final int izena,
                             final int puntuk,
                             final int bazak,
                             final int mahaikoTokie) {
        setTextViewText(izena, activity.getParticipantDisplayName(jokalarie.getId()));
        setTextViewText(puntuk, String.valueOf(jokalarie.getPuntuk()));
        setTextViewText(bazak, jokalarie.getBazaTextue());

        final Karta karta = jokalarie.getJokatutakoa();
        if (karta != null) {
            setImageViewDrawable(mahaikoTokie, Karta.getKartaDrawableId(karta));
        } else {
            findViewById(mahaikoTokie).setVisibility(View.INVISIBLE);
        }
    }

    private void setTextViewText(final int id, final String text) {
        final TextView textView = (TextView)findViewById(id);
        textView.setText(text);
    }

    private void setImageViewDrawable(final int id, final int drawable) {
        final ImageView imageView = (ImageView)findViewById(id);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(drawable);
    }
}
