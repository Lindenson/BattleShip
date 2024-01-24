package wolper.logic;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service("messenger")
@RequiredArgsConstructor
public class EventMessenger {

    private final SimpMessageSendingOperations messaging;

    public void listOfPlayersChangedEvent(){
        messaging.convertAndSend("/topic/renewList", "playersUpdated");
    }

    public void inviteEvent(@NonNull String from, @NonNull String to){
        messaging.convertAndSend("/topic/invite", to+"&invitedNew&"+from);
    }

    public void inviteAcceptedEvent(@NonNull String from, @NonNull String to) {
        messaging.convertAndSend("/topic/invite", from + "&invitedDone&" + to);
    }

    public void readyToPlayEvent(@NonNull String to){
        messaging.convertAndSend("/topic/"+to, "setUp&Cоперник уже расставил фигуры!");
    }


    public void missedPlayEvent(@NonNull String to, int x, int y){
        messaging.convertAndSend("/topic/" + to, "hitYou&" + x + "&" + y + "&zero");
    }

    public void hitPlayEvent(@NonNull String to, int x, int y){
        messaging.convertAndSend("/topic/" + to, "hitYou&" + x + "&" + y + "&injured");
    }

    public void killedPlayEvent(@NonNull String to, int x, int y){
        messaging.convertAndSend("/topic/" + to, "hitYou&" + x + "&" + y + "&killed");
    }

    public void winPlayEvent(@NonNull String to, int x, int y){
        messaging.convertAndSend("/topic/" + to, "hitYou&" + x + "&" + y + "&defeated");
    }

    public void escapedPlayEvent(@NonNull String to){
        messaging.convertAndSend("/topic/"+to, "escaped&Ваш соперник сбежал!");
    }

    public void errorEvent(@NonNull String to){
        messaging.convertAndSend("/topic/"+ to, "error&Что то пошло не так!");
    }

    public void inviteRejectedEvent(@NonNull String from, @NonNull String to){
        messaging.convertAndSend("/topic/invite", from +"&invitedFail&"+ to);
    }
}
