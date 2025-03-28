package com.blackjack.Models;
import com.blackjack.Services.UserService;
import com.blackjack.stubdatabase.StubDatabase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.blackjack.Main;


@SuppressWarnings("serial")
public class Game extends JPanel {
 

    private User currentUser; //  user data from stub database

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

	private JLabel score;
	private JLabel playerHandValue;
	private JLabel dealerHandValue;
	private JLabel gameMessage;
	private JLabel chips;
	
	private JLabel[] dealerCards;
	private JLabel[] playerCards;
	
	public static final int CARD_WIDTH = 100;
	public static final int CARD_HEIGHT = 145;
	public static final String IMAGE_DIR = "target/classes/cards/";
	

	//  Constructor to load user data from StubDatabase
    public Game(String username) {
        this.currentUser = Main.useStubDatabase ? StubDatabase.getUser(username) : UserService.getUser(username);

        if (currentUser == null) {
            // If the user is not found, create a new one
            this.currentUser = new User(username, "defaultPass", 1000, 0,0,0);
			if(Main.useStubDatabase){
				StubDatabase.addUser(currentUser);
			}
			else {
				UserService.addUser(currentUser);
			}
        }
		// this is to initialize the player with current user's chips
		this.player = new Player(currentUser.getChips());
        System.out.println("Loaded User: " + currentUser);
    
		//Initializing rest of the game 
		this.dealer = new Dealer();	
		this.deck = new deckOfCards();
		this.discarded = new deckOfCards();

		this.discarded.emptyDeck();
		this.deck.shuffle();
		
		setupGUI();
		startRound();
	}

//    public void updateChips(int amount) {
//        currentUser.setChips(currentUser.getChips() + amount);
//		if(Main.useStubDatabase){
//			StubDatabase.updateUser(currentUser);
//		}else{
//        UserService.updateUser(currentUser);
//		}
//        System.out.println("Updated User: " + currentUser);
//    }

