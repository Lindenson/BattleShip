package wolper.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({ "wolper.application.security", "wolper.web", "wolper.application.config", "wolper.game", "wolper.domain" })
public class TestConfig { }