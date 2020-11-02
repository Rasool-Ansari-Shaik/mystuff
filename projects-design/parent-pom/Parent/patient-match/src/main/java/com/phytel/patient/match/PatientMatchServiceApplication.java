package com.phytel.patient.match;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.phytel.patient.match.model.PatientMatchAlgorithms;
import com.phytel.patient.match.service.PatientMatchService;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class PatientMatchServiceApplication {
	
	@Autowired
    PatientMatchService patientMatchService;
	
	private static Logger logger = LoggerFactory.getLogger(PatientMatchServiceApplication.class);
	
	public static void main(String[] args) {
		logger.info("Patient Matching Service Started");
		SpringApplication.run(PatientMatchServiceApplication.class, args);
	}
	
	@Bean
    @PostConstruct
    public PatientMatchAlgorithms init() {
		logger.debug("init() method started");
        logger.info("Initialization of the algorithms started");
        
        PatientMatchAlgorithms pmasAlgorithms = new PatientMatchAlgorithms();
        pmasAlgorithms= patientMatchService.initAlgorithms();
        
        logger.info("Initialization of the algorithms : "+pmasAlgorithms);
        logger.debug("init() method completed");
        return pmasAlgorithms;
        
    }

}