	public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    g.setColor(Color.decode("#427643"));
	    g.fillRect(0,0,1000,1000);
	}

	public void setupGUI() {
		this.setSize(800, 500);

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
	    
	    int cardX = 10;
	    int cardY = 150;
	    
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
	    
	    score = new JLabel("Wins: " + currentUser.getWins() +
		 "  Losses: " + currentUser.getLosses() + 
		 "  Pushes: " + currentUser.getPushes());
	    score.setBounds(450, 10, 300, 50);
	    this.add(score);
	    
		chips = new JLabel("Chips: " + currentUser.getChips());
	    //chips = new JLabel("Chips: " + player.getChips());
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
	}

	public void gameOver() {
		gameMessage.setText("Game Over!");
		nextRound();
		nextRound.setVisible(false);
		restart.setVisible(true);
		exit.setVisible(true);
	}
	
	public void resetHands() {
		dealer.getHand().discardHandToDeck(discarded);
		player.getHand().discardHandToDeck(discarded);
	}

	public void dealCards() {
		dealer.getHand().takeCardFromDeck(deck);
		dealer.getHand().takeCardFromDeck(deck);
		
		player.getHand().takeCardFromDeck(deck);
		player.getHand().takeCardFromDeck(deck);
		
		dealer.printHand(dealerCards);
		player.printHand(playerCards);
		faceDown();
	}
	
	public void initialBlackjack() {
		revealAll();
		betAll.setVisible(false);
		bet50.setVisible(false);
		bet100.setVisible(false);

		if(dealer.has21()){
			currentUser.setPushes(currentUser.getPushes() + 1);
			player.pushBet();
			currentUser.setChips(player.getChips() );
			gameMessage.setText("Both Have 21! Push!");
			nextRound();
		}
		else{
			dealer.printHand(dealerCards);
			currentUser.setWins(currentUser.getWins() + 1);
			gameMessage.setText("Instant Blackjack Win!");
			player.instant21();
			currentUser.setChips(player.getChips());
		}
		if(Main.useStubDatabase){
			StubDatabase.updateUser(currentUser);
		}else {
			UserService.updateUser((currentUser));
		}
		nextRound();
	}
	
	public void dealerInitialBlackjack() {
		betAll.setVisible(false);
		bet50.setVisible(false);
		bet100.setVisible(false);
		
		gameMessage.setText("Dealer Has 21! You Lose!");
		revealAll();
		currentUser.setLosses(currentUser.getLosses() + 1);
		player.loseBet();
		currentUser.setChips(player.getChips());
		if(Main.useStubDatabase){
			StubDatabase.updateUser(currentUser);
		}else {
			UserService.updateUser((currentUser));// stub database
		}
		nextRound();
	}
	
	public void playerTurn(){
	    if (player.getHand().calculatedValue() > 21) {
	        gameMessage.setText("You BUST - Over 21");
			currentUser.setLosses(currentUser.getLosses() + 1);
			player.loseBet();
			currentUser.setChips(player.getChips()); // Deduct the bet for a bust
			revealAll();
			if(Main.useStubDatabase){
				StubDatabase.updateUser(currentUser);
			}else{
			UserService.updateUser((currentUser));
			}
	    	nextRound();
		}
	}
	
	//checks if the player hit a blackjack
	public void checkPlayer21() {
		if(player.getHand().calculatedValue() == 21){
            gameMessage.setText("You have 21!");
            revealAll();
			currentUser.setWins(currentUser.getWins() + 1);
      player.winBet();
			currentUser.setChips(player.getChips());
            
			if(Main.useStubDatabase){
				StubDatabase.updateUser(currentUser);
			}
			else {
			UserService.updateUser((currentUser));
			}
			nextRound();
		}
	}
	
	public void dealerTurn() {
		while(dealer.getHand().calculatedValue()<17){
			dealer.hit(deck, discarded);
			updateScreen();
		}
	}
	
	public void non21Win() {
		dealerHandValue.setText("Dealer's Hand Value: " + dealer.getHand().calculatedValue());
		if(dealer.getHand().calculatedValue()>21){
			gameMessage.setText("Dealer busts! You Win!");
			currentUser.setWins(currentUser.getWins() + 1);
			player.winBet();
			currentUser.setChips(player.getChips());
		}else if(dealer.getHand().calculatedValue() > player.getHand().calculatedValue()){
			gameMessage.setText("Dealer Higher Hand! You Lose!");
			currentUser.setLosses(currentUser.getLosses() + 1);
			player.loseBet();
			currentUser.setChips(player.getChips());
		}else if(player.getHand().calculatedValue() > dealer.getHand().calculatedValue()){
			gameMessage.setText("Player Higher Hand! You Win!");
			currentUser.setWins(currentUser.getWins() + 1);
			player.winBet();
			currentUser.setChips(player.getChips());
		}else {
			gameMessage.setText("Equal Value Hands! Push!");
			currentUser.setPushes(currentUser.getPushes() + 1);
			player.pushBet();
			currentUser.setChips(player.getChips());
		}
		if(Main.useStubDatabase){
			StubDatabase.updateUser(currentUser);
		}else{
		UserService.updateUser(currentUser);
		}
        updateStatsDisplay();
	}
	
	public void updateStatsDisplay() {
		currentUser.setChips(player.getChips());
		chips.setText("Chips: " + currentUser.getChips());
        score.setText("Wins: " + currentUser.getWins() + 
		" Losses: " + currentUser.getLosses() + 
		" Pushes: " + currentUser.getPushes());
	    //chips.setText("Chips: " + player.getChips());
	}
	
	public void surrenderRound() {
	    player.surrenderBet();
	    gameMessage.setText("You surrendered!");
	    dealer.printHand(dealerCards);
        dealerHandValue.setText("Dealer's Hand Value: " + dealer.getHand().calculatedValue());
	    //added 2 lines for the updating the losses below
		currentUser.setLosses(currentUser.getLosses() + 1);
		currentUser.setChips(currentUser.getChips() - player.getBet());
		if(Main.useStubDatabase){
			StubDatabase.updateUser(currentUser);
		}else {
			UserService.updateUser(currentUser);
		}
		nextRound();
	}

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
	
	public void insuranceOrSurrender() {
		betAll.setVisible(false);
        bet100.setVisible(false);
        bet50.setVisible(false);
        surrender.setVisible(true);
        insurance.setVisible(true);
        neither.setVisible(true);
        
        dealer.printHand(dealerCards);
        player.printHand(playerCards);
        dealerCards[1].setIcon(new ImageIcon(new ImageIcon(IMAGE_DIR + "CardDown.png").getImage()
				.getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_DEFAULT)));
        
        dealerHandValue.setText("Dealer's hand value: " + dealer.getHand().getCard(0).getValue() + " + ?");
		playerHandValue.setText("Your Hand Value: " + player.getHand().calculatedValue());
		
		gameMessage.setText("Surrender or Insurance?");

	}
	
	public void revealAll() {
        dealer.printHand(dealerCards);
        player.printHand(playerCards);
        
        dealerHandValue.setText("Dealer's hand value: " + dealer.getHand().calculatedValue());
		playerHandValue.setText("Your Hand Value: " + player.getHand().calculatedValue());
	}
	
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
	
	public void insurance() {
		player.winInsurance();
		revealAll();
		gameMessage.setText("You were insured against the dealer");
	}
	
	public void restart() {
		 
		/*this.wins = 0;
		this.losses = 0;
		this.pushes = 0;
		this.roundsPlayed = 0;

		// Reset the GUI
		startRound();
		restart.setVisible(false);
		exit.setVisible(false);
		betAll.setVisible(true);
		bet100.setVisible(true);
		bet50.setVisible(true);
	}

		currentUser.setChips(1000);
        currentUser.setWins(0);
        currentUser.setLosses(0);
        currentUser.setPushes(0);
        StubDatabase.updateUser(currentUser);*/

		this.dealer = new Dealer();
		this.player = new Player(currentUser.getChips());

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