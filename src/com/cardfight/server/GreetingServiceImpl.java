package com.cardfight.server;

import com.cardfight.client.GreetingService;
import com.cardfight.client.poker.event.VFEvent;
import com.cardfight.client.poker.event.VFChatMsg;
import com.cardfight.client.poker.event.VFLogMsg;
import com.cardfight.client.poker.event.VFRedirect;
import com.cardfight.client.poker.event.VFUserid;
import com.cardfight.client.poker.event.VFQueueName;
import com.cardfight.client.poker.event.VFBetResponse;
import com.cardfight.client.poker.event.VFSetPlayerShowCards;
import com.cardfight.client.poker.event.VFTakeAction;
import com.cardfight.client.poker.event.VFTakeSeat;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.cardfight.client.poker.VisualFeedback;
import com.cardfight.server.poker.VisualFeedbackEventQueue;
import com.cardfight.server.poker.VisualFeedbackServerQueue;
import com.cardfight.server.poker.VisualFeedbackMultiplexor;
import com.cardfight.server.poker.HoldemSimulation;
import com.cardfight.client.poker.ActionCallback;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.*;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
    GreetingService {

  public ArrayList<VFEvent> greetServer(String tableID, String userid, String queueID, int lastMsgID, ArrayList<VFEvent> sent) {
    ArrayList<VFEvent> elist = new ArrayList<VFEvent>();

    //String serverInfo = getServletContext().getServerInfo();
    //String userAgent = getThreadLocalRequest().getHeader("User-Agent");
    //return "Hello, " + input + "!<br><br>I am running " + serverInfo
    //    + ".<br><br>It looks like you are using:<br>" + userAgent;
	//System.out.println("tableID = "+tableID);

    HttpSession session = getSession();
    HashMap     hashMap = getHashMapFromSession("greet", session);

    
    boolean earlyReturn = false;
	if ( tableID.equals("initstate") ) {
	    String ctid = (String)hashMap.get("tableID");//getFromSession("tableID", session);

	    if ( ctid != null && !ctid.equals("") && !ctid.equals(tableID) ) {
	    	System.out.println("ctid :"+ctid);
	    	elist.add(new VFRedirect(ctid));
	    	earlyReturn = true;
			//return elist;
	    } else {
	    	elist.add(new VFRedirect("foobar"));
	    	System.out.println("put tableid:"+"foobar");
	    	hashMap.put("tableID", "foobar");//setInSession("tableID", "foobar", session);
	    	earlyReturn = true;
	    	//return elist;
	    }
	} else {
	    String ctid = (String) hashMap.get("tableID");  //getFromSession("tableID", session);
	    if ( ctid!= null && !ctid.equals(tableID) ) {
	    	System.out.println("put tableid:"+tableID);
	    	hashMap.put("tableID", tableID); //setInSession("tableID", tableID, session);
	    }
	}
	
	// If session stores a userid for this user then get it.  Otherwise, save it in cookies.
	if ( userid.equals("unknown") ){
	    String cuserid = (String)hashMap.get("userid");//getFromSession("userid", session);

	    if ( cuserid != null && !cuserid.equals("") && !cuserid.equals(userid) ) {
	    	System.out.println("cuserid:"+cuserid);

	    	elist.add(new VFUserid(cuserid));
			//return elist;
	    }
	} else {
    	//System.out.println("userid:"+userid);
	    String cuserid = (String) hashMap.get("userid"); //getFromSession("userid", session);
	    if ( cuserid == null || !cuserid.equals(userid) ) {
	    	System.out.println("put cuserid:"+cuserid);
	    	hashMap.put("userid", userid); //setInSession("userid", userid, session);
	    }
	}
	// Return any initialization responses to user immediately
	if ( earlyReturn )
		return elist;
	
	// Track what players are actively playing so that we can allow them to respond
	boolean isActivePlayer = false;
	int  num = -1;
	long now = -1;
	if ( userid.startsWith("player") ) {
		String snum = userid.substring(6);
		num = Integer.parseInt(snum);
		if ( num > 0 && num <= 10 ) {
			now = System.currentTimeMillis();
			isActivePlayer = true;
			activePlayers.put(tableID+userid, now);
		}
	}
	
	// Find the specific event queue for user or start a new one and ensure that the manager is running
	VisualFeedbackEventQueue equeue = null;
	synchronized(queueList) {
		equeue = queueList.get(queueID);
		if ( equeue == null ) {
			System.out.println("starting queue for qID :"+queueID);
			equeue = startupPoker(tableID);
			elist.add(new VFQueueName(equeue.getQueueID()));
			if ( manager == null ) {
				manager = new ManagerThread();
				manager.start();
			}
		}
	}
	
	// Process incoming events
	for (VFEvent evt : sent) {
		if (evt instanceof VFChatMsg) {
			VFChatMsg cmsg = (VFChatMsg) evt;
			String nick = cmsg.getNick();
			String msg = cmsg.getMsg();
		    //if (msg != null && msg.length() > 0 && nick != null) {
			System.out.println("CHAT> "+nick+":" +msg);
			for (Map.Entry<String, VisualFeedbackEventQueue> entry: queueList.entrySet()) {
				VisualFeedbackEventQueue queue = entry.getValue();
				queue.chatMsg(nick,msg);
			}
		} else if ( evt instanceof VFBetResponse ) {
			VFBetResponse cmsg = (VFBetResponse) evt;
			ActionCallback ac = equeue.getPendingAC();
			System.out.println("BetResp : " + cmsg.getBettingResponse());
			ac.deliverBettingResponse(cmsg.getBettingResponse());
		} else if ( evt instanceof VFTakeSeat ) {
			VFTakeSeat cmsg = (VFTakeSeat) evt;
			HoldemSimulation hs = equeue.getParent().getGame();
			if ( cmsg.getSeatNum() != -1 ) {
				System.out.println("Sitdown Player : " +userid +" seat: "+ cmsg.getSeatNum());
				hs.addPlayer(userid,cmsg.getSeatNum());
			} else {
				System.out.println("Standup Player : " +userid);
				hs.removePlayer(userid);
			}
				
		} else if ( evt instanceof VFLogMsg ) {
			VFLogMsg cmsg = (VFLogMsg) evt;
			System.out.println("C> " + cmsg.getMsg());
		}	
	}
	
	// Get new events for user
	VFEvent evt = null;
	int loopCount = 0;
	while (true) {
		loopCount++;
		//evt = new VFEvent();
		//return evt;
		evt = equeue.takeFromQueue();
		//System.out.println("evt: "+evt);
		if (evt != null) {
		    if ( evt instanceof VFTakeAction ) {
		    	VFTakeAction vfta = (VFTakeAction) evt;
		    	if ( /*isActivePlayer && */userid.equals(vfta.getNick()) ) {
			    	elist.add(evt);
		    	} else {
		    		// Throw away the event
		    		//checkIfDelayedResponseRequired(equeue, vfta);
		    	}
		    } else if ( evt instanceof VFSetPlayerShowCards ) {
		    	VFSetPlayerShowCards vfe = (VFSetPlayerShowCards) evt;
		    	VisualFeedbackMultiplexor vfm = equeue.getParent();
		    	HoldemSimulation hs = null;
		    	boolean realPlayerMode = false;
		    	if ( vfm != null )
		    		hs = vfm.getGame();
		    	if (hs != null )
		    		realPlayerMode = hs.getRealPlayerMode();

		    	if (  !realPlayerMode || (hs != null && userid.equals( hs.getNick(vfe.getPlayerNum()))) )  {
		    		elist.add(evt);
		    	} else {
		    		// Throw away the event
		    	}
		    } else {
		    	elist.add(evt);
		    }
			if ( elist.size() > 200 ) {
			    //System.out.println("svr+200 :"+tableID+"\t"+userid+"\t"+queueID+"\t"+lastMsgID+"\t"+elist.size());
				return elist;
			}
		} else {
			if ( elist.size() > 0 )  {
			    //System.out.println("svr :"+tableID+"\t"+userid+"\t"+queueID+"\t"+lastMsgID+"\t"+elist.size());
				return elist;
			}
			else
				if ( loopCount > 10 ) {
				    //System.out.println("svr10 :"+tableID+"\t"+userid+"\t"+queueID+"\t"+lastMsgID+"\t"+elist.size());
					return elist;
				}
				try { Thread.sleep(200); } catch (InterruptedException e) {}
		}
	}
  }
  
  /*private void checkIfDelayedResponseRequired(VisualFeedbackEventQueue equeue, VFTakeAction vfta) {
	    String tableID = equeue.getTableID();
		Long time = activePlayers.get(tableID+vfta.getNick());
		if (time != null ) {
			long lastTime = time;
			long now      = System.currentTimeMillis();
			// if someone is there to respond then leave it
			if ( (now - lastTime) < 1000 ) return;
		}
		// If there is nobody listening then generate a response
		// Note - might be doing this multiple times at the moment
		// Plus not at all if nobody listening
		ActionCallback ac = equeue.getPendingAC();
		ac.deliverBettingResponse(vfta.getCallable()+vfta.getPlayerBet());
  }*/
  
  /*public String  foobar(VFInitTable v1) {
	  return "foobar";
  }*/
  
  private static final HashMap<String, VisualFeedbackEventQueue>  queueList = new HashMap<String, VisualFeedbackEventQueue>();
  private static final HashMap<String, VisualFeedbackMultiplexor> vfList    = new HashMap<String, VisualFeedbackMultiplexor>();
  private static final HashMap<String, Thread>                    simList   = new HashMap<String, Thread>();
  private static ManagerThread                                    manager   = null;
  private static HashMap<String, Long>                            activePlayers = new HashMap<String, Long>();
  private static HashMap<String,VisualFeedbackServerQueue>        serverQueueList = new HashMap<String,VisualFeedbackServerQueue>();
  private VisualFeedbackEventQueue startupPoker(String tableID) {
	  VisualFeedbackEventQueue equeue = new VisualFeedbackEventQueue();
	  VisualFeedbackMultiplexor vfm = null;
	  vfm = vfList.get(tableID);
	  boolean newThreadNeeded = ( vfm == null );
	  if ( newThreadNeeded ){
		  System.out.println("newThreadNeeded for "+tableID);
	      vfm = new  VisualFeedbackMultiplexor();
		  vfm.addObserver(equeue);
		  VisualFeedbackServerQueue serverQueue = new VisualFeedbackServerQueue();
		  serverQueue.setParent(vfm);
		  serverQueue.setTableID(tableID);
		  vfm.addObserver(serverQueue);
		  serverQueueList.put(tableID, serverQueue);
		  vfList.put(tableID, vfm);
	  } else {
		  System.out.println("newThread not needed for "+tableID);
		  vfm.addObserverAfterSync(equeue);
	  }
	  equeue.setParent(vfm);
	  equeue.setTableID(tableID);
	  queueList.put(equeue.getQueueID(), equeue);
	  
	  if ( newThreadNeeded ) {
		  //queueList.put(tableID, equeue);
		  boolean automated = false;
		  int mode = FULL_MODE;
		  if ( tableID.startsWith("empty") )
			  mode = EMPTY_MODE;
		  GameThread gt1 = new GameThread(automated, mode, vfm);
		  simList.put(tableID, gt1);
		  gt1.start();

		  // Inform about a new user
		  String sid = tableID;
		  //if (sid.length() > 6)
		  //sid = sid.substring(sid.length()-6);
		  sendMsgToEveryone("Server", "New user - "+sid);
	  }
	  //System.out.println("out of startupPoker ");

	  return equeue;
  }
  
  private void sendMsgToEveryone(String nick, String msg) {
	  for (Map.Entry<String, VisualFeedbackEventQueue> entry: queueList.entrySet()) {
		  VisualFeedbackEventQueue queue = entry.getValue();
		  queue.chatMsg(nick, msg);
	  }
  }
  
  
  // ------- Session code ----------
  private HttpSession getSession() {
	 // Get the current request and then return its session
	 return this.getThreadLocalRequest().getSession();
  }

  public static void setInSession(String key, String value, HttpSession session) {
      session.setAttribute(key, new String(""));
  }
  
  public static String getFromSession(String key, HttpSession session) {
      if (session.getAttribute(key) == null) {
          session.setAttribute(key, new String(""));
      }
      return (String) session.getAttribute(key);
  }
  
  public static HashMap getHashMapFromSession(String key, HttpSession session) {
    // If the session does not contain anything, create a new HashMap
    if (session.getAttribute(key) == null) {
      session.setAttribute(key, new HashMap());
    }

    // Return the HashMap
    return (HashMap) session.getAttribute(key);
  }
  
  // ------- End Session code ----------

  private static int EMPTY_MODE = 1;
  private static int FULL_MODE = 2;
  private static class GameThread extends Thread {
	  private boolean automated;
	  private int     mode;
	  private VisualFeedbackMultiplexor equeue;
	  private HoldemSimulation hs;
	  public GameThread(boolean automated, int mode, VisualFeedbackMultiplexor equeue) {
		  this.automated = automated;
		  this.mode      = mode;
		  this.equeue    = equeue;
		  //setDaemon(true);
	  }

	  public void run() {
		  equeue.chatMsg("Dealer", "Starting game ...");
		  hs = new HoldemSimulation();
		  if (mode == FULL_MODE)
			  hs.initGame(automated, equeue);
		  else if (mode == EMPTY_MODE) {
			  equeue.setGame(hs);
			  hs.initEmptyGame(automated, equeue);
			  hs.awaitPlayers(2);
		  }
	  }
  }
  
  private class ManagerThread extends Thread {
	  public ManagerThread() {
		  //setDaemon(true);
	  }

	  public void run() {
		  long tdelta = 0; 
		  long startTime = System.currentTimeMillis();
		  long curTime;
		  do  {
			  try { Thread.sleep(100); } catch (InterruptedException e) {}  //TODO: 500
			  
			  // Check for required betting responses
			  respondToTakeActions();
			  
			  // Check for dead games and queues
			  curTime = System.currentTimeMillis();
			  tdelta = curTime - startTime;
			  if ( tdelta >= 1000*60*1 ) {
				  removeOldFromQueues();
				  startTime = System.currentTimeMillis();
			  }
			  yield();
			  
		  } while ( queueList.size() > 0 );
		  manager = null;
	  }
	  
	  private void removeOldFromQueues() {
		  ArrayList<String> badIDs = new ArrayList<String>();
		  ArrayList<String> badQueueIDs = new ArrayList<String>();

		  for (Map.Entry<String, VisualFeedbackEventQueue> entry: queueList.entrySet()) {
			  VisualFeedbackEventQueue queue = entry.getValue();
			  VisualFeedbackMultiplexor vfm = queue.getParent();
			  if ( queue.getSize() > 1000 ) {
				  //System.out.println("Manager: before remove: "+queue.getTableID()+"\t numEvts: "+queue.getSize());
				  vfm.removeObserver(queue);
				  if ( vfm.getNumChildren() <= 1 ) {
					  badIDs.add(queue.getTableID());
					  System.out.println("Manager: Event buildup - queue/thread shutdown: "+queue.getTableID()+"\t numEvts: "+queue.getSize());
				  } else {
					  System.out.println("Manager: Event buildup - queue shutdown: "+queue.getTableID()+"\t numEvts: "+queue.getSize());
				  }
				  badQueueIDs.add(entry.getKey());
			  } else {
				  System.out.println("Manager: Queue: "+queue.getTableID() +"\t Q: "+entry.getKey()+"\t numEvts: "+queue.getSize()+"\t"+ vfm.getNumChildren());
			  }
		  }
		  for( String badQID: badQueueIDs ) {
			  VisualFeedbackEventQueue queue = queueList.remove(badQID);
			  if ( queue == null )
				  System.out.println("Manager: missing queue on shutdown: "+badQID);
			  System.out.println("Manager: removed queueID "+badQID);
		  }
		  for( String badID: badIDs ) {
			  Thread badThread = simList.get(badID);
			  if ( badThread != null )
				  badThread.interrupt();
			  else
				  System.out.println("Manager: missing tableID on shutdown: "+badID);
			  vfList.remove(badID);
			  simList.remove(badID);
			  serverQueueList.remove(badID);
			  sendMsgToEveryone("manager", "shutting down tableID "+badID);
			  System.out.println("Manager: shutting down tableID "+badID);
		  }
	  }
	  
	  private void respondToTakeActions() {
		  for (Map.Entry<String, VisualFeedbackServerQueue> entry: serverQueueList.entrySet()) {
			  VisualFeedbackServerQueue queue = entry.getValue();
			  if ( queue.getParent() != null &&
				   queue.getParent().getGame() != null &&
				   queue.getParent().getGame().getRealPlayerMode() )
				  continue;
			  while ( queue.getSize() > 0 ) {
			      VFTakeAction vfta = (VFTakeAction) queue.takeFromQueue();
				  String tableID = queue.getTableID();
			      Long time = activePlayers.get(tableID+vfta.getNick());
			      if (time != null ) {
			    	  long lastTime = time;
			    	  long now      = System.currentTimeMillis();
			    	  //System.out.println("TA nick: "+vfta.getNick() +" time: "+(now-lastTime));
			    	  // if someone is there to respond then leave it
			    	  if ( (now - lastTime) < 1000 ) continue;
			      }	//else	    	  
			    	  //System.out.println("TA AUTO nick: "+vfta.getNick() );

			      // If there is nobody listening then generate a response
			      ActionCallback ac = queue.getPendingAC();
			      ac.deliverBettingResponse(vfta.getCallable()+vfta.getPlayerBet());
			  }
		  }
	  }
  }
  
}
