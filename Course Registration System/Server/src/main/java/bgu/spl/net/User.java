package bgu.spl.net;

public abstract class User {
    private String username;
    private String password;
    protected boolean logIn = false;
    public User (String username , String password){
        this.username =username;
        this.password  =password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
