package cn.like.code.testCase.str;

import java.util.concurrent.TimeUnit;

import static cn.like.code.testCase.Redis.reactive;

/**
 * .____    .__ __
 * |    |   |__|  | __ ____
 * |    |   |  |  |/ // __ \
 * |    |___|  |    <\  ___/
 * |_______ \__|__|_ \\___  >
 * \/       \/    \/
 * <p>
 * desc
 *
 * @author like
 * @date 2021-05-05 16:30
 */
public class 实现博客字数统计与文章预览_04 {

    public static void main(String[] args) {
        reactive().mget("article:1:title", "article:1:content", "article:1:author", "article:1:time")
                .subscribe(kv -> {
                    // 一条一条的消费
                    kv.map(s -> {
                        System.out.println(s);
                        return true;
                    });
                });

        // strlen
        reactive().strlen("article:1:content").subscribe(res -> {
            System.out.println("博客长度:" + res);
        });
        // getrange
        reactive().getrange("article:1:content", 0, 11).subscribe(res -> {
            System.out.println("博客预览:" + res);
        });
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

