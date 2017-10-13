package org.throwable.common.feign;

import feign.*;
import feign.okhttp.OkHttpClient;
import org.throwable.common.feign.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/10/13 15:19
 */
public class FeignCustomContractClient {

    public static <T> T createFeignTarget(Class<T> target, String url) {
        return Feign.builder().options(new Request.Options(5000, 5000))
                .logger(new Logger.NoOpLogger())
                .contract(new CustomContract())   //这里会调用到Class<T> target
                .client(new OkHttpClient())
                .target(target, url);
    }


    private static class CustomContract extends Contract.BaseContract {

        @Override
        protected void processAnnotationOnClass(MethodMetadata data, Class<?> clz) {
            System.out.println("MethodMetadata --> " + data);
            System.out.println("Target --> " + clz);
        }

        @Override
        protected void processAnnotationOnMethod(MethodMetadata data, Annotation annotation, Method method) {
            if (null != annotation) {
                Class<? extends Annotation> annotationType = annotation.annotationType();
                if (GetMapping.class == annotationType) {
                    GetMapping getMapping = (GetMapping) annotation;
                    data.template().method("GET");
                    data.template().append(getMapping.url());
                    data.template().decodeSlash(getMapping.decodeSlash());
                } else if (PostMapping.class == annotationType) {
                    PostMapping postMapping = (PostMapping) annotation;
                    data.template().method("POST");
                    data.template().append(postMapping.url());
                    data.template().header("Content-Type", postMapping.mediaType());
                    data.template().decodeSlash(postMapping.decodeSlash());
                }
            }
        }

        @Override
        protected boolean processAnnotationsOnParameter(MethodMetadata data, Annotation[] annotations, int paramIndex) {
            if (null != annotations && annotations.length > 0) {
                for (Annotation annotation : annotations) {
                    Class<? extends Annotation> annotationType = annotation.annotationType();
                    if (RequestParam.class == annotationType) {
                        RequestParam requestParam = (RequestParam) annotation;
                        nameParam(data, requestParam.name(), paramIndex);
                        data.indexToEncoded().put(paramIndex, requestParam.encoded());
                    } else if (RequestBody.class == annotationType) {
                        data.bodyIndex(paramIndex);
                    } else if (PathValue.class == annotationType) {
                        PathValue pathValue = (PathValue) annotation;
                        String name = '{' + pathValue.name() + '}';
                        if (!searchMapValuesContainsSubstring(data.template().headers(), name)
                                && !searchMapValuesContainsSubstring(data.template().queries(), name)){
                            data.formParams().add(name);
                        }
                    }
                }
            }
            return true;
        }

        protected void nameParam(MethodMetadata data, String name, int i) {
            Collection<String> names =
                    data.indexToName().containsKey(i) ? data.indexToName().get(i) : new ArrayList<>();
            names.add(name);
            data.indexToName().put(i, names);
        }

        private static <K, V> boolean searchMapValuesContainsSubstring(Map<K, Collection<String>> map,
                                                                       String search) {
            Collection<Collection<String>> values = map.values();
            if (values.isEmpty()) {
                return false;
            }
            for (Collection<String> entry : values) {
                for (String value : entry) {
                    if (value.contains(search)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public static void main(String[] args) {
//        String url = "http://localhost:9091";
//        CustomRequestLine customRequestLine = FeignCustomContractClient.createFeignTarget(CustomRequestLine.class, "");
//        customRequestLine.getRequest("doge");
//        Map<String, String> request = new HashMap<>();
//        request.put("name", "doge");
//        customRequestLine.postRequest(customRequestLine.getClass().getSimpleName());
        System.out.println(CustomRequestLine.class.getSimpleName());
    }
}
