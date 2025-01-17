package com.example.app.models;

public class User {
    private int id;
    private String name;
    private String username;
    private String email;
    private String street;
    private String suite;
    private String city;
    private String zipcode;
    private String lat;
    private String lng;
    private String phone;
    private String website;
    private String companyName;
    private String companyCatchPhrase;
    private String companyBs;
    private boolean isAdmin;
    private String processStatus;

    // Getters and setters for all fields
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getSuite() { return suite; }
    public void setSuite(String suite) { this.suite = suite; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getZipcode() { return zipcode; }
    public void setZipcode(String zipcode) { this.zipcode = zipcode; }
    public String getLat() { return lat; }
    public void setLat(String lat) { this.lat = lat; }
    public String getLng() { return lng; }
    public void setLng(String lng) { this.lng = lng; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getCompanyCatchPhrase() { return companyCatchPhrase; }
    public void setCompanyCatchPhrase(String companyCatchPhrase) { this.companyCatchPhrase = companyCatchPhrase; }
    public String getCompanyBs() { return companyBs; }
    public void setCompanyBs(String companyBs) { this.companyBs = companyBs; }
    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean isAdmin) { this.isAdmin = isAdmin; }
    public String getProcessStatus() { return processStatus; }
    public void setProcessStatus(String processStatus) { this.processStatus = processStatus; }

    // Combined address method
    public String getAddress() {
        StringBuilder addressBuilder = new StringBuilder();
        if (street != null && !street.isEmpty()) {
            addressBuilder.append(street);
        }
        if (suite != null && !suite.isEmpty()) {
            addressBuilder.append(", ").append(suite);
        }
        if (city != null && !city.isEmpty()) {
            addressBuilder.append(", ").append(city);
        }
        if (zipcode != null && !zipcode.isEmpty()) {
            addressBuilder.append(", ").append(zipcode);
        }
        return addressBuilder.toString().trim();
    }

    public void setAddress(String address) {
        if (address != null && !address.isEmpty()) {
            // Example address format: "123 Main St, Apt 456, Springfield, 12345"
            String[] parts = address.split(",\\s*");

            if (parts.length > 0) {
                this.street = parts[0]; // First part as street
            }
            if (parts.length > 1) {
                this.suite = parts[1]; // Second part as suite
            }
            if (parts.length > 2) {
                this.city = parts[2]; // Third part as city
            }
            if (parts.length > 3) {
                this.zipcode = parts[3]; // Fourth part as zipcode
            }
        } else {
            // If the address string is null or empty, clear all fields
            this.street = null;
            this.suite = null;
            this.city = null;
            this.zipcode = null;
        }
    }

}
