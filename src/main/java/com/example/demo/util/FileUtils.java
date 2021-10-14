package com.example.demo.util;

import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public abstract class FileUtils {

  @SneakyThrows
  public static Stream<File> stream(Path path) {
    // path를 넣으면 파일의 list를 Stream 형태로 가져옴
    return Files.list(path).map(Path::toFile);
  }
}
