package maa.ljt.model

//import com.arangodb.ArangoDB
//import com.arangodb.entity.BaseDocument
//import com.arangodb.model.AqlQueryOptions
//import com.arangodb.model.StreamTransactionOptions
//import org.neo4j.driver.*
//
//fun testNeo() {
//  val drv = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "password"))
//
//  val q = Query("MATCH (u:User) RETURN u")
//
//
//  drv.session().readTransaction {
//    it.run(q)
//  }
//}
//
//fun araTest() {
//  val db = ArangoDB.Builder()
//    .build().db("lifeJournal")
//  val tx = db.beginStreamTransaction(StreamTransactionOptions())
//
//  val c = db.query("asd", AqlQueryOptions(), BaseDocument::class.java)
//
//
//
//}