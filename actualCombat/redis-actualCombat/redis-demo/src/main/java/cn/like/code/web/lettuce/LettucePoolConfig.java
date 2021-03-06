package cn.like.code.web.lettuce;

import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import lombok.Getter;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.time.Duration;

/**
 * lettuce 连接池配置
 *
 * @author like
 * @date 2021/5/31 16:38
 */
@Component
public class LettucePoolConfig {

    @Resource
    private AbstractRedisClient redisClient;

    /** 设置可分配的最大Redis实例数量 */
    @Value("${spring.redis.lettuce.pool.max-active}")
    private Integer maxTotal;

    /** 设置最多空闲的Redis实例数量 */
    @Value("${spring.redis.lettuce.pool.max-idle}")
    private Integer maxIdle;

    /** 设置最多空闲的Redis实例数量 */
    @Value("${spring.redis.lettuce.pool.min-idle}")
    private Integer minIdle;

    /** 归还Redis实例时，检查有消息，如果失败，则销毁实例 */
    @Value("${spring.redis.lettuce.pool.test-on-return}")
    private Boolean testOnReturn;

    /** 当池耗尽时，在抛出异常之前连接分配应该阻塞的最长时间。使用负值无限期阻止。 */
    @Value("${spring.redis.lettuce.pool.max-wait}")
    private Long maxWait;

    /** 空闲对象退出线程的运行之间的时间。当为正时，空闲对象逐出线程启动，否则不执行空闲对象逐出。 */
    @Value("${spring.redis.lettuce.pool.time-between-eviction-runs}")
    private Duration timeBetweenEvictionRuns;

    /** 当Redis实例处于空闲时检查有效性，默认false */
    @Value("${spring.redis.lettuce.pool.test-while-idle}")
    private Boolean testWhileIdle;

    @Value("${spring.redis.lettuce.cluster.fire}")
    private Boolean fire;

    /**
     * Apache-Common-Pool是一个对象池，用于缓存Redis连接，
     * 因为 lettuce 本身基于Netty的异步驱动，但基于Servlet模型的同步访问时，连接池是必要的
     * 连接池可以很好的复用连接，减少重复的IO消耗与RedisURI创建实例的性能消耗
     */
    @Getter
    GenericObjectPool<StatefulRedisConnection<String, String>> redisConnectionPool;

    @Getter
    GenericObjectPool<StatefulRedisClusterConnection<String, String>> redisClusterConnectionPool;

    /**
     * Servlet初始化时先初始化Lettuce连接池
     */
    @PostConstruct
    private void init() {
        if (!fire) {
            System.out.println("初始化了单体连接池");
            GenericObjectPoolConfig<StatefulRedisConnection<String, String>> redisPoolConfig
                    = new GenericObjectPoolConfig<>();
            redisPoolConfig.setMaxTotal(this.maxTotal);
            redisPoolConfig.setMaxIdle(this.maxIdle);
            redisPoolConfig.setMinIdle(this.minIdle);
            redisPoolConfig.setMaxWaitMillis(this.maxWait);
            redisPoolConfig.setTestWhileIdle(this.testWhileIdle);
            if (this.testOnReturn != null) {
                redisPoolConfig.setTestOnReturn(this.testOnReturn);
            }
            if (this.timeBetweenEvictionRuns != null) {
                redisPoolConfig.setTimeBetweenEvictionRunsMillis(this.timeBetweenEvictionRuns.toMillis());
            }
            if (this.timeBetweenEvictionRuns != null) {
                redisPoolConfig.setMaxWaitMillis(this.timeBetweenEvictionRuns.toMillis());
            }
            this.redisConnectionPool =
                    ConnectionPoolSupport
                            .createGenericObjectPool(() -> ((RedisClient) redisClient).connect(), redisPoolConfig);
        } else {
            System.out.println("初始化了集群连接池");
            GenericObjectPoolConfig<StatefulRedisClusterConnection<String, String>> redisPoolConfig
                    = new GenericObjectPoolConfig<>();
            redisPoolConfig.setMaxTotal(this.maxTotal);
            redisPoolConfig.setMaxIdle(this.maxIdle);
            redisPoolConfig.setMinIdle(this.minIdle);
            redisPoolConfig.setMaxWaitMillis(this.maxWait);
            redisPoolConfig.setTestWhileIdle(this.testWhileIdle);
            if (this.testOnReturn != null) {
                redisPoolConfig.setTestOnReturn(this.testOnReturn);
            }
            if (this.timeBetweenEvictionRuns != null) {
                redisPoolConfig.setTimeBetweenEvictionRunsMillis(this.timeBetweenEvictionRuns.toMillis());
            }
            if (this.timeBetweenEvictionRuns != null) {
                redisPoolConfig.setMaxWaitMillis(this.timeBetweenEvictionRuns.toMillis());
            }
            this.redisClusterConnectionPool =
                    ConnectionPoolSupport
                            .createGenericObjectPool(() -> ((RedisClusterClient) redisClient)
                                    .connect(), redisPoolConfig);
        }
    }

    /**
     * Servlet销毁时先销毁Lettuce连接池
     */
    @PreDestroy
    private void destroy() {
        if (!fire) {
            redisConnectionPool.close();
        } else {
            redisClusterConnectionPool.close();
        }
        redisClient.shutdown();
    }
}