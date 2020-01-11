package org.jmotor.concurrent

import com.typesafe.config.ConfigFactory
import org.scalatest.FunSuite

/**
 *
 * @author AI
 *         2020/1/11
 */
class ExecutorLookupSpec extends FunSuite {

  test("lookup by partition") {
    val name = "repositories-dispatcher"
    ExecutorLookup.setup(ConfigFactory.load())
    assert(ExecutorLookup.lookupByPartition(name, Obj1) == ExecutorLookup.lookupByPartition(name, Obj1))
  }

}

object Obj1
