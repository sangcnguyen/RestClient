package com.github.sn;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CustomResponseHandler extends AbstractResponseHandler<String> {

    @Override
    public String handleResponse(HttpResponse response) throws IOException {
        final HttpEntity entity = response.getEntity();
        return entity == null ? null : handleEntity(entity);
    }

    @Override
    public String handleEntity(HttpEntity entity) throws IOException {
        return EntityUtils.toString(entity, StandardCharsets.UTF_8);
    }
}