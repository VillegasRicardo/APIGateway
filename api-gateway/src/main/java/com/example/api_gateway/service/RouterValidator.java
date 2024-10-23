package com.example.api_gateway.service;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.stereotype.Service;
import org.springframework.http.server.reactive.ServerHttpRequest;

@Service
public class RouterValidator {

	public static final List<String> openEndpoints = List.of(
			"/auth"
			);
	
	public Predicate<ServerHttpRequest> isSecured = serverHttRequest ->
	openEndpoints.stream().noneMatch(uri -> serverHttRequest.getURI().getPath().contains(uri));
	
}
