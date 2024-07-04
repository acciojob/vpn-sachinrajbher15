// Note: Do not write @Enumerated annotation above CountryName in this model.
package com.driver.model;

import javax.persistence.*;
import java.security.Provider;

@Entity
@Table(name="country")
public class Country{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private CountryName countryName;
    private String code;

    @ManyToOne
    @JoinColumn
    private ServiceProvider serviceProvider;

    @OneToOne
    @JoinColumn
    private User user;

    //constructor
    public Country(){}
    //getters
    public int getId(){ return id;}
    public CountryName getCountryName(){ return countryName;}
    public String getCode(){ return code;}
    //setters
    public void setId(int id){ this.id = id;}
    public void setCountryName(CountryName countryName){ this.countryName = countryName;}
    public void setCode(String code) { this.code = code;}

    public void setUser(User user) { this.user = user;}
    public User getUser(){ return user; }

    public void setServiceProvider(ServiceProvider serviceProvider) { this.serviceProvider = serviceProvider; }
    public ServiceProvider getServiceProvider() { return serviceProvider; }
}