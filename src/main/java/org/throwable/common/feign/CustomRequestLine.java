package org.throwable.common.feign;

import org.throwable.common.feign.annotation.GetMapping;
import org.throwable.common.feign.annotation.PostMapping;
import org.throwable.common.feign.annotation.RequestBody;
import org.throwable.common.feign.annotation.RequestParam;

import java.util.Map;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/10/13 16:42
 */
public interface CustomRequestLine {

    @GetMapping(url = "/get")
    String getRequest(@RequestParam(name = "key")String key);

    @PostMapping(url = "/post")
    String postRequest(@RequestBody Map<String,String> requestBody);
}
