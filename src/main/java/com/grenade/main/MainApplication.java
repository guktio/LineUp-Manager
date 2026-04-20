package com.grenade.main;

import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class MainApplication implements CommandLineRunner{
	
	private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

	public static void main(String[] args) {
 		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(MainApplication.class, args);
	}

	@Override
    public void run(String... args) {
        logger.info("Started!");
        logger.debug("Debug mode activated!");
    }
}