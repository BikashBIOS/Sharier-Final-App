package com.example.shareer.MultipleImagesPackage;


public class MultipleImagesHandler {

    String ImgLink, ImgKey;

    public MultipleImagesHandler() {
    }

    public MultipleImagesHandler(String imgLink, String imgKey) {
        this.ImgLink = imgLink;
        this.ImgKey = imgKey;
    }

    public String getImgLink() {
        return ImgLink;
    }

    public void setImgLink(String imgLink) {
        ImgLink = imgLink;
    }

    public String getImgKey() {
        return ImgKey;
    }

    public void setImgKey(String imgKey) {
        ImgKey = imgKey;
    }
}
