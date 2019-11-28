package com.gushici.common.utils;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Redis 工具类
 * <p/>
 * Created by Administrator on 16/10/14.
 */
public class RedisUtils {

	private static Logger logger = Logger.getLogger(RedisUtils.class);


	private RedisUtils() {}
	private static JedisPool jedisPool;
	private static int maxtotal;
	private static int maxwaitmillis;
	private static String host;
	private static int port;
	private static String passWord;
	private static int timeOut;

	/*读取jedis.properties配置文件*/
	static{
		ResourceBundle rb = ResourceBundle.getBundle("jedis");
		maxtotal = Integer.parseInt(rb.getString("maxtotal"));
		maxwaitmillis = Integer.parseInt(rb.getString("maxwaitmillis"));
		host = rb.getString("host");
		port = Integer.parseInt(rb.getString("port"));
		passWord = rb.getString("passWord");
		timeOut = Integer.parseInt(rb.getString("timeOut"));
	}

	/*创建连接池*/
	static{
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(maxtotal);
		jedisPoolConfig.setMaxWaitMillis(maxwaitmillis);
		jedisPool = new JedisPool(jedisPoolConfig,host,port,timeOut,passWord);
	}

	/*获取jedis*/
	public static Jedis getJedis(){
		return jedisPool.getResource();
	}

	/*关闭Jedis*/
	public static void close(Jedis jedis){
		if(jedis!=null){
			jedis.close();
		}
	}


	/**
	 * 设置 String
	 *
	 * @param key
	 * @param value
	 */
	public static String set(String key, String value) {
		String val = null;
		Jedis jedis = getJedis();
		try {
			//logger.info("key=" + key + ",value=" + value);
			value = StringUtils.isEmpty(value) ? "" : value;
			val = jedis.set(key, value);
		} catch (Exception e) {
			logger.error("Set key error : ", e);

		} finally {
			logger.info("set 方法释放redis资源");
			if(jedis != null){
				jedis.close();
			}

		}
		return val;
	}


	/**
	 * 设置 String
	 *
	 * @param key
	 */
	public static Long incr(String key) {
		long count = 0L;
		Jedis jedis = getJedis();
		try {
			//logger.info("key=" + key + ",value=" + value);
			count = jedis.incr(key);
		} catch (Exception e) {
			logger.error("Set key error : ", e);

		} finally {
			logger.info("set 方法释放redis资源");
			if(jedis != null){
				jedis.close();
			}

		}
		return count;
	}


	/**
	 * 设置 过期时间
	 *
	 * @param key
	 * @param seconds
	 *            以秒为单位
	 * @param value
	 */
	public static String setAndTime(String key, String value, int seconds) {
		String val = null;
		Jedis jedis = getJedis();
		try {
			//logger.info("key=" + key + ",seconds=" + seconds + ",value=" + value);
			value = StringUtils.isEmpty(value) ? "" : value;
			val = jedis.setex(key, seconds, value);
		} catch (Exception e) {
			logger.error("Set keyex error : ", e);
		} finally {
			logger.info("setAndTime 方法 释放redis资源");
			if(jedis != null){
				jedis.close();
			}

		}
		return val;
	}

	/**
	 * 获取String值
	 *
	 * @param key
	 * @return value
	 */
	public static String get(String key) {

		logger.info("根据 key值 获取value=" + key);
		String val = null;
		Jedis jedis = getJedis();
		if(null != jedis){
			try {
				if (!jedis.exists(key)) {
					return null;
				}
				val = jedis.get(key);
			} catch (Exception e) {
				logger.error("get keyex error : ", e);

			} finally {
				logger.info("get 方法释放redis资源");
				if(jedis != null){
					jedis.close();
				}
			}
		}
		return val;
	}

	/**
	 * 根据key删除数据
	 *
	 * @param key
	 * @return result 受影响行数
	 */
	public static long del(String key) {
		logger.info("根据key值" +  key + "删除value" );

		Jedis jedis = getJedis();
		long l = 0;
		if (jedis != null){
			try {
				if (!jedis.exists(key)) {
					return 0;
				}
				l = jedis.del(key);
			} catch (Exception e) {
				logger.error("del keyex error : ", e);
			} finally {
				logger.info("del 方法 释放redis资源");
				if(jedis != null){
					jedis.close();
				}
			}
		}
		return l;
	}

