package com.example.weather;

import java.time.LocalDate;

public class WeatherRecord {
    private String city;
    private double temperature;
    private String condition;
    private LocalDate date;

    public WeatherRecord() {
    }

    public WeatherRecord(String city, double temperature, String condition, LocalDate date) {
        this.city = city;
        this.temperature = temperature;
        this.condition = condition;
        this.date = date;
    }

    // Getters and setters
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "WeatherRecord{" +
                "city='" + city + '\'' +
                ", temperature=" + temperature +
                ", condition='" + condition + '\'' +
                ", date=" + date +
                '}';
    }
}
