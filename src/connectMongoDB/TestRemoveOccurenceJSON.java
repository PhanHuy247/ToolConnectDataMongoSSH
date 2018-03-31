/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connectMongoDB;

import com.jcraft.jsch.IdentityRepository;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;
import java.io.File;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.parser.ParseException;

/**
 *
 * @author phanhuy
 */
public class TestRemoveOccurenceJSON {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParseException, SQLException, JSchException, UnknownHostException {
        String path = "/home/phanhuy/Documents/sshKey";
        List<String> listIp = new ArrayList<>();
        listIp.add("123213");
//        String hostname = "10.64.100.22";
//        String hostname = "10.64.100.18";
//        String hostname = "202.221.140.134";
        String hostname = "160.13.90.96";
//        String hostname = "10.64.100.99";
//        String hostname = "202.32.203.185";
        String login = "root";
//        String password = "gvn123456";
//        String password = "gvn12345sv";
        String password = "itsfsi";
        Mongo mongo;
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");

        JSch ssh = new JSch();
//        ssh.addIdentity(path);
        Session session = ssh.getSession(login, hostname, 22);
        session.setConfig(config);
        session.setPassword(password);
//        session.setIdentityRepository(null);
        session.connect();
//        session.setPortForwardingL(27017, "10.64.100.26", 27017);
        session.setPortForwardingL(27017, "localhost", 27017);
//        session.setPortForwardingL("localhost", 27017, hostname, 22);
        ServerAddress sa = new ServerAddress("localhost", 27017);
//        ServerAddress sa = new ServerAddress("10.64.100.26", 27017);
        MongoOptions mo = new MongoOptions();
        mo.setConnectionsPerHost(1500);
        mongo = new Mongo(sa,mo);
        System.out.println(session.isConnected());
        DB dbIp = mongo.getDB("logdb");
        DB userDB = mongo.getDB("userdb");
        DBCollection userColl = userDB.getCollection("user");
        DBCollection dbColl = dbIp.getCollection("log_login");
        DBCursor cursor = dbColl.find();
        while(cursor.hasNext()){
            BasicDBObject objectIp = (BasicDBObject)cursor.next();
            String ip = (String)objectIp.getString("ip");
            System.out.println(ip);
            DBCursor cursorIP = dbColl.find(new BasicDBObject("ip", ip));
            if(cursorIP == null) continue;
            Integer count = cursorIP.count();
//            System.out.println(count);
            if(count >= 20  && !listIp.contains(ip)){
                
                listIp.add(ip);
                while(cursorIP.hasNext()){
                    BasicDBObject cursorIp = (BasicDBObject)cursorIP.next();
                    String userId = (String)cursorIp.getString("user_id");
                    BasicDBObject objectUserId = new BasicDBObject("user_id", userId);
                    DBCursor find = (DBCursor)userColl.find(objectUserId);
                    if(find != null && find.count() > 0){
                         System.out.println(ip);
                            System.out.println(userId);
                    }
//                    BasicDBObject updateFlag = new BasicDBObject("flag", 1);
//                    userColl.update(objectUserId,updateFlag);
                }
            }
        }
        
        session.disconnect();
    }
}
