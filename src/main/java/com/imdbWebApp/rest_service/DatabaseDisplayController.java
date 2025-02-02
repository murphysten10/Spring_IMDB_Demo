package com.imdbWebApp.rest_service;
import jakarta.persistence.metamodel.IdentifiableType;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping
public class DatabaseDisplayController implements WebMvcConfigurer {

    @Autowired
    IMDBMovieAndCrewRepository imdbMovieAndCrewRepository;

    public DatabaseDisplayController(IMDBMovieAndCrewRepository imdbMovieAndCrewRepository){
        this.imdbMovieAndCrewRepository = imdbMovieAndCrewRepository;
    }



//    @GetMapping("/dataBaseDisplay")
//    public String showMovies(Model model){
//        model.addAttribute("movies", imdbRepository.findAll());
//        return "dataBaseDisplay";
//    }

    @GetMapping("/dataBaseDisplay")
    public String getMovieByID(@RequestParam(value = "title", required = false, defaultValue = "0") String title, Model model) {
        try {
            Optional<GetCrewForMovieTitle> getCrewForMovieTitle = imdbMovieAndCrewRepository.findWritersByTitle(title);

            if (getCrewForMovieTitle.isPresent()) {
                model.addAttribute("getCrewForMovieTitle", getCrewForMovieTitle.get());
                return "dataBaseDisplay";
            } else {
                model.addAttribute("error", "Movie not found");
            }
        } catch (NumberFormatException e) {
            model.addAttribute("error", "Invalid ID format");
        }

        return "dataBaseDisplay";
    }

}
