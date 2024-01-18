package wolper.domain;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


//Сущность для сохранения в базе данных
@Data
public class Gamer {

    @NotEmpty(message = "Введите непустое имя")
    @Pattern(regexp="[a-zA-Z0-9]*", message = "Английские буквы и/или цифры без пробелов")
    private String name;

    @NotEmpty(message = "Введите непустой пароль")
    @Pattern(regexp="[a-zA-Z0-9]*", message = "Английские буквы и/или цифры без пробелов")
    private String password;

    private String role;
}
