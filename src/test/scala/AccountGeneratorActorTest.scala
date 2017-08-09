import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, FunSuiteLike}
import org.mockito.Mockito._


class AccountGeneratorActorTest extends TestKit(ActorSystem("CustomerAccountSystem")) with FunSuiteLike
  with BeforeAndAfterEach with ImplicitSender with MockitoSugar {

  val accountDatabaseServices = mock[AccountDatabaseServices]
  val ac = system.actorOf(AccountGeneratorActorMaster.props(accountDatabaseServices))

  test("valid account"){
    val customerAccount = CustomerAccount(4L, "Akansha Sharma", "Noida", "Akansha05", 10.00)
    doNothing().when(accountDatabaseServices).addAccount(customerAccount.username,customerAccount)
    ac ! List("Akansha", "Noida", "Akansha05", "10.00")

    expectMsgPF(){
      case (username: String, resultMsg: String) =>
        assert(username == "Akansha05" &&
          resultMsg == "created successfully")
      }
    }

  test("username already exists"){
    val customerAccount = CustomerAccount(4L, "Akansha Sharma", "Noida", "Akansha05", 10.00)
    when(accountDatabaseServices.checkUserName("Akansha05"))thenReturn true
    ac ! List("Akansha", "Noida", "Akansha05", "10.00")

    expectMsgPF(){
      case (username: String, resultMsg: String) =>
        assert(username == "Akansha05" &&
          resultMsg == "username already exist")
    }
  }

  protected def afterAll(): Unit = {
    system.terminate()
  }

}
