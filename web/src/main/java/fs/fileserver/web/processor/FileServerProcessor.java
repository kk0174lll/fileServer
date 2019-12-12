package fs.fileserver.web.processor;

import fs.fileserver.web.FileServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileServerProcessor
{
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final Path rootLocation;

  @Autowired
  public FileServerProcessor(FileServerProperties fileServerProperties)
  {
    this.rootLocation = Paths.get(fileServerProperties.getDirectory());
  }

  public String store(MultipartFile file, String fileId)
  {
    String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
    String filename = fileId + "." + originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
    try {
      if (file.isEmpty()) {
        throw new StorageException("Failed to store empty file " + filename);
      }
      if (filename.contains("..")) {
        // This is a security check
        throw new StorageException(
          "Cannot store file with relative path outside current directory "
          + filename);
      }
      try (InputStream inputStream = file.getInputStream()) {
        Files.copy(inputStream, this.rootLocation.resolve(filename),
                   StandardCopyOption.REPLACE_EXISTING);
      }
      saveFileName(fileId, filename);
    } catch (IOException e) {
      logger.error("save file error", e);
      throw new StorageException("Failed to store file " + filename, e);
    }
    return filename;
  }

  public File load(String name)
  {
    String filename = StringUtils.cleanPath(name);

    if (Utils.isEmpty(filename)) {
      throw new StorageException("Failed to load, file name is empty ");
    }
    if (filename.contains("..")) {
      // This is a security check
      throw new StorageException(
        "Cannot load file with relative path outside current directory "
        + filename);
    }

    return new File(rootLocation + "/" + name);
  }

  public String readFileName(String key) throws IOException
  {
    return new String(Files.readAllBytes(this.rootLocation.resolve(key)));
  }


  public void saveFileName(String key, String name) throws IOException
  {
    Files.write(this.rootLocation.resolve(key), name.getBytes());
  }


}
