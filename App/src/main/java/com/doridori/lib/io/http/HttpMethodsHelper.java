package com.doridori.lib.io.http;

import com.doridori.lib.util.XLog;
import com.squareup.mimecraft.FormEncoding;
import com.squareup.mimecraft.Multipart;
import com.squareup.mimecraft.Part;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;

/**
 * Deprecated - use OkHttp 2.0 instead
 *
 * Convenience methods for Http method calls. Removes a bit of boilerplate code and IMHO makes HttpUrlConnection easier to work with. Also handles posting multipart data :)
 *
 * You can use this with OkHttp by just calling OkHttpClient.open(url) and passing that in here.
 *
 * //TODO put examples for each call type especially multipart
 * //TODO may want to add builder class to config connection - should pass this in for all optional connection settings instead of overloading all the methods
 * //TODO OR may be better to refactor this to be linq style
 * //TODO OR may be better to pass in an httpUrlConnection instead - as then user can put settings on if needed without me having to redo the interface - NOW DOING
 * //TODO should be able to config the base default connection settings object (like timeout etc which are set in the get call below - on application start by supplying a default builder but can also customse per call by grabbing this default builder)
 *
 * @author dorian cussen
 */
@Deprecated
public class HttpMethodsHelper
{
    //============================================================================================================
    // Enums
    //============================================================================================================

    /**
     * TODO add "application/atom+xml", "application/octet-stream", "application/svg+xml", "application/xhtml+xml", "application/xml", "text/html", "text/plain", "text/xml"
     */
    public enum ContentTypes
    {
        APPLICATION_JSON("application/json"),
        FORM_URLENCODED("application/x-www-form-urlencoded"),
        MULTIPART("multipart/form-data");

        private String mType;

        ContentTypes(String type)
        {
            mType = type;
        }

        public String getAsString()
        {
            return mType;
        }
    }

    //============================================================================================================
    // POST
    //============================================================================================================

    /**
     * See {@link #postRaw(java.net.HttpURLConnection, ContentTypes, byte[]}
     */
    public static HttpResponse postRaw(
            @NotNull HttpURLConnection connection,
            @NotNull ContentTypes contentType,
            @NotNull String body) throws IOException
    {
        return postRaw(connection, contentType, body.getBytes("UTF-8"));
    }

    /**
     * Used to post any raw body contents e.g. JSON
     *
     * @param connection
     * @param contentType
     * @param body
     * @return
     * @throws java.io.IOException
     */
    public static HttpResponse postRaw(
            @NotNull HttpURLConnection connection,
            @NotNull ContentTypes contentType,
            @NotNull byte[] body) throws IOException
    {
        return doHttpWithBody(
                connection,
                "POST",
                contentType.getAsString(),
                new DefaultConnectionConfig(),
                body);
    }

    /**
     * For 'application/x-www-form-urlencoded' type
     *
     * @param connection
     * @return
     * @throws java.io.IOException
     */
    public static HttpResponse postUrlEncodedForm(
            @NotNull HttpURLConnection connection,
            @NotNull FormEncoding formEncoding) throws IOException
    {
        return postForm(connection, ContentTypes.FORM_URLENCODED, formEncoding);
    }


    /**
     * Use this for 'multipart/form-data' where the form parts are all of type 'text/plain'. Would generally make
     * sense to use 'application/x-www-form-urlencoded' for this kind of data anyhow.
     *
     * @param connection
     * @param formParts
     * @return
     * @throws java.io.IOException
     */
    public static HttpResponse postMultipartForm(
            @NotNull HttpURLConnection connection,
            @NotNull Map<String, String> formParts) throws IOException
    {
        //create multipart data
        Multipart.Builder builder = new Multipart.Builder();
        builder.type(Multipart.Type.FORM);

        Iterator<Map.Entry<String, String>> it = formParts.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<String, String> pairs = it.next();
            builder.addPart(
                    new Part.Builder()
                            .contentDisposition("form-data; name=\"" + pairs.getKey() + "\"")
                            .body(pairs.getValue())
                            .build());
            it.remove(); // avoids a ConcurrentModificationException
        }

