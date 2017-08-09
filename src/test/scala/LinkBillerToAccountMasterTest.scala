import java.text.SimpleDateFormat
import java.util.Calendar

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import org.mockito.Mockito.{doNothing, when}
import org.scalatest.{BeforeAndAfterEach, FunSuite, FunSuiteLike}
import org.scalatest.mockito.MockitoSugar

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class LinkBillerToAccountMasterTest extends TestKit(ActorSystem("CustomerAccountSystem")) with FunSuiteLike
    with BeforeAndAfterEach with ImplicitSender with MockitoSugar {

  val accountDatabaseServices = mock[AccountDatabaseServices]
  val ac = new AccountDatabaseServices
  protected def afterAll(): Unit = {
      system.terminate()
    }

  val LinkBillerToAccountRef : ActorRef = system.actorOf(LinkBillerToAccountMaster.props(accountDatabaseServices))


  test("Add link"){
    val dateFormat = new SimpleDateFormat("d-M-y")
    val currentDate = dateFormat.format(Calendar.getInstance().getTime())
    when(accountDatabaseServices.getLinkedBiller).thenReturn(mutable.Map(1L -> ListBuffer(
      LinkBiller(Category.car, "PhoneBiller", 1L, currentDate, 1000.00, 0, 0, 0.00),
      LinkBiller(Category.internet, "InternetBiller", 1L, currentDate, 500.00, 0, 0, 0.00)
    )))

    doNothing().when(accountDatabaseServices).addLink(4L,"PhoneBiller",Category.phone)

    LinkBillerToAccountRef ! (4L,"PhoneBiller",Category.phone)

    expectMsgPF(){
      case (resultMsg: String) =>
        assert(resultMsg == "Successfully Linked your account with the given biller!")
    }
  }


  test("Already link"){
    val dateFormat = new SimpleDateFormat("d-M-y")
    val currentDate = dateFormat.format(Calendar.getInstance().getTime())
    when(accountDatabaseServices.getLinkedBiller).thenReturn(mutable.Map(1L -> ListBuffer(
      LinkBiller(Category.phone, "PhoneBiller", 1L, currentDate, 1000.00, 0, 0, 0.00),
      LinkBiller(Category.internet, "InternetBiller", 1L, currentDate, 500.00, 0, 0, 0.00)
    )))

    doNothing().when(accountDatabaseServices).addLink(1L,"PhoneBiller",Category.phone)
    LinkBillerToAccountRef ! (1L,"PhoneBiller",Category.phone)

    expectMsgPF(){
      case (resultMsg: String) =>
        assert(resultMsg == "You are already linked to the given biller!")
    }
  }


}
