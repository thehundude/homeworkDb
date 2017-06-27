package runnersdb;

/**
 * Created by tamas on 2017. 06. 27..
 */
public class User {
    private int userId;
    private String userName;
    private String userPassword;
    private boolean userCanEdit;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public boolean isUserCanEdit() {
        return userCanEdit;
    }

    public void setUserCanEdit(boolean userCanEdit) {
        this.userCanEdit = userCanEdit;
    }
}
