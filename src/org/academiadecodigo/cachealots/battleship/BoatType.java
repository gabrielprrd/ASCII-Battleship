package org.academiadecodigo.cachealots.battleship;

public enum BoatType {
     JETSKI(2, 2),
     FISHINGBOAT(3, 2),
     BIGGER(4, 2),
     TITANIC(6, 1);

     private int size;
     private int quantity;

     BoatType(int size, int quantity) {
          this.size = size;
          this.quantity = quantity;
     }

     public int getSize() {
          return size;
     }

     public int getQuantity() {
          return quantity;
     }


}
