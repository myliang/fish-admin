package com.y.fish.base.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

/**
 * Created by myliang on 7/20/17.
 */
@Data
public class User {

    public static String TABLE_NAME = "users";

    private long id;

    private String userName;

    @JsonIgnore
    private String encryptedPassword;

    @JsonIgnore
    private String password;

    private LocalDateTime createdAt = LocalDateTime.now();

    private State state = State.enable;

    private long roleId;

    public enum State {
        enable, disable
    }

    public boolean isAdmin() {
        return userName.equals("admin");
    }

    public boolean isEnable() {
        return state == State.enable;
    }

    public User() {}

    public User(String userName, String password) {
        this.userName = userName;
        this.setEncoderPassword(password);
    }

    public void setEncoderPassword(String password) {
        if (password == null || password.length() == 0) return;
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.encryptedPassword = encoder.encode(password);
    }

    public boolean validatePassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(password, this.encryptedPassword);
    }

}
