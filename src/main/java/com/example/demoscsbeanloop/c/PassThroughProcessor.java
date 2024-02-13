package com.example.demoscsbeanloop.c;


import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PassThroughProcessor implements Processor {

    public Map<String, String> process(Map<String, String> input) {
        return input;
    }
}
