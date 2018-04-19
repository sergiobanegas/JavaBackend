package springskeleton.util;

import springskeleton.config.property.AdminUserProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

public class ControllerTest extends IntegrationTest {

    protected AdminUserProperties adminUserProperties;

    protected HttpUtils httpUtils;

    private FilterChainProxy springSecurityFilterChain;

    private WebApplicationContext webApplicationContext;

    @Autowired
    public void setSpringSecurityFilterChain(FilterChainProxy springSecurityFilterChain) {
        this.springSecurityFilterChain = springSecurityFilterChain;
    }

    @Autowired
    public void setWebApplicationContext(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    @Autowired
    public void setAdminUserProperties(AdminUserProperties adminUserProperties) {
        this.adminUserProperties = adminUserProperties;
    }

    protected void setUpHttpUtils() {
        this.httpUtils = new HttpUtils();
        this.httpUtils.setMockMvc(webAppContextSetup(this.webApplicationContext).addFilter(this.springSecurityFilterChain).build());
        this.httpUtils.setAdminUserProperties(adminUserProperties);
    }

}
