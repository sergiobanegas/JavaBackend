package springskeleton.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import springskeleton.config.ResourceNames;
import springskeleton.controller.exception.ServerErrorException;

@Service
@Transactional
public class FileUtils {

    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList("image/png", "image/jpeg");

    public void uploadFile(final MultipartFile file, final String destination, final String fileName) throws ServerErrorException {
        try {
            this.createDirectoryIfNotExists(destination);
            final byte[] bytes = file.getBytes();
            final Path path = Paths.get(ResourceNames.PUBLIC + destination + "/" + fileName);
            Files.write(path, bytes);
        } catch (IOException e) {
            throw new ServerErrorException("Error uploading file");
        }
    }

    public boolean isFileAnImage(final MultipartFile file) {
        return ALLOWED_IMAGE_EXTENSIONS.contains(file.getContentType());
    }

    private void createDirectoryIfNotExists(final String destination) throws ServerErrorException {
        File dir = new File(ResourceNames.PUBLIC + destination);
        if (!dir.exists()) {
            boolean successful = dir.mkdirs();
            if (!successful) {
                throw new ServerErrorException("Error creating file in " + destination);
            }
        }
    }

}
