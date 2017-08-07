import java.text.SimpleDateFormat
import java.util.Calendar

import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Map}

trait AccountDatabase {

  private val userAccountMap : mutable.Map[String,CustomerAccount]= Map(
    "Aakash06" -> CustomerAccount(1L, "Aakash", "Shahdara,Delhi-110032", "Aakash06", 50.00),
    "Kapil14" -> CustomerAccount(20L, "Kapil", "Laxmi Nagar,Delhi", "Kapil14", 20.00)
  )

  def addCustomerAccount(username:String,customerAccount: CustomerAccount):Unit={

    userAccountMap.+=(username->customerAccount)
  }

  def getUserDatabase: mutable.Map[String, CustomerAccount] = userAccountMap

  val dateFormat = new SimpleDateFormat("d-M-y")
  val currentDate = dateFormat.format(Calendar.getInstance().getTime())

  private val linkedBiller: mutable.Map[Long, ListBuffer[LinkBiller]] = Map(
    1L -> ListBuffer(
      LinkBiller(Category.phone, "PhoneBiller", 1L, currentDate, 0.00, 0, 0, 0.00),
      LinkBiller(Category.internet, "InternetBiller", 1L, currentDate, 0.00, 0, 0, 0.00)
    ),
    2L -> ListBuffer(
      LinkBiller(Category.electricity, "ElectricityBiller", 20L, currentDate, 0.00, 0, 0, 0.00),
      LinkBiller(Category.food, "FoodBiller", 20L, currentDate, 0.00, 0, 0, 0.00)
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

  def payBill(accountNo: Long, billToPay: Double): Boolean = {

    val initialAmount = userAccountMap.values.filter(_.accountNo == accountNo).map(_.initialAmount).toList
    if (initialAmount.head > billToPay) {
      userAccountMap map {
        case (username, customerAccount) =>
          if (customerAccount.accountNo == accountNo) {
            val newCustomerAccount = customerAccount.copy(initialAmount = customerAccount.initialAmount - billToPay)
            (username, newCustomerAccount)
          }
          else {
            (username, customerAccount)
          }
      }
      true
    }
    else {
      false
    }

  }

}
