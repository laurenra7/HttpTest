package org.la.http;

import edu.byu.wso2.core.provider.TokenHeaderProvider;

public interface HttpGet {

    public String httpGet(String url, boolean modeVerbose, TokenHeaderProvider tokenHeaderProvider);

}
