package wolper.dao;

import org.springframework.transaction.annotation.Transactional;
import wolper.domain.Gamer;

public interface UserDao {
    boolean ifDoubleGamer(String chalenger);

    @Transactional(rollbackFor = Exception.class)
    void saveGamer(Gamer candidat);

    void setRatingOnExit(String name, int rating);

    Integer getRatingOnStartUp(String name);
}
