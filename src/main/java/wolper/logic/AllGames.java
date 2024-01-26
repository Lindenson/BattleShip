package wolper.logic;

import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import wolper.dao.GamerDAO;
import wolper.domain.GamerSet;
import wolper.domain.ShipList;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;


//Класс объявлен как одиночка, так как все что он делает - это манипуляции с единственной на всю программу
//базой "ин-мемори" данных об игроках и о ходе игры, хранящейся в конкурентных коллекциях

@Service("allGames")
@RequiredArgsConstructor
public class AllGames {

    //Это основная игровая информация, доступная для всех игроков и, следовательно,  многих потоков
    private static final ConcurrentMap<String, GamerSet> listOfGamer = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, ShipList> listOfShips = new ConcurrentHashMap<>();



    private final GamerDAO gamerDAO;
    private final EventMessenger eventMessenger;



    @Async
    public void createGamerByName(@NonNull String name) {
        int rating = gamerDAO.getRatingOnStartUp(name);
        listOfGamer.put(name, GamerSet.freshGamerInstance(name, rating));
        //Даем время вновьприбывшему подключиться к Вебсокету
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException ie) {
            // игнорируем
        } finally {
            eventMessenger.listOfPlayersChangedEvent();
        }
    }

    public GamerSet getGamerByName(@NonNull String name) {
        return listOfGamer.get(name);
    }
    public Collection<GamerSet> getAllGamers() {
        return listOfGamer.values();
    }
    public void updateGamer(@NonNull GamerSet gamer) { listOfGamer.put(gamer.getName(), gamer); }

    public void removeByName(@NonNull String name) {
        informPartnerOnGoOut(name);
        deleteGamerByName(name);
    }

    public void updateGamersAtomically(@Nullable GamerSet gamerA, @Nullable GamerSet gamerB) {
        synchronized (this) {
            if(Objects.nonNull(gamerA)) listOfGamer.put(gamerA.getName(), gamerA);
            if(Objects.nonNull(gamerB)) listOfGamer.put(gamerB.getName(), gamerB);
        }
    }

    public boolean ifPlaying(@NonNull String name) {
        return !(Objects.isNull(listOfGamer.get(name)) || listOfGamer.get(name).isFree());
    }

    public ShipList getShipListByName(@NonNull String name) {
        return listOfShips.get(name);
    }
    public void setShipListByName(@NonNull String name, @NonNull ShipList shipList) { listOfShips.put(name, shipList); }

    private void deleteGamerByName(@NonNull String name) {
        Optional.ofNullable(listOfGamer.remove(name))
                .ifPresent(player -> {
                    gamerDAO.setRatingOnExit(name, player.getRating());
                    listOfShips.remove(name);
                    eventMessenger.listOfPlayersChangedEvent();
                });
    }

    private void informPartnerOnGoOut(@NonNull String name) {
        Optional.ofNullable(listOfGamer.get(name))
                .map(GamerSet::getPlayWith)
                .map(listOfGamer::get)
                .ifPresent(partner -> {
                    GamerSet resetPartner = partner.toBuilder().free(true)
                            .playWith("").invitedBy("").killed(0).build();
                    updateGamer(resetPartner);
                    eventMessenger.escapedPlayEvent(partner.getName());
                });
    }

}







