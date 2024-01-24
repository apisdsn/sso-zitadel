package com.demo.spring.upload.file.oauth.sso.controller;

import com.demo.spring.upload.file.oauth.sso.message.ResponseFile;
import com.demo.spring.upload.file.oauth.sso.message.ResponseMessage;
import com.demo.spring.upload.file.oauth.sso.model.FileDB;
import com.demo.spring.upload.file.oauth.sso.model.WebRequest;
import com.demo.spring.upload.file.oauth.sso.service.FileStorageService;
import io.cucumber.java.nl.Stel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileControllerTest {

    @InjectMocks
    private FileController fileController;

    @Mock
    private FileStorageService fileStorageService;

    // Existing tests...

    @Test
    void testGetAllFilesWhenIfFileExsist() {
        FileDB fileDB = new FileDB();
        fileDB.setData(new byte[0]); // Set the data to a non-null value

        MockHttpServletRequest  mockRequest = new MockHttpServletRequest();
        mockRequest.setContextPath("/");

        ServletRequestAttributes attrs = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attrs);

        // Mock the behavior of the FileStorageService
        when(fileStorageService.getAllFiles()).thenReturn(Stream.of(fileDB));

        // Call the getListFiles method
        ResponseEntity<List<ResponseFile>> response = fileController.getListFiles();

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(null, response.getBody().get(0).getName());
    }
    @Test
    void testUploadFileWhenFileIsUploadedThenReturnOk() throws IOException {
        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "Test Image".getBytes());
        String title = "Test Title";
        String content = "Test Content";
        WebRequest request = new WebRequest();
        request.setTitle(title);
        request.setContent(content);

        doNothing().when(fileStorageService).store(file, request);

        ResponseEntity<ResponseMessage> response = fileController.uploadFile(file, title, content);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Uploaded the file successfully: test.jpg", response.getBody().getMessage());
    }

    @Test
    void testUploadFileWhenFileUploadFailsThenReturnExpectationFailed() throws IOException {
        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "Test Image".getBytes());
        String title = "Test Title";
        String content = "Test Content";
        WebRequest request = new WebRequest();
        request.setTitle(title);
        request.setContent(content);

        doThrow(new IOException("File storage failure")).when(fileStorageService).store(file, request);

        ResponseEntity<ResponseMessage> response = fileController.uploadFile(file, title, content);

        assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
        assertEquals("Could not upload the file: test.jpg!", response.getBody().getMessage());
    }

    @Test
    void testUpdateFileWhenFileIsUpdatedThenReturnOk() throws IOException {
        MockMultipartFile file = new MockMultipartFile("image", "updated.jpg", "image/jpeg", "Updated Image".getBytes());
        String title = "Updated Title";
        String content = "Updated Content";
        String id = "123";
        WebRequest request = new WebRequest();
        request.setTitle(title);
        request.setContent(content);

        doNothing().when(fileStorageService).update(file, request, id);

        ResponseEntity<ResponseMessage> response = fileController.updateFile(file, title, content, id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Update the file successfully: updated.jpg", response.getBody().getMessage());
    }

    @Test
    void testUpdateFileWhenFileUpdateFailsThenReturnExpectationFailed() throws IOException {
        MockMultipartFile file = new MockMultipartFile("image", "updated.jpg", "image/jpeg", "Updated Image".getBytes());
        String title = "Updated Title";
        String content = "Updated Content";
        String id = "123";
        WebRequest request = new WebRequest();
        request.setTitle(title);
        request.setContent(content);

        doThrow(new IOException("File update failure")).when(fileStorageService).update(file, request, id);

        ResponseEntity<ResponseMessage> response = fileController.updateFile(file, title, content, id);

        assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
        assertEquals("Could not update the file: updated.jpg!", response.getBody().getMessage());
    }

    @Test
    void testDeleteFileWhenFileIsDeletedThenReturnOk() {
        String id = "123";

        doNothing().when(fileStorageService).delete(id);

        ResponseEntity<ResponseMessage> response = fileController.delteFile(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Delete the file successfully: " + id, response.getBody().getMessage());
    }

    @Test
    void testDeleteFileWhenFileDeletionFailsThenReturnExpectationFailed() {
        String id = "123";

        doThrow(new RuntimeException("File deletion failure")).when(fileStorageService).delete(id);

        ResponseEntity<ResponseMessage> response = fileController.delteFile(id);

        assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
        assertEquals("Could not delete the file: " + id + "!", response.getBody().getMessage());
    }

    @Test
    void testGetListFilesWhenNoFilesExistThenReturnEmptyList() {
        when(fileStorageService.getAllFiles()).thenReturn(Stream.empty());

        ResponseEntity<List<ResponseFile>> response = fileController.getListFiles();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
    }

    @Test
    void testGetListFilesWhenExceptionThrownThenReturnErrorMessage() {
        when(fileStorageService.getAllFiles()).thenThrow(new RuntimeException("File retrieval failure"));

        Exception exception = assertThrows(RuntimeException.class, () -> fileController.getListFiles());

        assertEquals("File retrieval failure", exception.getMessage());
    }
    @Test
    void testGetFileById() {
        FileDB fileDB = new FileDB();
        fileDB.setData(new byte[0]);

        when(fileStorageService.getFile(anyString())).thenReturn(fileDB);

        ResponseEntity<byte[]> response = fileController.getFile("fileId");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(fileDB.getData(), response.getBody());
    }

    // Additional tests...
}