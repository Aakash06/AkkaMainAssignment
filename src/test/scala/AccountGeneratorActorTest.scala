import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, FunSuiteLike}

class AccountGeneratorActorTest extends TestKit(ActorSystem("CustomerAccountSystem")) with FunSuiteLike
  with BeforeAndAfterEach with ImplicitSender with MockitoSugar {


  val accountDatabaseServices = new AccountDatabaseServices
  protected def afterAll(): Unit = {
    system.terminate()
  }

  val AccountGeneratorActorRef : ActorRef = system.actorOf(AccountGeneratorActorMaster.props(accountDatabaseServices))

  test("Testing AccountGeneratorActor which should return map containing status message for each account") {

   // val customerAccount = CustomerAccount(1L, "Akansha", "Noida", "Akansha", 0.00)

    AccountGeneratorActorRef ! List("Akansha", "Noida", "Akansha", "10.00")

    expectMsgPF() {
      case (username: String, resultMsg: String) =>
        assert(username == "Akansha" &&
          resultMsg == "created successfully")
    }
  }

  test("Testing AccountGeneratorActor for Already existing username") {

    // val customerAccount = CustomerAccount(1L, "Akansha", "Noida", "Akansha", 0.00)

    AccountGeneratorActorRef ! List("Akansha", "Noida", "Akansha", "10.00")

    expectMsgPF() {
      case (username: String, resultMsg: String) =>
        assert(username == "Akansha" &&
          resultMsg == "username already exist")
    }
  }
}
