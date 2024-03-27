package wolper.dao;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wolper.domain.GamerSet;
import wolper.domain.ShipList;
import wolper.events.EventMessenger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;


@Service("gameDao")
@RequiredArgsConstructor
public class GameInMemoryDaoImpl implements GameDao {

    private static final ConcurrentMap<String, GamerSet> listOfGamer = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, ShipList> listOfShips = new ConcurrentHashMap<>();


    private final UserDao userDAO;
    private final EventMessenger eventMessenger;


    @Override
    public void createGamerByName(@NonNull String name) {
        int rating = userDAO.getRatingOnStartUp(name);
        listOfGamer.put(name, GamerSet.freshGamerInstance(name, rating));
        //Даем время вновьприбывшему подключиться к Вебсокету
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException ie) {
            // игнорируем
        } finally {
            eventMessenger.listOfPlayersChangedEvent(getAllGamersNames());
        }
    }

    @Override
    public GamerSet getGamerByName(@NonNull String name) {
        return listOfGamer.get(name);
    }

    @Override
    public Collection<String> getAllGamersNames() {
        return listOfGamer.keySet();
    }

    @Override
    public Collection<GamerSet> getAllGamers() {
        return listOfGamer.values();
    }

    @Override
    public void updateGamer(@NonNull GamerSet gamer) {
        listOfGamer.put(gamer.getName(), gamer);
    }

    @Override
    public void removeGamerByName(@NonNull String name) {
        informPartnerOnGoOut(name);
        deleteGamerByName(name);
    }

    @Override
    public boolean tryUpdateGamersAtomically(@NonNull GamerSet beforeA, @NonNull GamerSet beforeB,
                                             @NonNull GamerSet afterA, @NonNull GamerSet afterB) {
        synchronized (this) {
            GamerSet beforeGamerA = getGamerByName(beforeA.getName());
            GamerSet beforeGamerB = getGamerByName(beforeB.getName());

            if (Objects.isNull(beforeGamerA) || Objects.isNull(beforeGamerB)) return false;
            if (!(beforeGamerA.equals(beforeA) && beforeGamerB.equals(beforeB))) return false;

            updateGamer(afterA);
            updateGamer(afterB);
            return true;
        }
    }

    @Override
    public boolean ifPlaying(@NonNull String name) {
        return !(Objects.isNull(listOfGamer.get(name)) || listOfGamer.get(name).isFree());
    }

    @Override
    public ShipList getShipListByName(@NonNull String name) {
        return listOfShips.get(name);
    }

    @Override
    public void updateShipListByName(@NonNull String name, @NonNull ShipList shipList) {
        listOfShips.put(name, shipList);
    }

    private void informPartnerOnGoOut(@NonNull String name) {
        Optional.ofNullable(getGamerByName(name))
                .map(GamerSet::getPartner)
                .map(this::getGamerByName)
                .ifPresent(partner -> {
                    updateGamer(GamerSet.withUntouchedRating(partner));
                    eventMessenger.escapedPlayEvent(partner.getName());
                });
    }

    private void deleteGamerByName(@NonNull String name) {
        Optional.ofNullable(GameInMemoryDaoImpl.listOfGamer.remove(name))
                .ifPresent(player -> {
                    userDAO.setRatingOnExit(name, player.getRating());
                    GameInMemoryDaoImpl.listOfShips.remove(name);
                    eventMessenger.listOfPlayersChangedEvent(getAllGamersNames());
                });
    }

}







