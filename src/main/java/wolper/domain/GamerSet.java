package wolper.domain;


import lombok.Builder;
import lombok.Getter;

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
    public boolean killedEnough() { return killed > ALL_SHIPS_CHECKS;}
}


