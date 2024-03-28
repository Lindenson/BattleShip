package wolper.web.home;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import wolper.persistence.inmemory.GameDao;
import wolper.persistence.database.UserDao;
import wolper.domain.Gamer;
import wolper.domain.LogicException;
import wolper.game.*;
import wolper.application.security.SessionService;

import java.util.Objects;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
    private final UserDao userDAO;
    private final GameLogic gameLogic;
    private final GameDao gameDao;
    private final SessionService session;

    @Value("${logout.secret}")
    public String secret;


    @RequestMapping({"/", "/home"})
    public ModelAndView home() {
        return new ModelAndView("home");
    }

    @RequestMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    @RequestMapping("/errors")
    public ModelAndView error() {
        return new ModelAndView("errors");
    }

    @RequestMapping("/reg_error")
    public ModelAndView regError() {
        return new ModelAndView("reg_error");
    }

    //Контроллер регистрации - попытка залогиниться дважды при открытой сессии в другом окне
    @RequestMapping("/double_reg/{name}")
    public ModelAndView doubleRegChecker(@PathVariable("name") String name, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("double_reg");
        modelAndView.addObject("name", name);
        ifHasRightThenToLogout(name, request, modelAndView);
        return modelAndView;
    }


    //Контроллер регистрации - логринимся занова а прошлые сессии надежно убиваем
    @RequestMapping("/double_reg_final/{name}/{token}")
    public ModelAndView doubleRegKiller(@PathVariable("name") String name, @PathVariable("token") String secret, ServletRequest request) {
        String token = String.format("token-%d", gameDao.getGamerByName(name).getToken());
        if (secret.equals(token)) {
            session.expireAndKillUserSessions(name, request.getLocalPort());
        }
        return new ModelAndView("redirect:/home");
    }

    //Контроллер регистрации нового игрока
    @RequestMapping("/register")
    public ModelAndView register(HttpServletResponse response, Model model) {
        model.addAttribute("gamer", new Gamer());
        return new ModelAndView("register");
    }

    //Контроллер входа нового игрока
    @RequestMapping("/game")
    public ModelAndView welcome() {
        return new ModelAndView("redirect:game");
    }

    //Контроллер входа нового игрока после регистрации
    @RequestMapping("/success")
    public ModelAndView successReg() {
        return new ModelAndView("success_reg");
    }

    //Контроллер регистрации нового игрока
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView getRegistered(@ModelAttribute("gamer") @Valid Gamer gamer, BindingResult result,
                                      @RequestParam("password2") String password2)
    {
        //Валидация формы
        if (result.hasErrors()) return new ModelAndView("register");
        if (!gamer.getPassword().equals(password2)) return new ModelAndView("not_equal");

        //Проверка совпадения имени игрока
        if (userDAO.ifDoubleGamer(gamer.getName())) return new ModelAndView("reg_error");

        //Сохранить игрока в базе данных
        userDAO.saveGamer(gamer);
        return new ModelAndView("redirect:success");
    }


    //Свомп контроллеры для передачи игровой инфорамации
    @RabbitListener(queues = "infoExchange")
    public void handleSubscription(Message<String> message) {
        String payload = message.getPayload();
        try{
            String[] names = payload.split("&");
            //Полчено приглашение
            if (names[0].equals("invite")) {
                gameLogic.inviteOneAnother(names[1], names[2]);
            }
        } catch (Exception e){
            log.error("wrong message format: {}", e.getMessage());
        }
    }


    private void ifHasRightThenToLogout(String name, HttpServletRequest request, ModelAndView modelAndView) {
        String secretForLogout = (String) request.getAttribute("secret");
        if (Objects.nonNull(secretForLogout) && secretForLogout.equals(secret)) {
            String token = String.format("token-%d", gameDao.getGamerByName(name).getToken());
            modelAndView.addObject("token", token);
        } else {
            modelAndView.addObject("token", "unknown");
        }
    }

}

//Обработка исключений с выводом сообщений в виде веб-страниц
@ControllerAdvice
class GlobalControllerExceptionHandler {
    @ExceptionHandler(LogicException.class)
    public ModelAndView gameExceptions(LogicException ex) {
        ModelAndView modelAndView = new ModelAndView("exception");
        modelAndView.addObject("errCode", ex.getErrCode());
        modelAndView.addObject("errMsg", ex.getErrMsg());
        return modelAndView;
    }
}
