package wolper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BattleShip {
    public static void main(String[] args) {
        SpringApplication.run(BattleShip.class, args);
    }
}