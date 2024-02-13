package com.example.demoscsbeanloop.c;

import java.util.Map;

@FunctionalInterface
public interface Processor {

     Map<String, String> process(Map<String, String> input);

}
