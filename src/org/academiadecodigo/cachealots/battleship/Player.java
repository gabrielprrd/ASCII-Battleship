package org.academiadecodigo.cachealots.battleship;

import org.academiadecodigo.bootcamp.Prompt;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Player implements Runnable {

    private Player opponent;
    private Socket socket;
    private boolean waitingForOpponent;

    private String[][] ownBoard;
    private String[][] opponentBoard; //presentation : initially only waves
    private String[][] actualOpponentBoard;
    private BoardBuilder bibi;

    private final PrintWriter out;
    private final Prompt prompt;

    public Player(Socket socket, boolean waitingForOpponent) throws IOException {
        this.socket = socket;
        this.waitingForOpponent = waitingForOpponent;
        bibi = new BoardBuilder(this);

        ownBoard = bibi.buildDefault();
        opponentBoard = bibi.buildDefault();

        out = new PrintWriter(socket.getOutputStream());
        prompt = new Prompt(socket.getInputStream(), new PrintStream(socket.getOutputStream()));

    }

    @Override
    public void run() {

        if(waitingForOpponent) { System.out.println("Waiting for opponent…"); }

        while(waitingForOpponent) {

            try { Thread.sleep(500);

            } catch (InterruptedException e) { e.printStackTrace(); }
        }


        bibi.build();




    }

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    public void setWaitingForOpponent(boolean waitingForOpponent) {
        this.waitingForOpponent = waitingForOpponent;
    }

    public String[][] getBoard() {
        return ownBoard;
    }

    public Prompt getPrompt() {
        return prompt;
    }

    public PrintWriter getOut() {
        return out;
    }

    public void updateBoard(int col, int row){

        ownBoard[col][row] = "🚢";

    }

}
