package org.academiadecodigo.cachealots.battleship;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BattleshipServer {

    private boolean playerWaiting;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private List<Player> players;

    public static void main(String[] args) {


        BattleshipServer bs = null;
        try {
            bs = new BattleshipServer();
            bs.start();


        } catch (IOException e) { e.printStackTrace(); }
    }

    public BattleshipServer() throws IOException {

        serverSocket = new ServerSocket(8080);
        threadPool = Executors.newCachedThreadPool();
        players = Collections.synchronizedList(new ArrayList<>());


    }


    public void start() throws IOException {
        
        // we need to broadcast this message
        System.out.println("Starting BattoruShippo server...\n\n");

        System.out.println("\n" +
                "                                     |__\n" +
                "                                     |\\/\n" +
                "                                     ---\n" +
                "                                     / | [\n" +
                "                              !      | |||\n" +
                "                            _/|     _/|-++'\n" +
                "                        +  +--|    |--|--|_ |-\n" +
                "                     { /|__|  |/\\__|  |--- |||__/\n" +
                "                    +---------------___[}-_===_.'____                 /\\ \n" +
                "                ____`-' ||___-{]_| _[}-  |     |_[___\\==--            \\/   _\n" +
                " __..._____--==/___]_|__|_____________________________[___\\==--____,------' .7\n" +
                "|                                                           USS Cachealots /\n" +
                " \\_________________________________________________________________________|\n");

        System.out.println("Server running on port: " + serverSocket.getLocalPort());

        while(true){

            System.out.println("Waiting for connections…");

            Socket playerSocket = serverSocket.accept(); //blocks

            Player player = new Player(playerSocket, !playerWaiting, this); //player will wait if alone
            players.add(player);
            threadPool.submit(player);

            if(!playerWaiting) {
                playerWaiting = true;
                continue;
            }

            Player opponent = players.get(players.indexOf(player)-1);
            player.setOpponent(opponent);
            player.setWaitingForOpponent(false);
            opponent.setWaitingForOpponent(false);

            opponent.setOpponent(player);

            playerWaiting = false;

        }
    }

    public void eject(Player player){
        players.remove(player);
    }

    public String getAddress() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress() + ":" + serverSocket.getLocalPort();
    }

}
