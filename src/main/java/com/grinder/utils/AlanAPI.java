package com.grinder.utils;

import com.grinder.domain.dto.AlanDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Objects;

@Component
public class AlanAPI {
    @Value("${alan.secret}")
    private String key;

    public AlanDTO.AlanResponse requestSummary(String cafeName, String address) {
        AlanDTO.AlanResponse alanResponse = new AlanDTO.AlanResponse();
//        RestTemplate restTemplate = new RestTemplate();
//
//        StringBuilder url = new StringBuilder();
//        url.append("https://kdt-api-function.azurewebsites.net/api/v1/question?content=")
//                .append("카페명 : ").append(cafeName).append(" 주소명 :").append(address)
//                .append("에 대해 1.분위기, 2. 맛, 3. 가격 형식으로 검색해서 250자 이내로 정리해서 알려줘.")
//                .append("&client_id=").append(key);
//
//        ResponseEntity<LinkedHashMap> response = restTemplate.getForEntity(url.toString(), LinkedHashMap.class);
//
//        if (response.getStatusCode().is2xxSuccessful()) {
//            LinkedHashMap<String, Object> responseBody = response.getBody();
//            if (responseBody != null) {
//                LinkedHashMap<String, String> action = (LinkedHashMap<String, String>) responseBody.get("action");
//                alanResponse.setActionName(action.get("name"));
//                alanResponse.setActionSpeak(action.get("speak"));
//                if (responseBody.get("content").toString().length() > 250) {
//                    alanResponse.setContent(responseBody.get("content").toString().substring(0,250));
//                    return alanResponse;
//                }
//                alanResponse.setContent((String) responseBody.get("content"));
//            }
//        } else throw new IllegalArgumentException("잘못된 입력입니다.");

        alanResponse.setActionSpeak("actionSpeak");
        alanResponse.setActionName("actionName");
        alanResponse.setContent("종료된 서비스");

        return alanResponse;
    }

    public AlanDTO.AlanResponse anyQuestion(String question) {
        AlanDTO.AlanResponse alanResponse = new AlanDTO.AlanResponse();
//        RestTemplate restTemplate = new RestTemplate();

//        StringBuilder url = new StringBuilder();
//        url.append("https://kdt-api-function.azurewebsites.net/api/v1/question?content=")
//                .append(question)
//                .append("&client_id=").append(key);
//
//        ResponseEntity<LinkedHashMap> response = restTemplate.getForEntity(url.toString(), LinkedHashMap.class);
//
//        if (response.getStatusCode().is2xxSuccessful()) {
//            LinkedHashMap<String, Object> responseBody = response.getBody();
//            if (responseBody != null) {
//                LinkedHashMap<String, String> action = (LinkedHashMap<String, String>) responseBody.get("action");
//                alanResponse.setActionName(action.get("name"));
//                alanResponse.setActionSpeak(action.get("speak"));
//                alanResponse.setContent((String) responseBody.get("content"));
//            }
//        } else throw new IllegalArgumentException("잘못된 입력입니다.");

        alanResponse.setActionSpeak("actionSpeak");
        alanResponse.setActionName("actionName");
        alanResponse.setContent("종료된 서비스");

        return alanResponse;
    }

    public AlanDTO.AlanResponse recommendCafeByTag(String tagList) {
        AlanDTO.AlanResponse alanResponse = new AlanDTO.AlanResponse();
//        RestTemplate restTemplate = new RestTemplate();

//        StringBuilder url = new StringBuilder();
//        url.append("https://kdt-api-function.azurewebsites.net/api/v1/question?content=")
//                .append(tagList)
//                .append("해당 태그들을 참고해서 [1. 카페명(위치) : 설명] 양식으로 3개 추천해줘.")
//                .append("가장 많이 등장한 태그를 중점적으로 찾아보고, 도심지 외곽이라고 하면 멀지 않은 곳까지만 추천해줘")
//                .append("서울 경기 위주로 찾아보고, 없다면 차츰 범위를 확장시켜줘")
//                .append("글자 길이는 300자 이내로 해줘")
//                .append("&client_id=").append(key);
//
//        ResponseEntity<LinkedHashMap> response = restTemplate.getForEntity(url.toString(), LinkedHashMap.class);
//
//        if (response.getStatusCode().is2xxSuccessful()) {
//            LinkedHashMap<String, Object> responseBody = response.getBody();
//            if (responseBody != null) {
//                LinkedHashMap<String, String> action = (LinkedHashMap<String, String>) responseBody.get("action");
//                alanResponse.setActionName(action.get("name"));
//                alanResponse.setActionSpeak(action.get("speak"));
//                alanResponse.setContent((String) responseBody.get("content"));
//            }
//        } else throw new IllegalArgumentException("잘못된 입력입니다.");

        alanResponse.setActionSpeak("actionSpeak");
        alanResponse.setActionName("actionName");
        alanResponse.setContent("종료된 서비스");

        return alanResponse;
    }
}











