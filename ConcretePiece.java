import java.util.ArrayList;

public abstract class ConcretePiece implements Piece
{
    protected String type;
    protected ConcretePlayer whoIsTheOwner;
    public ArrayList<Position> steps;
    public String name;
    public int killed;
    public String getType(){
        return this.type;
    }

    public ConcretePlayer getOwner(){
        return whoIsTheOwner;
    }

    public ArrayList<Position> getSteps() {return this.steps; }

    public void addSteps(Position add) {this.steps.add(add); }

    public int getNumberSteps () {return this.steps.size(); }
    public String getName(){ return this.name; }
    public void setName(String name){ this.name= name; }

    public int getKills() {
        return killed;
    }

    public abstract int distance();


}
