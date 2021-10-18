package com.signicat.interview.security.util;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.signicat.interview.security.core.userdetails.UserDetailsExtended;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;

import static com.nimbusds.jose.JWSAlgorithm.ES256;

@Component
@Slf4j
public class TokenFactory {

    private static ECKey key;
    @PostConstruct
    protected void init() throws JOSEException {
        key = new ECKeyGenerator(Curve.P_256).keyID("sample-secret").generate();
    }
    public String generateToken(UserDetailsExtended userDetail) throws JOSEException {
        SignedJWT signedJWT = new SignedJWT(createHeader(), createClaimSet(userDetail));
        signedJWT.sign(new ECDSASigner(key.toECPrivateKey()));

        return signedJWT.serialize();
    }

    private  JWTClaimsSet createClaimSet(UserDetailsExtended userDetail) {
        long TOKEN_EXPIRATION_TIME = 600L;
        JWTClaimsSet claims = new JWTClaimsSet.Builder().subject(String.valueOf(userDetail.getUserId())).
                claim("username",userDetail.getUsername()).claim("groups",userDetail.getUserGroups()).
                issueTime(Date.from(Instant.now())).expirationTime(Date.from(Instant.now().plusSeconds(TOKEN_EXPIRATION_TIME))).build();
        return claims;
    }

    private  JWSHeader createHeader() {
        return new JWSHeader.Builder(ES256).type(JOSEObjectType.JWT).keyID(key.getKeyID()).build();
    }

    public String getUsernameFromToken(String jwtToken) throws ParseException {

        return getClaimsFromToken(jwtToken, "username").toString();

    }

    private Object getClaimsFromToken(String jwtToken, String claimResolver) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(jwtToken);
        return signedJWT.getJWTClaimsSet().getClaim(claimResolver);
    }

    public boolean validateToken(String jwtToken, UserDetails userDetails) throws JOSEException, ParseException {

        return SignedJWT.parse(jwtToken).verify(new ECDSAVerifier(key.toECPublicKey()));
    }
}
