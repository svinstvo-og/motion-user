package motion.user.service;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Getter
@Service
public class JwtKeyService {

    // TODO make kay as env variable
    private final String keyEncoded = "QevWIEc/vDDQjEuuEC+26f9d8RejWH/RYn0mhcrMZFY=";

    JwtKeyService() throws NoSuchAlgorithmException {
        //logic for generating random keys
//        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
//        SecretKey secretKey = keyGenerator.generateKey();
//        keyEncoded = Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    public SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.keyEncoded);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}