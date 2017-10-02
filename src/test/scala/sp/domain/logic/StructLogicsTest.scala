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
    o1    ,
      o2  ,
      o3  ,
      o4  ,
      t1  ,
      t2  ,
      t3  ,
      t4  ,
      sop1,
      sop2
  )






  "Methods tests" - {
    "making a struct using the DSL" in {
      val aStruct = makeStructNodes(
        t1.children(o1, o2.children(sop1)),
        t2.children(t3)
      )
      val s = Struct("hej", aStruct)
      s.printNiceTree(list)
    }


  }
}


