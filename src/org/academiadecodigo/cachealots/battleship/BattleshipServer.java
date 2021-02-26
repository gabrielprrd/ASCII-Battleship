package org.academiadecodigo.cachealots.battleship;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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

        while(true){

            Socket playerSocket = serverSocket.accept(); //blocks

            Player player = new Player(playerSocket, !playerWaiting); //player will wait if alone
            players.add(player);
            threadPool.submit(player);

            if(!playerWaiting) {
                playerWaiting = true;
                continue;
            }

            Player opponent = players.get(players.indexOf(player)-1);
            player.setOpponent(opponent);
            player.setWaitingForOpponent(false);

            opponent.setOpponent(player);

            playerWaiting = false;

            //


        }
    }
}
