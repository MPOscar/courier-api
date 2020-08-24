package courier.uy.core.security.jwt;

import courier.uy.core.db.ParamsDAO;
import courier.uy.core.entity.Param;
import courier.uy.core.entity.Usuario;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.security.CustomUserDetailsService;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${security.jwt.token.secret-key}")
    private String secretKey = "secretKey";

    @Value("${security.jwt.token.expire-length:14400000}")
    private long validityInMilliseconds = 14400000; // 4h

    private CustomUserDetailsService userDetailsService;

    @Autowired
    private ParamsDAO paramsDAO;

    public JwtTokenProvider(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public JsonWebSignature buildToken(Usuario usuario, UsuarioEmpresa usuarioEmpresa, Param jwtKey, Float expTime) {
        final JwtClaims claims = new JwtClaims();

        claims.setSubject(usuario.getId().toString());
        claims.setStringClaim("usuario", usuario.getEmail());

        if (usuarioEmpresa != null) {
            claims.setStringClaim("usuario_empresa", "" + usuarioEmpresa.getId());
            claims.setStringClaim("empresa", usuarioEmpresa.getEmpresa().getGln());
            claims.setStringClaim("rol", usuarioEmpresa.getRol());
        }

        claims.setIssuedAtToNow();
        claims.setGeneratedJwtId();
        claims.setExpirationTimeMinutesInTheFuture(expTime);

        final JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
        jws.setKey(new HmacKey(jwtKey.getValor().getBytes()));

        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);

        return jws;
    }

    public String createTokenOld(Usuario usuario, UsuarioEmpresa usuarioEmpresa) {
        Claims claims = Jwts.claims().setSubject(usuario.getUsuario() != null ? usuario.getUsuario() : usuario.getEmail());
        if(usuario.getUsuario() != null)
            claims.put("usuario", usuario.getUsuario());
        else
            claims.put("usuario", usuario.getEmail());

        if (usuarioEmpresa != null) {
            claims.put("usuario_empresa", "" + usuarioEmpresa.getId());
            claims.put("empresa", usuarioEmpresa.getEmpresa().getGln());
            claims.put("rol", usuarioEmpresa.getRol());
        }

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token) + "+" + getUsuarioEmpresa(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("usuario", String.class);
    }

    public String getUsuarioEmpresa(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("usuario_empresa", String.class);
    }

    public String resolveToken(HttpServletRequest req) throws JoseException {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return decryptToken(bearerToken.substring(7));
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            //log.error("Expired or invalid JWT token");
            return false;
        }
    }

    public JsonWebEncryption encryptToken(String jwt, Param jwtKey) throws JoseException {
        JsonWebEncryption jwe = new JsonWebEncryption();

        jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.DIRECT);
        jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
        jwe.setKey(new HmacKey(jwtKey.getValor().getBytes()));
        jwe.setContentTypeHeaderValue("JWT");
        jwe.setPayload(jwt);
        String a = jwe.getPlaintextString();
        return jwe;
    }

    private String decryptToken(String jwt) throws JoseException {
        JsonWebEncryption jwe = new JsonWebEncryption();
        Param jwtKey = paramsDAO.findByNombre("JWT_SECRET_KEY");
        jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.DIRECT);
        jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
        jwe.setKey(new HmacKey(jwtKey.getValor().getBytes()));
        jwe.setContentTypeHeaderValue("JWT");
        jwe.setCompactSerialization(jwt);
        return jwe.getPayload();
    }

}