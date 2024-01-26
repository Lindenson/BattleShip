package wolper.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder(toBuilder = true)
@Getter
public class GamerSet {

    public static final int ALL_SHIPS_CHECKS = 19;

    private final String name;
    private final boolean free;
    private final String playWith;
    private final String invitedBy;
    private final int killed;
    private final int rating;

    public static GamerSet addKilled(GamerSet source)  {
        return source.toBuilder().killed(source.killed + 1).build();
    }

    public boolean ifKilledEnough() { return killed > ALL_SHIPS_CHECKS;}

    public static GamerSet withAddRating(@NonNull GamerSet base) {
        return base.toBuilder().free(true)
                .playWith("").invitedBy("").killed(0).rating(base.getRating() + 1).build();
    }

    public static GamerSet withUntouchedRating(@NonNull GamerSet base) {
        return base.toBuilder().free(true)
                .playWith("").invitedBy("").killed(0).build();
    }

    public static GamerSet freshGamerInstance(@NonNull String name, int rating) {
        return GamerSet.builder().free(true).name(name).playWith("")
                .invitedBy("").rating(rating).build();
    }

}