	/**
	 * 设置过期时间
	 * @param key
	 * @param seconds
	 * @return
	 */
	public static void expireKey(String key, int seconds) {
		String val = null;
		Jedis jedis = getJedis();
		try {
			logger.info("key=" + key + ",seconds=" + seconds);
			jedis.expire(key, seconds);
		} catch (Exception e) {
			logger.error("expire key error : ", e);
		} finally {
			logger.info("expireKey 方法 释放redis资源");
			if(jedis != null){
				jedis.close();
			}
		}
	}


	public static String getn(Jedis jedis, String key) {
		return jedis.get(key);
	}

	/**
	 * 集合(set)类型存储
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public static Long setCollection(Jedis jedis, String key, String value) {
		return jedis.sadd(key, value);
	}

	/**
	 * 查看元素是否在Set集合中存在
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public static Boolean checkSismemberIsExists(Jedis jedis, String key, String value) {
		return jedis.sismember(key, value);
	}

	/**
	 * 返回Set集合中元素的个数
	 *
	 * @param key
	 * @return
	 */
	public static Long scardNum(String key) {
		return getJedis().scard(key);
	}

	public static Set<String> members(String key) {
		return getJedis().smembers(key);
	}

	/**
	 * 原子i++
	 *
	 * @param key
	 * @return
	 */
	public static Long incr(Jedis jedis, String key) {
		return jedis.incr(key);
	}

	public static void setnx(Jedis jedis, String key, String value) {
		jedis.setnx(key, value);
	}

	/**
	 * @param lockKey
	 * @param expireMsecs
	 * @return
	 */
	public static boolean tryLock(Jedis jedis, String lockKey, long expireMsecs) {
		long expires = System.currentTimeMillis() + expireMsecs + 1;
		String expiresStr = String.valueOf(expires); // 锁到期时间
		if (jedis.setnx(lockKey, expiresStr) == 1) {
			// lock acquired
			return true;
		}
		String currentValueStr = jedis.get(lockKey); // redis里的时间
		if (currentValueStr != null && Long.parseLong(currentValueStr) < System.currentTimeMillis()) {
			// 判断是否为空，不为空的情况下，如果被其他线程设置了值，则第二个条件判断是过不去的
			// lock is expired
			String oldValueStr = jedis.getSet(lockKey, expiresStr);
			// 获取上一个锁到期时间，并设置现在的锁到期时间，
			// 只有一个线程才能获取上一个线上的设置时间，因为jedis.getSet是同步的
			if (oldValueStr != null && oldValueStr.equals(currentValueStr)) {
				// 如过这个时候，多个线程恰好都到了这里，但是只有一个线程的设置值和当前值相同，他才有权利获取锁
				// lock acquired
				return true;
			}
		}
		return false;
	}

	/**
	 * @param lockKey
	 */
	public static void delLock(Jedis jedis, String lockKey) {
		String currentValueStr = jedis.get(lockKey);
		if (currentValueStr != null && System.currentTimeMillis() < Long.parseLong(currentValueStr)) {
			jedis.del(lockKey);
		}
	}

	/**
	 * Hash 类型数据存储
	 *
	 * @return
	 */
	public static Long hset(Jedis jedis, String key, String field, String value) {
		return jedis.hset(key, field, value);
	}

	public static Long hsetnx(Jedis jedis, String key, String field, String value) {
		return jedis.hsetnx(key, field, value);
	}

	public static String hget(Jedis jedis, String key, String field) {
		return jedis.hget(key, field);
	}

	public static Map<String, String> hgetAll(Jedis jedis, String key) {
		return jedis.hgetAll(key);
	}

	public static Set<String> keys(Jedis jedis, String pattern) {
		return jedis.keys(pattern);
	}

	public static Long hdel(Jedis jedis, String key, String fields) {
		return jedis.hdel(key, fields);
	}


}
