package jeeves.utils;

import com.google.json.JsonSanitizer;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class SanitizeJson {

    public SanitizeJson() {}

    public String sanitize(String unsecuredJson) {
        /**
         * THIS IS PURE CRAP!!!!
         * The problem here is that the frontend does not always send a valid JSON as payload. Some services send
         * just a string like "["val1", "val2]". Therefore creating a json from this will fail. I can spent some time to fix it,
         * but God knows what crap is hiding behind all these services. So, I choose the easy path. Just return the string if
         * it cannot be transformed in a JSON. BUT THIS IS A VULNERABILITY. An attacker can insert malicious data in a string and
         * bypass the sanitize method.
         * The unsecuredJson MUST BE SANITIZED before usage.
         *
         * One more thing: why the try / catch at this point. It's because {@link JeevesJsonWrapper}#send method which
         * throws an {@link Exception}. Happy day!!!
         */
        try {
            JSONObject obj = new JSONObject(JsonSanitizer.sanitize(unsecuredJson));
            Map<String, Object> map = convertToMap(obj);
            return new JSONObject(map).toString();
        } catch (JSONException e) {
            return unsecuredJson;
        }
    }

    public Map<String, Object> convertToMap(JSONObject jsonObject) {
        Map<String, Object> map = new HashMap<>();
        List<Object> mapArr = null;
        for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
            Object key = it.next();
            if (jsonObject.get((String) key) instanceof JSONObject) {
                map.put((String) key, convertToMap((JSONObject) jsonObject.get((String) key)));
            } else if (jsonObject.get((String) key) instanceof JSONArray) {
                mapArr = new ArrayList<Object>();
                JSONArray jArray = (JSONArray) jsonObject.get((String) key);
                for (int i = 0; i < jArray.length(); i++) {
                    if (jArray.get(i) instanceof JSONObject || jArray.get(i) instanceof JSONArray) {
                        mapArr.add(convertToMap((JSONObject) jArray.get(i)));
                    } else {
                        mapArr.add(jArray.get(i));
                    }
                }
                map.put((String) key, mapArr);
            } else {
                if (jsonObject.get((String) key) instanceof String) {
                    map.put((String) key, StringEscapeUtils.escapeHtml(jsonObject.get((String) key).toString()));
                } else {
                    map.put((String) key, jsonObject.get((String) key));
                }

            }
        }
        return map;
    }
}
