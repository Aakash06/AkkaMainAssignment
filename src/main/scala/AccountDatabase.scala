import java.text.SimpleDateFormat
import java.util.Calendar

import org.apache.log4j.Logger

import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Map}
import scala.language.postfixOps

trait AccountDatabase {

  val logger = Logger.getLogger(this.getClass)

  private val userAccountMap : mutable.Map[String,CustomerAccount]= Map(
    "Aakash06" -> CustomerAccount(1L, "Aakash", "Shahdara,Delhi-110032", "Aakash06", 50.00),
    "Kapil14" -> CustomerAccount(2L, "Kapil", "Laxmi Nagar,Delhi", "Kapil14", 20.00)
  )

  def addCustomerAccount(username:String,customerAccount: CustomerAccount):Unit={

    userAccountMap.+=(username->customerAccount)
  }

  def getUserDatabase: mutable.Map[String, CustomerAccount] = userAccountMap

  val dateFormat = new SimpleDateFormat("d-M-y")
  val currentDate = dateFormat.format(Calendar.getInstance().getTime())

  private val linkedBiller: mutable.Map[Long, ListBuffer[LinkBiller]] = Map(
    1L -> ListBuffer(
      LinkBiller(Category.phone, "PhoneBiller", 1L, currentDate, 1000.00, 0, 0, 110.00),
      LinkBiller(Category.internet, "InternetBiller", 1L, currentDate, 500.00, 0, 0, 0.00)
    ),
    2L -> ListBuffer(
      LinkBiller(Category.electricity, "ElectricityBiller", 20L, currentDate, 800.00, 0, 0, 0.00),
      LinkBiller(Category.food, "FoodBiller", 20L, currentDate, 1300.00, 0, 0, 0.00),
      LinkBiller(Category.car, "CarBiller", 20L, currentDate, 800.00, 0, 0, 200.00)
    )
  )

  def getLinkedBiller: mutable.Map[Long, ListBuffer[LinkBiller]] = linkedBiller

  def linkBiller(accountNo: Long, billerName: String, billerCategory: Category.Value): Unit = {

    val listOfBillers = linkedBiller.getOrElse(accountNo, Nil)
    val linkedBillerCaseClass = LinkBiller(accountNo, billerName, billerCategory)

    listOfBillers match {

      case listOfBillers: List[LinkBiller] =>
        linkedBiller(accountNo) += linkedBillerCaseClass

      case Nil => linkedBiller += accountNo -> ListBuffer(linkedBillerCaseClass)

    }

  }

  def depositSalary(accountNo: Long, customerName: String, salary: Double): Unit = {
    userAccountMap map {
      case (username, customerAccount) =>
        if (customerAccount.accountNo == accountNo) {
          val newCustomerAccount = customerAccount.copy(initialAmount = customerAccount.initialAmount + salary)
          (username, newCustomerAccount)
        }
        else {
          (username, customerAccount)
        }
    }

  }

  def payBill(accountNo: Long, billerCategory :Category.Value): Boolean = {

    val billToPayList = linkedBiller.getOrElse(accountNo, Nil).filter(_.billerCategory == billerCategory)
    if (billToPayList.isEmpty) {
      false
    }
    else {
      val billToPay = billToPayList.head.amount
      val initialAmountList = userAccountMap.values.filter(_.accountNo == accountNo)
      val initialAmount = initialAmountList.map(_.initialAmount).toList
      logger.info("Amount in the account is " + initialAmount.head)
      if (initialAmount.head > billToPay) {
        logger.info("If condition satisfied in payBill")
        val linkedBillerCaseClass = linkedBiller(accountNo).filter(_.billerCategory == billerCategory).head
        val dateWhilePayingBill = dateFormat.format(Calendar.getInstance().getTime())
        val newlinkedBillerCaseClass = linkedBillerCaseClass.copy(transactionDate = dateWhilePayingBill,
          amount = billToPay, totalIterations = linkedBillerCaseClass.totalIterations + 1,
          executedIterations = linkedBillerCaseClass.executedIterations + 1, paidAmount = linkedBillerCaseClass.amount + billToPay
        )

        val listOfLinkedBiller = linkedBiller(accountNo)
        listOfLinkedBiller -= linkedBillerCaseClass
        listOfLinkedBiller += newlinkedBillerCaseClass
        linkedBiller(accountNo) = listOfLinkedBiller

        logger.info("LinkedBiller map is as: " + linkedBiller)

        userAccountMap foreach {
          case (username, customerAccount) =>
            if (customerAccount.accountNo == accountNo) {
              val newCustomerAccount = customerAccount.copy(initialAmount = customerAccount.initialAmount - billToPay)
              userAccountMap(username) = newCustomerAccount
            }
            else {
              userAccountMap(username) = customerAccount
            }
        }
        logger.info("Returning true")
        true
      }
      else {
        logger.info("Returning false")
        false
      }
    }
  }

}
