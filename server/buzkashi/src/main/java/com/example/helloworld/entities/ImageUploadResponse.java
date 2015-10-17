package com.example.helloworld.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by vaidyanathan.s on 16/05/15.
 */
public class ImageUploadResponse {
    @JsonProperty("uploaded_url")
    private String uploadedURL;

    @JsonProperty("status")
    private Boolean status;

    public ImageUploadResponse() {
        // Jackson deserialization
    }

    @JsonCreator
    public ImageUploadResponse(@JsonProperty("uploaded_url") String uploadedURL) {
        this.uploadedURL = uploadedURL;
        if (uploadedURL != null) {
            status = true;
        }else {
            status = false;
        }
    }
}
