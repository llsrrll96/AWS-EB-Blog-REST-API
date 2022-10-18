package com.springboot.blog.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.springboot.blog.exception.BlogAPIException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtTokenProvider 
{
	@Value("${app.jwt-secret}")
	private String jwtSecret;
	@Value("${app.jwt-expiration-milliseconds}")
	private int jwtExpirationInMs;
	
	public String generateToken(Authentication authentication)
	{
		String username= authentication.getName();
		System.out.println("username: "+ username);
		
		Date currentDate = new Date();
		Date expireDate= new Date(currentDate.getTime() + jwtExpirationInMs);
		
		String token = Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(expireDate)
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
		return token; 
	}
	
	// get username from the token
	public String getUsernameFromJWT(String token) {
		Claims claims = Jwts.parser()
				.setSigningKey(jwtSecret) // 시크릿 키 필요
				.parseClaimsJws(token)
				.getBody();
		return claims.getSubject();
	}

	public boolean validateToken(String token)
	{
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
			return true;
		}catch(SignatureException ex) {
			throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Invalid JWT signature");
		}catch(MalformedJwtException ex) {
			throw new BlogAPIException(HttpStatus.BAD_GATEWAY, "Invalid JWT token");
		}catch(ExpiredJwtException ex) {
			throw new BlogAPIException(HttpStatus.BAD_GATEWAY, "Expired JWT token");
		}catch(UnsupportedJwtException ex) {
			throw new BlogAPIException(HttpStatus.BAD_GATEWAY, "Unsupported JWT token");
		}catch(IllegalArgumentException ex) {
			throw new BlogAPIException(HttpStatus.BAD_GATEWAY, "JWT claims string is empty");
		}
	}
}
