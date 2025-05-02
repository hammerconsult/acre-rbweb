package br.com.webpublico.pncp.dto;

public class LocationDTO {

    private String location;

    public LocationDTO() {}

    public LocationDTO(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
