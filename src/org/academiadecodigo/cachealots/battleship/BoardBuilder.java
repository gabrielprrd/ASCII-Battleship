package org.academiadecodigo.cachealots.battleship;

import org.academiadecodigo.bootcamp.scanners.menu.MenuInputScanner;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class BoardBuilder {

    int jetski = BoatType.JETSKI.getQuantity();
    int fishingBoat = BoatType.FISHINGBOAT.getQuantity();
    int bigger = BoatType.BIGGER.getQuantity();
    int titanic = BoatType.TITANIC.getQuantity();

    int remainingBoats = jetski + fishingBoat + bigger + titanic;


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
    public String[][] build(Player player) {


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
           }


        }

        //menu

        // while(notDone)
        // --what ship?
        // --where (col/row)
        // --how(h/v, l/r|up/down)
        // --is this ok?

        //


        return null;
    }


    public boolean canBuildBoat(int playerChoice){

        return switch (playerChoice) {
            case 1 -> !(jetski == 0);
            case 2 -> !(fishingBoat == 0);
            case 3 -> !(bigger == 0);
            default -> !(titanic == 0);
        };

    }

    public ?? buildBoat(BoatType type){

        int cellsLeft = type.getSize();




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