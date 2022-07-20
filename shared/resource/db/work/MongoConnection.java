//package com.mynet.shared.resource.db.work;
//
//import com.mongodb.DB;
//import com.mongodb.DBCollection;
//import com.mongodb.Mongo;
//import com.mongodb.MongoException;
//
//public class MongoConnection {
//    private static Mongo m = null;
//    private static DB db = null;
//    private static DBCollection coll;
//
//    private MongoConnection() {}
//
//    public static void connect(String username, String password, String hostname, String database, int port) throws Exception {
//        try {
//            m = new Mongo(hostname, port);
//            m.getMongoOptions().wtimeout=20;
//            m.getMongoOptions().socketKeepAlive=false;
//            m.getMongoOptions().threadsAllowedToBlockForConnectionMultiplier=3;
//
//
//            db = m.getDB(database);  // Try to authenticate.
//            coll = db.getCollection("onlineusers2");
//
////            if(!db.authenticate(username, password.toCharArray()))
////            {
////                throw new Exception("Could not authenticate to database '" + database + "' with user '" + username + "'.");
////            }
//        } catch (MongoException.Network e) {
//            throw new Exception("Could not connect to Mongo DB.");
//        }
//    }
//
//    public static DB getDatabase() {
//        return db;
//    }
//
//    public static DBCollection getColl() {
//        return coll;
//    }
//}
