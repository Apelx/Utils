package cn.apelx.tool.http;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * http接口调用工具类
 *
 * @author lx
 */
public class HttpUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    /**
     * https连接超时
     */
    private static final int HTTPS_TIMEOUT = 5000;

    private HttpUtils() {
    }


    /**
     * get方式
     */
    public static String getHttp(String url) {
        String responseMsg = "";
        HttpClient httpClient = new HttpClient();
        GetMethod getMethod = new GetMethod(url);
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        try {
            httpClient.getParams().setBooleanParameter("http.protocol.expect-continue", false);//解决Httpclient远程请求所造成Socket没有释放
            getMethod.addRequestHeader("Connection", "close");
            httpClient.executeMethod(getMethod);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = getMethod.getResponseBodyAsStream();
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            responseMsg = out.toString("UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            //释放连接
            getMethod.releaseConnection();
        }
        return responseMsg;
    }

    public static String getHttpWithBearerAuthorization(String url, String authorization) {
        String responseMsg = "";
        HttpClient httpClient = new HttpClient();
        GetMethod getMethod = new GetMethod(url);
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        try {
            // 解决Httpclient远程请求所造成Socket没有释放
            httpClient.getParams().setBooleanParameter("http.protocol.expect-continue", false);
            getMethod.addRequestHeader("Authorization", "Bearer " + authorization);
            getMethod.addRequestHeader("Connection", "close");
            httpClient.executeMethod(getMethod);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = getMethod.getResponseBodyAsStream();
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            responseMsg = out.toString("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //释放连接
            getMethod.releaseConnection();
        }
        return responseMsg;
    }

    /**
     * post方式
     */
    public static String postHttp(String url, String parameters) {
        String responseMsg = "";
        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setContentCharset("UTF-8");
        PostMethod postMethod = new PostMethod(url);
        if (!StringUtils.isEmpty(parameters)) {
            String[] parameterArr = parameters.split("&");
            for (int i = 0; i < parameterArr.length; i++) {
                String[] para = parameterArr[i].split("=");
                // 参数空值
                if (para.length < 2) {
                    postMethod.addParameter(para[0], "");
                } else {
                    postMethod.addParameter(para[0], para[1]);
                }
            }
        }
        try {
            // 解决Httpclient远程请求所造成Socket没有释放
            httpClient.getParams().setBooleanParameter("http.protocol.expect-continue", false);
            postMethod.addRequestHeader("Connection", "close");
            httpClient.executeMethod(postMethod);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = postMethod.getResponseBodyAsStream();
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            responseMsg = out.toString("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            postMethod.releaseConnection();
        }
        return responseMsg;
    }


    /**
     * HTTP POST 带 Bearer Authorization头
     *
     * @param postUrl
     * @param jsonParam
     * @param authorization
     * @return
     */
    public static String postHttpWithBearerAuthorization(String postUrl, String jsonParam, String authorization) {
        StringBuilder res = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(postUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            urlConnection.setRequestProperty("Authorization", "Bearer " + authorization);
            if (!StringUtils.isEmpty(jsonParam)) {
                urlConnection.setRequestProperty("Content-Length", String.valueOf(jsonParam.length()));
                OutputStream outputStream = urlConnection.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                byte[] bytes = jsonParam.getBytes("UTF-8");
                dataOutputStream.write(bytes);
                dataOutputStream.flush();
                dataOutputStream.close();
                outputStream.close();
            }
            bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            int count;
            char[] temp = new char[1024];
            while ((count = bufferedReader.read(temp)) != -1) {
                res.append(new String(temp, 0, count));
            }
        } catch (Exception ex) {
            logger.debug(ex.getMessage());
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return res.toString();
    }

    /**
     * HTTP PUT 带 Bearer Authorization头
     *
     * @param putUrl
     * @param jsonParam
     * @param authorization
     */
    public static String putHttpWithBearerAuthorization(String putUrl, String jsonParam, String authorization) {
        String res = "";
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(putUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("PUT");
            urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            urlConnection.setRequestProperty("Authorization", "Bearer " + authorization);
            if (!StringUtils.isEmpty(jsonParam)) {
                urlConnection.setRequestProperty("Content-Length", String.valueOf(jsonParam.length()));
                OutputStream outputStream = urlConnection.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                byte[] bytes = jsonParam.getBytes("UTF-8");
                dataOutputStream.write(bytes);
                dataOutputStream.flush();
                dataOutputStream.close();
                outputStream.close();
            }
            bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            int count;
            char[] temp = new char[1024];
            while ((count = bufferedReader.read(temp)) != -1) {
                res += new String(temp, 0, count);
            }
        } catch (Exception ex) {
            logger.debug(ex.getMessage());
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }

    /**
     * HTTP DELETE 带 Bearer Authorization头
     *
     * @param deleteUrl
     * @param jsonParam
     * @param authorization
     */
    public static String deleteHttpWithBearerAuthorization(String deleteUrl, String jsonParam, String authorization) {
        String res = "";
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(deleteUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            urlConnection.setRequestProperty("Authorization", "Bearer " + authorization);
            if (!StringUtils.isEmpty(jsonParam)) {
                urlConnection.setRequestProperty("Content-Length", String.valueOf(jsonParam.length()));
                OutputStream outputStream = urlConnection.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                byte[] bytes = jsonParam.getBytes("UTF-8");
                dataOutputStream.write(bytes);
                dataOutputStream.flush();
                dataOutputStream.close();
                outputStream.close();
            }
            bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            int count;
            char[] temp = new char[1024];
            while ((count = bufferedReader.read(temp)) != -1) {
                res += new String(temp, 0, count);
            }
        } catch (Exception ex) {
            logger.debug(ex.getMessage());
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }

    public static String postConnection(String postUrl, String param) throws Exception {
        String res = "";
        BufferedReader input = null;
        try {
            URL url;
            HttpURLConnection urlConn;
            DataOutputStream printout;
            url = new URL(postUrl);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            // 设置调用方式
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Content-Type", "text/plain;charset=UTF-8");
            // 发送request
            urlConn.setRequestProperty("Content-Length", param.length() + "");
            printout = new DataOutputStream(urlConn.getOutputStream());
            byte[] pp = param.getBytes("UTF-8");
            printout.write(pp);
            printout.flush();
            printout.close();
            input = new BufferedReader(new InputStreamReader(urlConn.
                    getInputStream()));
            StringBuilder str = new StringBuilder();
            int count;
            char[] chs = new char[1024];
            while ((count = input.read(chs)) != -1) {
                str.append(new String(chs, 0, count));
            }
            res = str.toString();

        } catch (Exception ex) {
            throw ex;
        } finally {
            if (input != null) {
                input.close();
            }
        }
        return res;
    }

    /**
     * json参数转键值对
     *
     * @param url
     * @param param
     * @return
     */
    public static String post(String url, String param) {
        try {
            PostMethod postMethod = null;
            postMethod = new PostMethod(url);
            postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            NameValuePair[] data = null;
            // 参数设置，需要注意的就是里边不能传NULL，要传空字符串
            if (!StringUtils.isEmpty(param)) {
                JSONObject json = JSONObject.parseObject(param);
                Set<String> keySet = json.keySet();
                data = new NameValuePair[json.size()];
                int j = 0;
                for (String key : keySet) {
                    data[j] = new NameValuePair(key, json.getString(key));
                    j++;
                }
            }
            postMethod.setRequestBody(data);
            HttpClient httpClient = new HttpClient();
            //解决Httpclient远程请求所造成Socket没有释放
            httpClient.getParams().setBooleanParameter("http.protocol.expect-continue", false);
            postMethod.addRequestHeader("Connection", "close");
            // 执行POST方法
            httpClient.executeMethod(postMethod);
            return postMethod.getResponseBodyAsString();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String httpsPostJson(String url, String jsonParam) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = createSslClientDefault();
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(HTTPS_TIMEOUT).setConnectTimeout(HTTPS_TIMEOUT).build();
        httpPost.setConfig(requestConfig);
        // httpPost.addHeader("Content-Type", "application/json");
        try {
            // 设置参数到请求对象中
            httpPost.setEntity(new StringEntity(jsonParam, ContentType.create("application/json", "UTF-8")));
            response = httpClient.execute(httpPost, new BasicHttpContext());
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, "UTF-8");
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (response != null) {
                try {
                    // 最后关闭response
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * https请求忽略ssl证书-get
     *
     * @param url url
     * @return 返回信息
     */
    public static String httpsGet(String url) {
        // 单位毫秒
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(HTTPS_TIMEOUT).setConnectTimeout(HTTPS_TIMEOUT)
                .setSocketTimeout(HTTPS_TIMEOUT).build();

        CloseableHttpClient httpclient = createSslClientDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);

        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            } else {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    // 关闭response
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpclient != null) {
                try {
                    // 关闭httpclient
                    httpclient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /*   *//**
     * 下载文件 忽略ssl
     *
     * @param res
     * @param url
     *//*
    public static void httpsFile(HttpServletResponse res, String url) {
        // 生成一个httpclient对象
        CloseableHttpClient httpclient = createSslClientDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        InputStream in = null;
        try {
            response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            in = entity.getContent();
            OutputStream fout = res.getOutputStream();
            int l = -1;
            byte[] tmp = new byte[1024];
            while ((l = in.read(tmp)) != -1) {
                fout.write(tmp, 0, l);
                // 注意这里如果用OutputStream.write(buff)的话，图片会失真，大家可以试试
            }
            fout.flush();
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭低层流。
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (response != null) {
                try {
                    response.close();//关闭response
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpclient != null) {
                try {
                    httpclient.close();//关闭httpclient
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/

    /**
     * https请求忽略ssl证书
     *
     * @param url      url
     * @param paramMap 请求参数
     * @return 返回信息
     */
    public static String httpsPost(String url, Map<String, String> paramMap) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = createSslClientDefault();
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(HTTPS_TIMEOUT).setConnectTimeout(HTTPS_TIMEOUT).build();
        httpPost.setConfig(requestConfig);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");

        // 装填参数
        List<org.apache.http.NameValuePair> params = new ArrayList<>();
        if (paramMap != null) {
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        try {
            // 设置参数到请求对象中
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            response = httpClient.execute(httpPost, new BasicHttpContext());
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, "UTF-8");
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (response != null) {
                try {
                    // 最后关闭response
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 生成信任所有ssl的CloseableHttpClient对象
     *
     * @return CloseableHttpClient对象
     */
    private static CloseableHttpClient createSslClientDefault() {
        try {
            // 使用 loadTrustMaterial() 方法实现一个信任策略，信任所有证书
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            // NoopHostnameVerifier类:  作为主机名验证工具，实质上关闭了主机名验证，它接受任何
            // 有效的SSL会话并匹配到目标主机。
            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();
    }

    public static String httpPostRequest(String url, String param) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        StringEntity stringEntity = new StringEntity(param.toString(), "utf-8");
        stringEntity.setContentEncoding("UTF-8");
        stringEntity.setContentType("application/json");
        httpPost.setEntity(stringEntity);
        String resData = "";
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                resData = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpPost.releaseConnection();
        }
        return resData;
    }


    /**
     * 获取系统环境变量指定的key值
     *
     * @param
     * @return
     */
    public static Map<String, String> getSystemEnv(String httpsProxy, boolean isHttp) {
        if (isHttp) {
            logger.info("HTTP_PROXY VALUE >>> " + httpsProxy);
        } else {
            logger.info("HTTPS_PROXY VALUE >>> " + httpsProxy);
        }
        Map<String, String> stringMap = new HashMap<>();
        if (!"httpNoData".equals(httpsProxy) && !"httpsNoData".equals(httpsProxy)) {
            String userPwdStr = httpsProxy.substring(httpsProxy.indexOf("//") + 2, httpsProxy.lastIndexOf("@"));
            String user = userPwdStr.substring(0, userPwdStr.lastIndexOf(":"));
            String pwd = userPwdStr.substring(userPwdStr.indexOf(":") + 1);
            String proxyIp = httpsProxy.substring(httpsProxy.indexOf("@") + 1, httpsProxy.lastIndexOf(":"));
            String proxyPort = httpsProxy.substring(httpsProxy.lastIndexOf(":") + 1);
            stringMap.put("PROXY_IP", proxyIp);
            stringMap.put("PROXY_PORT", proxyPort);
            stringMap.put("PROXY_USERNAME", user);
            stringMap.put("PROXY_PASSWORD", pwd);
        }
        return stringMap;
    }

}
