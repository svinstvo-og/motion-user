package motion.user.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.util.UUID;

public record UserRecord(
        @JsonProperty("user-id")
        UUID userId,
        String username,
        @JsonProperty("created-at")
        Timestamp createdAt) {
}
