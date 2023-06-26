package pingpong

import loci.language._
import rescala.default._
import loci.language.transmitter.rescala._
import loci.communicator.tcp._
import loci.serializer.upickle._


@multitier object PingPong {
  @peer type Node
  @peer type Pinger <: Node { type Tie <: Single[Ponger] }
  @peer type Ponger <: Node { type Tie <: Single[Pinger] }

  val ping: Evt[String] on Pinger = Evt[String]()
  val pong: Evt[String] on Ponger = Evt[String]()

  on[Ponger] {
    ping.asLocal observe {
      case "ping" => println("ping received"); pong.fire("pong")
      case _ => println("error"); multitier.terminate()
    }
  }

  on[Pinger] {
    pong.asLocal observe {
      case "pong" => println("pong received"); ping.fire("ping")
      case _ => println("error"); multitier.terminate()
    }
  }

  def pinger(): Unit on Pinger = {
    ping.fire("ping")
  }

  def ponger(): Unit on Ponger = { }

  def main(): Unit on Node = on[Pinger] {
    pinger()
  } and on[Ponger] {
    ponger()
  }
}

object Pinger extends App {
  multitier start new Instance[PingPong.Pinger](
    listen[PingPong.Ponger] { TCP(43053) })
}

object Ponger extends App {
  multitier start new Instance[PingPong.Ponger](
    connect[PingPong.Pinger] { TCP("localhost", 43053) })
}
