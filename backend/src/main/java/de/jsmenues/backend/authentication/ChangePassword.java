package de.jsmenues.backend.authentication;

/**
 * Class ChangePasword to define old and new password as json object
 */
public class ChangePassword {

    private String oldPassword;
    private String newPassword;

    public ChangePassword() {
    }

    public ChangePassword(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
