package com.example.api_gateway.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JwtUtils {

	@Value("hdgfkhfkghdfkghkjfdhgkdfgdurbnyrbgjdgjfhkhjfkhskbnsdkjbfksdhhkhkhkghkhgkjdhfkdfhgkdhjfgb")
	private String secret;
	
	/**
     * Obtiene las reclamaciones (claims) de un token JWT.
     * @param token el token JWT
     * @return las reclamaciones del token
     */
	public Claims getClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(secret)
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

    /**
     * Verifica si un token JWT ha expirado.
     * @param token el token JWT
     * @return <code>true</code> si el token ha expirado, <code>false</code> en caso contrario
     */
    public boolean isExpired(String token) {
        try {
            return getClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extrae el ID de usuario de un token JWT.
     * @param token el token JWT
     * @return el ID de usuario
     */
    public Integer extractUserId(String token) {
        try {
            return Integer.parseInt(getClaims(token).getSubject());
        } catch (Exception e) {
        	System.out.println("Error Requested");
            return null;
        }
    }
	
	
}
