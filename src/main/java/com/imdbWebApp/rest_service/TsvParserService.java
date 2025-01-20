package com.imdbWebApp.rest_service;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TsvParserService {
    @Autowired
    IMDBRepository imdbRepository;
    @Autowired
    IMDBCrewRepository imdbCrewRepository;
    @Autowired
    IMDBMovieAndCrewRepository imdbMovieAndCrewRepository;

    //TODO : Refactor parsing service (1 method for both?)
    public void parseBasicsTsvFileAndSave(String filePath) throws IOException, CsvValidationException {
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
                .withCSVParser(new CSVParserBuilder().withSeparator('\t').build())
                .build()) {
            String[] line;
            int count = 0;
            while ((line = reader.readNext()) != null && count < 20) {
                Movie movie = new Movie();
                movie.setId(line[0]);
                movie.setOrdering(line[1]);
                movie.setTitle(line[2]);
                imdbRepository.save(movie);
                count++;
            }
        }
    }

    public void parseCrewAndMovieIntoQueryObjectAndSave(String dataset1FilePath, String dataset2FilePath) throws IOException, CsvException {
        Map<String, GetCrewForMovieTitle> mapOfMoviesAndCrews = new HashMap<>();
        List<GetCrewForMovieTitle> batchToSave = new ArrayList<>();
        int batchSize = 1000;
        try (BufferedReader br1 = new BufferedReader(new FileReader(dataset1FilePath));
             BufferedReader br2 = new BufferedReader(new FileReader(dataset2FilePath));
             CSVReader reader1 = new CSVReaderBuilder(br1)
                     .withCSVParser(new CSVParserBuilder()
                             .withSeparator('\t')
                             .build()).build();
             CSVReader reader2 = new CSVReaderBuilder(br2).
                     withCSVParser(new CSVParserBuilder()
                             .withSeparator('\t')
                             .build()).build()) {
            String[] line1;
            String[] line2;
            int count = 0;

            while ((line1 = reader1.readNext()) != null &&
                    (line2 = reader2.readNext()) != null &&
                    count < 1000) {

                GetCrewForMovieTitle getCrewForMovieTitle = new GetCrewForMovieTitle();
                getCrewForMovieTitle.setBasicsId(line1[0]);
                getCrewForMovieTitle.setOrdering(line1[1]);
                getCrewForMovieTitle.setTitle(line1[2]);

                getCrewForMovieTitle.setCrewId(line2[0]);
                List<String> directors = Arrays.asList(line2[1].split(",")).stream()
                        .map(String::trim)
                        .collect(Collectors.toList());
                getCrewForMovieTitle.setDirectors(directors);

                List<String> writers = Arrays.asList(line2[2].split(",")).stream()
                        .map(String::trim)
                        .collect(Collectors.toList());
                getCrewForMovieTitle.setWriters(writers);

                mapOfMoviesAndCrews.put(line1[0], getCrewForMovieTitle);

                synchronized (batchToSave) {
                    batchToSave.add(getCrewForMovieTitle);
                    if (batchToSave.size() >= batchSize) {
                        imdbMovieAndCrewRepository.saveAll(batchToSave);
                        batchToSave.clear();
                    }
                }
                count++;
            }
        }
    }
}

