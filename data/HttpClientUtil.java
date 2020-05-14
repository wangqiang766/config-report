package com.wq.multicommon.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author HX0013154
 *
 */
public class HttpClientUtil {

	public static String doGet(String url) throws Exception {
		return doGet(url, null);
	}

	public static String doGet(String url, Map<String, String> querys) throws Exception {
		return doGet(url, null, querys);
	}

	public static String doGet(String url, Map<String, String> headers, Map<String, String> querys) throws Exception {
		return doGet(url, null, headers, querys);
	}

	public static String doGet(String host, String path, Map<String, String> headers, Map<String, String> querys)
			throws Exception {
		CloseableHttpClient httpClient = wrapClient(host);
		HttpGet request = new HttpGet(buildUrl(host, path, querys));
		if (headers != null) {
			for (Map.Entry<String, String> e : headers.entrySet()) {
				request.addHeader(e.getKey(), e.getValue());
			}
		}

		return execute(httpClient, request);
	}

	/**
	 * post form表单格式
	 *
	 * @param url
	 * @param headers
	 * @param bodys
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String url, Map<String, String> headers, Map<String, String> bodys) throws Exception {
		return doPost(url, null, headers, null, bodys);
	}

	/**
	 * post form表单格式
	 *
	 * @param headers
	 * @param querys
	 * @param bodys
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String url, Map<String, String> headers, Map<String, String> querys,
			Map<String, String> bodys) throws Exception {
		return doPost(url, null, headers, querys, bodys);
	}

	/**
	 * post form表单格式
	 *
	 * @param host    请求主机
	 * @param path    请求路径,和host拼接组成请求地址
	 * @param headers 请求头参数
	 * @param querys  url地址上的拼接参数
	 * @param bodys   请求体参数
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String host, String path, Map<String, String> headers, Map<String, String> querys,
			Map<String, String> bodys) throws Exception {
		CloseableHttpClient httpClient = wrapClient(host);
		HttpPost request = new HttpPost(buildUrl(host, path, querys));
		if (headers != null) {
			for (Map.Entry<String, String> e : headers.entrySet()) {
				request.addHeader(e.getKey(), e.getValue());
			}
		}
		if (bodys != null) {
			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			for (String key : bodys.keySet()) {
				nameValuePairList.add(new BasicNameValuePair(key, bodys.get(key)));
			}
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairList, "utf-8");
			formEntity.setContentType("application/x-www-form-urlencoded;charset=utf-8");
			request.setEntity(formEntity);
		}

		return execute(httpClient, request);
	}

	/**
	 * Post String字符串，或JSON串
	 *
	 * @param host
	 * @param headers
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String host, Map<String, String> headers, String body) throws Exception {
		return doPost(host, null, headers, null, body);
	}

	/**
	 * Post String字符串，或JSON串
	 *
	 * @param host
	 * @param headers
	 * @param querys
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String host, Map<String, String> headers, Map<String, String> querys, String body)
			throws Exception {
		return doPost(host, null, headers, querys, body);
	}

	/**
	 * Post String字符串，或JSON串
	 *
	 * @param host
	 * @param path
	 * @param headers
	 * @param querys
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String host, String path, Map<String, String> headers, Map<String, String> querys,
			String body) throws Exception {
		CloseableHttpClient httpClient = wrapClient(host);
		HttpPost request = new HttpPost(buildUrl(host, path, querys));
		if (headers != null) {
			for (Map.Entry<String, String> e : headers.entrySet()) {
				request.addHeader(e.getKey(), e.getValue());
			}
		}
		if (StringUtils.isNotBlank(body)) {
			StringEntity entity = new StringEntity(body, "utf-8");
			entity.setContentType("application/json;charset=utf-8");
			request.setEntity(entity);
		}
		return execute(httpClient, request);
	}

	/**
	 * Post stream流
	 *
	 * @param host
	 * @param path
	 * @param headers
	 * @param querys
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String host, String path, Map<String, String> headers, Map<String, String> querys,
			byte[] body) throws Exception {
		CloseableHttpClient httpClient = wrapClient(host);
		HttpPost request = new HttpPost(buildUrl(host, path, querys));
		if (headers != null) {
			for (Map.Entry<String, String> e : headers.entrySet()) {
				request.addHeader(e.getKey(), e.getValue());
			}
		}
		if (body != null) {
			request.setEntity(new ByteArrayEntity(body));
		}
		return execute(httpClient, request);
	}

	private static String execute(CloseableHttpClient httpClient, HttpRequestBase request) throws Exception {
		String result = null;
		HttpResponse response = null;
		Integer soketOut = 20000;
		Integer connOut = 20000;
		try {
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(soketOut).setConnectTimeout(connOut)
					.build();// 设置请求和传输超时时间
			request.setConfig(requestConfig);
			response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(entity, "UTF-8");
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (response != null) {
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * @param host   请求host
	 * @param path   请求路径
	 * @param querys URL参数列表
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static String buildUrl(String host, String path, Map<String, String> querys)
			throws UnsupportedEncodingException {
		StringBuilder sbUrl = new StringBuilder();
		sbUrl.append(host);
		if (!StringUtils.isBlank(path)) {
			sbUrl.append(path);
		}
		if (null != querys) {
			StringBuilder sbQuery = new StringBuilder();
			for (Map.Entry<String, String> query : querys.entrySet()) {
				if (0 < sbQuery.length()) {
					sbQuery.append("&");
				}
				if (StringUtils.isBlank(query.getKey()) && !StringUtils.isBlank(query.getValue())) {
					sbQuery.append(query.getValue());
				}
				if (!StringUtils.isBlank(query.getKey())) {
					sbQuery.append(query.getKey());
					if (!StringUtils.isBlank(query.getValue())) {
						sbQuery.append("=");
						sbQuery.append(URLEncoder.encode(query.getValue(), "utf-8"));
					}
				}
			}
			if (0 < sbQuery.length()) {
				sbUrl.append("?").append(sbQuery);
			}
		}

		return sbUrl.toString();
	}

	private static CloseableHttpClient wrapClient(String host) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		if (host.startsWith("https://")) {
			httpClient = createSSLClientDefault();
		}

		return httpClient;
	}

	public static CloseableHttpClient createSSLClientDefault() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				// 信任所有
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		return HttpClients.createDefault();
	}

	public static void main(String[] args) throws Exception {
	}
}
