package Classes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


@SuppressWarnings("serial")
public class Game extends JPanel {

	private int wins;
	private int losses;
	private int pushes;
	@SuppressWarnings("unused")
	private int roundsPlayed;
	private int startingChips;

	private deckOfCards deck;
	private deckOfCards discarded;

	private Dealer dealer;
	private Player player;
	
	private JButton hit;
	private JButton stand;
	private JButton surrender;
	private JButton insurance;
	private JButton doubleDown;
	private JButton nextRound;
	private JButton neither;
	private JButton betAll;
	private JButton bet50;
	private JButton bet100;
	private JButton restart;
	private JButton exit;
	//private JButton menu;

	private JLabel score;
	private JLabel playerHandValue;
	private JLabel dealerHandValue;
	private JLabel gameMessage;
	private JLabel chips;
	
	private JLabel[] dealerCards;
	private JLabel[] playerCards;
	
	public static final int CARD_WIDTH = 100;
	public static final int CARD_HEIGHT = 145;
	public static final String IMAGE_DIR = "src/cards/";
	

	public Game(int startingChips) {
		
		this.wins = 0;
		this.losses = 0;
		this.pushes = 0;
		this.roundsPlayed = 0;
		this.startingChips = startingChips;

		this.dealer = new Dealer();
		this.player = new Player(startingChips);

		this.deck = new deckOfCards();
		this.discarded = new deckOfCards();
		this.discarded.emptyDeck();

		this.deck.shuffle();
		
		setupGUI();
		startRound();
	}
	
