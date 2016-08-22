package textmatching.matchers.markov.rockit;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;


public class PyExecutor
{
    public static String scheme = "http";
    public static String host   = "executor.informatik.uni-mannheim.de";
    public static int    port   = 80;
    public static String path   = "api/";
    public static String server = scheme + "://" + host + ":" + port + "/" + path;


    public synchronized static String getAction(String id, String action) throws Exception
    {
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("id", id));
        qparams.add(new BasicNameValuePair("action", action));
        URI uri = URIUtils.createURI(scheme, host, port, path + "process", URLEncodedUtils.format(qparams, "UTF-8"), null);

        HttpGet httpget = new HttpGet(uri);
        httpget.getParams().setParameter("action", action);

        HttpResponse response = httpclient.execute(httpget);
        HttpEntity resEntity = response.getEntity();

        String result = null;
        if(resEntity != null) {
            result = EntityUtils.toString(resEntity);
            EntityUtils.consume(resEntity);
        }
        httpclient.getConnectionManager().shutdown();
        return result;
    }


    public synchronized static List<String> getResult(String id, String action) throws Exception
    {
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("id", id));
        qparams.add(new BasicNameValuePair("action", action));
        URI uri = URIUtils.createURI(scheme, host, port, path + "process", URLEncodedUtils.format(qparams, "UTF-8"), null);

        HttpGet httpget = new HttpGet(uri);

        HttpResponse response = httpclient.execute(httpget);
        HttpEntity resEntity = response.getEntity();

        ArrayList<String> results = new ArrayList<String>();
        
        if(resEntity != null) {
            // start save file
            InputStream in = resEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line=reader.readLine()) != null) {
            	results.add(line);
            }
            in.close();
            EntityUtils.consume(resEntity);
        }
        httpclient.getConnectionManager().shutdown();
        return results;
    }

}
