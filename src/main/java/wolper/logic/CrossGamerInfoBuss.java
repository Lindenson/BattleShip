package wolper.logic;

import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wolper.domain.GamerSet;

import java.util.Objects;
import java.util.Optional;


@Service(value = "gamerBuss")
@RequiredArgsConstructor
public class CrossGamerInfoBuss {

    private final AllGames allGames;
    private final ShipMapper shipMapper;
    private final EventMessenger eventMessanger;


    //Сватаемся
    public void inviteOneAnother(@NonNull String from, @NonNull String to) {
        final GamerSet inviter = allGames.getGamerByName(from);
        final GamerSet invitee = allGames.getGamerByName(to);

        if (checkIfStatusChangeInBetween(from, to, inviter, invitee)) return;
        eventMessanger.inviteEvent(from, to);
    }

    //Соглашаемся поиграть
    public void acceptInvitation(@NonNull String from, @NonNull String to) {
        final GamerSet inviter = allGames.getGamerByName(from);
        final GamerSet invitee = allGames.getGamerByName(to);

        if (checkIfStatusChangeInBetween(from, to, inviter, invitee)) return;
        if (updateSidesOfGame(from, to, invitee, inviter)) {
            eventMessanger.inviteAcceptedEvent(from, to);
        } else  {
            eventMessanger.inviteRejectedEvent(from, to);
        }
    }

    //Отклоняем приглашение
    public void rejectInvitation(@NonNull String from, @NonNull String to) {
        final GamerSet inviter = allGames.getGamerByName(from);
        final GamerSet invitee = allGames.getGamerByName(to);

        if (ifDisappearedSendError(from, to, inviter, invitee)) return;
        rejectIfNotPlaying(from, to, inviter, invitee);
    }

    //Объявляем, что расставили корабли
    public void informPartnerOfFinishedSetUp(@NonNull String from) {
        final GamerSet inviter = allGames.getGamerByName(from);
        final String to = Optional.ofNullable(inviter).map(GamerSet::getPlayWith).orElse(null);
        final GamerSet invitee = Optional.ofNullable(to).map(allGames::getGamerByName).orElse(null);

        if (ifDisappearedSendError(from, to, inviter, invitee)) return;
        Objects.requireNonNull(to);
        eventMessanger.readyToPlayEvent(to);
    }

    //Ход соперника - проверка попадания - выдача поражения или победы
    public String doNextMove(@NonNull String attacker, @NonNull String victim, int x, int y) {
        if (checkMyRightToHit(attacker, victim))
            switch (shipMapper.doHit(victim, y - 1, x - 1)) {
                case 0:
                    eventMessanger.missedPlayEvent(victim, x, y);
                    return "zero";
                case 1:
                    eventMessanger.hitPlayEvent(victim, x, y);
                    return "injured";
                case 2:
                    if (shipMapper.checkKillAll(victim)) {
                        eventMessanger.winPlayEvent(victim, x, y);
                        updateRatings(attacker, victim);
                        return "victory";
                    }
                    eventMessanger.killedPlayEvent(victim, x, y);
                    return "killed";
            }
        return "error";
    }

    private void updateRatings(@NonNull String from, @NonNull String to) {
        GamerSet gamerFrom = allGames.getGamerByName(from);
        GamerSet gamerTo = allGames.getGamerByName(to);

        GamerSet updatedFrom = Optional.ofNullable(gamerFrom)
                .map(GamerSet::withAddRating).orElse(null);
        GamerSet updatedTo = Optional.ofNullable(gamerTo)
                .map(GamerSet::withAddRating).orElse(null);

        if ((Objects.isNull(gamerFrom) || Objects.isNull(gamerTo)
        || Objects.isNull(updatedFrom)) || Objects.isNull(updatedTo)) return;

        if (allGames.tryUpdateGamersAtomically(gamerFrom, gamerTo, updatedFrom, updatedTo)) {
            eventMessanger.listOfPlayersChangedEvent();
        }
    }

    //Безопасность - проверяем не подделан ли запрос
    private boolean checkMyRightToHit(@NonNull String from, @NonNull String to) {
        final GamerSet inviter = allGames.getGamerByName(from);
        final GamerSet invitee = allGames.getGamerByName(to);

        if (ifDisappearedSendError(from, to, inviter, invitee)) return false;
        return inviter.getPlayWith().equals(invitee.getName());
    }

    private boolean checkIfStatusChangeInBetween(@NonNull String from, @NonNull String to,
                                                 GamerSet inviter, GamerSet invitee)
    {
        if (ifDisappearedSendError(from, to, inviter, invitee)) return true;

        if (!(inviter.isFree() && invitee.isFree())) {
            rejectIfNotPlaying(from, to, inviter, invitee);
            return true;
        }
        return false;
    }

    private boolean updateSidesOfGame(@NonNull String from, @NonNull String to,
                                   @NonNull GamerSet invitee, @NonNull GamerSet inviter)
    {
        GamerSet inviteeNew = invitee.toBuilder().playWith(from).free(false).build();
        GamerSet inviterNew = inviter.toBuilder().free(false).playWith(to).build();

        if (allGames.tryUpdateGamersAtomically(invitee, inviter, inviterNew, inviteeNew)) {
            eventMessanger.listOfPlayersChangedEvent();
            return true;
        }
        else return false;
    }

    private boolean ifDisappearedSendError(@NonNull String from, @Nullable String to,
                                           GamerSet inviter, GamerSet invitee)
    {
        if (inviter == null && invitee == null) {
            eventMessanger.errorEvent(from);
            if (Objects.nonNull(to)) eventMessanger.errorEvent(to);
            return true;
        }
        if (invitee == null) {
            eventMessanger.escapedPlayEvent(from);
            return true;
        }
        if (inviter == null) {
            Objects.requireNonNull(to);
            eventMessanger.escapedPlayEvent(to);
            return true;
        }
        return false;
    }

    private void rejectIfNotPlaying(@NonNull String from, @NonNull String to,
                                    @NonNull GamerSet inviter, @NonNull GamerSet invitee)
    {
        //так как возможны множественные приглашения, то мы проверим
        //если еще не играют между собой
        if (!inviter.getPlayWith().equals(to) || !invitee.getPlayWith().equals(from))
            eventMessanger.inviteRejectedEvent(from, to);
    }

}
