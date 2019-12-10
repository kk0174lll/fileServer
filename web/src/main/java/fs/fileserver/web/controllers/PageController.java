package fs.fileserver.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fs.fileserver.web.models.UploadResponse;
import fs.fileserver.web.processor.FileServerProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.activation.FileTypeMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PageController
{
  private final ObjectMapper objectMapper;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final FileServerProcessor processor;

  public PageController(ObjectMapper objectMapper, FileServerProcessor fileServerProcessor)
  {
    this.processor = fileServerProcessor;
    this.objectMapper = objectMapper;
  }
  @GetMapping (value = "/")
  public String main()
  {
    return "image_hosting";
  }

  @GetMapping (value = "/i/{nameList}")
  public String info(@PathVariable String nameList, Model model) throws IOException
  {
    logger.info("nameList: " + nameList);
    String[] fileNameList = nameList.split(",");
    List<UploadResponse> files = new ArrayList<>();
    for (String file: fileNameList) {
      UploadResponse response = new UploadResponse();
      response.fileId = file;
      response.fileName = processor.readFileName(file);
      files.add(response);
    }
    model.addAttribute("files", objectMapper.writeValueAsString(files));
    model.addAttribute("filesList", files);
    model.addAttribute("nameList", nameList);
    return "info";
  }

  @GetMapping (value = "/v/{nameList}")
  public String view(@PathVariable String nameList, Model model) throws IOException
  {
    logger.info("nameList: " + nameList);
    String[] fileNameList = nameList.split(",");
    List<UploadResponse> files = new ArrayList<>();
    for (String file: fileNameList) {
      UploadResponse response = new UploadResponse();
      response.fileId = file;
      response.fileName = processor.readFileName(file);
      files.add(response);
    }
    model.addAttribute("files", objectMapper.writeValueAsString(files));
    model.addAttribute("filesList", files);
    model.addAttribute("nameList", nameList);
    return "view";
  }

}
