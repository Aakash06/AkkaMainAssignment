import akka.actor.{Actor, ActorLogging, Props, Terminated}
import akka.dispatch.{BoundedMessageQueueSemantics, RequiresMessageQueue}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}

class AccountGeneratorActorMaster(accountDatabaseServices: AccountDatabaseServices) extends Actor with ActorLogging
  with RequiresMessageQueue[BoundedMessageQueueSemantics]{

  var accountNo= 2

  var router: Router = {
      log.info("Creating user Account ")
    val routees = Vector.fill(5) {
      val r = context.actorOf(AccountGeneratorActor.props(accountDatabaseServices))
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive:Receive = {
    case customerAccount: List[_] => customerAccount.head match {
      case userName : String => log.info("Checking Is username already exist or not")
        if(!accountDatabaseServices.checkUserName(customerAccount(2).toString)){
          log.info(s"This $userName available")
          val customerInfo = (accountNo + 1).toString :: customerAccount
            router.route(customerInfo, sender())
        }
        else {
          log.error("This Username already exist try another one")
          sender() ! (customerAccount(2),"username already exist")
        }
      case _ => log.error("Invalid List")
    }

    case Terminated(a) =>
      log.info("Terminating")
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[AccountGeneratorActor])
      context watch r
      router = router.addRoutee(r)
  }
}

class AccountGeneratorActor(accountDatabaseServices: AccountDatabaseServices) extends Actor with ActorLogging {

  override def receive: Receive = {
    case listInfo : List[String] =>
      log.info("creating your account")
      val customerAccount = CustomerAccount(listInfo)
      accountDatabaseServices.addAccount(customerAccount.username,customerAccount)
      sender() ! (customerAccount.username, "created successfully")
    case _=> log.error("Error while creating your account")
      sender()! "Error while creating account"
  }

}

object AccountGeneratorActorMaster {

  def props(accountDatabaseServices: AccountDatabaseServices): Props = Props(classOf[AccountGeneratorActorMaster], accountDatabaseServices)

}

object AccountGeneratorActor {

  def props(accountDatabaseServices: AccountDatabaseServices): Props = Props(classOf[AccountGeneratorActor], accountDatabaseServices)

}
