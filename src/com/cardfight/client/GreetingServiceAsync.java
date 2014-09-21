package com.cardfight.client;

import com.cardfight.client.poker.event.VFEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
  void greetServer(String tableID, String userid, String queueID, int msgID, ArrayList<VFEvent> send, AsyncCallback<ArrayList<VFEvent>> callback);
  //void  foobar(VFInitTable v1, AsyncCallback<String> callback);
}
