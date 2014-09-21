
package com.cardfight.server.poker;

import com.cardfight.client.poker.CardCollection;
import com.cardfight.client.poker.PotWinner;
import com.cardfight.client.poker.VisualFeedback;
import com.cardfight.client.poker.ActionCallback;


//import java.util.concurrent.*;

public class VisualFeedbackThreader implements VisualFeedback {

	//private ExecutorService service = Executors.newSingleThreadExecutor();
	private VisualFeedback  vf;

	public VisualFeedbackThreader(VisualFeedback vf) {
		this.vf = vf;
	}
	
	public void sync() {
		
	}

	public void initTable(int numPlayers) {
		final int _numPlayers = numPlayers;
		//service.execute(new Runnable() { 
			//public void run() { 
				vf.initTable(_numPlayers); 
			//}  });
	}

	public void setDealer(int dealerLoc) {
		final int _dealerLoc = dealerLoc;
		/*service.execute(new Runnable() { public void run() {*/ vf.setDealer(_dealerLoc);// }  });
	}

	public void setPots(double pots[]) {
		final double _pots[] = pots;
		/*service.execute(new Runnable() { public void run() { */vf.setPots(_pots); //}  });
	}

	public void deliverPot(int potNum, PotWinner potWinners[]) {
		final int _potNum = potNum;
		final PotWinner _potWinners[] = potWinners;
		/*service.execute(new Runnable() { public void run() { */vf.deliverPot(_potNum, _potWinners);// }  });
	}

	public void setCommonCards(CardCollection cards) {
		final CardCollection _cards = cards;
		/*service.execute(new Runnable() { public void run() { */vf.setCommonCards(_cards); //}  });
	}

	public void setPlayer(int playerNum, String nickname) {
		final int _playerNum = playerNum;
		final String _nickname = nickname;
		/*service.execute(new Runnable() { public void run() { */vf.setPlayer(_playerNum, _nickname); //}  });
	}

	public void setPlayerAmount(int playerNum, String amount, int delay) {
		final int _playerNum = playerNum;
		final String _amount = amount;
		final int _delay = delay;
		/*service.execute(new Runnable() { public void run() { */vf.setPlayerAmount(_playerNum, _amount, _delay); //}  });
	}

	public void dealPlayerCard(int playerNum, CardCollection cards) {
		final int _playerNum = playerNum;
		final CardCollection _cards = cards;
		/*service.execute(new Runnable() { public void run() { */vf.dealPlayerCard(_playerNum, _cards); //}  });
	}

	public void setPlayerCards(int playerNum, CardCollection cards) {
		final int _playerNum = playerNum;
		final CardCollection _cards = cards;
		/*service.execute(new Runnable() { public void run() { */vf.setPlayerCards(_playerNum, _cards); //}  });
	}

	public void setAllShowCards(boolean showCards) {
		final boolean _showCards = showCards;
		/*service.execute(new Runnable() { public void run() { */vf.setAllShowCards(_showCards); //}  });
	}

	public void setPlayerShowCards(int playerNum, boolean showCards) {
		final int _playerNum = playerNum;
		final boolean _showCards = showCards;
		/*service.execute(new Runnable() { public void run() { */vf.setPlayerShowCards(_playerNum, _showCards); //}  });
	}

	public void setPlayerBets(int playerNum, double bets[], int delay) {
		final int _playerNum = playerNum;
		final double _bets[] = bets;
		final int _delay = delay;
		/*service.execute(new Runnable() { public void run() { */vf.setPlayerBets(_playerNum, _bets, _delay);// }  });
	}
	
	public void setPlayerBetState(int playerNum, String state) {
		final int _playerNum = playerNum;
		final String _state = state;
		/*service.execute(new Runnable() { public void run() { */vf.setPlayerBetState(_playerNum, _state); //}  });
	}

	public void collectBets(int potNum, boolean doAnimation) {
		final int     _potNum = potNum;
		final boolean _doAnim = doAnimation;
		/*service.execute(new Runnable() { public void run() { */vf.collectBets(_potNum, _doAnim);// }  });
	}
	
	
	public void takeAction(int bettingMode, String nick, double playerBet, double toCall, double callable, double bigBlind,
			  double maxRaiseIfNoLimit, double raiseIfLimit, double maxRaiseIfPotLimit, double minNonLimitRaise,
			  ActionCallback ac) {
		final int    _bettingMode        = bettingMode;
		final String _nick               = nick;
		final double _playerBet          = playerBet;
		final double _toCall             = toCall;
		final double _callable           = callable;
		final double _bigBlind           = bigBlind;
		final double _maxRaiseIfNoLimit  = maxRaiseIfNoLimit;
		final double _raiseIfLimit       = raiseIfLimit;
		final double _maxRaiseIfPotLimit = maxRaiseIfPotLimit;
		final double _minNonLimitRaise   = minNonLimitRaise;
		final ActionCallback _ac          = ac;
		//service.execute(new Runnable() { public void run() { 
			vf.takeAction(_bettingMode, _nick, _playerBet, _toCall, _callable, _bigBlind, _maxRaiseIfNoLimit, _raiseIfLimit, _maxRaiseIfPotLimit, 
				_minNonLimitRaise,_ac); 
		//}});
	}
	 
	public void actionTimeout() {
		//service.execute(new Runnable() { public void run() { 
			vf.actionTimeout(); 
		//}});
	} 

	public void initRound() {
		/*service.execute(new Runnable() { public void run() { */vf.initRound(); //}  });
	}

	public void update() {
		/*service.execute(new Runnable() { public void run() { */vf.update();// }  });
	}
	
	public void chatMsg(String nick, String msg) {
		// Not implemented here
	}

}
