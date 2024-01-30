package wolper.domain;


import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode
public class GamerSet {

    public static final int ALL_SHIPS_CHECKS = 19;

    private final String name;
    private final String partner;
    private final int scores;
    private final int rating;
    private final int token = ThreadLocalRandom.current().nextInt();


    public boolean isFree() { return Objects.isNull(partner) || partner.isBlank(); }

    public boolean ifKilledEnough() { return scores > ALL_SHIPS_CHECKS;}

    @NonNull
    public static GamerSet addKilled(GamerSet source)  {
        return source.toBuilder().scores(source.scores + 1).build();
    }

    @NonNull
    public static GamerSet withAddRating(GamerSet base) {
        return base.toBuilder().partner("").scores(0).rating(base.getRating() + 1).build();
    }

    @NonNull
    public static GamerSet withUntouchedRating(GamerSet base) {
        return base.toBuilder().partner("").scores(0).build();
    }

    @NonNull
    public static GamerSet freshGamerInstance( String name, int rating) {
        return GamerSet.builder().name(name).partner("").rating(rating).build();
    }

    @NonNull
    public static GamerSet makePlayingWith(GamerSet who, String with) {
        return who.toBuilder().partner(with).build();
    }

}


