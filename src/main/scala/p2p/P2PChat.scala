package p2p

import loci.language._
import rescala.default._
import loci.language.transmitter.rescala._
import loci.communicator.tcp._
import loci.serializer.upickle._

@multitier object P2PChat {
  @peer type Node <: { type Tie <: Multiple[Node] }

    val message = on[Node] { Evt[String]() }

    val messageSent = on[Node] sbj { node: Remote[Node] =>
      message.asLocalFromAllSeq collect {
        case (remote, message) if remote != node => message
      }
    }

    def main() = on[Node] {
      // Event[Remote[Node], String] on Node
      messageSent.asLocalFromAllSeq map { case (r, m) => m } observe println

      // Signal[Map[Remote[Node], Event[String]] on Node
//      messageSent.asLocalFromAll.observe(m => m.foreach { case (remote, event) => event observe println })

      for (line <- scala.io.Source.stdin.getLines()) {
        message.fire(line)
      }
    }
}

object A extends App {
  multitier start new Instance[P2PChat.Node](
    listen[P2PChat.Node] {
      TCP(43053)
    })
}

object B extends App {
  multitier start new Instance[P2PChat.Node](
    listen[P2PChat.Node] {
      TCP(43054)
    } and
      connect[P2PChat.Node] {
      TCP("localhost", 43053)
    })
}

object C extends App {
  multitier start new Instance[P2PChat.Node](
    connect[P2PChat.Node] {
      TCP("localhost", 43053)
    } and connect[P2PChat.Node] {
      TCP("localhost", 43054)
    })
}
