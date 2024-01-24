package com.demo.spring.upload.file.oauth.sso.repository;

import com.demo.spring.upload.file.oauth.sso.model.FileDB;
import com.demo.spring.upload.file.oauth.sso.repository.FileDBRepository;
import com.demo.spring.upload.file.oauth.sso.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
class FileDBRepositoryTest {

    @Mock
    private FileDBRepository fileDBRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        // Arrange
        List<FileDB> files = new ArrayList<>();
        files.add(new FileDB("1", "file1.txt", "File 1", "File content 1", "txt", new byte[]{}));
        files.add(new FileDB("2", "file2.txt", "File 2", "File content 2", "txt", new byte[]{}));
        when(fileDBRepository.findAll()).thenReturn(files);

        // Act
        List<FileDB> result = fileDBRepository.findAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals("file1.txt", result.get(0).getName());
        assertEquals("file2.txt", result.get(1).getName());
    }

    @Test
    void testFindById() {
        // Arrange
        FileDB file = new FileDB("1", "file1.txt", "File 1", "File content 1", "txt", new byte[]{});
        when(fileDBRepository.findById("1")).thenReturn(Optional.of(file));

        // Act
        Optional<FileDB> result = fileDBRepository.findById("1");

        // Assert
        assertEquals(file, result.get());
    }

    @Test
    void testSave() {
        // Arrange
        FileDB file = new FileDB("1", "file1.txt", "File 1", "File content 1", "txt", new byte[]{});
        when(fileDBRepository.save(file)).thenReturn(file);

        // Act
        FileDB result = fileDBRepository.save(file);

        // Assert
        assertEquals(file, result);
    }

    @Test
    void testDeleteById() {
        // Arrange
        String fileId = "1";

        // Act
        fileDBRepository.deleteById(fileId);

        // Assert
        verify(fileDBRepository, times(1)).deleteById(fileId);
    }
}