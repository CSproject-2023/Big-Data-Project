package com.packagename.myapp;

import java.io.Serializable;

import org.springframework.stereotype.Service;

@Service
public class GreetService implements Serializable {

    public String greet() {
        System.out.println("clicked");
            return "Starting " ;
    }

}
