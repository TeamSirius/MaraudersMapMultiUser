package com.tylerlubeck.maraudersmapmultiuser.Models;

/**
 * Created by Tyler on 4/16/2015.
 */
public class FindFriendBody {
    public FindFriendBody() {}
    public FindFriendBody(String friend_fb_id) {
        this.friend_fb_id = friend_fb_id;
    }
    public String friend_fb_id;
}
