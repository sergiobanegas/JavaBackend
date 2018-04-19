package springskeleton.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import springskeleton.config.Endpoints;
import springskeleton.controller.exception.ServerErrorException;
import springskeleton.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

@RestController
@RequestMapping(Endpoints.FILES)
public class FileController {

    private FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping(value = Endpoints.USER + Endpoints.ID + Endpoints.AVATAR, produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody
    byte[] get(@PathVariable final Long id) throws ServerErrorException {
        return this.fileService.loadUserAvatar(id);
    }

}
