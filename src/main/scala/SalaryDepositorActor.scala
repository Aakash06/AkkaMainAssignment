import akka.actor.{Actor, ActorLogging, Props}
import akka.util.Timeout

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class SalaryDepositorActor(databaseService: AccountDatabaseServices) extends Actor with ActorLogging {

  override def receive: Receive = {

    case (accountNo: Long, customerName: String, salary: Double) =>
      sender() ! databaseService.depositSalary(accountNo, customerName, salary)
      implicit val timeout = Timeout(10 seconds)
      val listOfBillers = databaseService.getLinkedBiller.getOrElse(accountNo,Nil).map(_.billerCategory)

          listOfBillers.foreach(billerCategory => context.actorOf(BillProcessingActor.props(databaseService)).forward(accountNo, billerCategory))

    case _ => log.info("Failed while receiving listOfBillers with exception " )

      }

}

class BillProcessingActor(databaseService: AccountDatabaseServices) extends Actor with ActorLogging {

  override def receive: Receive = {

    case (accountNo: Long, billerCategory: Category.Value) =>
      billerCategory match {

        case Category.car => databaseService.payBill(accountNo, Category.car)
        case Category.phone => databaseService.payBill(accountNo, Category.phone)
        case Category.internet => databaseService.payBill(accountNo, Category.internet)
        case Category.electricity => databaseService.payBill(accountNo, Category.electricity)
        case Category.food => databaseService.payBill(accountNo, Category.food)

      }
  }

}

object BillProcessingActor {

  def props(databaseService: AccountDatabaseServices): Props = Props(classOf[BillProcessingActor], databaseService)

}

object SalaryDepositorActor {

  def props(databaseService: AccountDatabaseServices): Props = Props(classOf[SalaryDepositorActor], databaseService)

}
