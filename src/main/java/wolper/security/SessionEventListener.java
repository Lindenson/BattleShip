package wolper.security;


import jakarta.servlet.http.HttpSessionEvent;
import lombok.AllArgsConstructor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.stereotype.Component;
import wolper.logic.AllGames;


@Component
@AllArgsConstructor
public class SessionEventListener extends HttpSessionEventPublisher {

    private final AllGames allGames;
    private final SessionRegistry sessionRegistry;

    //Установка таймаута сессии и определение имени пользователя
    @Override
    public void sessionCreated(HttpSessionEvent event) {
        super.sessionCreated(event);
        event.getSession().setMaxInactiveInterval(60*3);
    }

    //Удаляем запись об игре из репозитория когда игрок уходит
    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        String name;
        SessionInformation sessionInfo = sessionRegistry.getSessionInformation(event.getSession().getId());
        UserDetails ud = null;
        if (sessionInfo != null) {
            ud = (UserDetails) sessionInfo.getPrincipal();
        }
        if (ud != null) {
            name=ud.getUsername();
            //Извещаем соперников что мы ушли
            allGames.removeByName(name);
        }
        //---
        super.sessionDestroyed(event);
    }
}

