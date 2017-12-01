package sp.domain.logic

import sp.domain._

object StructLogic extends StructLogics

trait StructLogics {

  implicit class StructExtras(x: Struct) {
    def getChildren(nodeID: ID): Set[StructNode] = {
      x.items.filter(_.parent.contains(nodeID))
    }

    def getChildrenMap: Map[ID, Set[StructNode]] = {
      x.items.foldLeft(Map[ID, Set[StructNode]]()) { (a, b) =>
        val parentChildren = b.parent.map(p => a.getOrElse(p, Set()) + b)
        parentChildren.map(ch => a + (b.parent.get -> ch)).getOrElse(a)
      }
    }

    def getAllChildren(nodeID: ID): Set[StructNode] = {
      def rec(currentNodeID: ID, aggr: Set[StructNode]): Set[StructNode] = {
        val directChildren = getChildren(currentNodeID)
        if (directChildren.isEmpty) aggr
        else directChildren.flatMap(c => rec(c.nodeID, aggr ++ directChildren))
      }
      rec(nodeID, Set())
    }

    /*
    def getNodeAndChildren(nodeID: ID): Set[StructNode] = {
      x.items.find(_.nodeID == nodeID).map(n => getAllChildren(nodeID) + n).getOrElse(Set())
    }
    */

    // TODO completely untested
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

    def --(nodeIDs: Set[ID]) = {
      val filtered = x.items.filterNot(n => nodeIDs.contains(n.nodeID))
      x.copy(items = filtered)
    }


    def hasLoop = {
      def req(currentNode: StructNode, aggr: Set[ID]): Boolean = {
        currentNode.parent match {
          case None => true
          case Some(n) if aggr.contains(n) => false
          case Some(n) =>
            val p = x.nodeMap(n)
            req(p, aggr + currentNode.nodeID)
        }
      }
      !x.items.forall(s => req(s, Set()))
    }

    def +(node: StructNode) = {
      x.copy(items = x.items + node)
    }

    def ++(xs: Set[StructNode]) = {
      // map needed to get the parents right
      val oldToNewID = xs.foldLeft(Map[ID, ID]())((m, n) => m + (n.nodeID -> ID.newID))
      val newXs = xs.map(n => n.copy(parent = n.parent.map(oldToNewID), nodeID = oldToNewID(n.nodeID)))
      x.copy(items = x.items ++ newXs)
    }

    def addTo(parentNodeID: ID, xs: Set[StructNode]) = {
      // map needed to get the parents right
      val oldToNewID = xs.foldLeft(Map[ID, ID]())((m, n) => m + (n.nodeID -> ID.newID))
      val newXs = xs.map { n =>
        if (n.parent.isEmpty) n.copy(parent = Some(parentNodeID), nodeID = oldToNewID(n.nodeID))
        else n.copy(parent = Some(oldToNewID(n.parent.get)), nodeID = oldToNewID(n.nodeID))
      }
      x.copy(items = x.items ++ newXs)
    }

    def moveNode(movedNodeID: ID, receivingNodeID: ID) = {
      val movedNode = x.items.find(_.nodeID == movedNodeID)
      val receivingNode = x.items.find(_.nodeID == receivingNodeID)
      val maybeNewStruct = movedNode.zip(receivingNode).headOption.flatMap { case (mov, rec) =>
        val nodesToMove = getAllChildren(mov.nodeID) + mov.copy(parent = None)
        val newStruct = (x -- nodesToMove.map(_.nodeID)).addTo(rec.nodeID, nodesToMove)
        if (newStruct.hasLoop) None else Some(newStruct)
      }
      maybeNewStruct.getOrElse(x)
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



