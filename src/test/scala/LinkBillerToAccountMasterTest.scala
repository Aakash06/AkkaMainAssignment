import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterEach, FunSuite, FunSuiteLike}
import org.scalatest.mockito.MockitoSugar

class LinkBillerToAccountMasterTest extends TestKit(ActorSystem("CustomerAccountSystem")) with FunSuiteLike
    with BeforeAndAfterEach with ImplicitSender with MockitoSugar {

  val accountDatabaseServices = new AccountDatabaseServices

  protected def afterAll(): Unit = {
      system.terminate()
    }

  val AccountGeneratorActorRef : ActorRef = system.actorOf(AccountGeneratorActorMaster.props(accountDatabaseServices))


}
