package com.minispring.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * url资源加载器
 * 支持访问各种url协议的资源：file、http、ftp、jar、classpath等
 */
public class UrlResource implements Resource{
    private final URL url;

    /**
     * 构造函数
     * 根据url构造资源
     * @param url
     */
    public UrlResource(URL url) {
        if (url == null){
            throw new IllegalArgumentException("URL must not be null");
        }
        this.url = url;
    }

    /**
     * 构造函数
     * 根据url字符串构造资源
     * @param url
     * @throws MalformedURLException
     */
    public UrlResource(String url) throws MalformedURLException {
        if (url == null){
            throw new IllegalArgumentException("URL must not be null");
        }
        this.url = new URL(url);
    }

    public URL getUrl() {
        return url;
    }

    /**
     * 判断资源是否存在
     * @return
     */
    @Override
    public boolean exists() {
        try {
            // 尝试HTTP HEAD请求
            URLConnection conn = url.openConnection();
            if (conn instanceof HttpURLConnection) {
                HttpURLConnection httpConn = (HttpURLConnection) conn;
                httpConn.setRequestMethod("HEAD");
                int code = httpConn.getResponseCode();
                return (code >= 200 && code < 300);
            }
            // 对于非HTTP URL，尝试打开输入流
            try (InputStream is = conn.getInputStream()) {
                return true;
            }
        } catch (IOException ex) {
            return false;
        }
    }

    //url资源默认是可读的
    @Override
    public boolean isReadable() {
        return true;
    }

    /**
     * 获取资源输入流
     * @return
     * @throws IOException
     */
    @Override
    public InputStream getInputStream() throws IOException {
        URLConnection con = this.url.openConnection();
        try {
            return con.getInputStream();
        } catch (IOException ex) {
            if (con instanceof HttpURLConnection) {
                ((HttpURLConnection) con).disconnect();

            }
            throw ex;
        }
    }
    // 获取资源描述
    @Override
    public String getDescription() {
        return "URL资源 [ " + this.url + " ]";
    }

    /**
     * 判断两个url资源是否相等
     *（1）前面两个 if 的作用
     * 第一个 if：优化性能，快速返回相等的结果。
     * 第二个 if：确保类型匹配，快速排除不相等的情况。
     * （2）为什么还需要比较 URL？
     * 前面的判断只能排除一些明显不相等的情况，但无法确定两个对象在逻辑上是否相等。
     * 为了满足 equals 方法的定义，必须对 url 属性进行比较。
     * （3）完整流程
     * 检查是否是同一个对象（==）。
     * 检查类型是否匹配（instanceof）。
     * 比较核心属性（url.equals()）。
     *
     * 去掉前面两个if语句逻辑上也是可以的
     */
    public boolean equals(Object other) {
      if (other == this){
          return true;
      }
      if (!(other instanceof UrlResource)){
          return false;
      }
      // 比较url
        return this.url.equals(((UrlResource) other).url);
    }
    /**
     * hashCode
     * 返回url的hashCode值
     *
     */

    public int hashCode() {
        return this.url.hashCode();
    }

}
