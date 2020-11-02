package com.phytel.patient.match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class PatientMatchServiceApplication {
	
	private static Logger logger = LoggerFactory.getLogger(PatientMatchServiceApplication.class);
 
	public static void main(String[] args) {
		logger.info("Patient Matching Service Started");
		SpringApplication.run(PatientMatchServiceApplication.class, args);
	}

}
