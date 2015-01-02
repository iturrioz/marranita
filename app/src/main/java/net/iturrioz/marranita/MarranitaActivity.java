package net.iturrioz.marranita;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.OnTurnBasedMatchUpdateReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;

import com.google.example.games.basegameutils.BaseGameActivity;

import net.iturrioz.marranita.domain.Jokalarie;
import net.iturrioz.marranita.domain.Partida;

import java.util.ArrayList;

public class MarranitaActivity extends BaseGameActivity implements OnInvitationReceivedListener,
                                                                 OnTurnBasedMatchUpdateReceivedListener {
    public static final String TAG = "DrawingActivity";

    private AlertDialog mAlertDialog;

    // For our intents
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_LOOK_AT_MATCHES = 10001;

    // Should I be showing the turn API?
    public boolean jokun = false;

    // This is the current match we're in; null if not loaded
    public TurnBasedMatch mMatch;

    // This is the current match data after being unpersisted.
    // Do not retain references to match data once you have
    // taken an action on the match, such as takeTurn()
    public Partida partida;

    // This is the helper for updating the board
    private final TapeteaInput tapeteaInput = new TapeteaInput(this);

    // This is the helper for updating the board
    private final TapeteaUI tapeteaUI = new TapeteaUI(this);


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setLoginListeners();

        tapeteaInput.setKartaListeners();
    }

    // This function is what gets called when you return from either the Play
    // Games built-in inbox, or else the create game built-in interface.
    @Override
    public void onActivityResult(final int request, final int response, final Intent data) {
        super.onActivityResult(request, response, data);

        if (request == RC_LOOK_AT_MATCHES) {
            // Returning from the 'Select Match' dialog

            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            final TurnBasedMatch match = data
                    .getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);

            if (match != null) {
                updateMatch(match);
            }

            Log.d(TAG, "Match = " + match);
        } else if (request == RC_SELECT_PLAYERS) {
            // Returned from 'Select players to Invite' dialog

            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            // get the invitee list
            final ArrayList<String> invitees = data
                    .getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

            // get automatch criteria
            final Bundle autoMatchCriteria;

            int minAutoMatchPlayers = data.getIntExtra(
                    Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoMatchPlayers = data.getIntExtra(
                    Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

            if (minAutoMatchPlayers > 0) {
                autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                        minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            } else {
                autoMatchCriteria = null;
            }

            final TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                    .addInvitedPlayers(invitees)
                    .setAutoMatchCriteria(autoMatchCriteria).build();

            Games.TurnBasedMultiplayer.createMatch(getApiClient(), tbmc).setResultCallback(
                    new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
                        @Override
                        public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
                            processResult(result);
                        }
                    });

            showSpinner();
        }
    }

    /*************************************************
     * Result processes
     *************************************************/

    private void processResult(final TurnBasedMultiplayer.CancelMatchResult result) {
        dismissSpinner();

        if (!checkStatusCode(result.getStatus().getStatusCode())) {
            return;
        }

        jokun = false;

        showWarning("Match",
                "This match is canceled.  All other players will have their game ended.");
    }

    private void processResult(final TurnBasedMultiplayer.InitiateMatchResult result) {
        final TurnBasedMatch match = result.getMatch();
        dismissSpinner();

        if (!checkStatusCode(result.getStatus().getStatusCode())) {
            return;
        }

        if (match.getData() != null) {
            // This is a game that has already started, so I'll just start
            updateMatch(match);
            return;
        }

        startMatch(match);
    }


    private void processResult(final TurnBasedMultiplayer.LeaveMatchResult result) {
        final TurnBasedMatch match = result.getMatch();
        dismissSpinner();
        if (!checkStatusCode(result.getStatus().getStatusCode())) {
            return;
        }
        jokun = (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN ||
                match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN);
        showWarning("Left", "You've left this match.");
    }


    public void processResult(final TurnBasedMultiplayer.UpdateMatchResult result) {
        final TurnBasedMatch match = result.getMatch();
        dismissSpinner();
        if (!checkStatusCode(result.getStatus().getStatusCode())) {
            return;
        }
        if (match.canRematch()) {
            askForRematch();
        }

        jokun = (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN ||
                match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN);

        if (jokun) {
            updateMatch(match);
            return;
        }

        setViewVisibility();
    }

    /*************************************************
     * Login buttons
     *************************************************/

    private void setLoginListeners() {
        findViewById(R.id.sign_out_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signOut();
                        setViewVisibility();
                    }
                });

        findViewById(R.id.sign_in_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // start the asynchronous sign in flow
                        beginUserInitiatedSignIn();

                        findViewById(R.id.sign_in_button).setVisibility(
                                View.GONE);

                    }
                });
    }

    /*************************************************
     * Menu options
     *************************************************/

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            // Cancel the game. Should possibly wait until the game is canceled before
            // giving up on the view.
            case R.id.cancel:
                showSpinner();
                Games.TurnBasedMultiplayer.cancelMatch(getApiClient(), mMatch.getMatchId())
                        .setResultCallback(new ResultCallback<TurnBasedMultiplayer.CancelMatchResult>() {
                            @Override
                            public void onResult(final TurnBasedMultiplayer.CancelMatchResult result) {
                                processResult(result);
                            }
                        });
                jokun = false;
                setViewVisibility();
                return true;

            // Leave the game during your turn. Note that there is a separate
            // GamesClient.leaveTurnBasedMatch() if you want to leave NOT on your turn.
            case R.id.leave:
                showSpinner();
                final String nextParticipantId = getNextParticipantId();
                Games.TurnBasedMultiplayer.leaveMatchDuringTurn(getApiClient(), mMatch.getMatchId(),
                        nextParticipantId).setResultCallback(
                        new ResultCallback<TurnBasedMultiplayer.LeaveMatchResult>() {
                            @Override
                            public void onResult(final TurnBasedMultiplayer.LeaveMatchResult result) {
                                processResult(result);
                            }
                        });
                setViewVisibility();
                return true;

            // Finish the game. Sometimes, this is your only choice.
            case R.id.finish:
                showSpinner();
                Games.TurnBasedMultiplayer.finishMatch(getApiClient(), mMatch.getMatchId())
                        .setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                            @Override
                            public void onResult(final TurnBasedMultiplayer.UpdateMatchResult result) {
                                processResult(result);
                            }
                        });
                jokun = false;
                setViewVisibility();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*************************************************
     * Game options
     *************************************************/

    // Displays your inbox. You will get back onActivityResult where
    // you will need to figure out what you clicked on.
    public void onCheckGamesClicked(final View view) {
        final Intent intent = Games.TurnBasedMultiplayer.getInboxIntent(getApiClient());
        startActivityForResult(intent, RC_LOOK_AT_MATCHES);
    }

    // Open the create-game UI. You will get back an onActivityResult
    // and figure out what to do.
    public void onStartMatchClicked(final View view) {
        final Intent intent = Games.TurnBasedMultiplayer.getSelectOpponentsIntent(getApiClient(),
                4, 4, false);
        startActivityForResult(intent, RC_SELECT_PLAYERS);
    }


    /*************************************************
     * Turn helpers
     *************************************************/

    void jokatuTxandaBukatu(final int nerePosizioa) {
        final Jokalarie[] jokalarik = partida.getJokalarik();
        if (denakJokatuta(jokalarik)) {
            final int aurrenekoa = nerePosizioa < 4 ? nerePosizioa + 1 : 0;
            final Jokalarie bazaIrabazlea = partida.bazaIrabazlea(jokalarik[aurrenekoa]);
            bazaIrabazlea.bazaGehitu();
            for (Jokalarie jokalarie : jokalarik) {
                jokalarie.jokatutakoaKendu();
            }
            if (jokalarik[0].getKartak().length == 0) {
                for (Jokalarie jokalarie : jokalarik) {
                    jokalarie.puntukGehitu(partida.getEskue().isUrrek());
                }
                final Partida partida = this.partida.banatuKartak();
                if (partida != null) {
                    this.partida = partida;
                } else {
                    // TODO: puntuk eta klasifikazioa
                    showSpinner();
                    Games.TurnBasedMultiplayer.finishMatch(getApiClient(), mMatch.getMatchId())
                            .setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                                @Override
                                public void onResult(final TurnBasedMultiplayer.UpdateMatchResult result) {
                                    processResult(result);
                                }
                            });
                }
            }
            txandaBukatu(bazaIrabazlea);
        } else {
            txandaBukatu(null);
        }
    }

    private static boolean denakJokatuta(final Jokalarie[] jokalarik) {
        boolean denakJokatuta = true;
        for (Jokalarie jokalarie : jokalarik) {
            if (jokalarie.getJokatutakoa() == null) {
                denakJokatuta = false;
            }
        }
        return denakJokatuta;
    }

    // Upload your new gamestate, then take a turn, and pass it on to the next
    // player.
    void txandaBukatu(final Jokalarie jokalarie) {
        showSpinner();

        final String nextParticipantId = jokalarie != null ? jokalarie.getId() : getNextParticipantId();

        Games.TurnBasedMultiplayer.takeTurn(getApiClient(), mMatch.getMatchId(),
                partida.persist(), nextParticipantId).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                        processResult(result);
                    }
                }
        );

        partida = null;
    }


    /*************************************************
     * Visibility handler
     *************************************************/

    // Update the visibility based on what state we're in.
    public void setViewVisibility() {
        if (!isSignedIn()) {
            findViewById(R.id.login_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.matchup_layout).setVisibility(View.GONE);
            findViewById(R.id.tapetea).setVisibility(View.GONE);

            if (mAlertDialog != null) {
                mAlertDialog.dismiss();
            }
            return;
        }

        final String displayName = Games.Players.getCurrentPlayer(getApiClient()).getDisplayName();
        ((TextView) findViewById(R.id.name_field)).setText(displayName);
        findViewById(R.id.login_layout).setVisibility(View.GONE);

        if (jokun) {
            findViewById(R.id.matchup_layout).setVisibility(View.GONE);
            findViewById(R.id.tapetea).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.matchup_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.tapetea).setVisibility(View.GONE);
        }
    }


    /*************************************************
     * Dialogs
     *************************************************/

    public void showSpinner() {
        findViewById(R.id.progressLayout).setVisibility(View.VISIBLE);
    }

    public void dismissSpinner() {
        findViewById(R.id.progressLayout).setVisibility(View.GONE);
    }

    // Generic warning/info dialog
    public void showWarning(final String title, final String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle(title).setMessage(message);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                    }
                });

        // create alert dialog
        mAlertDialog = alertDialogBuilder.create();

        // show it
        mAlertDialog.show();
    }

    // Rematch dialog
    public void askForRematch() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage("Do you want a rematch?");

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Sure, rematch!",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                rematch();
                            }
                        })
                .setNegativeButton("No.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

        alertDialogBuilder.show();
    }


    /*************************************************
     * Match actions
     *************************************************/

    // startMatch() happens in response to the createTurnBasedMatch()
    // above. This is only called on success, so we should have a
    // valid match object. We're taking this opportunity to setup the
    // game, saving our initial state. Calling takeTurn() will
    // callback to OnTurnBasedMatchUpdated(), which will show the game
    // UI.
    public void startMatch(final TurnBasedMatch match) {
        partida = new Partida(match.getParticipantIds());
        // Some basic turn data
        //partida.data = "First turn";

        mMatch = match;

        final String aurrenekoa = match.getParticipantIds().get(partida.getAurrenekoa());

        showSpinner();

        // Taking this turn will cause turnBasedMatchUpdated
        Games.TurnBasedMultiplayer.takeTurn(getApiClient(), mMatch.getMatchId(),
                partida.persist(), aurrenekoa).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                        processResult(result);
                    }
                }
        );
    }

    // If you choose to rematch, then call it and wait for a response.
    public void rematch() {
        showSpinner();
        Games.TurnBasedMultiplayer.rematch(getApiClient(), mMatch.getMatchId()).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
                        processResult(result);
                    }
                });
        mMatch = null;
        jokun = false;
    }

    // This is the main function that gets called when players choose a match
    // from the inbox, or else create a match and want to start it.
    public void updateMatch(final TurnBasedMatch match) {
        mMatch = match;

        jokun = true;

        final int status = match.getStatus();
        final int turnStatus = match.getTurnStatus();

        switch (status) {
            case TurnBasedMatch.MATCH_STATUS_CANCELED:
                showWarning("Canceled!", "This game was canceled!");
                return;
            case TurnBasedMatch.MATCH_STATUS_EXPIRED:
                showWarning("Expired!", "This game is expired.  So sad!");
                return;
            case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
                showWarning("Waiting for auto-match...",
                        "We're still waiting for an automatch partner.");
                return;
            case TurnBasedMatch.MATCH_STATUS_COMPLETE:
                if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE) {
                    showWarning(
                            "Complete!",
                            "This game is over; someone finished it, and so did you!  There is nothing to be done.");
                    break;
                }

                // Note that in this state, you must still call "Finish" yourself,
                // so we allow this to continue.
                showWarning("Complete!",
                        "This game is over; someone finished it!  You can only finish it now.");
        }

        // OK, it's active. Check on turn status.
        switch (turnStatus) {
            case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:
                partida = Partida.unpersist(mMatch.getData(), mMatch.getParticipantIds());
                if (!partida.isFinished()) {
                    tapeteaInput.setListeners(mMatch.getParticipantIds(), getCurrentParticipantId());
                }
                tapeteaUI.setGameplayUI();
                return;
            case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
                partida = Partida.unpersist(mMatch.getData(), mMatch.getParticipantIds());
                tapeteaUI.setGameplayUI();
                return;
            case TurnBasedMatch.MATCH_TURN_STATUS_INVITED:
                showWarning("Good inititative!",
                        "Still waiting for invitations.\n\nBe patient!");
        }

        partida = null;

        setViewVisibility();
    }



    /*************************************************
     * Event listeners
     *************************************************/

    @Override
    public void onSignInFailed() {
        setViewVisibility();
    }

    @Override
    public void onSignInSucceeded() {
        final TurnBasedMatch turnBasedMatch = mHelper.getTurnBasedMatch();
        if (turnBasedMatch != null) {
            // GameHelper will cache any connection hint it gets. In this case,
            // it can cache a TurnBasedMatch that it got from choosing a turn-based
            // game notification. If that's the case, you should go straight into
            // the game.
            updateMatch(turnBasedMatch);
            return;
        }

        setViewVisibility();

        // We are registering the optional MatchUpdateListener, which
        // will replace notifications you would get otherwise. You do *NOT* have
        // to register a MatchUpdateListener.
        Games.TurnBasedMultiplayer.registerMatchUpdateListener(getApiClient(), this);
    }

    // Handle notification events.
    @Override
    public void onInvitationReceived(final Invitation invitation) {
        Toast.makeText(
                this,
                "An invitation has arrived from "
                        + invitation.getInviter().getDisplayName(), Toast.LENGTH_SHORT
        )
                .show();
    }

    @Override
    public void onInvitationRemoved(final String invitationId) {
        Toast.makeText(this, "An invitation was removed.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTurnBasedMatchReceived(final TurnBasedMatch match) {
        Toast.makeText(this, "A match was updated.", Toast.LENGTH_SHORT).show();
        updateMatch(match);
    }

    @Override
    public void onTurnBasedMatchRemoved(final String matchId) {
        Toast.makeText(this, "A match was removed.", Toast.LENGTH_SHORT).show();

    }

    public void showErrorMessage(final int stringId) {
        showWarning("Warning", getResources().getString(stringId));
    }

    // Returns false if something went wrong, probably. This should handle
    // more cases, and probably report more accurate results.
    private boolean checkStatusCode(final int statusCode) {
        switch (statusCode) {
            case GamesStatusCodes.STATUS_OK:
                return true;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_DEFERRED:
                // This is OK; the action is stored by Google Play Services and will
                // be dealt with later.
                Toast.makeText(
                        this,
                        "Stored action for later.  (Please remove this toast before release.)",
                        Toast.LENGTH_SHORT).show();
                // NOTE: This toast is for informative reasons only; please remove
                // it from your final application.
                return true;
            case GamesStatusCodes.STATUS_MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
                showErrorMessage(R.string.status_multiplayer_error_not_trusted_tester);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_ALREADY_REMATCHED:
                showErrorMessage(R.string.match_error_already_rematched);
                break;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_FAILED:
                showErrorMessage(R.string.network_error_operation_failed);
                break;
            case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
                showErrorMessage(R.string.client_reconnect_required);
                break;
            case GamesStatusCodes.STATUS_INTERNAL_ERROR:
                showErrorMessage(R.string.internal_error);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_INACTIVE_MATCH:
                showErrorMessage(R.string.match_error_inactive_match);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_LOCALLY_MODIFIED:
                showErrorMessage(R.string.match_error_locally_modified);
                break;
            default:
                showErrorMessage(R.string.unexpected_status);
                Log.d(TAG, "Did not have warning or string to deal with: "
                        + statusCode);
        }
        return false;
    }

    private String getNextParticipantId() {
        ArrayList<String> participantIds = mMatch.getParticipantIds();

        int desiredIndex = -1;

        for (int i = 0; i < participantIds.size(); i++) {
            if (participantIds.get(i).equals(getCurrentParticipantId())) {
                desiredIndex = i + 1;
            }
        }

        if (desiredIndex < participantIds.size()) {
            return participantIds.get(desiredIndex);
        }

        if (mMatch.getAvailableAutoMatchSlots() <= 0) {
            // You've run out of automatch slots, so we start over.
            return participantIds.get(0);
        } else {
            // You have not yet fully automatched, so null will find a new
            // person to play against.
            return null;
        }
    }

    private String getCurrentParticipantId() {
        final String playerId =  Games.Players.getCurrentPlayer(getApiClient()).getPlayerId();
        for (Participant participant : mMatch.getParticipants()) {
            if (participant.getPlayer().getPlayerId().equals(playerId)) {
                return participant.getParticipantId();
            }
        }
        throw new IllegalStateException("No participant id was found for the current player");
    }

    int getNerePosizioa() {
        return mMatch.getParticipantIds().indexOf(getCurrentParticipantId());
    }

//    int getTxandaPosizioa() {
//        final Jokalarie[] jokalarik = partida.getJokalarik();
//        for (int i=0; i<5; i++) {
//            if (jokalarik[i].getEskatutakok() == -1 &&
//                jokalarik[i == 0 ? 4 : i - 1].getEskatutakok() > -1) {
//                return i;
//            } else {
//                if (jokalarik[i].getJokatutakoa() == null &&
//                        jokalarik[i == 0 ? 4 : i - 1].getJokatutakoa() != null) {
//                    return i;
//                }
//            }
//        }
//        return getNerePosizioa();
//    }

    String getParticipantDisplayName(final String id) {
        return mMatch.getParticipant(id).getDisplayName().substring(0,10);
    }
}