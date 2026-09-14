package com.example.lifecycle;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupPhases implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        // ApplicationRunner runs after the context is refreshed and before
        // ApplicationReadyEvent. Use it for startup work that needs parsed
        // command-line arguments, not for bean construction or basic wiring.
        System.out.println("[ApplicationRunner] Startup work can run now");
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        // ApplicationReadyEvent is later than ApplicationRunner. It is a good
        // signal for readiness metrics, warmups, or announcing availability.
        System.out.println("[ApplicationReadyEvent] Application is ready to serve traffic");
    }
}