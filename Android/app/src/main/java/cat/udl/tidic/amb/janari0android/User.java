package cat.udl.tidic.amb.janari0android;

public class User {
    String username;
    String phoneNumber;
    public User(String username, String phoneNumber){
        this.username = username;
        this.phoneNumber = phoneNumber;
    }
    public User(String username){
        this.username = username;
    }
    public User(){}
    public String getUsername() {
        return username;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
}
