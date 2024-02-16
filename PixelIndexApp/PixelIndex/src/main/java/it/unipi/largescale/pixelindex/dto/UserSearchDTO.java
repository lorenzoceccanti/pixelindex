package it.unipi.largescale.pixelindex.dto;

public class UserSearchDTO {
    private String username;
    private int followersCount;
    private int followingsCount;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public void setFollowingsCount(int followingsCount) {
        this.followingsCount = followingsCount;
    }

    @Override
    public String toString(){
        return "username: " + username + " followers: " + followersCount + " following: " + followingsCount;
    }
}
