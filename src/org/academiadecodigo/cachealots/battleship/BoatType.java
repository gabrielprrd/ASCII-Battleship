package org.academiadecodigo.cachealots.battleship;

public enum BoatType {
     JETSKI("jetski", 2, 2),
     FISHINGBOAT("fishingBoat", 3, 2),
     BIGGER("bigger",4, 2),
     TITANIC("titanic", 6, 1);

     private String name;
     private int size;
     private int quantity;

     BoatType(String name, int size, int quantity) {
          this.name = name;
          this.size = size;
          this.quantity = quantity;
     }

     public int getSize() {
          return size;
     }

     public int getQuantity() {
          return quantity;
     }

     public String getName() {
          return name;
     }
}
