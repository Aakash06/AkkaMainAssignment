import java.text.SimpleDateFormat
import java.util.Calendar


case class LinkBiller( billerCategory: Category.Value, billerName: String, CustomerAccountNumber: Long,
                         transactionDate: String, amount: Double, totalIterations: Int,
                         executedIterations: Int, paidAmount: Double)

object LinkBiller {

  def apply(CustomerAccountNo: Long, billerName: String, billerCategory: Category.Value): LinkBiller = {

    val dateFormat = new SimpleDateFormat("d-M-y")
    val currentDate = dateFormat.format(Calendar.getInstance().getTime())

    LinkBiller(billerCategory, billerName, CustomerAccountNo, currentDate, 0.00, 0, 0, 0.00)

  }

}
