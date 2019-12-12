package fs.fileserver.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fs.fileserver.web.FileServerProperties;
import fs.fileserver.web.models.UploadResponse;
import fs.fileserver.web.processor.FileServerProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.activation.FileTypeMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PageController {
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final FileServerProcessor processor;
    private final FileServerProperties fileServerProperties;

    public PageController(ObjectMapper objectMapper, FileServerProcessor fileServerProcessor, FileServerProperties fileServerProperties) {
        this.processor = fileServerProcessor;
        this.objectMapper = objectMapper;
        this.fileServerProperties = fileServerProperties;
    }

    @GetMapping(value = "/")
    public String main(Model model) {
        model.addAttribute("title", fileServerProperties.getTitle());
        model.addAttribute("domain", fileServerProperties.getDomain());
        return "image_hosting";
    }

    @GetMapping(value = "/i/{nameList}")
    public String info(@PathVariable String nameList, @RequestHeader MultiValueMap<String, String> headers, Model model) {
        try {
            logger.info("nameList: " + nameList);
            logHeaders(headers);
            String[] fileNameList = nameList.split(",");
            List<UploadResponse> files = new ArrayList<>();
            for (String file : fileNameList) {
                UploadResponse response = new UploadResponse();
                response.fileId = file;
                response.fileName = processor.readFileName(file);
                files.add(response);
                model.addAttribute("title", fileServerProperties.getTitle());
                model.addAttribute("domain", fileServerProperties.getDomain());
                model.addAttribute("filesList", files);
                model.addAttribute("nameList", nameList);
            }
        } catch (Exception e) {
            logger.error("load info error:", e);
        }
        return "info";
    }

    @GetMapping(value = "/v/{nameList}")
    public String view(@PathVariable String nameList, @RequestHeader MultiValueMap<String, String> headers, Model model) throws IOException {
        try {
            logger.info("nameList: " + nameList);
            logHeaders(headers);
            String[] fileNameList = nameList.split(",");
            List<UploadResponse> files = new ArrayList<>();
            for (String file : fileNameList) {
                UploadResponse response = new UploadResponse();
                response.fileId = file;
                response.fileName = processor.readFileName(file);
                files.add(response);
            }
            model.addAttribute("title", fileServerProperties.getTitle());
            model.addAttribute("domain", fileServerProperties.getDomain());
            model.addAttribute("filesList", files);
            model.addAttribute("nameList", nameList);
        } catch (Exception e) {
            logger.error("load info error:", e);
        }
        return "view";
    }

    private void logHeaders(MultiValueMap<String, String> headers) {
        try {
            logger.info(String.format("Headers: %s", objectMapper.writeValueAsString(headers)));
        } catch (JsonProcessingException e) {
            headers.forEach((key, value) -> logger.info(String.format(
                    "Header '%s' = %s", key, String.join("|", value))));
        }

    }
}
