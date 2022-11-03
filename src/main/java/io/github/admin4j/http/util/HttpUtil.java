package io.github.admin4j.http.util;

import io.github.admin4j.http.ApiJsonClient;
import io.github.admin4j.http.core.HttpConfig;
import io.github.admin4j.http.core.MediaTypeEnum;
import io.github.admin4j.http.core.Method;
import io.github.admin4j.http.core.Pair;
import lombok.Cleanup;
import okhttp3.Call;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Map;
import java.util.Objects;

/**
 * HTTP  返回body为json格式的请求工具类
 *
 * @author andanyang
 * @since 2022/5/10 14:31
 */
public class HttpUtil {
    /**
     * 单列
     */
    private static volatile ApiJsonClient SINGLETON_REQUEST = null;

    private HttpUtil() {

    }

    private static ApiJsonClient getHttpRequest() {
        if (null == SINGLETON_REQUEST) {

            synchronized (HttpUtil.class) {
                if (null == SINGLETON_REQUEST) {
                    SINGLETON_REQUEST = new ApiJsonClient(new HttpConfig());
                }
            }
        }
        return SINGLETON_REQUEST;
    }

    public static Response get(String url, Pair<?>... queryParams) {

        return getHttpRequest().get(url, (Map<String, Object>) null, queryParams);
    }


    public static Response get(String url, Map<String, Object> queryParams) {

        return getHttpRequest().get(url, queryParams, (Pair<?>[]) null);
    }


    public static Response post(String url, Object body) {
        return getHttpRequest().post(url, MediaTypeEnum.JSON, body, (Map<String, Object>) null, (Map<String, Object>) null);
    }

    public static Response postForm(String url, Map<String, Object> formParams) {
        return getHttpRequest().post(url, MediaTypeEnum.FORM, null, formParams, (Map<String, Object>) null);
    }

    public static Response upload(String url, Map<String, Object> formParams) {
        return getHttpRequest().post(url, MediaTypeEnum.FORM_DATA, null, formParams, (Map<String, Object>) null);
    }

    public static InputStream down(String url) {
        ApiJsonClient httpRequest = getHttpRequest();
        Call call = httpRequest.buildCall(url, Method.GET, null, null, null);
        Response response = httpRequest.execute(call);
        return Objects.requireNonNull(response.body()).byteStream();
    }


    public static void down(String url, String savePath) throws IOException {
        InputStream in = down(url);

        File file = new File(savePath);

        boolean isFile = savePath.contains(".");
        if (isFile) {
            file.getParentFile().mkdirs();
        } else {
            file.mkdirs();
        }

        if (!isFile) {
            String fName = StringUtils.substringBefore(url, "?");
            fName = StringUtils.substringAfterLast(fName, "/");
            savePath = savePath + (StringUtils.endsWith(savePath, "/") ? "" : "/") + fName;
        }
        @Cleanup BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        @Cleanup FileOutputStream fileOutputStream = new FileOutputStream(savePath);
        byte[] b = new byte[1024];
        while ((bufferedInputStream.read(b)) != -1) {
            // 写入数据
            fileOutputStream.write(b);
        }
    }
}
