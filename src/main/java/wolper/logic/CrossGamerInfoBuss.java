package wolper.logic;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import wolper.domain.GamerSet;


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
        updateSidesOfGame(from, to, invitee, inviter, false);
        listOfPlayersChangedEvent();
        messaging.convertAndSend("/topic/invite", from+"&invitedDone&"+to);
    }



    //Отклоняем приглашение
    public void rejectInvitation(String from, String to) {
        final GamerSet inviter = allGames.getGamerByName(from);
        final GamerSet invitee = allGames.getGamerByName(to);
        if ((inviter==null)||(invitee==null)) {
            messaging.convertAndSend("/topic/"+from, "error&Что то пошло не так!");
            return;
        }
        updateSidesOfGame(from, to, invitee, inviter, true);
        listOfPlayersChangedEvent();
        messaging.convertAndSend("/topic/invite", from+"&invitedFail&"+to);
    }

    //Объявляем, что расставили корабли
    public void informPartnerOfFinishedSetUp(String name) {
        final GamerSet partner1 = allGames.getGamerByName(name);
        if (partner1==null) {
            messaging.convertAndSend("/topic/"+name, "error&Что то пошло не так!");
            return;
        }
        final GamerSet partner2 = allGames.getGamerByName(partner1.getPlayWith());
        if (partner2==null) {
            messaging.convertAndSend("/topic/"+name, "esceped&Ваш соперник сбежал!");
            return;
        }
        messaging.convertAndSend("/topic/"+partner1.getPlayWith(), "setUp&Cоперник уже расставил фигуры!");
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
    }

    //Безопасность - проверяем не подделан ли запрос
    private boolean checkMyRightToHit(String attacker, String suffer) {
        final GamerSet gamerSetAttacker = allGames.getGamerByName(attacker);
        final GamerSet gamerSetSuffer = allGames.getGamerByName(suffer);
        if ((gamerSetAttacker==null)||(gamerSetSuffer==null)) return false;
        return gamerSetAttacker.getPlayWith().equals(gamerSetSuffer.getName());
    }

    private boolean checkIfStatusChangeInBetween(String from, String to, GamerSet inviter, GamerSet invitee) {
        if ((inviter==null)||(invitee==null)) {
            messaging.convertAndSend("/topic/"+from, "error&Что то пошло не так!");
            listOfPlayersChangedEvent();
            return true;
        }
        if (!inviter.isFree()) {
            //Приглашение не состоялось
            messaging.convertAndSend("/topic/invite", from +"&invitedFail&"+ to);
            listOfPlayersChangedEvent();
            return true;
        }
        if (!invitee.isFree()) {
            //Приглашение не состоялось
            messaging.convertAndSend("/topic/invite", from +"&invitedFail&"+ to);
            listOfPlayersChangedEvent();
            return true;
        }
        return false;
    }

    private void updateSidesOfGame(String from, String to, GamerSet invitee, GamerSet inviter, boolean positive) {
        GamerSet inviteeNew = invitee.toBuilder().playWith(positive?"":from).free(positive).build();
        GamerSet inviterNew = inviter.toBuilder().free(positive).playWith(positive?"":to).build();
        allGames.updateGamersAtomically(inviterNew, inviteeNew);
    }
}
