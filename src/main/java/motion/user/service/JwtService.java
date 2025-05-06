package motion.user.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import motion.user.repository.UserRepository;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;

@Service
@NoArgsConstructor
@Getter
public class JwtService {

    private UserRepository userRepository;
    private JwtKeyService jwtKeyService;

    @Autowired
    public JwtService(JwtKeyService jwtKeyService, UserRepository userRepository) {
        this.jwtKeyService = jwtKeyService;
        this.userRepository = userRepository;
    }

    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", userRepository.findByEmail(email).getUId());

        Key secretKey = jwtKeyService.getSecretKey();

        //System.out.println(Arrays.toString(Decoders.BASE64.decode(keyEncoded)));

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + 1000 * 60 * 15)) //15min
                .signWith(secretKey)
                .compact();
    }

    public String extractEmail(String token) {
        // extract the username from jwt token
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtKeyService.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractEmail(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    //FIX: development stage solution, not safe for deployment

}