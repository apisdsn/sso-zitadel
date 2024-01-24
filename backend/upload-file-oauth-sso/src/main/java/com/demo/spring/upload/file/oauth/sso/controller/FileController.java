package com.demo.spring.upload.file.oauth.sso.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.demo.spring.upload.file.oauth.sso.message.ResponseFile;
import com.demo.spring.upload.file.oauth.sso.message.ResponseMessage;
import com.demo.spring.upload.file.oauth.sso.model.FileDB;
import com.demo.spring.upload.file.oauth.sso.model.WebRequest;
import com.demo.spring.upload.file.oauth.sso.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
@RequestMapping("/api/v1/image")
@CrossOrigin("*")
public class FileController {

    @Autowired
    private FileStorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("image") MultipartFile image,
            @RequestParam("title") String title, @RequestParam("content") String content) {

        String message = "";

        try {
            WebRequest request = new WebRequest();
            request.setTitle(title);
            request.setContent(content);

            storageService.store(image, request);

            message = "Uploaded the file successfully: " + image.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + image.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseMessage> updateFile(@RequestParam("image") MultipartFile image,
            @RequestParam("title") String title, @RequestParam("content") String content, @PathVariable String id) {
        String message = "";
        try {
            WebRequest request = new WebRequest();
            request.setTitle(title);
            request.setContent(content);

            storageService.update(image, request, id);

            message = "Update the file successfully: " + image.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));

        } catch (Exception e) {
            message = "Could not update the file: " + image.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseMessage> delteFile(@PathVariable("id") String id) {
        String message = "";
        try {
            storageService.delete(id);
            message = "Delete the file successfully: " + id;
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Could not delete the file: " + id + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<ResponseFile>> getListFiles() {
        List<ResponseFile> files = storageService.getAllFiles().map(dbFile -> {
            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/v1/image/files/")
                    .path(dbFile.getId())
                    .toUriString();

            return ResponseFile.builder()
                    .id(dbFile.getId())
                    .name(dbFile.getName())
                    .url(fileDownloadUri)
                    .title(dbFile.getTitle())
                    .content(dbFile.getContent())
                    .type(dbFile.getType())
                    .size(dbFile.getData().length)
                    .build();
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(files);
    }

    @GetMapping("/files/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable String id) {
        FileDB fileDB = storageService.getFile(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
                .body(fileDB.getData());
    }
}
