package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{
        User user = userRepository2.findById(userId).get();
        if(user.getMaskedIp()!=null){
            throw new Exception("Already connected");
        }
        else if(countryName.equalsIgnoreCase(user.getOriginalCountry().getCountryName().toString())){
            return user;
        }
        else{
            if(user.getServiceProviderList().size()==0){
                throw new Exception("Unable to connect");
            }
            List<ServiceProvider> serviceProviderList = user.getServiceProviderList();
            int a = Integer.MAX_VALUE;
            ServiceProvider serviceProvider = null;
            Country country = null;
            Boolean flag = false;

            for(ServiceProvider serviceProvider1:serviceProviderList){
                List<Country> countryList = serviceProvider1.getCountryList();
                for(Country country1: countryList){
                    if(countryName.equalsIgnoreCase(country1.getCountryName().toString())){
                        a = serviceProvider1.getId();
                        serviceProvider = serviceProvider1;
                        country = country1;
                        flag = true;
                    }
                }
            }

            if(!flag){
                throw new Exception("Unable to connect");
            }
            Connection connection = new Connection();
            connection.setUser(user);
            connection.setServiceProvider(serviceProvider);
            String countryCode=country.getCode();
            String mask=countryCode+"."+serviceProvider.getId()+"."+userId;

            user.setMaskedIp(mask);
            user.setConnected(true);

            user.getConnectionList().add(connection);

            serviceProvider.getConnectionList().add(connection);

            userRepository2.save(user);
            serviceProviderRepository2.save(serviceProvider);
        }
        return user;
    }

    @Override
    public User disconnect(int userId) throws Exception {
        User user = userRepository2.findById(userId).get();
        if(!user.getConnected()){
            throw new Exception("Already Disconnected");
        }
        user.setMaskedIp(null);
        user.setConnected(false);
        userRepository2.save(user);
        return user;
    }

    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        User sender = userRepository2.findById(senderId).get();
        User receiver = userRepository2.findById(receiverId).get();
        if(receiver.getMaskedIp()!=null){
            String mask = receiver.getMaskedIp();
            String cc = mask.substring(0,3);
            if(cc.equals(sender.getOriginalCountry().getCode())){
                return sender;
            }
            else{
                String countryName="";

                if(cc.equalsIgnoreCase(CountryName.IND.toCode())){
                    countryName=CountryName.IND.toString();
                }
                if(cc.equalsIgnoreCase(CountryName.USA.toCode())){
                    countryName=CountryName.USA.toString();
                }
                if(cc.equalsIgnoreCase(CountryName.AUS.toCode())){
                    countryName=CountryName.AUS.toString();
                }
                if(cc.equalsIgnoreCase(CountryName.JPN.toCode())){
                    countryName=CountryName.JPN.toString();
                }
                if(cc.equalsIgnoreCase(CountryName.CHI.toCode())){
                    countryName=CountryName.CHI.toString();
                }
                User user2=connect(senderId,countryName);
                if(!user2.getConnected()){
                    throw new Exception("Unable to connect");
                }
                else return user2;
            }
        }
        else{
            if(receiver.getOriginalCountry().equals(sender.getOriginalCountry())){
                return sender;
            }
            String countryName=receiver.getOriginalCountry().getCountryName().toString();

            User user2=connect(senderId,countryName);

            if(!user2.getConnected()){
                throw new Exception("Unable to connect");
            }
            else return user2;
        }
    }
}