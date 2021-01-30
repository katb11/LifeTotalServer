package service;

import java.util.Base64;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import model.User;

public class JWT {

    private static String SECRET_KEY = System.getenv("JWT_KEY");

    public static User decodeJWT(String jwt) {
        Claims claims = Jwts.parser()
                .setSigningKey(Base64.getEncoder().encode(SECRET_KEY.getBytes()))
                .parseClaimsJws(jwt).getBody();

        if (claims == null) {
            return null;
        } else {
            return(new User((String)claims.get("username"), (Integer)claims.get("accountID")));
        }
    }
}