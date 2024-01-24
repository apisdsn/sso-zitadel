package com.demo.spring.upload.file.oauth.sso.service;

import com.demo.spring.upload.file.oauth.sso.model.FileDB;
import com.demo.spring.upload.file.oauth.sso.model.WebRequest;
import com.demo.spring.upload.file.oauth.sso.repository.FileDBRepository;
import com.demo.spring.upload.file.oauth.sso.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Incubating;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FileStorageServiceTest {

    @Mock
    private FileDBRepository fileDBRepository;
@InjectMocks

    private FileStorageService fileStorageService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
//        fileStorageService = new FileStorageService(fileDBRepository);
    }

    @Test
    public void testStore() throws IOException {
        // Arrange
        MultipartFile file = new MockMultipartFile("test.txt", "Hello, World!".getBytes());
        WebRequest request = new WebRequest();
        request.setTitle("Test Title");
        request.setContent("Test Content");

        // Act
        fileStorageService.store(file, request);

        // Assert
        verify(fileDBRepository, times(1)).save(any(FileDB.class));
    }

    @Test
    public void testUpdate() throws IOException {
        // Arrange
        String fileId = "123";
        MultipartFile file = new MockMultipartFile("test.txt", "Hello, World!".getBytes());
        WebRequest request = new WebRequest();
        request.setTitle("Updated Title");
        request.setContent("Updated Content");

        FileDB existingFile = new FileDB();
        existingFile.setId(fileId);

        when(fileDBRepository.findById(fileId)).thenReturn(Optional.of(existingFile));

        // Act
        fileStorageService.update(file, request, fileId);

        // Assert
        verify(fileDBRepository, times(1)).save(any(FileDB.class));
    }

    @Test
    public void testDelete() {
        // Arrange
        String fileId = "123";

        // Act
        fileStorageService.delete(fileId);

        // Assert
        verify(fileDBRepository, times(1)).deleteById(fileId);
    }

    @Test
    public void testGetFile() {
        // Arrange
        String fileId = "123";
        FileDB expectedFile = new FileDB();
        expectedFile.setId(fileId);

        when(fileDBRepository.findById(fileId)).thenReturn(Optional.of(expectedFile));

        // Act
        FileDB actualFile = fileStorageService.getFile(fileId);

        // Assert
        assertEquals(expectedFile, actualFile);
    }

    @Test
    public void testGetAllFiles() {
        // Arrange
        List<FileDB> expectedFiles = new ArrayList<>();
        expectedFiles.add(new FileDB());
        expectedFiles.add(new FileDB());

        when(fileDBRepository.findAll()).thenReturn(expectedFiles);

        // Act
        Stream<FileDB> actualFiles = fileStorageService.getAllFiles();
        List<FileDB> actualFileList = actualFiles.collect(Collectors.toList());

        // Assert
        assertEquals(expectedFiles.size(), actualFileList.size());
        assertEquals(expectedFiles, actualFileList);
    }
}