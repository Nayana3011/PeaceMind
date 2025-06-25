package com.example.peacemind.model;

import java.util.List;

public class EmotionResponse {
    private String status;
    private List<String> emotions;

    public String getStatus() {
        return status;
    }

    public List<String> getEmotions() {
        return emotions;
    }
}