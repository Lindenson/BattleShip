package wolper.application.security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import wolper.messaging.EventMessenger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service("loggedNames")
@RequiredArgsConstructor
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionService {

    private final SessionRegistry sessionRegistry;
    private final EventMessenger eventMessenger;


    public List<SessionInformation> getActiveSessions() {
        List<SessionInformation> activeSessions = new ArrayList<>();
        for(Object principal : sessionRegistry.getAllPrincipals()) {
            activeSessions.addAll(sessionRegistry.getAllSessions(principal, false));
        }
        return activeSessions;
    }

    public List<String> getLoggedUsersNames() {
        List<SessionInformation> session = getActiveSessions();
        List<String> result = new ArrayList<>();
        for (SessionInformation sessionInformation : session) {
            Object principalObj = sessionInformation.getPrincipal();
            if (principalObj instanceof User user) {
                result.add(user.getUsername());
            }
        }
    return result;
    }


    //Длелаем сессию expired
    //Применяем для того, чтобы наш пользователь регистрируясь в другом окне
    //Таким образом состаривал и убивал все сесии в других окнах и на других машинах
    public void expireAndKillUserSessions(String username, int port) {
        if (username==null) return;
	    Set<SessionInformation> sessionID=new HashSet<>();
            for (Object principal : sessionRegistry.getAllPrincipals()) {
                if (principal instanceof User) {
                    UserDetails userDetails = (UserDetails) principal;
                    if (userDetails.getUsername().equals(username)) {
                        for (SessionInformation information : sessionRegistry.getAllSessions(userDetails, true)) {
			                sessionID.add(information);
                            eventMessenger.logoutEvent(((User) principal).getUsername());
                        }
                    }
                }
            }
	    killExpiredSession(sessionID, port);
      }


    //Надежно убиваем сессию (для которой мы сделали expired)
    //Проблема контейнера сервлетов в том, что он не убивает сессиии которые мы искуственно состарили
    //через вызов sessionInformation.expireNow() пока эта сессия не сделает реквест
    //И вот костыль - реквест вызываем в ручную, через РестТемплейт
    //В результате открытая на другой машине сессия в браузере закрывается с переходом на страницу логина
    //То же семое можно применить и для контроля закрытия окна браузера с последующим закрытием сессии
    //Для этого случая нужно доделать тикер
    void killExpiredSession(Set<SessionInformation> idList, int port) {
        try {
            for (SessionInformation sessionInfo : idList) {
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.add("Cookie", "JSESSIONID=" + sessionInfo.getSessionId());
                HttpEntity<Object> requestEntity = new HttpEntity<>(null, requestHeaders);
                RestTemplate rt = new RestTemplate();
                sessionInfo.expireNow();
                rt.exchange("http://localhost:"+port, HttpMethod.GET, requestEntity, String.class);
            }
        } catch (Exception ex) {
            //не допустим никаких исключений
        }
    }
}
