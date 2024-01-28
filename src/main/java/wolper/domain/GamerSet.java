package wolper.domain;


import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.util.concurrent.ThreadLocalRandom;

@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode
public class GamerSet {

    public static final int ALL_SHIPS_CHECKS = 19;

    private final String name;
    private final boolean free;
    private final String playWith;
    private final String invitedBy;
    private final int killed;
    private final int rating;
    private final int token = ThreadLocalRandom.current().nextInt();


    public boolean ifKilledEnough() { return killed > ALL_SHIPS_CHECKS;}

    @NonNull
    public static GamerSet addKilled(GamerSet source)  {
        return source.toBuilder().killed(source.killed + 1).build();
    }

    @NonNull
    public static GamerSet withAddRating(GamerSet base) {
        return base.toBuilder().free(true)
                .playWith("").invitedBy("").killed(0).rating(base.getRating() + 1).build();
    }

    @NonNull
    public static GamerSet withUntouchedRating(GamerSet base) {
        return base.toBuilder().free(true)
                .playWith("").invitedBy("").killed(0).build();
    }

    @NonNull
    public static GamerSet freshGamerInstance( String name, int rating) {
        return GamerSet.builder().free(true).name(name).playWith("")
                .invitedBy("").rating(rating).build();
    }

    @NonNull
    public static GamerSet makePlayingWith(GamerSet who, String with) {
        return who.toBuilder().playWith(with).free(false).build();
    }

}


