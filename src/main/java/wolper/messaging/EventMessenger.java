package wolper.messaging;

import lombok.NonNull;

import java.util.Collection;

public interface EventMessenger {
    void listOfPlayersChangedEvent(Collection<String> names);

    void inviteEvent(@NonNull String from, @NonNull String to);

    void inviteAcceptedEvent(@NonNull String from, @NonNull String to);

    void readyToPlayEvent(@NonNull String to);

    void missedPlayEvent(@NonNull String to, int x, int y);

    void hitPlayEvent(@NonNull String to, int x, int y);

    void killedPlayEvent(@NonNull String to, int x, int y);

    void gameOverPlayEvent(@NonNull String to, int x, int y);

    void escapedPlayEvent(@NonNull String to);

    void errorEvent(@NonNull String to);

    void logoutEvent(@NonNull String to);

    void inviteRejectedEvent(@NonNull String from, @NonNull String to);
}
