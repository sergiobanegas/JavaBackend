package springskeleton.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ResponseData {


    private MockHttpServletResponse response;

    public ResponseData(final MockHttpServletResponse response) {
        this.response = response;
    }

    public Cookie getCookie(final String name) {
        return response.getCookie(name);
    }

    public String getContent() throws UnsupportedEncodingException {
        return this.response.getContentAsString();
    }

    public final Object mapContent(final Class<?> classToMap) throws IOException {
        return new ObjectMapper().readValue(this.getContent(), classToMap);
    }

    public boolean isOk() {
        return this.response.getStatus() == HttpStatus.OK.value();
    }

    public boolean isCreated() {
        return this.response.getStatus() == HttpStatus.CREATED.value();
    }

    public boolean isUnauthorized() {
        return this.response.getStatus() == HttpStatus.UNAUTHORIZED.value();
    }

    public boolean isForbidden() {
        return this.response.getStatus() == HttpStatus.FORBIDDEN.value();
    }

    public boolean isNotFound() {
        return this.response.getStatus() == HttpStatus.NOT_FOUND.value();
    }

    public boolean isBadRequest() {
        return this.response.getStatus() == HttpStatus.BAD_REQUEST.value();
    }

}
