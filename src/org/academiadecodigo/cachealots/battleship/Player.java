package org.academiadecodigo.cachealots.battleship;

import java.net.Socket;

public class Player implements Runnable {

    private Player opponent;
    private Socket clientSocket;
    private boolean waitingForOpponent;

    private String[][] ownBoard;
    private String[][] opponentBoard; //presentation
    private String[][] actualOpponentBoard;

    public Player(Socket clientSocket, boolean waitingForOpponent) {
        this.clientSocket = clientSocket;
        this.waitingForOpponent = waitingForOpponent;
    }

    @Override
    public void run() {

        if(waitingForOpponent) { System.out.println("Waiting for opponent…"); }

        while(waitingForOpponent) {

            try { Thread.sleep(500);

            } catch (InterruptedException e) { e.printStackTrace(); }

        }





    }




    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    public void setWaitingForOpponent(boolean waitingForOpponent) {
        this.waitingForOpponent = waitingForOpponent;
    }
}
