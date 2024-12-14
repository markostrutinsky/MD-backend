package com.strutynskyi.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strutynskyi.api.dto.director.CreateDirectorRequestDTO;
import com.strutynskyi.api.dto.director.DirectorDTO;
import com.strutynskyi.api.dto.director.UpdateDirectorRequestDTO;
import com.strutynskyi.api.mappers.DirectorMappers;
import com.strutynskyi.api.models.Director;
import com.strutynskyi.api.models.Movie;
import com.strutynskyi.api.services.interfaces.DirectorService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("api/directors")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class DirectorController {
    private final DirectorService directorService;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LogManager.getLogger("project");
    @GetMapping
    public ResponseEntity<List<DirectorDTO>> getAll() {
        logger.info("DirectorController:getAll() Received request to fetch all directors");
        List<DirectorDTO> directors = directorService.findAll()
                .stream()
                .map(DirectorMappers::toDirectorDTO)
                .toList();
        logger.info("DirectorController:getAll() Fetched {} directors", directors.size());
        return ResponseEntity.ok(directors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DirectorDTO> getById(@PathVariable Long id) {
        logger.info("DirectorController:getById() Received request to fetch director by id: {}", id);
        Director existingDirector = directorService.findById(id);
        DirectorDTO directorDTO = DirectorMappers.toDirectorDTO(existingDirector);
        logger.info("DirectorController:getById() Successfully fetched director by id: {}", id);
        return ResponseEntity.ok(directorDTO);
    }

    @GetMapping("/{directorId}/image")
    public ResponseEntity<?> getImage(@PathVariable Long directorId) {
        logger.info("DirectorController:getImage() Received request to fetch director image by id: {}", directorId);
        Director director = directorService.findById(directorId);

        if (director == null || director.getImageData() == null)
            return new ResponseEntity<>("Image not found", HttpStatus.NOT_FOUND);

        byte[] imageFile = director.getImageData();
        logger.info("DirectorController:getImage() Successfully fetched director image by id: {}", directorId);

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(director.getImageType()))
                .body(imageFile);
    }

    @PostMapping
    public ResponseEntity<String> createDirector(@RequestPart("createDTO") String createDTOstr,
                                                 @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
        CreateDirectorRequestDTO createDTO = objectMapper.readValue(createDTOstr, CreateDirectorRequestDTO.class);
        logger.info("DirectorController:createDirector() Received request to create director: {}", createDTO);
        Director savedDirector = directorService.save(createDTO, imageFile);
        logger.info("DirectorController:createDirector() Successfully created director with id: {}", savedDirector.getId());
        return new ResponseEntity<>("New director was created with id: " + savedDirector.getId(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DirectorDTO> updateDirector(@PathVariable Long id, @RequestPart("updateDTO") String updateDTOstr, @RequestPart(value = "imageFile", required = false) MultipartFile multipartFile) throws IOException {
        UpdateDirectorRequestDTO updateDTO = objectMapper.readValue(updateDTOstr, UpdateDirectorRequestDTO.class);
        logger.info("DirectorController:updateDirector() Received request to update director by id: {}", id);
        Director updatedDirector = directorService.update(id, updateDTO, multipartFile);
        DirectorDTO directorDTO = DirectorMappers.toDirectorDTO(updatedDirector);
        logger.info("DirectorController:updateDirector() Successfully updated director: {}", directorDTO);
        return new ResponseEntity<>(directorDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDirector(@PathVariable Long id) {
        logger.info("DirectorController:deleteDirector() Received request to delete director by id: {}", id);
        Director deletedDirector = directorService.delete(id);
        logger.info("DirectorController:deleteDirector() Successfully deleted director by id: {}", id);
        return new ResponseEntity<>("Director was deleted by id: " + deletedDirector.getId(), HttpStatus.OK);
    }
}
