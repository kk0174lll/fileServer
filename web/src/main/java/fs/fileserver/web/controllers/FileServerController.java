package fs.fileserver.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import filter.SID;
import fs.fileserver.web.models.CaptchaRequest;
import fs.fileserver.web.models.UploadResponse;
import fs.fileserver.web.processor.FileServerProcessor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import utils.Utils;

import javax.activation.FileTypeMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@RestController
public class FileServerController {

    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final FileServerProcessor processor;

    public FileServerController(ObjectMapper objectMapper, FileServerProcessor fileServerProcessor) {
        this.objectMapper = objectMapper;
        this.processor = fileServerProcessor;
    }

    @RequestMapping(method = RequestMethod.POST, produces = Utils.CONTENT_TYPE, value = "/captcha/check")
    public Boolean captchaCheck(@RequestBody CaptchaRequest fileRequest, @RequestHeader MultiValueMap<String, String> headers) {
        //todo check
        return false;
    }

    @PostMapping("/upload")
    public UploadResponse fileUpload(@RequestParam("fileData") MultipartFile file, @RequestHeader MultiValueMap<String, String> headers) {

        String fileId = SID.getSID();
        logHeaders(headers);
        UploadResponse response = new UploadResponse();
        try {
            logger.info("upload file: " + file.getOriginalFilename());
            response.fileId = fileId;
            response.fileName = processor.store(file, fileId);
            response.success = true;
            logger.info("save success as: " + response.fileName);
        } catch (Exception e) {
            logger.info("save error");
            logger.error(e.getMessage());
            response.success = false;
        }
        return response;
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
