package org.jmotor.scalikejdbc

import org.jmotor.concurrent.ExecutorLookup
import scalikejdbc.{ ConnectionPool, DB, DBSession, using }

import scala.concurrent.{ Future, ExecutionContext }

/**
 * Component:
 * Description:
 * Date: 2018/8/16
 *
 * @author AI
 */
trait RepositorySupport {

  private[this] lazy implicit val ec: ExecutionContext =
    ExecutionContext.fromExecutor(ExecutorLookup.lookup("repositories-dispatcher"))

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
