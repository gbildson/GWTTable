package com.cardfight.client;

import com.cardfight.client.poker.event.VFEvent;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import java.util.ArrayList;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
  ArrayList<VFEvent> greetServer(String tableID, String userid, String queueID, int msgID, ArrayList<VFEvent> send);
  //String  foobar(VFInitTable v1);
}
