package com.example.demoscsbeanloop.c;


import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PassThroughProcessor implements Processor {

    public Object process(Object input) {
        return input;
    }
}
