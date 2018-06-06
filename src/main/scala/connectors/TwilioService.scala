package connectors

import com.example.ServiceActor.ActionPerformed
import com.twilio.Twilio
import com.twilio.`type`.PhoneNumber
import com.twilio.rest.api.v2010.account.Message
import com.typesafe.config.ConfigFactory

object TwilioService {

  lazy val config = ConfigFactory.load("application.conf")

  lazy val ACCOUNT_SID = config.getString("twilio.sid")
  lazy val AUTH_TOKEN = config.getString("twilio.token")

  Twilio.init(ACCOUNT_SID, AUTH_TOKEN)

  def sendMessage(messageReq: String): ActionPerformed = {

    val from = new PhoneNumber(config.getString("twilio.number"))
    val to = new PhoneNumber(config.getString("twilio.receiver"))
    val body = messageReq

    try {
      val message = Message.creator(to, from, body).create()
      ActionPerformed(s"Message sent ${message.getSid}")
    } catch {
      case e =>
        e.getStackTrace
        ActionPerformed("its broke")
    }
  }
}
