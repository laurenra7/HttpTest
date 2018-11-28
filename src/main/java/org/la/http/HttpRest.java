package org.la.http;

import java.util.Map;

public interface HttpRest {

    public String httpGet(String url);

    public boolean isVerbose();

    public void setVerbose(boolean verbose);

    public Map<String, String[]> getHeaders();

    public void setHeaders(Map<String, String[]> headers);

    public String getConsumerKey();

    public void setConsumerKey(String consumerKey);

    public String getConsumerSecret();

    public void setConsumerSecret(String consumerSecret);

}
