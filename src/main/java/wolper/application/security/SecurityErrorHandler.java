package wolper.application.security;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

import java.io.IOException;

@RequiredArgsConstructor
public class SecurityErrorHandler extends SimpleUrlAuthenticationFailureHandler {

    private final String secret;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        if (exception.getClass().isAssignableFrom(SessionAuthenticationException.class)) {
            request.setAttribute("secret", secret);
            request.getRequestDispatcher("/double_reg/" +
                    request.getParameterValues("username")[0]).forward(request, response);
        } else if (exception.getClass().isAssignableFrom(BadCredentialsException.class)) {
            request.getRequestDispatcher("/errors").forward(request, response);
        } else super.onAuthenticationFailure(request, response, exception);
    }
}


