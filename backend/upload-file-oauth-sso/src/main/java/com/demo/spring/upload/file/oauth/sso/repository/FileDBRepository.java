package com.demo.spring.upload.file.oauth.sso.repository;

import com.demo.spring.upload.file.oauth.sso.model.FileDB;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface FileDBRepository extends JpaRepository<FileDB, String> {

}