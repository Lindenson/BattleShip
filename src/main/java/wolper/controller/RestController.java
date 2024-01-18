package wolper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import wolper.domain.BoardOfShips;
import wolper.domain.GamerSet;
import wolper.domain.StepsMe;
import wolper.logic.*;
import java.security.Principal;
import java.util.Collection;
import java.util.List;



//Рест контроллеры для передачи игровой инфорамации

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/rest")
public class RestController {

        public final AllGames allGames;
        public final ShipMapper shipMapper;
        public final CrossGamerInfoBuss crossGamerInfoBuss;
        public final SimpMessageSendingOperations messaging;


        //Контроллер сохранения расстановки кораблей
        @PostMapping("/{nameGamer}/modelBoards")
        @ResponseBody
        public BoardOfShips saveGamerChoice(@PathVariable String nameGamer, @RequestBody BoardOfShips boOfS) {
            shipMapper.detectSips(nameGamer, boOfS);
            crossGamerInfoBuss.informPartnerOfFinishedSetUp(nameGamer);
            return boOfS;
        }


        //Тестовый контроллер сервис предоставляет отладочную информвцию (и заодно можно подглядкть корабли соперника)
        @GetMapping("/test/{name}")
        @ResponseBody
        public List<?> listGamerChoice(@PathVariable String name) {
            return allGames.getShipListByName(name).smallSipList;
        }


        //Контроллер выдачи информации об участниках поединка
        @GetMapping("/gamerInfo")
        @ResponseBody
        public Collection<GamerSet> getGamersInfo() {
            return allGames.getAllGamers();
        }


        //Контроллер для приема акцепта приглашения поиграть
        @GetMapping("/invitationAccepted/{acceptedBy}")
        @ResponseBody
        public String[] getAccepted(@PathVariable String acceptedBy) {
            String [] names = acceptedBy.split("&");
            if (names[0].equals("accepted")) {
                crossGamerInfoBuss.acceptInvitation(names[1], names[2]);
            }
            if (names[0].equals("rejected")) {
                crossGamerInfoBuss.rejectInvitation(names[1], names[2]);
            }
            return new String[] {"inv","OK"};
        }


        //Контроллер для приема ходов соперников
        @PostMapping("/doMove/{attacker}/{suffer}")
        @ResponseBody
        public String [] getMoves(@PathVariable(value = "attacker") String attacker,
                                  @PathVariable(value = "suffer") String suffer,
                                  @RequestBody StepsMe step, Principal principal)
        {
            String name = principal.getName();
            //Безопасность. Проверяем, не фальсифицирован ли ход
            if (!name.equals(attacker)) return new String[] {"you are a cheater", "error"};
            String hit=crossGamerInfoBuss.doNextMove(attacker, suffer, step.x(), step.y());
            return new String[] {hit,"OK"};
        }
}
