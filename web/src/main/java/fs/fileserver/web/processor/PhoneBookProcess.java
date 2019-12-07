package fs.fileserver.web.processor;

import org.springframework.stereotype.Component;
import fs.fileserver.web.DirectoryProperties;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class PhoneBookProcess
{

  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy_HHmm");

  private final DirectoryProperties directoryProperties;

  PhoneBookProcess(DirectoryProperties directoryProperties)
  {
    this.directoryProperties = directoryProperties;
  }


  public String writePrepareFile(String json) throws IOException
  {
    String fileName = "data_" + LocalDateTime.now().format(formatter) + ".json";
    writeFile(prepareFileName(fileName), json);
    return fileName;
  }

  public String deletePrepareFile(String fileName) throws IOException
  {
    deleteFile(prepareFileName(fileName));
    return fileName;
  }

  public String transferFile(String fileName) throws IOException
  {
    String prepareFileName = prepareFileName(fileName);
    String timeFormat = LocalDateTime.now().format(formatter);
    String newFileName = "json_" + timeFormat + ".txt";
    String flagFileName = "json_" + timeFormat + ".ready";
    String workFileName = workFileName(newFileName);
    Files.move(Paths.get(prepareFileName), Paths.get(workFileName), StandardCopyOption.REPLACE_EXISTING);
    writeFile(workFileName(flagFileName), "");
    return prepareFileName + "=>" + workFileName;
  }

  private void writeFile(String fileName, String json) throws IOException
  {
    try (PrintWriter writer = new PrintWriter(fileName, "UTF-8");) {
      writer.write(json);
      writer.flush();
    }
  }


  private void deleteFile(String fileName) throws IOException
  {
    Files.deleteIfExists(Paths.get(fileName));
  }

  private String workFileName(String fileName)
  {
    return createFileName(directoryProperties.getWork(), fileName);
  }

  private String prepareFileName(String fileName)
  {
    return createFileName(directoryProperties.getPrepare(), fileName);
  }

  private String createFileName(String directory, String fileName)
  {
    String result;
    if (directory.endsWith("\\")) {
      result = directory + fileName;
    } else {
      result = directory + "\\" + fileName;
    }
    return result;
  }

}
