package org.jmotor.scalikejdbc

import org.jmotor.concurrent.ExecutorLookup
import scalikejdbc.ConnectionPool
import scalikejdbc.DB
import scalikejdbc.DBSession
import scalikejdbc.using

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/**
 * Component:
 * Description:
 * Date: 2018/8/16
 *
 * @author AI
 */
trait RepositorySupport {

  private[this] lazy implicit val ec: ExecutionContext = {
    ExecutionContext.fromExecutor(ExecutorLookup.lookupByPartition("repositories-dispatcher", this))
  }

  def readOnly[A](execution: DBSession ⇒ A)(implicit cp: ConnectionPool): Future[A] = {
    execute(execution, db ⇒ db.readOnly[A])
  }

  def autoCommit[A](execution: DBSession ⇒ A)(implicit cp: ConnectionPool): Future[A] = {
    execute(execution, db ⇒ db.autoCommit[A])
  }

  def localTx[A](execution: DBSession ⇒ A)(implicit cp: ConnectionPool): Future[A] = {
    execute(execution, db ⇒ db.localTx[A])
  }

  private[this] def execute[A](execution: DBSession ⇒ A, sessionfn: DB ⇒ (DBSession ⇒ A) ⇒ A)
    (implicit cp: ConnectionPool, ec: ExecutionContext): Future[A] = Future {
    using(DB(cp.borrow())) { db ⇒
      sessionfn(db)(execution)
    }
  }(ec)

}
