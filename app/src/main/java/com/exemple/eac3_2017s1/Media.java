package com.exemple.eac3_2017s1;

/**
 * Created by BlueStorm on 28/10/2017.
 */

public class Media {
    private int id;
    private String name;
    private String file;
    private int photoOrVideo;
    private double latitude;
    private double longitude;

    public Media(int id, String name, String file, int photoOrVideo, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.file = file;
        this.photoOrVideo = photoOrVideo; // 0 for photo, 1 for video
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Media(){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getPhotoOrVideo() {
        return photoOrVideo;
    }

    public void setPhotoOrVideo(int photoOrVideo) {
        this.photoOrVideo = photoOrVideo;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
