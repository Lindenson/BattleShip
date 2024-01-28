package wolper.logic;

import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wolper.domain.GamerSet;

import java.util.Objects;


@Service
@RequiredArgsConstructor
public class PlayerValidator {

    private final EventMessenger eventMessenger;

    public boolean ifAnyIsNotValid(@NonNull String from, @Nullable String to,
                                   @Nullable GamerSet inviter, @Nullable GamerSet invitee)
    {
        if (inviter == null && invitee == null) {
            eventMessenger.errorEvent(from);
            if (Objects.nonNull(to)) eventMessenger.errorEvent(to);
            return true;
        }
        if (invitee == null) {
            eventMessenger.escapedPlayEvent(from);
            return true;
        }
        if (inviter == null) {
            Objects.requireNonNull(to);
            eventMessenger.escapedPlayEvent(to);
            return true;
        }
        return false;
    }

    public boolean ifAnyIsNull(Object gamerFrom, Object gamerTo, Object updatedFrom, Object updatedTo) {
        return (Objects.isNull(gamerFrom) || Objects.isNull(gamerTo)
                || Objects.isNull(updatedFrom)) || Objects.isNull(updatedTo);
    }
}