	public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    g.setColor(Color.decode("#427643"));
	    g.fillRect(0,0,1000,1000);
	}

	public void setupGUI() {
		this.setSize(800, 500);

	    //Make Buttons for actions
		surrender = new JButton("Surrender");
	    surrender.setBounds(10, 10, 100, 20);
	    insurance = new JButton("Insurance");
	    insurance.setBounds(120, 10, 100, 20);
	    neither = new JButton("Neither");
	    neither.setBounds(230, 10, 100, 20);
	    bet50 = new JButton("Bet 50");
	    bet50.setBounds(10, 10, 100, 20);
	    bet100 = new JButton("Bet 100");
	    bet100.setBounds(120, 10, 100, 20);
	    betAll = new JButton("Bet All");
	    betAll.setBounds(230, 10, 100, 20);
	    hit = new JButton("Hit");
	    hit.setBounds(10, 10, 50, 20);
	    stand = new JButton("Stand");
	    stand.setBounds(70, 10, 100, 20);
	    nextRound = new JButton("Next Round");
	    nextRound.setBounds(10, 10, 140, 20);
	    doubleDown = new JButton("Double Down");
	    doubleDown.setBounds(180, 10, 140, 20);
	    restart = new JButton("Restart");
	    restart.setBounds(10, 10, 100, 20);
	    exit = new JButton("Exit");
	    exit.setBounds(120, 10, 100, 20);
	    hit.setVisible(false);
	    stand.setVisible(false);
	    nextRound.setVisible(false);
	    doubleDown.setVisible(false);
	    surrender.setVisible(false);
	    neither.setVisible(false);
	    insurance.setVisible(false);
	    restart.setVisible(false);
	    exit.setVisible(false);

	    this.setLayout(null);
	    
	    //add to JPanel
	    this.add(hit);
	    this.add(stand);
	    this.add(nextRound);
	    this.add(surrender);
	    this.add(insurance);
	    this.add(neither);
	    this.add(bet100);
	    this.add(bet50);
	    this.add(betAll);
	    this.add(doubleDown);
	    this.add(restart);
	    this.add(exit);

	    this.dealerCards = new JLabel[11];
	    this.playerCards = new JLabel[11];
	    
	    //card placement
	    int cardX = 10;
	    int cardY = 150;
	    
	    //card images
	    for (int i = 0; i < dealerCards.length; i++) {

	        dealerCards[i] = new JLabel(new ImageIcon(new ImageIcon(IMAGE_DIR + "CardDown.png").getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH)));
	        playerCards[i] = new JLabel(new ImageIcon(new ImageIcon(IMAGE_DIR + "CardDown.png").getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH)));

	        dealerCards[i].setBounds(cardX, cardY, CARD_WIDTH, CARD_HEIGHT);
	        playerCards[i].setBounds(cardX, cardY+250, CARD_WIDTH, CARD_HEIGHT);

	        this.add(dealerCards[i]);
	        this.add(playerCards[i]);

	        cardX += 60;
	        cardY -= 17;

	    }
	    
	    //JLabel setup
	    score = new JLabel("Wins: 0   Losses: 0   Pushes: 0");
	    score.setBounds(450, 10, 300, 50);
	    this.add(score);
	    
	    chips = new JLabel("Chips: " + player.getChips());
	    chips.setBounds(700, 10, 300, 50);
	    this.add(chips);

	    gameMessage = new JLabel("Starting round! Hit or Stand?");
	    gameMessage.setBounds(450, 200, 300, 40);
	    gameMessage.setFont(new Font("Arial", 1, 20));
	    this.add(gameMessage);

	    dealerHandValue = new JLabel("Dealer's Hand Value:");
	    playerHandValue = new JLabel("Player's Hand Value:");
	    dealerHandValue.setBounds(20, 280, 300, 50);
	    playerHandValue.setBounds(20, 530, 300, 50);
	    this.add(dealerHandValue);
	    this.add(playerHandValue);

	    gameMessage.setForeground(Color.WHITE);
	    dealerHandValue.setForeground(Color.WHITE);
	    playerHandValue.setForeground(Color.WHITE);
	    score.setForeground(Color.WHITE);
	    chips.setForeground(Color.WHITE);
	    
	    //action listeners for buttons
	    hit.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            player.hit(deck, discarded);
	            updateScreen();
	            playerTurn();
	            checkPlayer21();
	        }
	    });
	    
	    stand.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            dealerTurn();
	            non21Win();
	            updateScreen();
	            dealer.printHand(dealerCards);
	            hit.setVisible(false);
	            stand.setVisible(false);
	            doubleDown.setVisible(false);
	            nextRound.setVisible(true);
	        }
	    });
	    
	    nextRound.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            nextRound.setVisible(false);
	            hit.setVisible(false);
	            stand.setVisible(false);
	            doubleDown.setVisible(false);
	            bet100.setVisible(true);
	            bet50.setVisible(true);
	            betAll.setVisible(true);
	            startRound();
	        }
	    });
	    
	    surrender.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            surrenderRound();
	        }
	    });
	    
	    neither.addActionListener(new ActionListener() {
	    	@Override
	    	public void actionPerformed(ActionEvent e) {
	    		surrender.setVisible(false);
	    		neither.setVisible(false);
	    		insurance.setVisible(false);
	    		hit.setVisible(true);
	    		stand.setVisible(true);
	    		if(player.getBet() <= player.getChips()) {
	    			doubleDown.setVisible(true);
	    		}
	    		if(dealer.has21()) {
	    			dealerInitialBlackjack();
	    		}else {
		    		gameMessage.setText("Hit or Stand?");
	    		}
	    	}
	    });
	    
	    insurance.addActionListener(new ActionListener() {
	    	@Override
	    	public void actionPerformed(ActionEvent e) {
	    		player.insuranceBet();
	    		if(dealer.has21()) {
	    			insurance();
	    			nextRound.setVisible(true);
	    		}else {
	    			player.loseInsurance();
	    			hit.setVisible(true);
		    		stand.setVisible(true);
		    		if(player.getBet() <= player.getChips()) {
		    			doubleDown.setVisible(true);
		    		}
	    		}
	    		surrender.setVisible(false);
	    		neither.setVisible(false);
	    		insurance.setVisible(false);
	    		updateStatsDisplay();
	    	}
	    });
	    
	    bet100.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            player.placeBet(100);
	            updateStatsDisplay();
	            if(player.has21()) {
	            	initialBlackjack();
	            }else {
	            	insuranceOrSurrender();
	            }
	        }
	    });
	    
	    bet50.addActionListener(new ActionListener() {
	    	@Override
	    	public void actionPerformed(ActionEvent e) {
	    		player.placeBet(50);
	            updateStatsDisplay();
	            if(player.has21()) {
	            	initialBlackjack();
	            }else {
	            	insuranceOrSurrender();
	            }
	        }
	    });
	    
	    betAll.addActionListener(new ActionListener() {
	    	@Override
	    	public void actionPerformed(ActionEvent e) {
	    		player.placeBet(player.getChips());
	            updateStatsDisplay();
	            if(player.has21()) {
	            	initialBlackjack();
	            }else {
	            	insuranceOrSurrender();
	            }
	    	}
	    });
	    
	    restart.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            restart();
	        }
	    });
	    
	    doubleDown.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	player.hit(deck, discarded);
	        	player.doubleDown();
	        	updateScreen();
	        	updateStatsDisplay();
	        	if (player.getHand().calculatedValue() > 21) {
	        		playerTurn();
	        	}else if(player.getHand().calculatedValue() == 21) {
		            checkPlayer21();
	        	}else {
	        		dealerTurn();
		            non21Win();
		            updateScreen();
		            dealer.printHand(dealerCards);
	        	}  
	            hit.setVisible(false);
	            stand.setVisible(false);
	            doubleDown.setVisible(false);
	            nextRound.setVisible(true);
	        }
	    });
	    
	    exit.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	System.exit(0);
	        }
	    });
	}
	
	//update's players hand value and hand's cards images
	public void updateScreen(){
        playerHandValue.setText("Player's Hand Value: " + player.getHand().calculatedValue());
        player.printHand(playerCards);
    }
	
	public void startRound() {
		updateStatsDisplay();

		resetHands();

		if(deck.cardsLeft() < 4){
			deck.reloadDeckFromDiscard(discarded);
		}
						
		dealCards();
		
		if(player.getChips() < 50) {
			bet100.setVisible(false);
			bet50.setVisible(false);
		} else if(player.getChips() < 100) {
			bet100.setVisible(false);
		}
		
		if(player.getChips() <= 0) {
			gameOver();
		}

		this.roundsPlayed++;
	}

	//handles the game over feature when the player gets to 0 chips
	public void gameOver() {
		gameMessage.setText("Game Over!");
		nextRound();
		nextRound.setVisible(false);
		restart.setVisible(true);
		exit.setVisible(true);
	}
	
	//resets the hands to empty for the start of the round
	public void resetHands() {
		dealer.getHand().discardHandToDeck(discarded);
		player.getHand().discardHandToDeck(discarded);
	}

	//deals cards to both the dealer and the player
	public void dealCards() {
		dealer.getHand().takeCardFromDeck(deck);
		dealer.getHand().takeCardFromDeck(deck);
		
		player.getHand().takeCardFromDeck(deck);
		player.getHand().takeCardFromDeck(deck);
		
		//print the hands and then hide them for the betting phase
		dealer.printHand(dealerCards);
		player.printHand(playerCards);
		faceDown();
	}
	
	//check if the player has a blackjack after the betting phase
	public void initialBlackjack() {
		revealAll();
		betAll.setVisible(false);
		bet50.setVisible(false);
		bet100.setVisible(false);

		if(dealer.has21()){
			pushes++;
			gameMessage.setText("Both Have 21! Push!");
			nextRound();
			player.pushBet();
		}
		else{
			dealer.printHand(dealerCards);
			this.wins++;
			player.instant21();
			gameMessage.setText("Instant Blackjack Win!");
			nextRound();
			player.instant21();
		}
	}
	
	//checks if the dealer has a blackjack after the insurance or surrender phase
	public void dealerInitialBlackjack() {
		betAll.setVisible(false);
		bet50.setVisible(false);
		bet100.setVisible(false);
		losses++;
		gameMessage.setText("Dealer Has 21! You Lose!");
		revealAll();
		nextRound();
		player.loseBet();
	}
	
	//checks if the player busts during his turn
	public void playerTurn(){
	    if (player.getHand().calculatedValue() > 21) {
	        gameMessage.setText("You BUST - Over 21");
	        revealAll();
	        losses++;
	        nextRound();
	        player.loseBet();
	    }
	}
	
	//checks if the player hit a blackjack
	public void checkPlayer21() {
		if(player.getHand().calculatedValue() == 21){
            gameMessage.setText("You have 21!");
            revealAll();
            wins++;
            nextRound();
            player.winBet();
        }
	}
	
	//dealer hits until he's above 18
	public void dealerTurn() {
		while(dealer.getHand().calculatedValue()<17){
			dealer.hit(deck, discarded);
			updateScreen();
		}
	}
	
	//sorts out who wins if neither gets 21
	public void non21Win() {
		dealerHandValue.setText("Dealer's Hand Value: " + dealer.getHand().calculatedValue());
		if(dealer.getHand().calculatedValue()>21){
			gameMessage.setText("Dealer busts! You Win!");
			player.winBet();
			wins++;
		}else if(dealer.getHand().calculatedValue() > player.getHand().calculatedValue()){
			gameMessage.setText("Dealer Higher Hand! You Lose!");
			player.loseBet();
			losses++;
		}else if(player.getHand().calculatedValue() > dealer.getHand().calculatedValue()){
			gameMessage.setText("Player Higher Hand! You Win!");
			player.winBet();
			wins++;
		}else {
			gameMessage.setText("Equal Value Hands! Push!");
			player.pushBet();
			pushes++;
		}
	}
	
	//updates the win loss push stats and the chip count on the gui
	public void updateStatsDisplay() {
	    chips.setText("Chips: " + player.getChips());
		score.setText("Wins: " + wins + "   Losses: " + losses + "   Pushes: " + pushes);
	}
	
	//called to surrender the round
	public void surrenderRound() {
	    player.surrenderBet();
	    gameMessage.setText("You surrendered!");
	    dealer.printHand(dealerCards);
        dealerHandValue.setText("Dealer's Hand Value: " + dealer.getHand().calculatedValue());
	    nextRound();
	}

	//puts the dealt cards face down during the betting phase
	public void faceDown() {
		for(int i = 0; i < 2; i++) {
			dealerCards[i].setIcon(new ImageIcon(new ImageIcon(IMAGE_DIR + "CardDown.png").getImage()
					.getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_DEFAULT)));
			playerCards[i].setIcon(new ImageIcon(new ImageIcon(IMAGE_DIR +"CardDown.png").getImage()
					.getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_DEFAULT)));
		}
		dealerHandValue.setText("Dealer's Hand Value: ?");
		playerHandValue.setText("Your Hand Value: ?");
		gameMessage.setText("Betting Phase");

	}
	
	//handles the insurance or surrender logic
	public void insuranceOrSurrender() {
		//set the right buttons on the screen
		betAll.setVisible(false);
        bet100.setVisible(false);
        bet50.setVisible(false);
        surrender.setVisible(true);
        insurance.setVisible(true);
        neither.setVisible(true);
        
        //reveal cards
        dealer.printHand(dealerCards);
        player.printHand(playerCards);
        dealerCards[1].setIcon(new ImageIcon(new ImageIcon(IMAGE_DIR + "CardDown.png").getImage()
				.getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_DEFAULT)));
        
        //reveal hand values
        dealerHandValue.setText("Dealer's hand value: " + dealer.getHand().getCard(0).getValue() + " + ?");
		playerHandValue.setText("Your Hand Value: " + player.getHand().calculatedValue());
		
		gameMessage.setText("Surrender or Insurance?");

	}
	
	//reveals all hands and all hand values
	public void revealAll() {
		//reveal cards
        dealer.printHand(dealerCards);
        player.printHand(playerCards);
        
        //reveal hand values
        dealerHandValue.setText("Dealer's hand value: " + dealer.getHand().calculatedValue());
		playerHandValue.setText("Your Hand Value: " + player.getHand().calculatedValue());
	}
	
	//puts only the next round button on screen
	public void nextRound() {
		hit.setVisible(false);
	    stand.setVisible(false);
	    nextRound.setVisible(true);
	    surrender.setVisible(false);
	    neither.setVisible(false);
	    insurance.setVisible(false);
	    betAll.setVisible(false);
        bet100.setVisible(false);
        bet50.setVisible(false);
        doubleDown.setVisible(false);
	}
	
	//handles insurance
	public void insurance() {
		player.winInsurance();
		revealAll();
		gameMessage.setText("You were insured against the dealer");
	}
	
	//a restart method to restart the game anew
	public void restart() {
		this.wins = 0;
		this.losses = 0;
		this.pushes = 0;
		this.roundsPlayed = 0;

		this.dealer = new Dealer();
		this.player = new Player(this.startingChips);

		this.deck = new deckOfCards();
		this.discarded = new deckOfCards();
		this.discarded.emptyDeck();

		this.deck.shuffle();
		
		startRound();
		restart.setVisible(false);
		exit.setVisible(false);
		betAll.setVisible(true);
        bet100.setVisible(true);
        bet50.setVisible(true);
	}
}

