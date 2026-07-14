package com.javaoffers.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * 样例 mock 商品接口服务，对应原 Go 的 ItemServer.go。
 * GET /query_item?type=xxx → {"size": N, "data":[{"id": N, "item_name": "..."}]}
 */
@SpringBootApplication
@RestController
public class ItemServerApplication {

    private static final String PARAM_TYPE = "type";
    private static final String[] ITEM_TYPES = {"food", "phone", "book", "pen"};
    private final Random random = new Random();

    public static void main(String[] args) {
        SpringApplication.run(ItemServerApplication.class, args);
        System.out.println(" start done port 8090");
    }

    @GetMapping("/query_item")
    public Map<String, Object> queryItem(@RequestParam(name = PARAM_TYPE, required = false) String param) {
        System.out.println(param);

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", random.nextInt(100));
        item.put("item_name", ITEM_TYPES[random.nextInt(ITEM_TYPES.length)]);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("size", random.nextInt(100));
        result.put("data", Collections.singletonList(item));
        return result;
    }
}
