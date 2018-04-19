package springskeleton.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import springskeleton.config.Endpoints;
import springskeleton.config.property.AdminUserProperties;
import springskeleton.controller.wrapper.request.UserLoginRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class HttpUtils {

    private MockMvc mockMvc;

    private AdminUserProperties adminUserProperties;

    public ResponseData loginWithCredentials(final String name, final String password) throws Exception {
        final UserLoginRequest user = new UserLoginRequest(name, password);
        ResultActions result = this.mockMvc.perform(post(Endpoints.AUTH + Endpoints.LOGIN).contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user)));
        return this.buildResponseData(result);
    }

    public ResponseData loginWithAdmin() throws Exception {
        return this.loginWithCredentials(adminUserProperties.getEmail(), adminUserProperties.getPassword());
    }

    public JSONObject getJSONObjectFromBase64(final String encodedString) {
        final String decodedValue = new String((Base64.getDecoder().decode(encodedString)));
        try {
            return new JSONObject(decodedValue);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResponseData getRequest(final RequestData requestData) throws Exception {
        return this.performRequest(requestData, get(requestData.getUrl()));
    }

    public ResponseData postRequest(final RequestData requestData) throws Exception {
        return this.performRequest(requestData, post(requestData.getUrl()));
    }

    public ResponseData patchRequest(final RequestData requestData) throws Exception {
        return this.performRequest(requestData, patch(requestData.getUrl()));
    }

    public ResponseData putRequest(final RequestData requestData) throws Exception {
        return this.performRequest(requestData, put(requestData.getUrl()));
    }

    public ResponseData deleteRequest(final RequestData requestData) throws Exception {
        return this.buildResponseData(this.mockMvc.perform(delete(requestData.getUrl())));
    }

    public void setMockMvc(final MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public void setAdminUserProperties(final AdminUserProperties adminUserProperties) {
        this.adminUserProperties = adminUserProperties;
    }

    private ResponseData performRequest(RequestData requestData, MockHttpServletRequestBuilder requestBuilder) throws Exception {
        requestData.getCookies().forEach(requestBuilder::cookie);
        if (requestData.getContentType() != null) {
            requestBuilder.contentType(requestData.getContentType());
        }
        if (requestData.getContent() != null) {
            requestBuilder.content(requestData.getContent());
        }
        return this.buildResponseData(this.mockMvc.perform(requestBuilder));
    }

    private ResponseData buildResponseData(final ResultActions resultActions) {
        return new ResponseData(resultActions.andReturn().getResponse());
    }

}
