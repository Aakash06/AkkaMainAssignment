case class CustomerAccount(accountNo: Long, customerName: String, address: String, username: String, initialAmount: Double)

object CustomerAccount {

  def apply(CustomerInfo: List[String]): CustomerAccount = {

    val customerName = 1
    val address = 2
    val username =3
    val initialAmount = 4


    new CustomerAccount(CustomerInfo.head.toLong,CustomerInfo(customerName),CustomerInfo(address),CustomerInfo(username),
                        CustomerInfo(initialAmount).toDouble)
  }
}
