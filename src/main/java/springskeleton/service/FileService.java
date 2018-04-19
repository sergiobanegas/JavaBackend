package springskeleton.service;

import springskeleton.config.ResourceNames;
import springskeleton.controller.exception.ServerErrorException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {

    public byte[] loadUserAvatar(final Long userId) throws ServerErrorException {
        return this.loadImageAsResource(ResourceNames.USER + "/" + userId + "/" + ResourceNames.AVATAR_FILE);
    }

    private byte[] loadImageAsResource(final String filename) throws ServerErrorException {
        final Path path = Paths.get(ResourceNames.PUBLIC + filename);
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new ServerErrorException(e.getMessage());
        }
    }


}
