package com.imdbWebApp.rest_service;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Controller
public class DatabaseDisplayController implements WebMvcConfigurer {

    @Autowired
    IMDBRepository imdbRepository;

    public DatabaseDisplayController(IMDBRepository imdbRepository){
        this.imdbRepository = imdbRepository;
    }

    @GetMapping("/dataBaseDisplay")
    public String showMovies(Model model){
        model.addAttribute("movies", imdbRepository.findAll());
        return "dataBaseDisplay";
    }

}
