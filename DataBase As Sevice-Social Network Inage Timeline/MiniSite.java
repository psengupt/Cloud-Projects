package cc.cmu.edu.minisite;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.*;
import java.util.Set;
import java.lang.Integer;
import io.undertow.io.Sender;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.io.IoCallback;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

// hbase api import
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;

// dynamodb api import
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.codehaus.jackson.map.ObjectMapper;
import java.util.Map;
import java.util.HashMap;

public class MiniSite {

    public MiniSite() throws Exception{

    }


    public static void main(String[] args) throws Exception{
        final MiniSite minisite = new MiniSite();
        final ObjectMapper mapper = new ObjectMapper();
        final String url = "jdbc:mysql://project3-3.ctskwjqyfx6f.us-east-1.rds.amazonaws.com:3306/test";
        final String userName = "Project33";
        final String password = "Project33";
        Undertow.builder()
        .addHttpListener(8080, "0.0.0.0")
        .setHandler(new HttpHandler() {

            public void handleRequest(final HttpServerExchange exchange) throws Exception {
         //       try {
                    Connection connection = null;
                connection = DriverManager.getConnection(url, userName, password);
                String query = exchange.getQueryString();
                //Set<String> keys = map.keySet();
                  String[] split = query.split("&");
                  String id = split[1].split("=")[1];
                  String password = split[2].split("=")[1];
                    //SQL statement                    
                    PreparedStatement statemnt;// = connection.createStatement();
                    ResultSet rs;
                    statemnt = connection.prepareStatement("SELECT Name FROM user WHERE user_id = "+id+" and Password = '"+password+"'");
                    String ans = "Unauthorised";
                    rs = statemnt.executeQuery();
                    int flag = 0;
                    while(rs.next())
                    {
                         ans = rs.getString(1);
                        flag++;
                    }
                    
//                  System.out.println(" The id and the passowrd is" +id+" "+password);
                 
               
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json; encoding=UTF-8");
                Sender sender = exchange.getResponseSender();
                JSONObject response = new JSONObject();
                 response.put("name", ans);
//                response.put("password", "b");

                String content = "returnRes("+mapper.writeValueAsString(response)+")";
                sender.send(content);
            }
        }).build().start();

    }
}
/*catch(Exeception e)
{

    e.printStack();
}*/

