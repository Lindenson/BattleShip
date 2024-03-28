package wolper.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan("wolper")
public class BattleShip {
    public static void main(String[] args) {
        SpringApplication.run(BattleShip.class, args);
    }
}