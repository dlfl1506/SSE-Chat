package com.cos.reactorChat.controller;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@CrossOrigin
@RestController
public class SseChatController {
	
	Sinks.Many<String> sink;
	
	public SseChatController() {
		this.sink = Sinks.many().multicast().onBackpressureBuffer();
	}
	
	@GetMapping("/send")
	public void send(String username, String msg) {
		String content = username + " : " + msg;
		sink.tryEmitNext(content);
	}
	
	
	@GetMapping(value = "/sse")
	public Flux<ServerSentEvent<String>> sse() {
		return sink.asFlux().map(e -> ServerSentEvent.builder(e).build()).doOnCancel(()->{
			sink.asFlux().blockLast(); 
		}); // 구독
	}
}