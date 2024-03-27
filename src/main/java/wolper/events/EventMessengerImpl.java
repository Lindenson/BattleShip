package wolper.events;

import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service("messenger")
@EnableAsync
@RequiredArgsConstructor
public class EventMessengerImpl implements EventMessenger {


    private final RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() { rabbitTemplate.setExchange("amq.topic"); }

    @Override
    @Async
    public void listOfPlayersChangedEvent(Collection<String> names) {
        names.stream().forEach(it -> rabbitTemplate.convertAndSend(it, "playersUpdated"));
    }

    @Override
    public void inviteEvent(@NonNull String from, @NonNull String to) {
        rabbitTemplate.convertAndSend(to, to + "&invitedNew&" + from);
    }

    @Override
    public void inviteAcceptedEvent(@NonNull String from, @NonNull String to) {
        rabbitTemplate.convertAndSend(from, from + "&invitedDone&" + to);
        rabbitTemplate.convertAndSend(to, from + "&invitedDone&" + to);
    }

    @Override
    public void readyToPlayEvent(@NonNull String to) {
        rabbitTemplate.convertAndSend(to, "setUp&Cоперник уже расставил фигуры!");
    }


    @Override
    public void missedPlayEvent(@NonNull String to, int x, int y) {
        rabbitTemplate.convertAndSend(to, "hitYou&" + x + "&" + y + "&zero");
    }

    @Override
    public void hitPlayEvent(@NonNull String to, int x, int y) {
        rabbitTemplate.convertAndSend(to, "hitYou&" + x + "&" + y + "&injured");
    }

    @Override
    public void killedPlayEvent(@NonNull String to, int x, int y) {
        rabbitTemplate.convertAndSend(to, "hitYou&" + x + "&" + y + "&killed");
    }

    @Override
    public void gameOverPlayEvent(@NonNull String to, int x, int y) {
        rabbitTemplate.convertAndSend(to, "hitYou&" + x + "&" + y + "&defeated");
    }

    @Override
    public void escapedPlayEvent(@NonNull String to) {
        rabbitTemplate.convertAndSend(to, "escaped&Ваш соперник сбежал!");
    }

    @Override
    public void errorEvent(@NonNull String to) {
        rabbitTemplate.setExchange("amq.topic");
        rabbitTemplate.convertAndSend(to, "error&Что то пошло не так!");
    }

    @Override
    public void logoutEvent(@NonNull String to) {
        rabbitTemplate.convertAndSend(to, "logout&Вы вышли из игры!");
    }

    @Override
    public void inviteRejectedEvent(@NonNull String from, @NonNull String to) {
        rabbitTemplate.convertAndSend(from, from + "&invitedFail&" + to);
        rabbitTemplate.convertAndSend(to, from + "&invitedFail&" + to);
    }
}
