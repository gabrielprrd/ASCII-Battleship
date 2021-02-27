package org.academiadecodigo.cachealots.battleship;

import examples.integer.IntegerRangeInputScannerTest;
import org.academiadecodigo.bootcamp.scanners.integer.IntegerInputScanner;
import org.academiadecodigo.bootcamp.scanners.integer.IntegerRangeInputScanner;
import org.academiadecodigo.bootcamp.scanners.menu.MenuInputScanner;
import org.academiadecodigo.bootcamp.scanners.string.StringInputScanner;
import org.academiadecodigo.bootcamp.scanners.string.StringSetInputScanner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class BoardBuilder {

    int jetski = BoatType.JETSKI.getQuantity();
    int fishingBoat = BoatType.FISHINGBOAT.getQuantity();
    int bigger = BoatType.BIGGER.getQuantity();
    int titanic = BoatType.TITANIC.getQuantity();
    private boolean buildSuccessful;

    int remainingBoats = jetski + fishingBoat + bigger + titanic;

    Player player;

    public BoardBuilder(Player player) {
        this.player = player;
    }

    public String[][] defaultBoard = new String[10][10];

    public String[][] buildDefault() {

        for (int col = 0; col < defaultBoard.length; col++) {
            for (int row = 0; row < defaultBoard.length; row++) {
                defaultBoard[col][row] = "🌊";
            }
        }

        return defaultBoard;
    }

    // return and array with
    public void build() {

        while (remainingBoats > 0) {

            String[] menuOptions = {
                    "Jetski (2 cells) (" + jetski + " left)",
                    "Fishing boat (3 cells) (" + fishingBoat + " left)",
                    "Bigger Fishing boat (4 cells) (" + bigger + " left)",
                    "Titanic (6 cells) (" + titanic + " left)"};

            MenuInputScanner menu = new MenuInputScanner(menuOptions);

            int choice = player.getPrompt().getUserInput(menu);

            if (canBuildBoat(choice)) {
                BoatType boat = BoatType.values()[choice - 1];
                buildBoat(boat);
                remainingBoats--;

                // decrements number of available boats for the specific option
                switch (boat.getName()){
                    case "jetski":
                        jetski--;
                    break;
                    case "fishingBoat":
                        fishingBoat--;
                    break;
                    case "bigger":
                        bigger--;
                        break;
                    case "titanic":
                        titanic--;
                        break;
                    default:
                        System.out.println("deu merda");
                        break;
                }

                // draw board through player's output stream
                player.getOut().print(player.toString(true));
                player.getOut().flush();
                buildSuccessful = false;
            }

        }

    }


    public boolean canBuildBoat(int playerChoice) {

        switch (playerChoice) {
            case 1:
                return !(jetski == 0);
            case 2:
                return !(fishingBoat == 0);
            case 3:
                return !(bigger == 0);
            case 4:
                return !(titanic == 0);
            default:
                return false;
        }

    }

    public void buildBoat(BoatType type) {


        int cellSize = type.getSize();

        IntegerInputScanner askCol = new IntegerRangeInputScanner(0, 9);
        IntegerInputScanner askRow = new IntegerRangeInputScanner(0, 9);

        askCol.setMessage("Column? ");
        askRow.setMessage("Row? ");

        String[] directions = {"Up", "Down", "Left", "Right", "Back"};

        MenuInputScanner directionMenu = new MenuInputScanner(directions);

        directionMenu.setMessage("Choose a direction: ");

        //---------------
        while (!buildSuccessful) {

            int col = player.getPrompt().getUserInput(askCol);
            int row = player.getPrompt().getUserInput(askRow);

            if (!player.getBoard()[col][row].equals("🌊")) {
                player.getOut().print("Invalid coordinates, try again!");
                continue;
            }

            int direction = player.getPrompt().getUserInput(directionMenu);

            if (checkIfCanDraw(col, row, direction, cellSize)) {
                draw(col, row, direction, cellSize);

            }
        }


    }

    // check if can draw and draw
    public boolean checkIfCanDraw(int col, int row, int direction, int cellSize) {

        switch (direction) {
            case 1:

                for (int thisRow = row; thisRow > row - cellSize; thisRow--) {
                    if(thisRow<0){return false;}
                    if (!player.getBoard()[col][thisRow].equals("🌊")) {
                        return false;
                    }
                }

                return true;

            case 2:
                for (int thisRow = row; thisRow < row + cellSize; thisRow++) {
                    if(thisRow>player.getBoard().length-1){return false;}
                    if (!player.getBoard()[col][thisRow].equals("🌊")) {
                        return false;
                    }
                }
                return true;

            case 3:
                for (int thisCol = col; thisCol > col -cellSize; thisCol--) {
                    if(thisCol<0){return false;}
                    if (!player.getBoard()[thisCol][row].equals("🌊")) {
                        return false;
                    }
                }
                return true;

            case 4:
                for (int thisCol = col; thisCol < col +  cellSize; thisCol++) {
                    if(thisCol>player.getBoard().length-1){return false;}
                    if (!player.getBoard()[thisCol][row].equals("🌊")) {
                        return false;
                    }
                }
                return true;

            default:

                return false;
        }

    }

    public void draw(int col, int row, int direction, int cellSize) {

        buildSuccessful = true;

        switch (direction) {
            case 1: //up
                for (int thisRow = row; thisRow > row-cellSize; thisRow--) {
                    player.updateBoard(col, thisRow);
                }
                break;
            case 2: //down
                for (int thisRow = row; thisRow < row+cellSize; thisRow++) {
                    player.updateBoard(col, thisRow);
                }
                break;
            case 3: //left
                for (int thisCol = col; thisCol > col-cellSize; thisCol--) {
                    player.updateBoard(thisCol, row);
                }
                break;
            case 4: //right
                for (int thisCol = col; thisCol < col+cellSize; thisCol++) {
                    player.updateBoard(thisCol, row);
                }
                break;
            default: //exit
                return;
        }

    }

}


/*
 *  menu
 *
 *  int playerChoice
 *
 *  public BoatType getBoatType (int playerChoice)
 *
 * if(playerCanBuildBoat(getBoatType(playerChoice))){
 *
 *  buildBoat(getBoatType(playerChoice))
 * }
 *
 *
 *
 *
 * public ?? buildBoat(BoatType type){
 *
 *       int cellsLeft = type.getSize();
 *
 *       while(cellsLeft > 0) {
 *
 * ------
 * cellsLeft--
 * }
 *
 *
 *
 * */