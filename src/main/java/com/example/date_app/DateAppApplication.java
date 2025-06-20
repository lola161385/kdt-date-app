package com.example.date_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = {
    org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration.class
})
@EnableCaching
public class DateAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(DateAppApplication.class, args);
	}
//conf test

	// pr test
}
