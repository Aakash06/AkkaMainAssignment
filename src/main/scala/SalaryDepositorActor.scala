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

  val CAR_BILL: Double = 1000
  val PHONE_BILL: Double = 500
  val INTERNET_BILL: Double = 800
  val ELECTRICITY_BILL: Double = 4000
  val FOOD_BILL: Double = 3500

  override def receive: Receive = {

    case (accountNo: Long, billerCategory: Category.Value) =>
      billerCategory match {

        case Category.car => databaseService.payBill(accountNo, CAR_BILL)
        case Category.phone => databaseService.payBill(accountNo, PHONE_BILL)
        case Category.internet => databaseService.payBill(accountNo, INTERNET_BILL)
        case Category.electricity => databaseService.payBill(accountNo, ELECTRICITY_BILL)
        case Category.food => databaseService.payBill(accountNo, FOOD_BILL)

      }
  }

}

object BillProcessingActor {

  def props(databaseService: AccountDatabaseServices): Props = Props(classOf[BillProcessingActor], databaseService)

}

object SalaryDepositorActor {

  def props(databaseService: AccountDatabaseServices): Props = Props(classOf[SalaryDepositorActor], databaseService)

}
