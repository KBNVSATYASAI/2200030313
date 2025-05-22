package com.example.question1.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.question1.model.baluResponse;

@Service
public class BaluService {

    private static final int WINDOW_LIMIT = 10;
    private static final String TOKEN = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJNYXBDbGFpbXMiOnsiZXhwIjoxNzQ3ODk2NTM3LCJpYXQiOjE3NDc4OTYyMzcsImlzcyI6IkFmZm9yZG1lZCIsImp0aSI6ImY1ZGZlNGI3LWU5NTItNDA0My04ZDllLTFjYTI2NjBhYTIxYiIsInN1YiI6IjIyMDAwMzAzMTNjc2VoQGdtYWlsLmNvbSJ9LCJlbWFpbCI6IjIyMDAwMzAzMTNjc2VoQGdtYWlsLmNvbSIsIm5hbWUiOiJrYXJuYSBiYWxhIG5hZ2EgdmVua2F0YSBzYXR5YSBzYWkiLCJyb2xsTm8iOiIyMjAwMDMwMzEzIiwiYWNjZXNzQ29kZSI6ImJlVEpqSiIsImNsaWVudElEIjoiZjVkZmU0YjctZTk1Mi00MDQzLThkOWUtMWNhMjY2MGFhMjFiIiwiY2xpZW50U2VjcmV0IjoiY2JTand4U1B0WHVGeHhWQyJ9.0LMEeLODX9PsSUczw7wsZJQZiF7bIimf4_kkwoOyw-0"; 

    private final Map<String, String> apiSources = Map.of(
        "p", "http://20.244.56.144/evaluation-service/primes",
        "f", "http://20.244.56.144/evaluation-service/fibo",
        "e", "http://20.244.56.144/evaluation-service/even",
        "r", "http://20.244.56.144/evaluation-service/rand"
    );

    private final List<Integer> recentNumbers = new LinkedList<>();
    private final RestTemplate restTemplate = new RestTemplate();

    public baluResponse handleRequest(String type) {
        List<Integer> previousNumbers = new ArrayList<>(recentNumbers);
        List<Integer> fetchedNumbers = fetchFromApi(type);

        for (int number : fetchedNumbers) {
            if (!recentNumbers.contains(number)) {
                if (recentNumbers.size() >= WINDOW_LIMIT) {
                    recentNumbers.remove(0);
                }
                recentNumbers.add(number);
            }
        }

        double average = calculateAverage(recentNumbers);

        return new baluResponse(previousNumbers, new ArrayList<>(recentNumbers), fetchedNumbers, average);
    }

    private List<Integer> fetchFromApi(String type) {
        String url = apiSources.get(type);
        if (url == null) return Collections.emptyList();

        try {
            URI endpoint = URI.create(url);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", TOKEN);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(endpoint, HttpMethod.GET, entity, Map.class);
            Map body = response.getBody();

            if (body != null && body.get("numbers") instanceof List) {
                List<Integer> result = new ArrayList<>();
                for (Object num : (List<?>) body.get("numbers")) {
                    if (num instanceof Number) {
                        result.add(((Number) num).intValue());
                    }
                }
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    private double calculateAverage(List<Integer> numbers) {
        if (numbers.isEmpty()) return 0.0;
        double total = 0;
        for (int num : numbers) {
            total += num;
        }
        return Math.round((total / numbers.size()) * 100.0) / 100.0;
    }
}
