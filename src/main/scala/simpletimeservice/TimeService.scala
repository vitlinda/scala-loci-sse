package simpletimeservice

import clientserver.interactive.chatsimple.Client
import loci.language._
import rescala.default._
import loci.language.transmitter.rescala._
import loci.communicator.tcp._
import loci.serializer.upickle._

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

@multitier object TimeService {
  @peer type Peer
  @peer type Server <: Peer { type Tie <: Multiple[Client] }
  @peer type Client <: Peer { type Tie <: Single[Server] }

  val time = on[Server] { Var(0L) }

  def main(): Unit on Peer =
    (on[Server] {
      while (true) {
        time.set(Calendar.getInstance.getTimeInMillis)
        Thread.sleep(1000)
      }
    }) and
      (on[Client] {
        val format = new SimpleDateFormat("h:m:s")

        val display = Signal { format.format(new Date(time.asLocal())) }

//        display.changed observe println
        display observe println
      })
}

  object Server extends App {
    multitier start new Instance[TimeService.Server](
      listen[TimeService.Client] {
        TCP(43053)
      })
  }

  object Client extends App {
    multitier start new Instance[TimeService.Client](
      connect[TimeService.Server] {
        TCP("localhost", 43053)
      })
  }
