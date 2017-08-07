import akka.actor.{Actor, ActorLogging, Props, Terminated}
import akka.dispatch.{BoundedMessageQueueSemantics, RequiresMessageQueue}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}

class LinkBillerToAccountMaster(accountDatabaseServices: AccountDatabaseServices) extends Actor with ActorLogging
  with RequiresMessageQueue[BoundedMessageQueueSemantics]{

  var accountNo= 0

  var router: Router = {
    log.info("Creating user Account ")
    val routees = Vector.fill(5) {
      val r = context.actorOf(LinkBillerToAccount.props(accountDatabaseServices))
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive:Receive = {
    case (accountNo: Long, billerName: String, billerCategory: Category.Value) =>
      log.info("Forwarding to databaseServiceActor for linking")
      router.route((accountNo, billerName, billerCategory), sender())


    case Terminated(a) =>
      log.info("Terminating")
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[LinkBillerToAccount])
      context watch r
      router = router.addRoutee(r)

    case _ => log.info("Invalid information received")
      sender() ! "Invalid information received while linking!"

  }
}

class LinkBillerToAccount(accountDatabaseServices: AccountDatabaseServices) extends Actor with ActorLogging {

  override def receive: Receive = {
    case (accountNo: Long, billerName: String, billerCategory: Category.Value) =>
      val listOfBillers = accountDatabaseServices.getLinkedBiller.getOrElse(accountNo , Nil)
      if(listOfBillers.exists(_.billerCategory == billerCategory)) {
        accountDatabaseServices.addLink(accountNo, billerName, billerCategory)
        sender() ! "Successfully Linked your account with the given biller!"
      }
      else
      {
        sender() ! "You are already linked to the given biller!"
      }
    case _=> log.error("Error while linking your account")
      sender()! "Error while linking account"
  }

}

object LinkBillerToAccountMaster {

  def props(accountDatabaseServices: AccountDatabaseServices): Props = Props(classOf[LinkBillerToAccountMaster], accountDatabaseServices)

}

object LinkBillerToAccount {

  def props(accountDatabaseServices: AccountDatabaseServices): Props = Props(classOf[LinkBillerToAccount], accountDatabaseServices)

}
