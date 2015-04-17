package com.tylerlubeck.maraudersmapmultiuser.Models;

/**
 * Created by Tyler on 4/16/2015.
 */
public class MyLocation {
    String building_name;
    int floor_number;
    int x_coordinate;
    int y_coordinate;
    String image_url;

    public MyLocation() {}

    public MyLocation(String buildingName, int floorNumber, int x, int y, String imageUrl) {
        this.building_name = buildingName;
        this.floor_number = floorNumber;
        this.x_coordinate = x;
        this.y_coordinate = y;
        this.image_url = imageUrl;
    }

    public String getImageUrl() {
        return image_url;
    }

    public int getXCoordinate() {
        return x_coordinate;
    }

    public int getYCoordinate() {
        return y_coordinate;
    }

    public String getFloorName() {
        return String.format("%s Floor %d", building_name, floor_number);
    }

    public int getFloorNumber() {
        return this.floor_number;
    }

    public String toString() {
        return String.format("%s Floor %s: (%d, %d)",
                building_name,
                floor_number,
                x_coordinate,
                y_coordinate);
    }
}
