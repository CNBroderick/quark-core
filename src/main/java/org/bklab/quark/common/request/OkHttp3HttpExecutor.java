package org.bklab.quark.common.request;

import dataq.core.httpclient.HttpPostClient;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.util.Optional;

public class OkHttp3HttpExecutor extends SimpleHttpExecutor {
	@Override
	public String postForObject(String postData) throws Exception {
		HttpPostClient client = new HttpPostClient(getServerUrl());
		client.setPostData(postData);
		client.setMime(getMime());
		ResponseBody responseBody = Optional.ofNullable(client.doRequest()).map(Response::body).orElse(null);
		return responseBody == null ? null : responseBody.string();
	}


}
