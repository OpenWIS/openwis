package jeeves.utils;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class SanitizeJson {

    public SanitizeJson() {}

    public String sanitize(String unsecuredJson) {
        Log.debug(Log.ENGINE, "Sanitize json array: " + unsecuredJson);
        try {
            if (unsecuredJson.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(unsecuredJson);
                List<JSONObject> l = new ArrayList<>();
                for(int i=0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    l.add(sanitize0(jsonObject));
                }

                JSONArray sanitizedArray = new JSONArray(l);
                Log.debug(Log.ENGINE, "Sanitized json array: " + sanitizedArray.toString());
                return sanitizedArray.toString();
            } else {
                return sanitize0(new JSONObject(unsecuredJson)).toString();
            }
        } catch (JSONException e) {
            return unsecuredJson;
        }
    }

    private JSONObject sanitize0(JSONObject unsafeJSON) {
        Map<String, Object> map = convertToMap(unsafeJSON);
        return new JSONObject(map);
    }

    private Map<String, Object> convertToMap(JSONObject jsonObject) {
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
