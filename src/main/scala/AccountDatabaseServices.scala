import org.apache.log4j.Logger

class AccountDatabaseServices extends AccountDatabase{

  def checkUserName(username:String):Boolean ={
    val logger = Logger.getLogger(this.getClass)

    logger.info("Checking username in Account Database")
    if(getUserDatabase.contains(username)) true else false
  }

  def addAccount(username: String, customerAccount: CustomerAccount): Unit = {
    addCustomerAccount(username, customerAccount)

  }

  def addLink(accountNo: Long, billerName: String, billerCategory: Category.Value):Unit ={

    linkBiller(accountNo,billerName,billerCategory)
  }

}
