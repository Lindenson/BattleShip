package wolper.game;

import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wolper.persistence.inmemory.GameDao;
import wolper.domain.GamerSet;
import wolper.domain.ShipList;
import wolper.messaging.EventMessenger;

import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class GameLogic {

    private final GameDao gameDao;
    private final EventMessenger eventMessenger;
    private final PlayerValidator playerValidator;

    //Сватаемся
    public void inviteOneAnother(@NonNull String from, @NonNull String to) {
        final GamerSet inviter = gameDao.getGamerByName(from);
        final GamerSet invitee = gameDao.getGamerByName(to);

        if (ifBusy(from, to, inviter, invitee)) return;
        eventMessenger.inviteEvent(from, to);
    }

    //Соглашаемся поиграть
    public void acceptInvitation(@NonNull String from, @NonNull String to) {
        final GamerSet inviter = gameDao.getGamerByName(from);
        final GamerSet invitee = gameDao.getGamerByName(to);

        if (ifBusy(from, to, inviter, invitee)) return;

        if (tryPlayTogether(from, to, invitee, inviter)) {
            eventMessenger.inviteAcceptedEvent(from, to);
        } else  {
            eventMessenger.inviteRejectedEvent(from, to);
        }
    }

    //Отклоняем приглашение
    public void rejectInvitation(@NonNull String from, @NonNull String to) {
        final GamerSet inviter = gameDao.getGamerByName(from);
        final GamerSet invitee = gameDao.getGamerByName(to);

        if (playerValidator.ifAnyIsNotValid(from, to, inviter, invitee)) return;

        rejectIfNotYetPlaying(from, to, inviter, invitee);
    }

    //Объявляем, что расставили корабли
    public void informPartnerOfFinishedSetUp(@NonNull String from) {
        final GamerSet inviter = gameDao.getGamerByName(from);
        final String to = Optional.ofNullable(inviter).map(GamerSet::getPartner).orElse(null);
        final GamerSet invitee = Optional.ofNullable(to).map(gameDao::getGamerByName)
                .filter(it -> it.getPartner().equals(from)).orElse(null);

        if (playerValidator.ifAnyIsNotValid(from, to, inviter, invitee)) return;

        Objects.requireNonNull(to);
        eventMessenger.readyToPlayEvent(to);
    }

    //Ход соперника - проверка попадания - выдача поражения или победы
    public String doNextMove(@NonNull String attacker, @NonNull String victim, int x, int y) {

        if (checkMyRightToHit(attacker, victim))
            switch (doHit(victim, y - 1, x - 1)) {
                case 0:
                    eventMessenger.missedPlayEvent(victim, x, y);
                    return "zero";
                case 1:
                    eventMessenger.hitPlayEvent(victim, x, y);
                    return "injured";
                case 2:
                    if (checkKillAll(victim)) {
                        eventMessenger.gameOverPlayEvent(victim, x, y);
                        updateRatings(attacker, victim);
                        return "victory";
                    }
                    eventMessenger.killedPlayEvent(victim, x, y);
                    return "killed";
            }
        return "error";
    }

    private void updateRatings(@NonNull String from, @NonNull String to) {
        GamerSet gamerFrom = gameDao.getGamerByName(from);
        GamerSet gamerTo = gameDao.getGamerByName(to);

        GamerSet updatedFrom = Optional.ofNullable(gamerFrom)
                .map(GamerSet::withAddRating).orElse(null);
        GamerSet updatedTo = Optional.ofNullable(gamerTo)
                .map(GamerSet::withUntouchedRating).orElse(null);

        if (playerValidator.ifAnyIsNull(gamerFrom, gamerTo, updatedFrom, updatedTo)) return;

        if (gameDao.tryUpdateGamersAtomically(gamerFrom, gamerTo, updatedFrom, updatedTo)) {
            eventMessenger.listOfPlayersChangedEvent(gameDao.getAllGamersNames());
        }
    }

    //Безопасность - проверяем не подделан ли запрос
    private boolean checkMyRightToHit(@NonNull String from, @NonNull String to) {
        final GamerSet inviter = gameDao.getGamerByName(from);
        final GamerSet invitee = gameDao.getGamerByName(to);

        if (playerValidator.ifAnyIsNotValid(from, to, inviter, invitee)) return false;
        return inviter.getPartner().equals(invitee.getName());
    }


    private boolean tryPlayTogether(@NonNull String from, @NonNull String to,
                                    @NonNull GamerSet invitee, @NonNull GamerSet inviter)
    {
        GamerSet inviterUpdated = GamerSet.makePlayingWith(inviter, to);
        GamerSet inviteeUpdated = GamerSet.makePlayingWith(invitee, from);

        if (gameDao.tryUpdateGamersAtomically(inviter, invitee,  inviterUpdated, inviteeUpdated)) {
            eventMessenger.listOfPlayersChangedEvent(gameDao.getAllGamersNames());
            return true;
        }
        else return false;
    }


    private int doHit(@NonNull String name, int i, int j) {
        ShipList shipList = gameDao.getShipListByName(name);

        for (ShipList.SmallSip smallSip : shipList.smallSipList) {
            if (smallSip.contains(i,j)) {
                if(smallSip.checkIfKilled()) {
                    updateGamersScore(name);
                    return 2;
                }
                updateGamersScore(name);
                return 1;
            }
        }
        return 0;
    }

    private void updateGamersScore(String name) {
        GamerSet gamerByName = gameDao.getGamerByName(name);
        GamerSet gamerUpdated = GamerSet.addKilled(gamerByName);

        gameDao.updateGamer(gamerUpdated);
    }

    private boolean checkKillAll(String name){
        GamerSet gamerSet = gameDao.getGamerByName(name);
        return (gamerSet.ifKilledEnough());
    }

    private boolean ifBusy(@NonNull String from, @NonNull String to,
                           @Nullable GamerSet inviter, @Nullable GamerSet invitee)
    {
        if (playerValidator.ifAnyIsNotValid(from, to, inviter, invitee)) return true;
        Objects.requireNonNull(inviter);
        Objects.requireNonNull(invitee);

        if (!(inviter.isFree() && invitee.isFree())) {
            rejectIfNotYetPlaying(from, to, inviter, invitee);
            return true;
        }
        return false;
    }

    private void rejectIfNotYetPlaying(@NonNull String from, @NonNull String to,
                                       @NonNull GamerSet inviter, @NonNull GamerSet invitee)
    {
        //так как возможны множественные приглашения, то мы проверим
        //если еще не играют между собой
        if (!inviter.getPartner().equals(to) || !invitee.getPartner().equals(from))
            eventMessenger.inviteRejectedEvent(from, to);
    }
}
