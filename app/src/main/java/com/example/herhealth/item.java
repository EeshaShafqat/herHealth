package com.example.herhealth;

public class item {

    String name;
    String Location;

    int age;
    int pdf;

    public item(String name, String location, int age, int pdf) {
        this.name = name;
        this.Location = location;
        this.age = age;
        this.pdf = pdf;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getPdf() {
        return pdf;
    }

    public void setPdf(int pdf) {
        this.pdf = pdf;
    }
}