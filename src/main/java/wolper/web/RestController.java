package wolper.web;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import wolper.persistence.inmemory.GameDao;
import wolper.domain.BoardOfShips;
import wolper.domain.GamerSet;
import wolper.domain.ShipList;
import wolper.domain.StepsMe;
import wolper.game.*;
import java.security.Principal;
import java.util.Collection;
import java.util.Objects;


//Рест контроллеры для передачи игровой инфорамации

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/rest")
public class RestController {

        public final GameDao gameDao;
        public final ShipMapper shipMapper;
        public final GameLogic crossGamerInfoBuss;


        //Контроллер сохранения расстановки кораблей
        @PostMapping("/update/{gamer}")
        @ResponseBody
        public ShipList update(@PathVariable String gamer, @RequestBody @Nullable BoardOfShips boOfS) {
            if (Objects.isNull(boOfS)) {
                return gameDao.getShipListByName(gamer);
            }
            ShipList shipsCreated = shipMapper.map(boOfS);

            if (Objects.nonNull(shipsCreated)) {
                gameDao.updateShipListByName(gamer, shipsCreated);
                crossGamerInfoBuss.informPartnerOfFinishedSetUp(gamer);
            }
            else gameDao.removeGamerByName(gamer);
            return shipsCreated;
        }


        //Контроллер выдачи информации об участниках поединка
        @GetMapping("/gamers")
        @ResponseBody
        public Collection<GamerSet> gamers() {
            return gameDao.getAllGamers();
        }


        //Контроллер для приема акцепта приглашения поиграть
        @PostMapping("/accept/{from}/{to}")
        @ResponseBody
        public String[] accepted(@PathVariable String from, @PathVariable String to) {
            crossGamerInfoBuss.acceptInvitation(from, to);
            return new String[] {"inv","OK"};
        }


        //Контроллер для отказа в приглашении поиграть
        @PostMapping("/reject/{from}/{to}")
        @ResponseBody
        public String[] rejected(@PathVariable String from, @PathVariable String to) {
            crossGamerInfoBuss.rejectInvitation(from, to);
            return new String[] {"rej","OK"};
        }

        //Контроллер для приема ходов соперников
        @PostMapping("/move/{from}/{to}")
        @ResponseBody
        public String [] move(@PathVariable(value = "from") String from,
                                  @PathVariable(value = "to") String to,
                                  @RequestBody @NotNull StepsMe step, Principal principal)
        {
            String name = principal.getName();
            //Безопасность. Проверяем, не фальсифицирован ли ход
            if (!name.equals(from)) return new String[] {"error", "вы жулик"};
            String hit=crossGamerInfoBuss.doNextMove(from, to, step.x(), step.y());
            if (hit.equals("error")) return new String[] {hit,"ваш ход не принят"};

            return new String[] {hit,"OK"};
        }
}
