package com.doridori.lib.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * User: doriancussen
 * Date: 11/09/2013
 */
public class UriUtils
{
    /**
     * There seems to be no good way to encode url params in Java / Android! See http://stackoverflow.com/a/8962869/236743
     *
     * @return
     * @throws java.net.MalformedURLException
     * @throws java.net.URISyntaxException
     */
    public static URL encodeUrl(String urlStr) throws MalformedURLException, URISyntaxException
    {
        //this encodes all the query params
        URL url = new URL(urlStr);
        URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
        url = uri.toURL();
        return url;
    }
}
