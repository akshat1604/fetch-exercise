package com.akshat.healthcheck.service;
import com.akshat.healthcheck.model.EndpointDetails;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.stereotype.Service;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class HealthCheckService {
    static int count = 1;

    public HashMap<String,Integer> getUniqueDomainCountMap(List<EndpointDetails> endpointDetailsList){
        HashMap<String,Integer> domainMap = new HashMap<>();
        for(EndpointDetails endpointDetail: endpointDetailsList){
            String domain = getDomainFromUrl(endpointDetail.getUrl());
            domainMap.put(domain,domainMap.getOrDefault(domain,0)+1);
        }
        return domainMap;
    }
    public List<EndpointDetails> parseYml(String ymlPath) throws IOException {
        File yamlFile = new File(ymlPath);
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        List<EndpointDetails> endpointDetailsList = new ArrayList<>();
        try {
            List<Map<String, Map<String, Object>>> requestList = objectMapper.readValue(yamlFile, List.class);
            for (Map<String, Map<String, Object>> requestMap : requestList) {
                for(String key: requestMap.keySet()){
                    ObjectMapper obj = new ObjectMapper();
                    obj.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    EndpointDetails endpointDetails = obj.convertValue(requestMap.get(key), EndpointDetails.class);
                    endpointDetailsList.add(endpointDetails);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return endpointDetailsList;
    }


    public void hitApis(List<EndpointDetails> endpointDetailsList, HashMap<String,Integer> domainMap,HashMap<String,Integer> successfulMap) throws IOException {

        for (EndpointDetails endpointDetail: endpointDetailsList){
            long startTime = System.currentTimeMillis();
            URL url = new URL(endpointDetail.getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if(endpointDetail.getHeaders()!=null){
                HashMap<String,String> headerMap = (HashMap<String, String>) endpointDetail.getHeaders();
                for(String header: headerMap.keySet()){
                    String value =  headerMap.get(header);
                    connection.setRequestProperty(header,value);
                }
            }
            if(endpointDetail.getMethod()!=null && !endpointDetail.getMethod().equals("GET")){
                connection.setDoOutput(true);
                OutputStream output = connection.getOutputStream();
                output.write(endpointDetail.getBody().getBytes());
                output.close();
            }

            int responseCode = connection.getResponseCode();
            long endTime = System.currentTimeMillis();
            long latency = endTime - startTime;
            if(latency < 500 && responseCode>=200 && responseCode<=299){
                String domain = getDomainFromUrl(endpointDetail.getUrl());
                successfulMap.putIfAbsent(domain,0);
                successfulMap.put(domain,successfulMap.get(domain)+1);
            }

        }

        for(String domain: successfulMap.keySet()){
            int success = successfulMap.get(domain);
            int total = domainMap.get(domain) * count;
            double avail = Math.ceil(100 * ((double)success/(double) total));
            int a =(int) avail;
            System.out.println(domain + " " + a + "% availability percentage");

        }
        count++;

    }

    private String getDomainFromUrl(String url){
        int index = url.indexOf("com");
        index += 2;
        int temp = index;
        while(url.charAt(temp)!= '/'){
            temp--;
        }
        String domain = url.substring(temp+1,index+1);
        return domain;
    }

}
