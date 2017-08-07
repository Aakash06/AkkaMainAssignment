import java.text.SimpleDateFormat
import java.util.Calendar

import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Map}

trait AccountDatabase {

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
      LinkBiller(Category.phone, "PhoneBiller", 1L, currentDate, 0.00, 0, 0, 0.00),
      LinkBiller(Category.internet, "InternetBiller", 1L, currentDate, 0.00, 0, 0, 0.00)
    ),
    2L -> ListBuffer(
      LinkBiller(Category.electricity, "ElectricityBiller", 2L, currentDate, 0.00, 0, 0, 0.00),
      LinkBiller(Category.food, "FoodBiller", 2L, currentDate, 0.00, 0, 0, 0.00)
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

}