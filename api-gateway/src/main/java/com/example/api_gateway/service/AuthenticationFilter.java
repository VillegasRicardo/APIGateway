package com.example.api_gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

	@Autowired
	private RouterValidator validator;
	
	@Autowired
	private JwtUtils jwtUtils; 
	
	public static class Config{}
	
	public AuthenticationFilter(){ super(Config.class);}
	
	@Override
	public GatewayFilter apply(Config config) {
		
		return (exchange, chain) -> {
			
			var request = exchange.getRequest();
			
			ServerHttpRequest serverHttpRequest = null;
			if(validator.isSecured.test(request)) {
				if(authMissing(request)) {
					return onError(exchange , HttpStatus.UNAUTHORIZED);
				}
				
				String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
				
				if(authHeader != null && authHeader.startsWith("Bearer ")) {
					authHeader = authHeader.substring(7);
				}else {
					return onError(exchange , HttpStatus.UNAUTHORIZED);
				}
				
				if(jwtUtils.isExpired(authHeader)) {
					return onError(exchange , HttpStatus.UNAUTHORIZED);
				}
				
				serverHttpRequest = exchange.getRequest()
						.mutate()
						.header("userIdRequest", jwtUtils.extractUserId(authHeader).toString())
						.build();
			}
			
			return chain.filter(exchange.mutate().request(serverHttpRequest).build());
		};
	}
	
	private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus){
		ServerHttpResponse response = (ServerHttpResponse) exchange.getResponse();
		response.setStatusCode(httpStatus);		
		return null;
	}
	
	private boolean authMissing(ServerHttpRequest request) {
		return !request.getHeaders().containsKey("Authorization");
	}
	
}

