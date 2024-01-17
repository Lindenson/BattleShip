package wolper.logic;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import java.io.IOException;

public class SecurityErrorHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (exception.getClass().isAssignableFrom(SessionAuthenticationException.class)) {
            request.getRequestDispatcher("/double_reg/" + request.getParameterValues("username")[0]).forward(request, response);
        }
        else if (exception.getClass().isAssignableFrom(BadCredentialsException.class)) {
            request.getRequestDispatcher("/errors").forward(request, response);}
        else super.onAuthenticationFailure(request, response, exception);
    }
}


