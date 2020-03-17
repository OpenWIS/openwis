package org.openwis.metadataportal.services.login;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONException;
import org.json.JSONObject;
import org.openwis.metadataportal.common.configuration.ConfigurationConstants;
import org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig;

import java.io.IOException;

public class GoogleCaptchaVerificator {

    // Url for verifying client captcha response
    private static final String GOOGLE_CAPTCHA_VERIFICATION_URL = "https://www.google.com/recaptcha/api/siteverify";

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
