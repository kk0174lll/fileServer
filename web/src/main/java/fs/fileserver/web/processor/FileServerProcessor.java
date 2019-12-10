package fs.fileserver.web.processor;

import filter.SID;
import fs.fileserver.web.DirectoryProperties;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
import java.util.Collection;
import java.util.Iterator;

@Service
public class FileServerProcessor
{

  private final Path rootLocation;
  private final DirectoryProperties directoryProperties;

  @Autowired
  public FileServerProcessor(DirectoryProperties directoryProperties)
  {
    this.directoryProperties = directoryProperties;
    this.rootLocation = Paths.get(directoryProperties.getWork());
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
    return new String(Files.readAllBytes(Paths.get(rootLocation + key)));
  }


  public void saveFileName(String key, String name) throws IOException
  {
    Files.write(Paths.get(rootLocation + key), name.getBytes());
  }


}
