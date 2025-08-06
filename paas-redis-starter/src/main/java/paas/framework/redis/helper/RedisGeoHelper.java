package paas.framework.redis.helper;

import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 基于redis地理位置 api
 */
@Component
public class RedisGeoHelper {
    @Resource
    RedisTemplate redisTemplate;

    /**
     * 添加地址位置
     *
     * @param key          存储KEY
     * @param point        经维度
     * @param locationName 位置名称
     */
    public void add(String key, Point point, String locationName) {
        redisTemplate.boundGeoOps(key).add(point, locationName);
    }

    /**
     * 返回位置的坐标信息
     *
     * @param key          存储KEY
     * @param locationName 位置名称-获取坐标
     * @return
     */
    public List<Point> position(String key, String... locationName) {
        return redisTemplate.boundGeoOps(key).position(locationName);
    }

    /**
     * 计算两个位置之间的距离
     *
     * @param key           存储KEY
     * @param locationName1 位置名称1
     * @param locationName2 位置名称2
     */
    public Distance distance(String key, String locationName1, String locationName2) {
        return redisTemplate.boundGeoOps(key).distance(locationName1, locationName2);
    }

    /**
     * 计算两个位置之间的距离
     *
     * @param key           存储KEY
     * @param locationName1 位置名称1
     * @param locationName2 位置名称2
     * @param metrics       单位
     * @return
     */
    public Distance distance(String key, String locationName1, String locationName2, Metrics metrics) {
        return redisTemplate.boundGeoOps(key).distance(locationName1, locationName2, metrics);
    }

    /**
     * 以中心点查询指定范围内的位置
     *
     * @param key      存储KEY
     * @param point    中心点
     * @param distance 指定范围
     */
    public GeoResults<RedisGeoCommands.GeoLocation<String>> radius(String key, Point point, Distance distance, RedisGeoCommands.GeoRadiusCommandArgs args) {
        Circle circle = new Circle(point, distance);
        return redisTemplate.boundGeoOps(key).radius(circle, args);
    }

    /**
     * 以中心点查询指定范围内的位置
     *
     * @param key          存储KEY
     * @param locationName 中心位置名称
     * @param distance     指定范围
     */
    public GeoResults<RedisGeoCommands.GeoLocation<String>> radiusByMember(String key, String locationName, Distance distance, RedisGeoCommands.GeoRadiusCommandArgs args) {
        return redisTemplate.boundGeoOps(key).radius(locationName, distance, args);
    }

    /**
     * 移动位置
     *
     * @param key          存储KEY
     * @param locationName 位置名称
     * @return
     */
    public Long remove(String key, String... locationName) {
        return redisTemplate.boundGeoOps(key).remove(locationName);
    }

    /**
     * 删除
     *
     * @param key
     * @return
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

}
