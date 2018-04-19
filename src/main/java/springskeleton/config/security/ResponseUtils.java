package springskeleton.config.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import springskeleton.controller.exception.ServerErrorException;
import springskeleton.controller.wrapper.response.ApiResponse;
import org.springframework.http.MediaType;

public class ResponseUtils {

    private final Gson gson = new Gson();

    public void generateResponse(HttpServletResponse response, final ApiResponse apiResponse) {
        response.setStatus(apiResponse.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8.toString());
        try {
            PrintWriter out = response.getWriter();
            out.print(gson.toJson(apiResponse));
            out.flush();
        } catch (IOException e) {
            throw new ServerErrorException(e.getMessage());
        }
    }

}
