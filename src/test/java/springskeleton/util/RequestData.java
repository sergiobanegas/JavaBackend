package springskeleton.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class RequestData {

    @NonNull
    private String url;

    private List<Cookie> cookies = new ArrayList<>();

    private MediaType contentType;

    private String content;

    public void addCookie(final Cookie cookie) {
        this.cookies.add(cookie);
    }

    public void setContent(Object object) throws JsonProcessingException {
        this.content = new ObjectMapper().writeValueAsString(object);
    }

}
