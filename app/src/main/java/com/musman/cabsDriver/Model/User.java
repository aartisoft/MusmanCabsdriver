package com.musman.cabsDriver.Model;

/**
 * Created by Musman on 2018-01-03.
 */

public class User {
    private String name;
    private String surname;
    private String cell;
    private String email;
    private String password;

    public User() {
    }

    public User(String name, String surname, String cellno, String email, String password) {
        this.name = name;
        this.surname = surname;
        this.cell = cellno;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
