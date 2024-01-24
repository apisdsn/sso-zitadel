package com.demo.spring.upload.file.oauth.sso.service;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

import com.demo.spring.upload.file.oauth.sso.model.WebRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.demo.spring.upload.file.oauth.sso.model.FileDB;
import com.demo.spring.upload.file.oauth.sso.repository.FileDBRepository;

@Service
public class FileStorageService {

  @Autowired
  private FileDBRepository fileDBRepository;

  public void store(MultipartFile files, WebRequest request) throws IOException {
    String fileName = StringUtils.cleanPath(Objects.requireNonNull(files.getOriginalFilename()));
    FileDB fileDB = new FileDB();
    fileDB.setTitle(request.getTitle());
    fileDB.setContent(request.getContent());
    fileDB.setName(fileName);
    fileDB.setType(files.getContentType());
    fileDB.setData(files.getBytes());

    fileDBRepository.save(fileDB);
  }

  public void update(MultipartFile file, WebRequest request, String id) throws IOException {
    String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

    FileDB fileDB = fileDBRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cannot find file with id: " + id));

    fileDB.setTitle(request.getTitle());
    fileDB.setContent(request.getContent());
    fileDB.setName(fileName);
    fileDB.setType(file.getContentType());
    fileDB.setData(file.getBytes());

    fileDBRepository.save(fileDB);
  }
  public void delete(String id) {
    fileDBRepository.deleteById(id);
  }

  public FileDB getFile(String id) {
    return fileDBRepository.findById(id).orElseThrow(() -> new RuntimeException("Cannot find file with id: " + id));
  }

  public Stream<FileDB> getAllFiles() {
    return fileDBRepository.findAll().stream();
  }
}
