package fs.fileserver.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fs.fileserver.web.processor.PhoneBookProcess;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.activation.FileTypeMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class FileServerController {

    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PhoneBookProcess processor;


    public FileServerController(ObjectMapper objectMapper, PhoneBookProcess processor) {
        this.objectMapper = objectMapper;
        this.processor = processor;
    }

    @RequestMapping(value = "/ex/foos/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public byte[] getFoosBySimplePathWithPathVariable(
            @PathVariable String id) throws IOException {
        File initialFile = new File("/" + id);
        InputStream in = new FileInputStream(initialFile);
        return IOUtils.toByteArray(in);
    }

    @RequestMapping(value = "/download/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> what(@PathVariable String id) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get("/Users/kk0174lll/temp/" + id)));
        File file = new File("/" + content);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + file.getName())
                .contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(file)))
                .body(Files.readAllBytes(file.toPath()));
    }


}
