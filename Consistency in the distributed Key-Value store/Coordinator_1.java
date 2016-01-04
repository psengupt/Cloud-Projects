import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TimeZone;
import java.util.Iterator;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.sql.Timestamp;

import javax.xml.crypto.dsig.keyinfo.KeyValue;

import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

public class Coordinator extends Verticle {

	// Default mode: Strongly consistent. Possible values are "strong" and
	// "causal"
	private static String consistencyType = "strong";

	/**
	 * TODO: Set the values of the following variables to the DNS names of your
	 * three dataCenter instances
	 */
	private static final String dataCenter1 = "ec2-52-4-253-153.compute-1.amazonaws.com";
	private static final String dataCenter2 = "ec2-52-4-138-151.compute-1.amazonaws.com";
	private static final String dataCenter3 = "ec2-52-1-33-250.compute-1.amazonaws.com";
	static ConcurrentHashMap<String, Queue> hMap = new ConcurrentHashMap<String, Queue>();

	@Override
	public void start() {
		// DO NOT MODIFY THIS
		KeyValueLib.dataCenters.put(dataCenter1, 1);
		KeyValueLib.dataCenters.put(dataCenter2, 2);
		KeyValueLib.dataCenters.put(dataCenter3, 3);
		final RouteMatcher routeMatcher = new RouteMatcher();
		final HttpServer server = vertx.createHttpServer();
		server.setAcceptBacklog(32767);
		server.setUsePooledBuffers(true);
		server.setReceiveBufferSize(4 * 1024);

		routeMatcher.get("/put", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				try {
					MultiMap map = req.params();
					final String key = map.get("key");
					final String value = map.get("value");
					// You may use the following timestamp for ordering requests
					final String timestamp = new Timestamp(System
							.currentTimeMillis()
							+ TimeZone.getTimeZone("EST").getRawOffset())
							.toString();
					String valueHash = timestamp;
					System.out.println(valueHash);
					System.out.println(key + ":key");
					Queue<String> cString = new LinkedBlockingQueue<String>();
					cString.add(valueHash);

					if (!hMap.containsKey(key)) {
						hMap.put(key, cString);
					}else{
						LinkedBlockingQueue<String> temp = (LinkedBlockingQueue<String>) hMap.get(key);
						temp.add(valueHash);
						hMap.put(key, temp);
					}
					Thread t = new Thread(new Runnable() {
						public void run() {
							{
								try {
									synchronized (hMap.get(key)) {
										/*LinkedBlockingQueue q = (LinkedBlockingQueue<String>) hMap
												.get(key);
										String head = (String) q.peek();*/
										// String[] parts = head.split(" ");
										while (!(hMap.get(key).peek().equals(timestamp))) {
											try {
												System.out.println("wait:put");
												hMap.get(key).wait();

											} catch (Exception e) {
												e.printStackTrace();
											}
										}

										hMap.get(key).poll();
										try {
											KeyValueLib.PUT(dataCenter1, key,
													value);

											System.out.println("inDataCenter1");
											KeyValueLib.PUT(dataCenter2, key,
													value);
											KeyValueLib.PUT(dataCenter3, key,
													value);
											hMap.get(key).notifyAll();
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}

							}
							// TODO: Write code for PUT operation here.
						}
					});
					t.start();
					req.response().end();// Do not remove this
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		routeMatcher.get("/get", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				try {
					MultiMap map = req.params();
					final String key = map.get("key");
					final String loc = map.get("loc");
					// You may use the following timestamp for ordering requests
					final String timestamp = new Timestamp(System
							.currentTimeMillis()
							+ TimeZone.getTimeZone("EST").getRawOffset())
							.toString();
					Thread t = new Thread(new Runnable() {
						public void run() {
							// TODO: Write code for GET operation here.
							// Each GET operation is handled in a different
							// thread.
							// Highly recommended that you make use of helper
							// functions.
							String valueHash = timestamp;
							String s = "0";
							if (!hMap.containsKey(key)) {
								LinkedBlockingQueue<String> q = new LinkedBlockingQueue<>();
								q.add(valueHash);
								hMap.put(key, q);
							} else {
								LinkedBlockingQueue q = (LinkedBlockingQueue<String>) hMap
										.get(key);
								q.add(valueHash);
								hMap.put(key, q);
							}
							try {
								synchronized (hMap.get(key)) {
									/*LinkedBlockingQueue q = (LinkedBlockingQueue<String>) hMap
											.get(key);*/
									// String head = (String)q.peek();
									// String head = (String) q.poll();
									// String[] parts = head.split(" ");
									// System.out.println(parts[1]);
									while (!(hMap.get(key).peek().equals(timestamp))) {
										try {
											System.out.println("GET:wait");
											hMap.get(key).wait();
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
									System.out.println("out of get:wait");
									hMap.get(key).poll();
									try {
										if (loc.equals("1")) {
											s = KeyValueLib.GET(dataCenter1,
													key);
										} else if (loc.equals("2")) {
											s = KeyValueLib.GET(dataCenter2,
													key);
										} else if (loc.equals("3")) {
											s = KeyValueLib.GET(dataCenter3,
													key);
										} else {
										}
										hMap.get(key).notifyAll();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								req.response().end(s); // Default response = 0
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					t.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		routeMatcher.get("/consistency", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				consistencyType = map.get("consistency");
				// This endpoint will be used by the auto-grader to set the
				// consistency type that your key-value store has to support.
				// You can initialize/re-initialize the required data structures
				// here
				req.response().end();
			}
		});

		routeMatcher.noMatch(new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				req.response().putHeader("Content-Type", "text/html");
				String response = "Not found.";
				req.response().putHeader("Content-Length",
						String.valueOf(response.length()));
				req.response().end(response);
				req.response().close();
			}
		});
		server.requestHandler(routeMatcher);
		server.listen(8080);
	}
}

