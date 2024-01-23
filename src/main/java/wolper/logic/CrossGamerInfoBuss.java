package wolper.logic;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import wolper.domain.GamerSet;

import java.util.Optional;


@Service(value = "gamerBuss")
@RequiredArgsConstructor
public class CrossGamerInfoBuss {

    private final AllGames allGames;
    private final ShipMapper shipMapper;
    private final SimpMessageSendingOperations messaging;


    //Сообщаем, что нас можно приглашать
    public void listOfPlayersChangedEvent(){
        messaging.convertAndSend("/topic/renewList", "newCreated");
    }

    //Сватаемся
    public void inviteOneAnother(String from, String to) {
        final GamerSet inviter = allGames.getGamerByName(from);
        final GamerSet invitee = allGames.getGamerByName(to);

        if (checkIfStatusChangeInBetween(from, to, inviter, invitee)) return;
        messaging.convertAndSend("/topic/invite", to+"&invitedNew&"+from);
    }

    //Соглашаемся поиграть
    public void acceptInvitation(String from, String to) {
        final GamerSet inviter = allGames.getGamerByName(from);
        final GamerSet invitee = allGames.getGamerByName(to);

        if (checkIfStatusChangeInBetween(from, to, inviter, invitee)) return;
        updateSidesOfGame(from, to, invitee, inviter);
        messaging.convertAndSend("/topic/invite", from+"&invitedDone&"+to);
        listOfPlayersChangedEvent();
    }

    //Отклоняем приглашение
    public void rejectInvitation(String from, String to) {
        final GamerSet inviter = allGames.getGamerByName(from);
        final GamerSet invitee = allGames.getGamerByName(to);

        if (ifDisappearedSendError(from, to, inviter, invitee)) return;
        rejectIfNotPlaying(from, to, inviter, invitee);
    }

    //Объявляем, что расставили корабли
    public void informPartnerOfFinishedSetUp(String from) {
        final GamerSet inviter = allGames.getGamerByName(from);
        final String to = Optional.ofNullable(inviter).map(GamerSet::getPlayWith).orElse(null);
        final GamerSet invitee = Optional.ofNullable(to).map(allGames::getGamerByName).orElse(null);

        if (ifDisappearedSendError(from, to, inviter, invitee)) return;
        messaging.convertAndSend("/topic/"+inviter, "setUp&Cоперник уже расставил фигуры!");
    }

    //Ход соперника - проверка попадания - выдача поражения или победы
    public String doNextMove(String attacker, String victim, int x, int y) {
        if (checkMyRightToHit(attacker, victim))
            switch (shipMapper.doHit(victim, y - 1, x - 1)) {
                case 0:
                    messaging.convertAndSend("/topic/" + victim, "hitYou&" + x + "&" + y + "&zero");
                    return "zero";
                case 1:
                    messaging.convertAndSend("/topic/" + victim, "hitYou&" + x + "&" + y + "&injured");
                    return "injured";
                case 2:
                    if (shipMapper.checkKillAll(victim)) {
                        messaging.convertAndSend("/topic/" + victim, "hitYou&" + x + "&" + y + "&defeated");
                        updateRatings(attacker, victim);
                        return "victory";
                    }
                    messaging.convertAndSend("/topic/" + victim, "hitYou&" + x + "&" + y + "&killed");
                    return "killed";
            }
        return "";
    }

    private void updateRatings(String attacker, String victim) {
        GamerSet attackerGamer = allGames.getGamerByName(attacker);
        GamerSet victimGamer = allGames.getGamerByName(victim);

        GamerSet newAttacker = attackerGamer.toBuilder().free(true)
                .playWith("").invitedBy("").rating(attackerGamer.getRating() + 1).build();
        GamerSet newVictim = victimGamer.toBuilder().free(true)
                .playWith("").invitedBy("").rating(attackerGamer.getRating()).build();

        allGames.updateGamersAtomically(newAttacker, newVictim);
        listOfPlayersChangedEvent();
    }

    //Безопасность - проверяем не подделан ли запрос
    private boolean checkMyRightToHit(String from, String to) {
        final GamerSet inviter = allGames.getGamerByName(from);
        final GamerSet invitee = allGames.getGamerByName(to);

        if (ifDisappearedSendError(from, to, inviter, invitee)) return false;
        return inviter.getPlayWith().equals(invitee.getName());
    }

    private boolean checkIfStatusChangeInBetween(String from, String to, GamerSet inviter, GamerSet invitee) {
        if (ifDisappearedSendError(from, to, inviter, invitee)) return true;

        if (!(inviter.isFree() && invitee.isFree())) {
            rejectIfNotPlaying(from, to, inviter, invitee);
            return true;
        }
        return false;
    }

    private void updateSidesOfGame(String from, String to, GamerSet invitee, GamerSet inviter) {
        GamerSet inviteeNew = invitee.toBuilder().playWith(from).free(false).build();
        GamerSet inviterNew = inviter.toBuilder().free(false).playWith(to).build();
        allGames.updateGamersAtomically(inviterNew, inviteeNew);

        listOfPlayersChangedEvent();
    }

    private boolean ifDisappearedSendError(String from, String to, GamerSet inviter, GamerSet invitee) {
        if ((inviter != null) && (invitee == null)) {
            messaging.convertAndSend("/topic/"+inviter, "escaped&Ваш соперник сбежал!");
            return true;
        }
        if ((invitee != null) && (inviter == null)) {
            messaging.convertAndSend("/topic/"+invitee, "escaped&Ваш соперник сбежал!");
            return true;
        }
        if (inviter == null) {
            messaging.convertAndSend("/topic/"+ from, "error&Что то пошло не так!");
            messaging.convertAndSend("/topic/"+ to, "error&Что то пошло не так!");
            return true;
        }
        return false;
    }

    private void rejectIfNotPlaying(String from, String to, GamerSet inviter, GamerSet invitee) {
        //так как возможны множественные приглашения, то мы проверим
        //если еще не играют между собой
        if (!inviter.getPlayWith().equals(to) || !invitee.getPlayWith().equals(from))
            messaging.convertAndSend("/topic/invite", from +"&invitedFail&"+ to);
    }

}
