package org.openwis.metadataportal.services.login;

import jeeves.utils.Log;
import jeeves.server.context.ServiceContext;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.json.JSONException;
import org.json.JSONObject;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;
import java.net.URI;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class GoogleCaptchaVerificator {

    // Url for verifying client captcha response
    private static final String GOOGLE_CAPTCHA_VERIFICATION_URL = "https://www.google.com/recaptcha/api/siteverify";

    private static final String HTTPS_PROXY = "https_proxy";
    public static String PROXY_USER = "proxy_user";
    public static String USER_PASSWORD = "proxy_pwd";

    /**
     * Verify against user response if google captcha passses
     *
     * @param userCaptchaResponse user captcha response
     * @return true if catpcha pass false otherwise
     * @throws IOException
     * @throws JSONException
     */
    public static Boolean verify(String userCaptchaResponse) throws IOException, JSONException {

        HttpClient client = new HttpClient();

        if (System.getenv(HTTPS_PROXY) != null) {
            Log.info(LoginConstants.LOG,"Use proxy: " + System.getenv(HTTPS_PROXY));
            String proxyHttps = System.getenv(HTTPS_PROXY);
            try {
                URI proxyUrl = new URI(proxyHttps);
                client.getHostConfiguration().setProxy(proxyUrl.getHost(), proxyUrl.getPort());
            } catch (URISyntaxException e) {
                Log.error(LoginConstants.LOG, "Proxy malformed " + proxyHttps);
            }
            if (System.getenv(PROXY_USER) != null && System.getenv(USER_PASSWORD) != null) {
                Credentials cred = new UsernamePasswordCredentials(System.getenv(PROXY_USER) , System.getenv(USER_PASSWORD));
                AuthScope scope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM);

                client.getState().setProxyCredentials(scope, cred);

            }
        }
        PostMethod method = new PostMethod(GOOGLE_CAPTCHA_VERIFICATION_URL);

        // set headers
        method.setRequestHeader("Content-Type", "application/json");
        method.setRequestHeader("Accept", "application/json");

        NameValuePair[] queryParam = {
                new NameValuePair("secret", OpenwisMetadataPortalConfig
                        .getString(ConfigurationConstants.GOOGLE_RECAPTCHA_SECRET_KEY)),
                new NameValuePair("response", userCaptchaResponse)
        };
        method.setQueryString(queryParam);

        int statusCode = client.executeMethod(method);
        if (statusCode == HttpStatus.SC_OK) {
            // Read the response body.
            byte[] responseBody = method.getResponseBody();

            JSONObject json = new JSONObject(new String(responseBody));
            // Deal with the response.
            return json.getBoolean("success");
        }

        return false;
    }
}
