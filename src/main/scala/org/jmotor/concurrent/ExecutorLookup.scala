package org.jmotor.concurrent

import java.util.Objects
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.hash.HashCode
import com.google.common.hash.Hashing
import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.typesafe.config.Config

/**
 * Component:
 * Description:
 * Date: 2018/8/16
 *
 * @author AI
 */
object ExecutorLookup {

  private[this] var _config: Config = _
  private[this] val executors: Cache[String, Executor] = CacheBuilder.newBuilder().build()

  def setup(config: Config): Unit = _config = config

  def config(): Config = {
    if (Objects.isNull(_config)) {
      throw new IllegalStateException("Using org.jmotor.concurrent.ExecutorLookup.setup(config) to init")
    }
    _config
  }

  def lookup(configName: String, name: Option[String] = None): Executor = {
    val dispatchConfig = config().getConfig(configName)
    val executor = dispatchConfig.getString("executor")
    executor match {
      case "thread-pool-executor" ⇒
        val max = dispatchConfig.getInt(s"$executor.core-pool-size-max")
        val min = dispatchConfig.getInt(s"$executor.core-pool-size-min")
        val factor = dispatchConfig.getInt(s"$executor.core-pool-size-factor")
        val desired = Runtime.getRuntime.availableProcessors() * factor
        val core = Math.max(Math.min(max, desired), min)
        val tf = new ThreadFactoryBuilder().setNameFormat(s"${name.getOrElse(configName)}-%d").build()
        new ThreadPoolExecutor(core, max, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue[Runnable], tf)
      case _ ⇒ throw new UnsupportedOperationException
    }

  }

  def lookupByPartition(name: String, partitionKey: Any, partitionSize: Int = Runtime.getRuntime.availableProcessors()): Executor = {
    val partition = Hashing.consistentHash(HashCode.fromInt(com.google.common.base.Objects.hashCode(partitionKey)), partitionSize)
    val executorName = s"$name-part-$partition"
    executors.get(executorName, () ⇒ {
      lookup(name, Option(executorName))
    })
  }

}
