
package com.cardfight.client.poker;


//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import com.google.gwt.user.client.rpc.IsSerializable;

public class PlayerInGame implements IsSerializable {
	
	//private static final Log LOG = LogFactory.getLog(PlayerInGame.class);
	
	public static final int SITTING_OUT  = 0;
	public static final int PLAYING      = 1;
	public static final int ALL_IN       = 2;
	public static final int OUT_OF_HAND  = 3;
	Player _player;
	double _money;

	Hand   _hand;
	
	double _currentBet;
	double _betInRound;
	double _highestAskedBet;
	double _bets[];
	double _bigBlind;

	int    _seatNum;
	int    _state;

	public PlayerInGame() {
	}
	
	public PlayerInGame(Player player, double money) {
		_player  = player;
		_money   = money;
		_state   = PLAYING;
		_bets    = new double[0];
		_seatNum = -1;
	}

	public void initialize() {
		_hand = new Hand();
		
		clearBet();
		if (_state != SITTING_OUT )
			_state = PLAYING;
	}

	public String getNick() {
		return _player.getNick();
	}

	public double getMoney() {
		return _money;
	}

	public Hand getHand() {
		return _hand;
	}

	public double getCurrentBet() {
		return _currentBet;
	}

	public double getBetInRound() {
		return _betInRound;
	}

	public double[] getAllBets() {
		return _bets;
	}

	public int getSeatNum() {
		return _seatNum;
	}

	public void setSeatNum(int seatNum) {
		_seatNum = seatNum;
	}

	public boolean isPlaying() {
		return (_state == PLAYING);
	}

	public boolean isActive() {
		return (_state == ALL_IN || _state == PLAYING);
	}

	public boolean isSittingOut() {
		return (_state == SITTING_OUT);
	}

	public int getState() {
		return _state;
	}

	public void setState(int state) {
		_state = state;
	}

	public double getHighestAskedBet() {
		return _highestAskedBet;
	}

	public void setHighestAskedBet(double bet) {
		_highestAskedBet = bet;
	}

	public void initializeHand() {
		_hand = new Hand();
	}

	public void dealCard(Card card) {
		_hand.add(card);
	}

	public double takeAnte(double amount) {
		double remainder = takeMoney(amount);
		//if ( remainder >= 0 ) {  Ante is not a bet in round
		//    _betInRound = amount;
		//} else {
		//	double covered = amount + remainder;
		//	_betInRound = covered;
		//}
		return remainder;
	}

	public void setBigBlind(double bigBlind) {
		_bigBlind = bigBlind;
	}

	public double takeBlind(double amount, double bigBlind) {
		_bigBlind = bigBlind;
		double remainder = takeMoney(amount);
		if ( remainder >= 0 ) { 
		    _currentBet = amount;
		    _betInRound = amount;
		} else {
			double covered = amount + remainder;
			_currentBet = covered;
			_betInRound = covered;
		}
		addToBets(_betInRound, null);
		return remainder;
	}

	public double takeBet(double amount, double[] tableBetStructure) {
		double remainder = takeMoney(amount);
		if ( remainder >= 0 ) {
		    _currentBet += amount;
			_betInRound += amount;
		    //System.out.println("TAKEBET amount:"+amount+" _currentBet:"+_currentBet+" betInRound:"+_betInRound);
			addToBets(_currentBet, tableBetStructure);
		} else {
			double covered = amount + remainder;
			_currentBet += covered;
			_betInRound += covered;
			addToBets(covered, tableBetStructure);
		}
		return remainder;
	}

	private double takeMoney(double amount) {
		if ( amount < _money ) {
			_money -= amount;
			return _money;
		} else {
			double remainder = _money - amount;
			_money = 0 ;
			return remainder;
		}
	}

	// Replicate the existing betting structure as much as possible 
	// and add on anything new
	private void addToBets(double amount, double[] tableBetStructure) {
		//if (LOG.isDebugEnabled()) {
			//LOG.debug(" ADD BET:"+amount+" Current:"+ _currentBet+" - Table BETS:");
			//printBets(tableBetStructure);
		//}
		
		// Handle blinds 
		if (tableBetStructure == null || tableBetStructure.length == 0) {
			_bets = new double[1];
			_bets[0] = amount;
			return;
		}
		
		double tableAmount = doubleAmount(tableBetStructure);
		if ( amount ==  tableAmount) {
			//_bets = tableBetStructure.clone();
			_bets = new double[tableBetStructure.length];
			for (int i = 0; i < _bets.length; i++)
				_bets[i] = tableBetStructure[i];
			return;
		} else if ( amount > tableAmount ) {
			
		}
		
		// Always redo bets from scratch in case last bet wasn't a full bet (small blind for example).
		//amount += sumDoubles(_bets);
		//System.out.println(" ADD BET:"+amount);
		_bets = new double[0];
		
		// Handle bets
		double fullAmount = amount;
		double curBet;
		double maxBet = 0;
		int    betIndex = 0;
		while (fullAmount > 0 && betIndex < tableBetStructure.length) {
			extendBets();
			curBet = tableBetStructure[betIndex];
			maxBet = Math.max(maxBet, curBet);
			if (fullAmount >= maxBet) {
				_bets[_bets.length-1] = maxBet;
				fullAmount -= maxBet;
			} else {
				_bets[_bets.length-1] = fullAmount;
				fullAmount = 0;
			}
			betIndex++;
		}
		if (fullAmount > 0) {
			if (--betIndex > 0 && _bets[betIndex] < maxBet) {
				_bets[betIndex] += fullAmount;
			} else {
				extendBets();
				_bets[_bets.length-1] = fullAmount;
			}
		}
		/* Old Logic 
		if ( _bets.length == 0 ) 
			_bets    = new double[1];

		if ( _betInRound <= _bigBlind ) {
			_bets[0] = _betInRound;
		} else if ( _betInRound - amount < _bigBlind && 
					_betInRound > _bigBlind ) {
			_bets[_bets.length-1] = _bigBlind;
			extendBets();
			_bets[_bets.length-1] = _betInRound - _bigBlind;
		} else if ( _betInRound - amount >= _bigBlind ) {
			extendBets();
			_bets[_bets.length-1] = amount;
		}
		*/
		//if (LOG.isDebugEnabled())
			//printBets(_bets);
	}

	public static void printBets(double []bets) {
		
		//LOG.debug("BBB BETS:");
		for (int i = 0; bets != null && i < bets.length; i++) {
			//LOG.debug(" "+bets[i]);
		}
		//LOG.debug("");
	}
	
	private double doubleAmount(double []bets) {
		double res = 0;
		for (int i = 0; bets != null && i < bets.length; i++) {
			res += bets[i];
		}
		return res;
	}
	
	private void extendBets() {
		double newBets[] = new double[_bets.length+1];
		for (int i = 0; i < _bets.length; i++) {
			newBets[i] = _bets[i];
		}
		_bets = newBets;
	}
	
	private double sumDoubles(double[] values) {
		double sum = 0 ;
		for (int i = 0; i < values.length; i++) {
			sum += values[i];
		}
		return sum;
	}

	public void giveWinnings(double amount) {
		_money += amount;
	}

	public void clearBet() {
		_currentBet = 0;
		_betInRound = 0;
		_bets       = new double[0];
		//_bigBlind   = 0;
		_highestAskedBet = 0;
	}

	//public void clearBetInRound() {
	//	_betInRound = 0;
	//}

	public void addToPot(double amount) {
		_betInRound -= amount;
		_bets        = new double[0];
		//_bigBlind    = 0;
	}
}
