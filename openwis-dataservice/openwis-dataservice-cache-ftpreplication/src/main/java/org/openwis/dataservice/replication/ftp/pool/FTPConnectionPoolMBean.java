package org.openwis.dataservice.replication.ftp.pool;

import org.apache.commons.pool.PoolableObjectFactory;


public interface FTPConnectionPoolMBean {

   /**
    * Returns the maximum number of objects that can be allocated by the pool
    * (checked out to clients, or idle awaiting checkout) at a given time.
    * When non-positive, there is no limit to the number of objects that can
    * be managed by the pool at one time.
    *
    * @return the cap on the total number of object instances managed by the pool.
    */
   int getMaxActive();

   /**
    * Sets the cap on the number of objects that can be allocated by the pool
    * (checked out to clients, or idle awaiting checkout) at a given time. Use
    * a negative value for no limit.
    *
    * @param maxActive The cap on the total number of object instances managed by the pool.
    * Negative values mean that there is no limit to the number of objects allocated
    * by the pool.
    * @see #getMaxActive
    */
   void setMaxActive(int maxActive);

   /**
    * Returns the maximum amount of time (in milliseconds) the
    * {@link #borrowObject} method should block before throwing
    * an exception when the pool is exhausted and the
    * {@link #setWhenExhaustedAction "when exhausted" action} is
    * {@link #WHEN_EXHAUSTED_BLOCK}.
    *
    * When less than or equal to 0, the {@link #borrowObject} method
    * may block indefinitely.
    *
    * @return maximum number of milliseconds to block when borrowing an object.
    * @see #setMaxWait
    * @see #setWhenExhaustedAction
    * @see #WHEN_EXHAUSTED_BLOCK
    */
   long getMaxWait();

   /**
    * Sets the maximum amount of time (in milliseconds) the
    * {@link #borrowObject} method should block before throwing
    * an exception when the pool is exhausted and the
    * {@link #setWhenExhaustedAction "when exhausted" action} is
    * {@link #WHEN_EXHAUSTED_BLOCK}.
    *
    * When less than or equal to 0, the {@link #borrowObject} method
    * may block indefinitely.
    *
    * @param maxWait maximum number of milliseconds to block when borrowing an object.
    * @see #getMaxWait
    * @see #setWhenExhaustedAction
    * @see #WHEN_EXHAUSTED_BLOCK
    */
   void setMaxWait(long maxWait);

   /**
    * Returns the cap on the number of "idle" instances in the pool.
    * @return the cap on the number of "idle" instances in the pool.
    * @see #setMaxIdle
    */
   int getMaxIdle();

   /**
    * Sets the cap on the number of "idle" instances in the pool.
    * If maxIdle is set too low on heavily loaded systems it is possible you
    * will see objects being destroyed and almost immediately new objects
    * being created. This is a result of the active threads momentarily
    * returning objects faster than they are requesting them them, causing the
    * number of idle objects to rise above maxIdle. The best value for maxIdle
    * for heavily loaded system will vary but the default is a good starting
    * point.
    * @param maxIdle The cap on the number of "idle" instances in the pool.
    * Use a negative value to indicate an unlimited number of idle instances.
    * @see #getMaxIdle
    */
   void setMaxIdle(int maxIdle);

   /**
    * Returns the minimum number of objects allowed in the pool
    * before the evictor thread (if active) spawns new objects.
    * (Note no objects are created when: numActive + numIdle >= maxActive)
    *
    * @return The minimum number of objects.
    * @see #setMinIdle
    */
   int getMinIdle();

   /**
    * Sets the minimum number of objects allowed in the pool
    * before the evictor thread (if active) spawns new objects.
    * Note that no objects are created when
    * <code>numActive + numIdle >= maxActive.</code>
    * This setting has no effect if the idle object evictor is disabled
    * (i.e. if <code>timeBetweenEvictionRunsMillis <= 0</code>).
    *
    * @param minIdle The minimum number of objects.
    * @see #getMinIdle
    * @see #getTimeBetweenEvictionRunsMillis()
    */
   void setMinIdle(int minIdle);

   /**
    * Returns the number of milliseconds to sleep between runs of the
    * idle object evictor thread.
    * When non-positive, no idle object evictor thread will be
    * run.
    *
    * @return number of milliseconds to sleep between evictor runs.
    * @see #setTimeBetweenEvictionRunsMillis
    */
   long getTimeBetweenEvictionRunsMillis();

   /**
    * Sets the number of milliseconds to sleep between runs of the
    * idle object evictor thread.
    * When non-positive, no idle object evictor thread will be
    * run.
    *
    * @param timeBetweenEvictionRunsMillis number of milliseconds to sleep between evictor runs.
    * @see #getTimeBetweenEvictionRunsMillis
    */
   void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis);

   /**
    * Returns the minimum amount of time an object may sit idle in the pool
    * before it is eligible for eviction by the idle object evictor
    * (if any).
    *
    * @return minimum amount of time an object may sit idle in the pool before it is eligible for eviction.
    * @see #setMinEvictableIdleTimeMillis
    * @see #setTimeBetweenEvictionRunsMillis
    */
   long getMinEvictableIdleTimeMillis();

   /**
    * Sets the minimum amount of time an object may sit idle in the pool
    * before it is eligible for eviction by the idle object evictor
    * (if any).
    * When non-positive, no objects will be evicted from the pool
    * due to idle time alone.
    * @param minEvictableIdleTimeMillis minimum amount of time an object may sit idle in the pool before
    * it is eligible for eviction.
    * @see #getMinEvictableIdleTimeMillis
    * @see #setTimeBetweenEvictionRunsMillis
    */
   void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis);

   /**
    * Returns the minimum amount of time an object may sit idle in the pool
    * before it is eligible for eviction by the idle object evictor
    * (if any), with the extra condition that at least
    * "minIdle" amount of object remain in the pool.
    *
    * @return minimum amount of time an object may sit idle in the pool before it is eligible for eviction.
    * @since Pool 1.3
    * @see #setSoftMinEvictableIdleTimeMillis
    */
   long getSoftMinEvictableIdleTimeMillis();

   /**
    * Sets the minimum amount of time an object may sit idle in the pool
    * before it is eligible for eviction by the idle object evictor
    * (if any), with the extra condition that at least
    * "minIdle" object instances remain in the pool.
    * When non-positive, no objects will be evicted from the pool
    * due to idle time alone.
    *
    * @param softMinEvictableIdleTimeMillis minimum amount of time an object may sit idle in the pool before
    * it is eligible for eviction.
    * @since Pool 1.3
    * @see #getSoftMinEvictableIdleTimeMillis
    */
   void setSoftMinEvictableIdleTimeMillis(long softMinEvictableIdleTimeMillis);

   ///////// Monitoring services

   /**
    * Return the number of instances
    * currently idle in this pool (optional operation).
    * This may be considered an approximation of the number
    * of objects that can be {@link #borrowObject borrowed}
    * without creating any new instances.
    * Returns a negative value if this information is not available.
    *
    * @return the number of instances currently idle in this pool or a negative value if unsupported
    */
   int getNumIdle();

   /**
    * Return the number of instances
    * currently borrowed from this pool
    * (optional operation).
    * Returns a negative value if this information is not available.
    *
    * @return the number of instances currently borrowed from this pool or a negative value if unsupported
    */
   int getNumActive();

   /**
    * Clears any objects sitting idle in the pool, releasing any
    * associated resources (optional operation).
    * Idle objects cleared must be {@link PoolableObjectFactory#destroyObject(Object) destroyed}.
    *
    */
   void clear();
}
