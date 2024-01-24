package wolper.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LogicException extends RuntimeException {

    private String errCode;
    private String errMsg;

    public LogicException(String errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

}


