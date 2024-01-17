package com.akshat.healthcheck;

import com.akshat.healthcheck.model.EndpointDetails;
import com.akshat.healthcheck.service.HealthCheckService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class HealthcheckApplication {

	HashMap<String, Integer> domainMap = new HashMap<>();

	HashMap<String,Integer> successfulMap = new HashMap<>();

	List<EndpointDetails> endpointDetailsList = new ArrayList<>();

	static String ymlPath = null;
	public static void main(String[] args) throws IOException {
		ymlPath = args[0];
		SpringApplication.run(HealthcheckApplication.class, args);


	}
	@Scheduled(fixedRate = 15000)
	public void execute() throws IOException {
		HealthCheckService healthCheckService = new HealthCheckService();
		if(endpointDetailsList.isEmpty()){
			endpointDetailsList = healthCheckService.parseYml(ymlPath);
		}
		if(domainMap.isEmpty()){
			domainMap = healthCheckService.getUniqueDomainCountMap(endpointDetailsList);
		}
		healthCheckService.hitApis(endpointDetailsList,domainMap,successfulMap);

	}

}
