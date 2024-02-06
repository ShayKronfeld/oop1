import java.util.ArrayList;

public class Pawn extends ConcretePiece {

    public Pawn(ConcretePlayer owner) {
        this.whoIsTheOwner = owner;
        this.killed = 0;
        if (owner.isPlayerOne()) {
           this.type = "♟";
        } else {
            this.type = "♙";
        }
        this.steps = new ArrayList<>();
    }
    public void kill() {
        killed++;
    }
    public void reduceKill() {
        killed--;
    }

    public int getKills(){ return this.killed; }

    @Override
    public ConcretePlayer getOwner() {
        return this.whoIsTheOwner;
   }

    public String getType() {
        if (!this.whoIsTheOwner.isPlayerOne())
            return  "♟";
        return "♙";
    }

    public String colorType(){
        if (!this.whoIsTheOwner.isPlayerOne())
            return "black";
        return "white";
    }

    public int distance() {
        int dist = 0;
       // System.out.println("this.steps.size" + this.steps.size());
        for (int i = 0; i < this.steps.size()-1; i++) {
            Position current = this.steps.get(i);
            Position next = this.steps.get(i + 1);

            if (current.getX() == next.getX()) {
                dist += Math.abs(current.getY() - next.getY());
            } else if (current.getY() == next.getY()) {
                dist += Math.abs(current.getX() - next.getX());
            }
        }
        return dist;
    }


}
