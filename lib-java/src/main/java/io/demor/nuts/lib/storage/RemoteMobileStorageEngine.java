package io.demor.nuts.lib.storage;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import io.demor.nuts.lib.controller.AppInstance;
import io.demor.nuts.lib.controller.MethodInfoUtil;
import io.demor.nuts.lib.module.StorageResponse;

import java.io.IOException;

public class RemoteMobileStorageEngine implements IStorageEngine {

    private final AppInstance mInstance;
    private final OkHttpClient mClient;

    public RemoteMobileStorageEngine(final AppInstance instance) {
        mInstance = instance;
        mClient = new OkHttpClient();
    }

    @Override
    public void set(final String key, final String value) {
        try {
            mClient.newCall(new Request.Builder()
                    .get()
                    .url(mInstance.getApiUrl() + "storage/" + key + "?action=save")
                    .post(RequestBody.create(MediaType.parse("application/json"), value))
                    .build())
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String get(final String key) {
        try {
            StorageResponse response = MethodInfoUtil.GSON.fromJson(mClient.newCall(new Request.Builder()
                    .get()
                    .url(mInstance.getApiUrl() + "storage/" + key + "?action=get")
                    .build())
                    .execute().body().string(), StorageResponse.class);
            return response.mData;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delete(final String key) {
        try {
            mClient.newCall(new Request.Builder()
                    .get()
                    .url(mInstance.getApiUrl() + "storage/" + key + "?action=delete")
                    .build())
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean contains(final String key) {
        try {
            StorageResponse response = MethodInfoUtil.GSON.fromJson(mClient.newCall(new Request.Builder()
                    .get()
                    .url(mInstance.getApiUrl() + "storage/" + key + "?action=contains")
                    .build())
                    .execute().body().string(), StorageResponse.class);
            return Boolean.parseBoolean(response.mData);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
