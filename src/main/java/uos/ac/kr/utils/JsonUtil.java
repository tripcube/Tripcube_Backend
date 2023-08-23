package uos.ac.kr.utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonUtil {
    // JSONObject to JSONArray
    public static JSONArray covertJsonObjectToJsonArray(Object InsideArray) {

        JSONArray jsonArray;

        if (InsideArray instanceof JSONArray) {
            jsonArray = (JSONArray) InsideArray;
        } else {
            jsonArray = new JSONArray();
            jsonArray.put((JSONObject) InsideArray);
        }
        return jsonArray;
    }
}
