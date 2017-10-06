package sp.domain.logic

import sp.domain._

object StructLogic extends StructLogics

trait StructLogics {
  // TODO: need some more test before released

  implicit class StructExtras(x: Struct) {
    def getChildren(node: StructNode): List[StructNode] = {
      x.items.filter(_.parent.contains(node.nodeID)).toList
    }

    def getChildrenMap: Map[ID, List[StructNode]] = {
      x.items.foldLeft(Map[ID, List[StructNode]]()){(a, b) =>
        val parentChildren = b.parent.map(p => b :: a.getOrElse(p, List()))
        parentChildren.map(ch => a + (b.parent.get -> ch)).getOrElse(a)
      }
    }

    def getAllChildren(node: StructNode): List[StructNode] = {
      def rec(currentNode: StructNode, aggr: Set[StructNode]): Set[StructNode] = {
        if (aggr.contains(currentNode)) aggr
        else {
          val direct = getChildren(currentNode).toSet
          val updA = aggr ++ direct
          direct.flatMap(n => rec(n, updA))
        }
      }
      rec(node, Set()).toList
    }

    def getItemNodeMap: Map[ID, Set[StructNode]] = {
      x.items.foldLeft(Map[ID, Set[StructNode]]()) { (a, b) =>
        val m = a.getOrElse(b.item, Set()) + b
        a + (b.item -> m)
      }
    }

    def getRootNodes = x.items.filter(_.parent.isEmpty)

    def printNiceTree(xs: List[IDAble] = List()): Unit = {
      val chM = x.getChildrenMap
      val roots = x.getRootNodes
      val idM = xs.map(i => i.id -> i).toMap
      def getName(id: ID) = idM.get(id).map(_.name).getOrElse(id.toString)
      def diggerPrint(x: StructNode, pre: String): Unit = {
        println(pre + s"item:${getName(x.item)}, nodeID:${x.nodeID}")
        chM.getOrElse(x.nodeID, List()).foreach(ch => diggerPrint(ch, pre+"--"))
      }
      roots.foreach(diggerPrint(_, ""))

    }

    def removeItem(itemID: ID) = {
      val filtered = x.items.filter(_.item == itemID)
      x.copy(items = filtered)
    }

    def removeNode(nodeID: ID) = {
      val filtered = x.items.filter(_.nodeID == nodeID)
      x.copy(items = filtered)
    }

    def hasLoops = {
      def req(currentNode: StructNode, aggr: Set[ID]): Boolean = {
        currentNode.parent match {
          case None => true
          case Some(n) if aggr.contains(n) => false
          case Some(n) =>
            val p = x.nodeMap(n)
            req(p, aggr + currentNode.nodeID)
        }
      }
      x.items.forall(s => req(s, Set()))
    }

    def +(node: StructNode) = {
      x.copy(items = x.items + node)
    }

    def ++(xs: List[StructNode]) = {
      x.copy(items = x.items ++ xs)
    }

  }

  // Mutable class to simplify the DSL
  implicit class StructWrapper(x: IDAble) {
    val id = x.id
    var ch: List[StructWrapper] = List()
    def children(xs: StructWrapper*) = {
      ch = xs.toList
      this
    }
  }

  def makeStructNodes(xs: StructWrapper*) = {
    def digger(xs: List[StructWrapper], parent: Option[ID]): Set[StructNode] = {
      xs match {
        case Nil => Set()
        case x :: xs =>
          val n = StructNode(x.id, parent)
          val ch = digger(x.ch, Some(n.nodeID))
          val rest = digger(xs, parent)
          ch ++ rest + n
      }
    }
    digger(xs.toList, None)
  }


}



