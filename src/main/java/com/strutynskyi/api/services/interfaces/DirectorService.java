package com.strutynskyi.api.services.interfaces;

import com.strutynskyi.api.dto.director.CreateDirectorRequestDTO;
import com.strutynskyi.api.dto.director.UpdateDirectorRequestDTO;
import com.strutynskyi.api.models.Director;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DirectorService {
    List<Director> findAll();
    Director findById(Long id);
    Director save(CreateDirectorRequestDTO director, MultipartFile imageFile) throws IOException;
    Director update(Long id, UpdateDirectorRequestDTO director, MultipartFile multipartFile) throws IOException;
    Director delete(Long id);
    boolean existsByFirstNameAndLastName(String firstName, String lastName);
}
