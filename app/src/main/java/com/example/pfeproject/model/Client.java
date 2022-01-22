package com.example.pfeproject.model;

public class Client {

    private String id,email,password,phone,imageLink,firstName,lastName,adress;
    public static final String role  = "client";
    private TotalPoint[] totalpointsPerEntreprise;
    private Command[] commands;
    private Point[] points;

    public Client(String email, String phone, String firstName, String lastName, String adress) {
        this.email = email;
        this.phone = phone;
        this.firstName = firstName;
        this.lastName = lastName;
        this.adress = adress;
    }

    public Client(String email, String phone, String imageLink, String firstName, String lastName, String adress) {
        this.email = email;
        this.phone = phone;
        this.imageLink = imageLink;
        this.firstName = firstName;
        this.lastName = lastName;
        this.adress = adress;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public static String getRole() {
        return role;
    }

    public TotalPoint[] getTotalpointsPerEntreprise() {
        return totalpointsPerEntreprise;
    }

    public void setTotalpointsPerEntreprise(TotalPoint[] totalpointsPerEntreprise) {
        this.totalpointsPerEntreprise = totalpointsPerEntreprise;
    }

    public Command[] getCommands() {
        return commands;
    }

    public void setCommands(Command[] commands) {
        this.commands = commands;
    }

    public Point[] getPoints() {
        return points;
    }

    public void setPoints(Point[] points) {
        this.points = points;
    }
}
