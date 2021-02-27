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

        if(waitingForOpponent) {
            out.println("Waiting for opponent…");
            out.flush();
        }

        while(waitingForOpponent) {

            try { Thread.sleep(500);

            } catch (InterruptedException e) { e.printStackTrace(); }
        }

        out.print(this.toString(true));
        out.flush();
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

    public String toString(boolean justOwnBoard) {
        StringBuilder display = new StringBuilder();

        if (!justOwnBoard){
            display.append("Opponent Board \n");
            display.append("🍆 0️⃣ 1️⃣ 2️⃣ 3️⃣ 4️⃣ 5️⃣ 6️⃣ 7️⃣ 8️⃣ 9️⃣\n");
            for (int row=0; row< opponentBoard.length; row++) {
                display.append(numberToString(row)+" ");
                for (int col = 0; col < opponentBoard.length; col++) {
                    display.append(opponentBoard[col][row]+" ");
                }
                display.append("\n");
            }
            display.append("\n");
        }

        display.append("\n");
        display.append("My Board \n");
        display.append("🍆 0️⃣ 1️⃣ 2️⃣ 3️⃣ 4️⃣ 5️⃣ 6️⃣ 7️⃣ 8️⃣ 9️⃣\n");
        for (int row=0; row< ownBoard.length; row++) {
            display.append(numberToString(row)+" ");
            for (int col = 0; col < ownBoard.length; col++) {
                display.append(ownBoard[col][row]+" ");
            }
            display.append("\n");
        }



        return display.toString();
    }

    private String numberToString(int row) {
        switch (row){
            case 0:
                return "0️⃣";
            case 1:
                return "1️⃣";
            case 2:
                return "2️⃣";
            case 3:
                return "3️⃣";
            case 4:
                return "4️⃣";
            case 5:
                return "5️⃣";
            case 6:
                return "6️⃣";
            case 7:
                return "7️⃣";
            case 8:
                return "8️⃣";
            case 9:
                return "9️⃣";
            default:
                return "🍆";
        }
    }
}
