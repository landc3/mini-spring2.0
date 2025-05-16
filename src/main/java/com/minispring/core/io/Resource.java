package com.minispring.core.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * 资源访问接口
 * 定义了资源的基本操作方法
 */
public interface Resource {

    /**
     * 检查资源是否存在
     * return
     */
    boolean exists();

    /**
     * 检查资源是否可读
     * return
     */
    boolean isReadable();

    /**
     * 获取资源输入流
     * return 资源输入流
     * throws IOException
     */
    InputStream getInputStream() throws IOException;

    /**
     * 获取资源描述
     * return 资源描述
     */
    String getDescription();
}
