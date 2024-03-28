package wolper.persistence.database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import wolper.domain.Gamer;
import wolper.domain.LogicException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Repository
@EnableTransactionManagement
public class UserDaoImpl implements UserDao {

    public UserDaoImpl(NamedParameterJdbcTemplate template, PasswordEncoder passwordEncoder) {
        this.template = template;
        this.passwordEncoder = passwordEncoder;
    }
    private final NamedParameterJdbcTemplate template;
    private final PasswordEncoder passwordEncoder;


    @Override
    public boolean ifDoubleGamer(String chalenger) {
        String ifQuery = "select username from USER_AUTHENTICATION where username = :name";
        List<String> results;
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("name", chalenger);
        try {
            results = template.queryForList(ifQuery, namedParameters, String.class);
        }
        catch (DataAccessException e) {
            throw new LogicException("Ошибка чтения БД", e.getMessage());
        }
        return !results.isEmpty();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveGamer(Gamer candidat) {

        String ifQuery1 = "insert into USER_AUTHENTICATION (username, password, enabled, won)"
                                    + "VALUES (:name, :passwd, :enabled, :won)";
        String ifQuery2 = "insert into USER_AUTHORIZATION (username, role)"
                                    + "VALUES (:username, :role)";

        KeyHolder holder = new GeneratedKeyHolder();
        String encoded = passwordEncoder.encode(candidat.getPassword());

        //Транзакция
        Map<String, Object> parameters1 = new HashMap<>();
        parameters1.put("name", candidat.getName());
        parameters1.put("passwd", encoded);
        parameters1.put("enabled", true);
        parameters1.put("won", 0);
        SqlParameterSource parameterSource1 = new MapSqlParameterSource(parameters1);
        try {
            template.update(ifQuery1, parameterSource1, holder);
        }
        catch (DataAccessException e) {
            throw new LogicException("Ошибка сохраниения пользователя в БД", e.getMessage());
        }
        
        Map<String, Object> parameters2 = new HashMap<>();
        parameters2.put("username", candidat.getName());
        parameters2.put("role", "GAMER");
        SqlParameterSource parameterSource2 = new MapSqlParameterSource(parameters2);

        try {
            template.update(ifQuery2, parameterSource2);
        }
        catch (DataAccessException e) {
            throw new LogicException("Ошибка сохраниения пользователя в БД", e.getMessage());
        }
    }


    @Override
    public void setRatingOnExit(String name, int rating) {
        String ifQuery = "update USER_AUTHENTICATION set won = :rating where username = :name";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("rating", rating);
        SqlParameterSource parameterSource = new MapSqlParameterSource(parameters);
        try {
            template.update(ifQuery, parameterSource);
        }
        catch (DataAccessException e) {
            log.error("Read user info error: {}", e.getMessage());
        }
    }


    @Override
    public Integer getRatingOnStartUp(String name) {
        String ifQuery = "select won from USER_AUTHENTICATION where username = :name";
        Map<String, Object> parameters = Map.of("name", name);
        Integer result;
        try {
            result = template.queryForObject(ifQuery, parameters, Integer.class);
        }
        catch (DataAccessException e) {
            log.error("Save user info error: {}", e.getMessage());
            return 0;
        }
        return result;
    }
}
