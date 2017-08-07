import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration.DurationInt
import scala.concurrent.Future
import scala.language.postfixOps


class SalaryDeposit {

  def salaryDeposit(accountNo: Long, customerName: String, salary: Double, salaryDepositActorRef: ActorRef): Future[Boolean] = {

    implicit val timeout = Timeout(10 seconds)
    (salaryDepositActorRef ? (accountNo, customerName, salary)).mapTo[Boolean]

  }

}
