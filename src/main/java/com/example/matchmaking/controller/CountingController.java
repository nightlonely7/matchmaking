package com.example.matchmaking.controller;

import com.example.matchmaking.service.CountingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CountingController {

    private final CountingService countingService;

}
