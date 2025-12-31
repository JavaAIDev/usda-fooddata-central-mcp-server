package com.javaaidev.usdafdc.mcp.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FileCacheService {

  private final ObjectMapper mapper = new ObjectMapper();
  private final Path cacheDir = Paths.get(".", "file-cache").normalize().toAbsolutePath();
  private static final Logger LOGGER = LoggerFactory.getLogger(FileCacheService.class);

  public FileCacheService() {
    try {
      Files.createDirectories(cacheDir);
      LOGGER.info("Cache dir is {}", cacheDir);
    } catch (IOException e) {
      LOGGER.error("Failed to create cache dir: {}", cacheDir, e);
    }
  }

  public <T> T runWithCache(String cacheType, Object[] args, Class<T> clazz, Callable<T> callable)
      throws Exception {
    String key = generateKey(cacheType, args);
    LOGGER.info("Cache key {}", key);
    Path cacheFile = cacheDir.resolve(cacheType + "-" + key + ".json");
    if (Files.exists(cacheFile)) {
      return mapper.readValue(cacheFile.toFile(), clazz);
    }
    var result = callable.call();
    mapper.writeValue(cacheFile.toFile(), result);
    return result;
  }

  private String generateKey(String cacheType, Object[] args) throws Exception {
    String base = cacheType + ":" + mapper.writeValueAsString(args);
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    byte[] digest = md.digest(base.getBytes());
    return Base64.getUrlEncoder().withoutPadding().encodeToString(digest).substring(0, 16);
  }
}