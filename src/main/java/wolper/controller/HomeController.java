package wolper.controller;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import wolper.logic.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
    private final GamerDAO gamerDAO;
    private final CrossGamerInfoBuss crossGamerInfoBuss;
    private final SessionService session;


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
    public ModelAndView doubleRegChecker(@PathVariable("name") String name) {
        ModelAndView modelAndView = new ModelAndView("double_reg");
        modelAndView.addObject("name", name);
        return modelAndView;
    }

    //Контроллер регистрации - логринимся занова а прошлые сессии надежно убиваем
    @RequestMapping("/double_reg_final/{name}")
    public ModelAndView doubleRegKiller(@PathVariable("name") String name, ServletRequest request) {
        session.expireAndKillUserSessions(name, request.getLocalPort());
        //TODO передалать на POST - так будет безопаснее.
        // Злой польщователь не сможет указав логин в URL завалить другого пользователя
        return new ModelAndView("redirect:/home");
    }

    //Контроллер регистрации нового игрока
    @RequestMapping("/register")
    public ModelAndView register(HttpServletResponse response, Model model) {
        model.addAttribute("gamer", new Gamer());
        return new ModelAndView("register");
    }

    //Контроллер входа нового игрока
    @RequestMapping("/goin")
    public ModelAndView welcome() {
        return new ModelAndView("redirect:mainflow");
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
        if (!gamer.getPassword().equals(password2)) return new ModelAndView("notequeal_paswords");

        //Проверка совпадения имени игрока
        if (gamerDAO.ifDoubleGamer(gamer.getName())) return new ModelAndView("reg_error");

        //Сохранить игрока в базе данных
        gamerDAO.saveGamer(gamer);

        //По каким то причинам вход пользователя через АПИ (ниже) не позволяет контролировать таймаут сессии
        //пока не разобрался почему так!!!! Приходиться возвращать пользователя на страницу логина
        //Authentication request = new UsernamePasswordAuthenticationToken(gamer.getName(), gamer.getPassword());
        //SecurityContextHolder.getContext().setAuthentication(request);
        return new ModelAndView("redirect:success");
    }


    //Свомп контроллеры для передачи игровой инфорамации
    @MessageMapping("/infoExchange")
    public void handleSubscription(String name) {
        String[] names = name.split("&");
        //Полчено приглашение
        if (names[0].equals("invite")) {
            crossGamerInfoBuss.inviteOneAnother(names[1], names[2]);
        }
        //Todo!!! Сделать полноценный обмен сообщениями между игроками
    }

}

//Обработка исключений с выводом сообщений в виде веб-страниц
@ControllerAdvice
class GlobalControllerExceptionHandler {
    @ExceptionHandler(LogicEception.class)
    public ModelAndView gameExceptions(LogicEception ex) {
        ModelAndView modelAndView = new ModelAndView("exception");
        modelAndView.addObject("errCode", ex.getErrCode());
        modelAndView.addObject("errMsg", ex.getErrMsg());
        return modelAndView;
    }
}
