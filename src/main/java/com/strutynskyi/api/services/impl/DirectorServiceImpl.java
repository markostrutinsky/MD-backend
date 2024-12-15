package com.strutynskyi.api.services.impl;

import com.strutynskyi.api.dto.director.CreateDirectorRequestDTO;
import com.strutynskyi.api.dto.director.UpdateDirectorRequestDTO;
import com.strutynskyi.api.exceptions.DirectorAlreadyExistsException;
import com.strutynskyi.api.exceptions.NoSuchDirectorException;
import com.strutynskyi.api.mappers.DirectorMappers;
import com.strutynskyi.api.models.Director;
import com.strutynskyi.api.repositories.DirectorRepository;
import com.strutynskyi.api.services.interfaces.DirectorService;
import com.strutynskyi.api.validators.RequestDTOValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {
    private final DirectorRepository directorRepository;
    private final RequestDTOValidator<CreateDirectorRequestDTO> createDirectorRequestDTOValidator;
    private final RequestDTOValidator<UpdateDirectorRequestDTO> updateDirectorRequestDTOValidator;
    private static final Logger logger = LogManager.getLogger("project");

    @Override
    public List<Director> findAll() {
        logger.info("DirectorServiceImpl:findAll() Retrieving all directors from repository");
        List<Director> directors = directorRepository.findAll();
        directors.sort(Comparator.comparing(Director::getId));
        logger.info("DirectorServiceImpl:findAll() Retrieved {} directors", directors.size());
        return directors;
    }

    @Override
    @Cacheable(value = "directors", key = "#id")
    public Director findById(Long id) {
        logger.info("DirectorServiceImpl:findAll() Retrieving director from repository with id: {}", id);

        Director foundDirector = directorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("DirectorServiceImpl:findById() Director not found by id: {}", id);
                    return new NoSuchDirectorException();
                });

        logger.info("DirectorServiceImpl:findById() Director with id {} found in repository", id);
        return foundDirector;
    }

    @Override
    public Director save(CreateDirectorRequestDTO directorDTO, MultipartFile imageFile) throws IOException {
        createDirectorRequestDTOValidator.validate(directorDTO);
        Director validDirectorModel = DirectorMappers.toDirectorFromCreateDTO(directorDTO);
        Optional<Director> directorToSave = directorRepository.findAll()
                .stream()
                .filter(d -> d.equals(validDirectorModel))
                .findFirst();

        if (directorToSave.isPresent()) {
            logger.error("DirectorServiceImpl:save() Director already exists with id: {}", directorToSave.get().getId());
            throw new DirectorAlreadyExistsException();
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            validDirectorModel.setImageName(imageFile.getOriginalFilename());
            validDirectorModel.setImageType(imageFile.getContentType());
            validDirectorModel.setImageData(imageFile.getBytes());
        }

        Director savedDirector = directorRepository.save(validDirectorModel);
        logger.info("DirectorServiceImpl:save() Saved new director with id: {}", savedDirector.getId());
        return savedDirector;
    }

    @Override
    @CachePut(value = "directors", key = "#id")
    public Director update(Long id, UpdateDirectorRequestDTO directorDTO, MultipartFile imageFile) throws IOException {
        updateDirectorRequestDTOValidator.validate(directorDTO);
        Director validDirectorModel = DirectorMappers.toDirectorFromUpdateDTO(directorDTO);
        Director directorToUpdate = directorRepository.findById(id)
                .orElseThrow(
                        () -> {
                            logger.error("Director not found with id: {}", id);
                            return new NoSuchDirectorException();
                        });

        directorToUpdate.setFirstName(validDirectorModel.getFirstName());
        directorToUpdate.setLastName(validDirectorModel.getLastName());
        directorToUpdate.setBiography(validDirectorModel.getBiography());
        directorToUpdate.setBirthDate(validDirectorModel.getBirthDate());

        if (imageFile != null && !imageFile.isEmpty()) {
            directorToUpdate.setImageName(imageFile.getOriginalFilename());
            directorToUpdate.setImageType(imageFile.getContentType());
            directorToUpdate.setImageData(imageFile.getBytes());
        }

        Director savedDirector = directorRepository.save(directorToUpdate);

        logger.info("Updated director with id: {}", savedDirector.getId());
        return savedDirector;
    }

    @Override
    @CacheEvict(value = "directors", key = "#id")
    public Director delete(Long id) {
        Director directorToDelete = directorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Director not found with id: {}", id);
                    return new NoSuchDirectorException();
                });
        directorRepository.deleteById(id);
        logger.info("Deleted director with id: {}", directorToDelete.getId());
        return directorToDelete;
    }

    @Override
    public boolean existsByFirstNameAndLastName(String firstName, String lastName) {
        logger.info("DirectorServiceImpl:existsByFirstNameAndLastName(): Сhecking for the existence of the director");
        return directorRepository.existsByFirstNameAndLastName(firstName, lastName);
    }
}