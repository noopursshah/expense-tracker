package persistence;

import org.json.JSONObject;

// Interface used to get data as JSON Object
public interface Writable {
    // EFFECTS: returns this as JSON object
    JSONObject toJson();
}
