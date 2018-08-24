package com.example.josh_assignment.database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import okhttp3.ResponseBody;

public class DBManager {
    private static final DBManager ourInstance = new DBManager();
    private Realm realm;

    public static DBManager getInstance() {
        return ourInstance;
    }

    private DBManager() {
        realm = Realm.getDefaultInstance();
    }

    public List<PostEntity> setFeedDataInDB(ResponseBody responseBody){
        List<PostEntity> postEntityList = new ArrayList<>();
        try {
            String responseString = responseBody.string();
            try {
                JSONObject jsonObject = new JSONObject(responseString);
                JSONArray jsonArray = jsonObject.getJSONArray("posts");
                realm.beginTransaction();
                for(int index = 0; index < jsonArray.length(); index++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(index);
                    PostEntity postEntity = realm.createOrUpdateObjectFromJson(PostEntity.class, jsonObject1);
                    postEntityList.add(postEntity);
                }
              realm.commitTransaction();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return postEntityList;
    }


}
