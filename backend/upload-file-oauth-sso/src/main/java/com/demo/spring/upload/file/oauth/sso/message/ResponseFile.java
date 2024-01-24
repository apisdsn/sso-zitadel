package com.demo.spring.upload.file.oauth.sso.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseFile {
  private String id;
  private String name;
  private String url;
  private String title;
  private String content;
  private String type;
  private long size;
}
