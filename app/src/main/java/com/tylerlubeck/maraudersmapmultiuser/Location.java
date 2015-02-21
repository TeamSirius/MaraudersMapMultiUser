package com.tylerlubeck.maraudersmapmultiuser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tyler on 10/29/2014.
 */
public class Location {
    public enum Direction {
        NORTH, EAST, SOUTH, WEST, INVALID
    }

    private Direction direction;
    private String verbose_name;
    private String short_name;
    private int x_coordinate;
    private int y_coordinate;
    private String resource_uri;


    Location(JSONObject obj) throws JSONException {
        this.x_coordinate = obj.getInt("x_coordinate");
        this.y_coordinate = obj.getInt("y_coordinate");
        this.verbose_name = obj.getString("verbose_name");
        this.short_name = obj.getString("short_name");
        this.resource_uri = obj.getString("resource_uri");

        int degrees = obj.getInt("direction");

        switch (degrees) {
            case 0:
                this.direction = Direction.NORTH;
                break;
            case 90:
                this.direction = Direction.EAST;
                break;
            case 180:
                this.direction = Direction.SOUTH;
                break;
            case 270:
                this.direction = Direction.WEST;
                break;
            default:
                this.direction = Direction.INVALID;
                break;
        }
    }

    public int getX_coordinate() {
        return this.x_coordinate;
    }

    public int getY_coordinate() {
        return this.y_coordinate;
    }

    public String toString() {
        String direction_string = this.direction.name().toLowerCase();
        direction_string = String.valueOf(direction_string.charAt(0)).toUpperCase() + direction_string.substring(1);

        return String.format("%s: %s", this.verbose_name, direction_string);
    }

    public String getResourceUri() {
        return resource_uri;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public String getShortName() {
        return short_name;
    }
}
