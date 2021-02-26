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

    int remainingBoats = jetski + fishingBoat + bigger + titanic;

    Player player;

    public BoardBuilder(Player player) {
        this.player = player;
    }

    public String[][] defaultBoard=new String[10][10];

    public String[][] buildDefault() {

        for (int col=0; col< defaultBoard.length; col++) {
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

            if (canBuildBoat(choice)){
                buildBoat(BoatType.values()[choice-1]);
                remainingBoats--;
            }

        }

        //menu

        // while(notDone)
        // --what ship?
        // --where (col/row)
        // --how(h/v, l/r|up/down)
        // --is this ok?

        //


    }


    public boolean canBuildBoat(int playerChoice){

        return switch (playerChoice) {
            case 1 -> !(jetski == 0);
            case 2 -> !(fishingBoat == 0);
            case 3 -> !(bigger == 0);
            default -> !(titanic == 0);
        };

    }

    public void buildBoat(BoatType type){


        int cellsLeft = type.getSize();

        IntegerInputScanner askCol = new IntegerRangeInputScanner(1, 10);
        IntegerInputScanner askRow = new IntegerRangeInputScanner(1, 10);

        askCol.setMessage("Column? ");
        askRow.setMessage("Row? ");

        Set<String> hvOptions = new HashSet<>();
        hvOptions.add("horizontal");
        hvOptions.add("vertical");
        hvOptions.add("exit");

        StringInputScanner askHorizontalVertical = new StringSetInputScanner(hvOptions);

        Set<String> horizontalOptions = new HashSet<>();
        horizontalOptions.add("left");
        horizontalOptions.add("right");
        horizontalOptions.add("exit");


        StringInputScanner askHorizontalDirection = new StringSetInputScanner(horizontalOptions);

        Set<String> verticalOptions = new HashSet<>();
        verticalOptions.add("up");
        verticalOptions.add("down");
        verticalOptions.add("exit");


        StringInputScanner askVerticalDirection = new StringSetInputScanner(verticalOptions);


        //---------------

        while (cellsLeft > 0) {

            int col = player.getPrompt().getUserInput(askCol);
            int row = player.getPrompt().getUserInput(askRow);

            if(!player.getBoard()[col][row].equals("🌊")){
                player.getOut().print("Invalid coordinates, try again!");
                continue;
            }

            String hv= player.getPrompt().getUserInput(askHorizontalVertical);

            String direction;

            switch (hv) {
                case "horizontal":
                    direction = player.getPrompt().getUserInput(askHorizontalDirection);
                    break;
                case "vertical":
                    direction = player.getPrompt().getUserInput(askVerticalDirection);
                    break;
                default:
                    continue;
            }

            switch (direction){
                case "up":

                case "down":

                case "left":

                default:


            }

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