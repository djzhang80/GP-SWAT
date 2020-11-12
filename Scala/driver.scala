:require E:\projects\p17\lib\commons-io-2.0.jar
:require E:\projects\p17\lib\graphswat.jar
import scala.reflect.ClassTag
import org.apache.spark._
import org.apache.spark.rdd.RDD
import org.apache.spark.graphx._
import cn.edu.xmut.model._

def parseSubbasin(str: String): (VertexId, (Int, Int)) = {
  val token = str.split("\\s+")
  (token(0).toLong, (token(1).toInt, 0))
}

def parseStream(str: String): Edge[Int] = {
  val token = str.split("\\s+")
  Edge(token(0).toLong, token(1).Int)
}

val subbasins: RDD[(VertexId, (Int, Int))] = sc.textFile("E:/projects/p17/subbasin.txt").map(parseSubbasin(_));

val streams: RDD[Edge[Int]] = sc.textFile("E:/projects/p17/stream.txt").map(parseStream(_));

val graph = Graph(subbasins, streams).partitionBy(PartitionStrategy.RandomVertexCut,4)

graph.pregel(0, Int.MaxValue, EdgeDirection.Out)(
  (vid, usc, msg) => {
    println("in vprog")
    if (usc._1 == (msg + usc._2)) {
      Model.call(vid)
      //println("in vprog:"+vid)
    }
    var rs = msg + usc._2
    (usc._1, rs)
  },
  triplet => {
    if (triplet.srcAttr._1 == triplet.srcAttr._2) {
      //println("in sendmsg1: " + triplet.srcId + " " + triplet.dstId + " " + 1)
      Iterator((triplet.dstId, 1))
    } else {
      //println("in sendmsg2: " + triplet.srcId + " " + triplet.dstId + " " + 0)
      Iterator((triplet.dstId, 0))
    }

  },
  (msg1, msg2) => {
    println("in merge: " + msg1 + " " + msg2 + " result:" + (msg1 + msg2))
    msg1 + msg2
  }
)