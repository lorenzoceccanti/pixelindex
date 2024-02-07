package it.unipi.largescale.pixelindex.dto;

public class UserSearchDTO {
    private String isFollowed;
    private String username;
    private int countFollowed;
    private int countFollower;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCountFollowed() {
        return countFollowed;
    }

    public void setCountFollowed(int countFollowed) {
        this.countFollowed = countFollowed;
    }

    public int getCountFollower() {
        return countFollower;
    }

    public void setCountFollower(int countFollower) {
        this.countFollower = countFollower;
    }

    public String getIsFollowed() {
        return isFollowed;
    }

    public void setIsFollowed(String isFollowed) {
        this.isFollowed = isFollowed;
    }

    @Override
    public String toString(){
        return "[" + isFollowed + "] username: " + username + " followers: " + countFollower + " followed: " + countFollowed;
    }
}
