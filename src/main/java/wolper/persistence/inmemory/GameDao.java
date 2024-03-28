package wolper.persistence.inmemory;

import lombok.NonNull;
import wolper.domain.GamerSet;
import wolper.domain.ShipList;

import java.util.Collection;


public interface GameDao {
    void createGamerByName(@NonNull String name);

    GamerSet getGamerByName(@NonNull String name);

    Collection<String> getAllGamersNames();

    Collection<GamerSet> getAllGamers();

    void updateGamer(@NonNull GamerSet gamer);

    void removeGamerByName(@NonNull String name);

    boolean tryUpdateGamersAtomically(@NonNull GamerSet beforeA, @NonNull GamerSet beforeB,
                                      @NonNull GamerSet afterA, @NonNull GamerSet afterB);

    boolean ifPlaying(@NonNull String name);

    ShipList getShipListByName(@NonNull String name);

    void updateShipListByName(@NonNull String name, @NonNull ShipList shipList);
}
