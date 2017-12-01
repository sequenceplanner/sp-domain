package sp.domain.logic

import org.scalatest._

import sp.domain.logic.StructLogic._

/**
  * Created by kristofer on 2017-03-27.
  */
class StructLogicsTest extends FreeSpec {
  import sp.domain._

  val o1 = Operation("o1")
  val o2 = Operation("o2")
  val o3 = Operation("o3")
  val o4 = Operation("o4")
  val t1 = Thing("t1")
  val t2 = Thing("t2")
  val t3 = Thing("t3")
  val t4 = Thing("t4")
  val sop1 = SOPSpec("sop1", List(Sequence(List(SOP(o1), SOP(o2), SOP(o3)))))
  val sop2 = SOPSpec("sop2", List(Parallel(List(SOP(o1), SOP(o2), SOP(o3)))))
  val list = List(
    o1,o2,o3,o4,t1,t2,t3,t4,sop1,sop2
  )

  val sn1 = StructNode(o1.id)
  val sn2 = StructNode(o2.id)
  val sn3 = StructNode(o3.id)

  val snTest = Struct("test", Set(sn1, sn2.copy(parent = Some(sn1.nodeID)), sn3))

  "Methods tests" - {

    "making a struct using the DSL" in {
      val simpleStruct = Struct("test", makeStructNodes(
        o1.children(o2), o3
      ))
      val aStruct = makeStructNodes(
        t1.children(o1, o2.children(sop1)),
        t2.children(t3)
      )
      val s = Struct("hej", aStruct)
      s.printNiceTree(list)
    }

    val struct = Struct(
      "test",
      makeStructNodes(
        t1.children(
          o1,
          o2.children(
            sop1
          )
        ),
        t2.children(
          t3
        )
      )
    )
    val t1NodeID = struct.items.find(_.item == t1.id).get.nodeID
    val t2NodeID = struct.items.find(_.item == t2.id).get.nodeID
    val t3NodeID = struct.items.find(_.item == t3.id).get.nodeID
    val o2NodeID = struct.items.find(_.item == o2.id).get.nodeID
    val sop1NodeID = struct.items.find(_.item == sop1.id).get.nodeID

    "getChildren(t1-nodeID) should return nodes of o1 and o2" in {
      val allChildrenItemIDs = struct.getChildren(t1NodeID).map(_.item)
      assert(allChildrenItemIDs == Set(o1, o2).map(_.id))
    }
    "getAllChildren(t1-nodeID) should return nodes of o1, o2 and sop1" in {
      val allChildrenItemIDs = struct.getAllChildren(t1NodeID).map(_.item)
      assert(allChildrenItemIDs == Set(o1, o2, sop1).map(_.id))
    }

    "addTo(t2-nodeID, t4.children(sop2)) should make getAllChildren(t2-nodeID) return nodes of t3, t4 and sop2" in {
      val structAfterAdd = struct.addTo(t2NodeID, makeStructNodes(t4.children(sop2)))
      val allChildrenItemIDs = structAfterAdd.getAllChildren(t2NodeID).map(_.item)
      assert(allChildrenItemIDs == Set(t3, t4, sop2).map(_.id))
    }

    "hasLoops should return false, but return true if sop1-node is made parent of t1-node" in {
      val structWithLoop = struct.copy(
        items = struct.items.map { n =>
          if (n.nodeID == t1NodeID) n.copy(parent = Some(sop1NodeID)) else n
        }
      )
      assert(struct.hasLoop == false)
      assert(structWithLoop.hasLoop == true)
    }

    "after moveNode(o2-nodeID, t3-nodeID) t2-node should have t3, o2 and sop1, and t1-node just o1" in {
      val structAfterMove = struct.moveNode(o2NodeID, t3NodeID)
      val allChildrenItemIDsOft2 = structAfterMove.getAllChildren(t2NodeID).map(_.item)
      assert(allChildrenItemIDsOft2 == Set(t3, o2, sop1).map(_.id))
      val allChildrenItemIDsOft1 = structAfterMove.getAllChildren(t1NodeID).map(_.item)
      assert(allChildrenItemIDsOft1 == Set(o1.id))
    }

  }
}


