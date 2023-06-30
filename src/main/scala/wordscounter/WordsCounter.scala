package wordscounter

import loci.language._
import loci.language.transmitter.rescala._
import loci.communicator.tcp._
import loci.serializer.upickle._
import rescala.default._
import scala.language.implicitConversions
import loci.transmitter.IdenticallyTransmittable
import upickle.default._

@multitier object WordsCounter {
  @peer type Node
  @peer type DocDiscoverer <: Node { type Tie <: Single[Buffer] }
  @peer type Buffer <: Node { type Tie <: Single[DocDiscoverer] with Single[Counter] }
  @peer type Counter <: Node { type Tie <: Single[Buffer] }

  val newFile: Evt[String] on DocDiscoverer =  { Evt[String]() }

  val buffer: Signal[List[String]] on Buffer = {
    newFile.asLocal.fold(List[String]()){ (list, file) => list :+ file }
  }

  def discover(files: List[String]): Unit on DocDiscoverer = {
      files.foreach { file =>
        newFile.fire(file)
      }
    }

  def counter(): Unit on Counter = {
    buffer.asLocal.observe { list =>
      println(list)
      val words = list.flatMap(_.split(" "))
      println(words.size)
    }
  }

  def main(): Unit on Node = on[DocDiscoverer] {
    discover(List("A very long text", "A long text", "A short text", "A not so short text"))
  } and on[Counter] {
    counter()
  }
}

object Counter extends App {
  multitier start new Instance[WordsCounter.Counter](
    connect[WordsCounter.Buffer] { TCP(43053).firstConnection })
}

object Buffer extends App {
  multitier start new Instance[WordsCounter.Buffer](
    connect[WordsCounter.DocDiscoverer] { TCP(43054).firstConnection } and
      connect[WordsCounter.Counter] { TCP("localhost", 43053) } )
}

object DocDiscoverer extends App {
  multitier start new Instance[WordsCounter.DocDiscoverer](
    connect[WordsCounter.Buffer] { TCP("localhost", 43054) })
}


