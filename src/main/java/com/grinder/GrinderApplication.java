package com.grinder;

import com.grinder.utils.AwsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class GrinderApplication {

	public static void main(String[] args) {
		SpringApplication.run(GrinderApplication.class, args);
	}

}