        return postForm(connection, ContentTypes.MULTIPART, builder.build());
    }

    /**
     * Use this for 'multipart/form-data' where the form parts are of mixed type'
     *
     * @param connection
     * @param part should be either {@link com.squareup.mimecraft.Multipart} || {@link com.squareup.mimecraft.FormEncoding}
     * @return
     * @throws java.io.IOException
     */
    private static HttpResponse postForm(
            @NotNull HttpURLConnection connection,
            @NotNull ContentTypes formContentTypes,
            @NotNull final Part part) throws IOException
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        part.writeBodyTo(byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        return doHttpWithBody(connection, "POST", formContentTypes.getAsString(), new DefaultConnectionConfig(), bytes);
    }

    //============================================================================================================
    // PUT
    //============================================================================================================

    public static HttpResponse putRawData(
            @NotNull HttpURLConnection connection,
            @NotNull ContentTypes contentType,
            @NotNull final String rawBody) throws IOException
    {
        return doHttpWithBody(
                connection,
                "PUT",
                contentType.getAsString(),
                new DefaultConnectionConfig(),
                rawBody.getBytes("UTF-8"));

    }

    //============================================================================================================
    // GET
    //============================================================================================================

    /**
     *
     * @param connection
     * @return
     * @throws java.io.IOException
     */
    @NotNull
    public static HttpResponse get(
            @NotNull HttpURLConnection connection) throws IOException
    {
        return get(connection, new DefaultConnectionConfig());
    }

    /**
     * @param connection
     * @param connectionConfig
     * @return
     * @throws java.io.IOException
     */
    @NotNull
    public static HttpResponse get(
            @NotNull HttpURLConnection connection,
            @NotNull DefaultConnectionConfig connectionConfig) throws IOException
    {
        //auto uses GET
        connection.setDoOutput(false); //true will switch the request to a POST - dont need this for a GET
        connection.setDoInput(true); //setting for GET
        //should prob set the below two as part of the default config obj
        connection.setConnectTimeout(20000);
        connection.setReadTimeout(60000);
        connection.setRequestMethod("GET");

        connectionConfig.config(connection);

        InputStream in = null;

        try
        {
            return processResponse(connection);
        }
        finally
        {
            if (in != null)
            {
                in.close();
            }
        }
    }

    //============================================================================================================
    // GENERIC HTTP METHOD (with body)
    //============================================================================================================

    /**
     * Generic http call with body.
     *
     * @param connection
     * @param method
     * @param contentType
     * @param outBody
     * @return
     * @throws java.io.IOException
     */
    @NotNull
    private static HttpResponse doHttpWithBody(
            @NotNull HttpURLConnection connection,
            @NotNull String method,
            @NotNull String contentType,
            @NotNull DefaultConnectionConfig connectionConfig,
            @NotNull byte[] outBody) throws IOException
    {
        XLog.d(method+"/"+connection.getURL().toString());

        connectionConfig.config(connection);

        OutputStream out = null;

        try
        {
            // Write the request.
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", contentType);

            out = connection.getOutputStream();
            out.write(outBody);

            out.flush();
            out.close();

            return processResponse(connection);
        }
        finally
        {
            IOUtils.closeQuietly(out);
        }
    }

    //============================================================================================================
    // COMMON HELPER METHODS
    //============================================================================================================

    /**
     * Will throw an {@link HttpMethodsHelper.HttpStatusCodeException} if above 400
     *
     * This will convert the response to a string and then pass back. You may want to stream data to a streaming parser - an
     * InputStreamHandler will need to be passed
     *
     * @param connection
     * @return
     * @throws java.io.IOException
     */
    private static HttpResponse processResponse(
            HttpURLConnection connection) throws IOException
    {
        InputStream in = null;

        try
        {
            int httpResponseCode = connection.getResponseCode();

            XLog.d(httpResponseCode + "code");

            if (httpResponseCode >= HttpStatus.SC_BAD_REQUEST)
                in = connection.getErrorStream();
            else
                in = connection.getInputStream();

            byte[] inBytes = IOUtils.toByteArray(in);

            if (httpResponseCode >= HttpStatus.SC_BAD_REQUEST)
                throw new HttpStatusCodeException(httpResponseCode, new String(inBytes));
            else
                return new HttpResponse(connection.getResponseCode(), inBytes, connection);
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }

    }

    //============================================================================================================
    // RETURN OBJ
    //============================================================================================================

    public static class HttpResponse
    {
        public final int responseCode;
        /**
         * WIll be null if no body
         */
        public final byte[] rawContents;

        /**
         * The connection object with a closed input stream. Can be used to read headers etc
         */
        public final URLConnection urlConnection;

        public HttpResponse(int responseCode, @Nullable byte[] rawContents, @NotNull URLConnection urlConnection)
        {
            this.responseCode = responseCode;
            this.rawContents = rawContents;
            this.urlConnection = urlConnection;
        }

        public String contentsAsString() throws UnsupportedEncodingException
        {
            return new String(rawContents, "UTF-8");
        }
    }

    //============================================================================================================
    // EXCEPTIONS
    //============================================================================================================

    /**
     * By default this should be thrown for any http code above 400.
     */
    public static class HttpStatusCodeException extends IOException
    {
        private final int statusCode;
        private final String body;

        public HttpStatusCodeException(int statusCode, @Nullable String body)
        {
            this.statusCode = statusCode;
            this.body = body;
        }

        public int getStatusCode()
        {
            return statusCode;
        }

        public String getBody()
        {
            return body;
        }

        @Override
        public String getMessage()
        {
            return "\n\n STATUS CODE " + getStatusCode() + "\n\n" + getBody();
        }
    }

    //============================================================================================================
    // CONNECTION CONFIG
    //============================================================================================================

    public static class DefaultConnectionConfig
    {
        //DEFAULTS
        private boolean mFollowRedirects = false;
        private boolean mEnableCache = false; ////disable cache as causing FileNotFoundException for most calls - https://github.com/square/okhttp/blob/master/okhttp/src/main/java/com/squareup/okhttp/HttpResponseCache.java

        public void config(HttpURLConnection httpURLConnection)
        {
            httpURLConnection.setInstanceFollowRedirects(mFollowRedirects);

            if(!mEnableCache)
                httpURLConnection.addRequestProperty("Cache-Control","no-cache");
        }

        public void setFollowRedirects(boolean followRedirects)
        {
            mFollowRedirects = followRedirects;
        }

        public boolean isEnableCache()
        {
            return mEnableCache;
        }
    }
}
