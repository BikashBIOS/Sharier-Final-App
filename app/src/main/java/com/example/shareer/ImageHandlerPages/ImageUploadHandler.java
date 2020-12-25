package com.example.shareer.ImageHandlerPages;

public class ImageUploadHandler {
    private String mName;
    private String mImageUri;
    private String mDesc;
    private String mKey;

    public ImageUploadHandler()
    {

    }
    public ImageUploadHandler(String mName, String mImageUri,String mDesc, String mKey) {

        if (mName.trim().equals(""))
        {
            mName="No Name";
        }
        this.mName = mName;
        this.mImageUri = mImageUri;
        this.mDesc=mDesc;
        this.mKey=mKey;
    }



    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmImageUri() {
        return mImageUri;
    }

    public void setmImageUri(String mImageUri) {
        this.mImageUri = mImageUri;
    }

    public String getmDesc() {
        return mDesc;
    }

    public void setmDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    public String getmKey() {
        return mKey;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }

}
