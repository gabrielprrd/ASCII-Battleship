package org.academiadecodigo.cachealots.battleship;

import org.academiadecodigo.bootcamp.Prompt;
import org.academiadecodigo.bootcamp.scanners.integer.IntegerInputScanner;
import org.academiadecodigo.bootcamp.scanners.integer.IntegerRangeInputScanner;
import org.academiadecodigo.bootcamp.scanners.menu.MenuInputScanner;
import sun.misc.Signal;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Player implements Runnable {

    private Player opponent;
    private Socket socket;
    private boolean waitingForOpponent;

    private final BattleshipServer server;
    private final BoardBuilder bibi;

    private final String[][] ownBoard;
    private final String[][] opponentBoard; //presentation : initially only waves
    private final PrintWriter out;
    private final Prompt prompt;
    private String[][] actualOpponentBoard;

    private boolean finishedBuilding;
    private boolean gameOver;
    private boolean myTurn;
    private boolean emoji;


    public Player(Socket socket, boolean waitingForOpponent, BattleshipServer server) throws IOException {
        this.socket = socket;
        this.waitingForOpponent = waitingForOpponent;
        this.server = server;

        bibi = new BoardBuilder(this);

        opponentBoard = bibi.buildDefault();
        ownBoard = bibi.buildDefault();

        out = new PrintWriter(socket.getOutputStream());
        prompt = new Prompt(socket.getInputStream(), new PrintStream(socket.getOutputStream()));

    }

    @Override
    public void run() {

        out.write(server.drawWelcomeBoat());

        out.flush();

        if(waitingForOpponent) {
            out.println("Waiting for opponent…");
            out.flush();
        }

        while(waitingForOpponent) {
            try { Thread.sleep(500);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
        String [] gameOptions = {"ASCII", "Emoji"};

        MenuInputScanner gameDisplay = new MenuInputScanner(gameOptions);
        gameDisplay.setMessage("Do you want you display to be with emojis or ASCII?");
        // do you want to play emoji or ascii
        int chooseDisplay =  prompt.getUserInput(gameDisplay);

        if (chooseDisplay ==1){
            printBoard(ownBoard);
        }else {
            out.print(this.toString(true));
            out.flush();
            emoji = true;
        }


        bibi.build(); //run() blocks here while player is building


        finishedBuilding = true;


        if(!opponent.isFinishedBuilding()) {
            out.println("\nWaiting for opponent to finish building...\n");
            out.flush();
            myTurn = true;
        }

        while (!opponent.isFinishedBuilding()){
            try { Thread.sleep(500);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }

        out.println("Both finished building!\n");
        out.flush();

        actualOpponentBoard = opponent.getBoard();

        try { startGame();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

    }


    private void startGame() throws InterruptedException, IOException {

        Signal.handle(new Signal("INT"),  // SIGINT
                signal -> System.out.println("Interrupted by Ctrl+C"));

        IntegerInputScanner askShotCol = new IntegerRangeInputScanner(0, 9);
        IntegerInputScanner askShotRow = new IntegerRangeInputScanner(0, 9);
        askShotCol.setMessage("\nWhere to shoot? (Column) ");
        askShotRow.setMessage("Where to shoot? (Row) ");

        String [] gameOptions = {"Shoot", "Show my board", "Show opponent board", "RAGE QUIT!"};

        MenuInputScanner gameMenu = new MenuInputScanner(gameOptions);
        gameMenu.setMessage("Your turn. Choose an option: ");


        int opponentBoatCells = BoatType.JETSKI.getSize() * BoatType.JETSKI.getQuantity() +
                BoatType.FISHINGBOAT.getSize() * BoatType.FISHINGBOAT.getQuantity() +
                BoatType.BIGGER.getSize() * BoatType.BIGGER.getQuantity() +
                BoatType.TITANIC.getSize() * BoatType.TITANIC.getQuantity();




        while (opponentBoatCells > 0){ //

            if (!myTurn) {
                out.println("Waiting for opponent to shoot...");
                out.flush();
            }


            while(!myTurn){
                if(gameOver) return;
                Thread.sleep(100);

            }


            while(true){ //my Turn in progress...

                boolean shoot = false;

                while (!shoot) {

                    int gameMenuChoice = prompt.getUserInput(gameMenu);

                    switch (gameMenuChoice) {
                        case 1:
                            shoot = true;
                            break;
                        case 2:
                            if (!emoji) {
                                printBoard(ownBoard);
                                break;
                            }
                            out.print(toString(true));
                            out.flush();
                            break;
                        case 3:
                            if (!emoji) {
                                printBoard(opponentBoard);
                                break;
                            }
                            out.print(printEmojiEnemyBoard());
                            out.flush();
                            break;
                        case 4:
                            out.println("\nYou gave up, little chicken.");
                            out.println(showEndGameText(false));
                            out.flush();

                            opponent.getOut().println("Opponent gave up.");
                            opponent.getOut().println(showEndGameText(true));
                            opponent.getOut().flush();


                            closeClients();
                            break;
                        default:
                            System.out.println("error in game menu!");
                            break;
                    }
                }

                int shotCol = prompt.getUserInput(askShotCol);
                int shotRow = prompt.getUserInput(askShotRow);


                if(opponentBoard[shotCol][shotRow].equals("💥") || opponentBoard[shotCol][shotRow].equals("💦️")) {
                    out.println("Invalid shot: You already shot here!\n"); out.flush();
                    continue;
                }

                out.println("\nSending shot...\n"); out.flush();


                //Shot Successful

                if(actualOpponentBoard[shotCol][shotRow].equals("️🚢️")) {

                    opponentBoard[shotCol][shotRow] = "💥";
                    opponent.updateBoard(shotCol, shotRow, "💥");
                    out.println("\n💥 HIT BOAT!!! \nYour turn again!\n\n");

                    showEquivalentBoard();

                    opponent.getOut().println("\nYour boat got shot!\n" +
                            "Coordinates: Col: " + shotCol + " | Row: " + shotRow + "\n");
                    opponent.getOut().flush();

                    opponentBoatCells--;

                    if(opponentBoatCells == 0) break;

                    opponent.getOut().println("Preparing to receive another shot...\n");
                    opponent.getOut().flush();

                }

                // Shot Unsuccessful

                else {

                    opponentBoard[shotCol][shotRow] = "💦️";
                    opponent.updateBoard(shotCol, shotRow, "💦️");

                    out.println("\n💦️ Hit water... \n");

                    showEquivalentBoard();


                    opponent.getOut().println("Opponent missed!\n" +
                            "Coordinates: Col: " + shotCol + " | Row: " + shotRow + "\n" +
                            "Your turn again!\n");
                    opponent.getOut().flush();

                    myTurn = false;
                    opponent.setTurn(true);
                    break;

                }

            }

        }

        opponent.setGameOver(true);

        opponent.getOut().println(showEndGameText(false));
        out.println(showEndGameText(true));
        closeClients();
    }


    //-------------------------

    public void closeClients() {

        opponent.getOut().flush();
        out.flush();

        server.eject(opponent);
        server.eject(this);

        try {
            opponent.closeSocket();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSocket() throws IOException {
        socket.close();
    }

    public String[][] getOpponentBoard() {
        return opponentBoard;
    }

    public boolean isEmoji() {
        return emoji;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
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

    public Socket getSocket() {
        return socket;
    }

    public boolean isFinishedBuilding() {
        return finishedBuilding;
    }

    public void updateBoard(int col, int row, String emoji){

        ownBoard[col][row] = emoji;

    }

    public void setTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    public String showEndGameText(boolean statusWin) throws UnknownHostException {

        String youWin = "\n" +
                "██╗   ██╗ ██████╗ ██╗   ██╗           \n" +
                "╚██╗ ██╔╝██╔═══██╗██║   ██║           \n" +
                " ╚████╔╝ ██║   ██║██║   ██║           \n" +
                "  ╚██╔╝  ██║   ██║██║   ██║           \n" +
                "   ██║   ╚██████╔╝╚██████╔╝           \n" +
                "   ╚═╝    ╚═════╝  ╚═════╝            \n" +
                "                                      \n" +
                "        ██╗    ██╗██╗███╗   ██╗    ██╗\n" +
                "        ██║    ██║██║████╗  ██║    ██║\n" +
                "        ██║ █╗ ██║██║██╔██╗ ██║    ██║\n" +
                "        ██║███╗██║██║██║╚██╗██║    ╚═╝\n" +
                "        ╚███╔███╔╝██║██║ ╚████║    ██╗\n" +
                "         ╚══╝╚══╝ ╚═╝╚═╝  ╚═══╝    ╚═╝\n" +
                "\n" +
                "Congratulations!!\n" +
                "\n" +
                "--------------\n" +
                "\n";

        String youLose = "\n" +
                "    ██╗   ██╗ ██████╗ ██╗   ██╗           \n" +
                "    ╚██╗ ██╔╝██╔═══██╗██║   ██║           \n" +
                "     ╚████╔╝ ██║   ██║██║   ██║           \n" +
                "      ╚██╔╝  ██║   ██║██║   ██║           \n" +
                "       ██║   ╚██████╔╝╚██████╔╝           \n" +
                "       ╚═╝    ╚═════╝  ╚═════╝            \n" +
                "██╗      ██████╗ ███████╗███████╗         \n" +
                "██║     ██╔═══██╗██╔════╝██╔════╝         \n" +
                "██║     ██║   ██║███████╗█████╗           \n" +
                "██║     ██║   ██║╚════██║██╔══╝           \n" +
                "███████╗╚██████╔╝███████║███████╗██╗██╗██╗\n" +
                "╚══════╝ ╚═════╝ ╚══════╝╚══════╝╚═╝╚═╝╚═╝\n" +
                "\n" +
                "Better luck next time!\n" +
                "\n" +
                "--------------\n" +
                "\n";

        String thankYou = "Thank you for playing\n" +
                "\n" +
                "-- B A T T O R U S H I P P O --\n" +
                "\n" +
                " ~ by: GabiGold, Rû, e VH ~\n" +
                " < A/C_ >  #57 < CACHEalots_ > \n" +
                "\n" +
                "Play again via netcat @ " + server.getAddress() + "\n";

        return (statusWin ? youWin : youLose).concat(thankYou);
    }

    public String toString(boolean justOwnBoard) {
        StringBuilder display = new StringBuilder();

        if (!justOwnBoard){
            display.append("Opponent Board \n");
            display.append("🍆 0️⃣  1️⃣  2️⃣  3️⃣  4️⃣  5️⃣  6️⃣  7️⃣  8️⃣  9️⃣\n");
            for (int row=0; row< opponentBoard.length; row++) {
                display.append(numberToString(row)+"  ");
                for (int col = 0; col < opponentBoard.length; col++) {
                    display.append(opponentBoard[col][row]+" ");
                }
                display.append("\n");
            }
            display.append("\n");
        }

        display.append("\n");
        display.append("My Board \n");
        display.append("🍆 0️⃣  1️⃣  2️⃣  3️⃣  4️⃣  5️⃣  6️⃣  7️⃣  8️⃣  9️⃣\n");
        for (int row=0; row< ownBoard.length; row++) {
            display.append(numberToString(row)+"  ");
            for (int col = 0; col < ownBoard.length; col++) {
                display.append(ownBoard[col][row]+" ");
            }
            display.append("\n");
        }



        return display.toString();
    }

    public String printEmojiEnemyBoard(){
        StringBuilder display = new StringBuilder();
        display.append("Opponent Board \n");
        display.append("🍆 0️⃣  1️⃣  2️⃣  3️⃣  4️⃣  5️⃣  6️⃣  7️⃣  8️⃣  9️⃣\n");
        for (int row=0; row< opponentBoard.length; row++) {
            display.append(numberToString(row)+"  ");
            for (int col = 0; col < opponentBoard.length; col++) {
                display.append(opponentBoard[col][row]+" ");
            }
            display.append("\n");
        }
        display.append("\n");
        return display.toString();
    }

    public String toString(){
        return toString(false);
    }

    public void showEquivalentBoard(){
        if (emoji) {
            out.println(this.printEmojiEnemyBoard());
        }else {
            printBoard(opponentBoard);
        }
        out.flush();

        if(opponent.isEmoji()){
            opponent.getOut().println(opponent.toString() + "\n");
        } else {

            opponent.printBoard(opponent.getBoard());

        }
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

    public void printBoard(String[][] board){

        StringBuilder builder = new StringBuilder("X 0 1 2 3 4 5 6 7 8 9 \n");

        String cell = "";

        for (int row = 0; row < board.length; row++) {

            builder.append(row).append(" ");

            for (int col = 0; col < board[row].length; col++) {

                cell = board[col][row];

                switch (cell){
                    case "️🚢️":
                        cell = "@ ";
                        break;
                    case "💦️":
                        cell = "O ";
                        break;
                    case "💥":
                        cell = "X ";
                        break;
                    case "🌊":
                        cell = "~ ";
                    default:
                        cell = "~ ";
                        break;

                }

                /*
                if (cell.equals("️🚢️")) {
                    cell = "@ ";
                } else {
                    cell = "~ ";
                }
                */

                builder.append(cell);

            }
            builder.append("\n");
        }
        out.println(builder.toString());
        out.flush();


    }

}
