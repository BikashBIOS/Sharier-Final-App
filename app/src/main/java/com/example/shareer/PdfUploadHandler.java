package com.example.shareer;

public class PdfUploadHandler {
    public String name;
    public String url;
    public String key;

    public PdfUploadHandler() {
    }

    public PdfUploadHandler(String name, String url, String key) {
        this.name = name;
        this.url = url;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
