package org.jmotor.concurrent

import java.util.Objects
import java.util.concurrent.{ Executor, LinkedBlockingQueue, ThreadPoolExecutor, TimeUnit }

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.typesafe.config.Config

/**
 * Component:
 * Description:
 * Date: 2018/8/16
 *
 * @author AI
 */
object ExecutionContext {

  private[this] var _config: Config = _

  def setup(config: Config): Unit = _config = config

  def config(): Config = {
    if (Objects.isNull(_config)) {
      throw new IllegalStateException("Using ExecutionContext.setup(config) to init")
    }
    _config
  }

  def lookup(name: String): Executor = {
    val dispatchConfig = config().getConfig(name)
    val executor = dispatchConfig.getString("executor")
    executor match {
      case "thread-pool-executor" ⇒
        val max = dispatchConfig.getInt(s"$executor.core-pool-size-max")
        val min = dispatchConfig.getInt(s"$executor.core-pool-size-min")
        val factor = dispatchConfig.getInt(s"$executor.core-pool-size-factor")
        val desired = Runtime.getRuntime.availableProcessors() * factor
        val core = Math.max(Math.min(max, desired), min)
        val tf = new ThreadFactoryBuilder().setNameFormat(s"$name-%d").build()
        new ThreadPoolExecutor(core, max, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue[Runnable], tf)
      case _ ⇒ throw new UnsupportedOperationException
    }

  }

}
