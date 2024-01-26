package wolper.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import wolper.security.SecurityErrorHandler;
import javax.sql.DataSource;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public UserDetailsManager users(DataSource dataSource) {
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        users.setUsersByUsernameQuery("select username, password, enabled from USER_AUTHENTICATION where username = ?");
        users.setAuthoritiesByUsernameQuery("select `username`, `role` from USER_AUTHORIZATION where username = ?");
        return users;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return
        http
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/game")
                        .failureForwardUrl("/regerror")
                        .failureHandler(new SecurityErrorHandler())
                )
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/", "/home", "/login", "/register", "success",
                                "js/**", "/plugins/**","/double_reg_final/**", "/double_reg/**",
                                "/regerror", "/errors")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/home")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID", "SESSION")
                )
                .sessionManagement(sessionManagement -> sessionManagement
                    .invalidSessionUrl("/home")
                    .maximumSessions(1)
                    .maxSessionsPreventsLogin(true)
                    .expiredUrl("/")
                    .sessionRegistry(sessionRegistry())
                )
                .build();
    }

    // Настройка класса, читающего данные сессии - для списка залогиненных пользователей
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}