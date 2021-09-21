package com.kumuluz.jsp;

public class Customer 
{
    private String id;
    private String firstName;
    private String lastName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public String toString() 
    {
        return "Customer {\n" +
                "  id='" + id + "',\n" +
                "  firstName='" + firstName + "',\n" +
                "  lastName='" + lastName + "'\n" +
                "}";
    }
}