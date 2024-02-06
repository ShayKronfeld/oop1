import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class GameLogic implements PlayableLogic {
    private ConcretePiece[][] board = new ConcretePiece[11][11];
    private ArrayList<ConcretePiece> [][] positionBoard = new ArrayList [11][11];
    private Position [][] position= new Position[11][11];
    private ArrayList<Position> listPosition= new ArrayList<>();
    private final ConcretePlayer defender; //black
    private final ConcretePlayer attacker; //white
    private boolean isAttackerTurn;
    private boolean finish;
    public static boolean theWinnerIsPlayerOne;
    private ArrayList<ConcretePiece> pieces= new ArrayList<>();
    private ArrayList<ConcretePiece> piecesOnTheBoard= new ArrayList<>();
     private ArrayList<Position> list = new ArrayList<>();
    GameLogic() {
        this.defender = new ConcretePlayer(true);
        this.attacker = new ConcretePlayer(false);
        reset();
    }

    public boolean move(Position a, Position b) {

        boolean isAttackerrTurn = isSecondPlayerTurn();
        boolean defenderPosition = getPieceAtPosition(a).getOwner().isPlayerOne();

        if ((isAttackerrTurn && defenderPosition) || (!isAttackerrTurn && !defenderPosition))
            return false;
        if (!legal(a, b))
            return false;

        String name= board[a.getX()][a.getY()].getName();
        boolean flag=false;
        for (int i=0; i<positionBoard[b.getX()][b.getY()].size(); i++){
            if (positionBoard[b.getX()][b.getY()].get(i).getName().equals(name))
            {
                flag=true;
            }
        }
        if (!flag){
            positionBoard[b.getX()][b.getY()].add(board[a.getX()][a.getY()]);
        }
        
        board[a.getX()][a.getY()].addSteps(b);

        checkKilling(a, b);
        ConcretePiece pieceThatMoving = board[a.getX()][a.getY()]; //create the piece that we want to moving
        board[b.getX()][b.getY()] = pieceThatMoving;
        board[a.getX()][a.getY()] = null;

        this.isAttackerTurn = !this.isAttackerTurn;

        ConcretePlayer finish= isFinish(b);
        if (this.finish) {
            if (finish.getType().equals("defender")) {
                this.defender.updateWins();
                theWinnerIsPlayerOne=false;
            } else {
                if (finish.getType().equals("attacker")) {
                    this.attacker.updateWins();
                    theWinnerIsPlayerOne=true;
                }
            }

            printComperator();
            reset();
        }
        return true;
    }

    public ConcretePiece getPieceAtPosition(Position position) {

        return this.board[position.getX()][position.getY()];
    }

    public ConcretePlayer getFirstPlayer() {
        return this.defender;
    }

    public ConcretePlayer getSecondPlayer() {
        return this.attacker;
    }

    public Boolean gameFinished() { return this.finish;
    }


    public boolean isSecondPlayerTurn() {
        return this.isAttackerTurn;
    }

    public void reset() {

        this.isAttackerTurn = true;

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                board[i][j] = null;
            }
        }

        ConcretePlayer defender = new ConcretePlayer(true);

        for (int i = 3; i <= 7; i++) {
            board[0][i] = new Pawn(defender);
            board[i][0] = new Pawn(defender);
            board[10][i] = new Pawn(defender);
            board[i][10] = new Pawn(defender);
        }
        board[1][5] = new Pawn(defender);
        board[5][1] = new Pawn(defender);
        board[9][5] = new Pawn(defender);
        board[5][9] = new Pawn(defender);

        ConcretePlayer attackerr = new ConcretePlayer(false);
        for (int i = 4; i <= 6; i++) {
            board[4][i] = new Pawn(attackerr);
            board[6][i] = new Pawn(attackerr);
        }
        board[3][5] = new Pawn(attackerr);
        board[7][5] = new Pawn(attackerr);
        board[5][3] = new Pawn(attackerr);
        board[5][4] = new Pawn(attackerr);
        board[5][6] = new Pawn(attackerr);
        board[5][7] = new Pawn(attackerr);

        board[5][5] = new King(attackerr);


        for (int i = 0; i < positionBoard.length; i++) {
            for (int j = 0; j < positionBoard[i].length; j++) {
                positionBoard[i][j] = new ArrayList<>();
            }
        }

        for (int i = 0; i < position.length; i++) {
            for (int j = 0; j < position[i].length; j++) {
                position[i][j] = new Position(i, j);
            }
        }


        int counterD = 1, counterA = 1;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] != null) {
                    positionBoard[i][j].add(board[i][j]);
                    pieces.add(board[i][j]);
                    piecesOnTheBoard.add(board[i][j]);
                    Position p = new Position(i, j);
                    board[i][j].addSteps(p);

                    if (board[j][i] instanceof Pawn) {
                        if (board[j][i].getOwner().isPlayerOne()) {
                            board[j][i].setName("D" + counterD);
                            counterD++;
                        } else {
                            if (!board[j][i].getOwner().isPlayerOne()) {
                                board[j][i].setName("A" + counterA);
                                counterA++;

                            }
                        }
                    } else {
                        board[j][i].setName("K" + counterD);
                        counterD++;
                    }
                }
            }
        }

    }


    public void undoLastMove() {

    }

    public int getBoardSize() {
        return board.length;
    }

    public boolean isGameFinished() {
        if (gameFinished())
            return true;
        return false;
    }

    private boolean isEmptyAtPosition(Position pos) {
        if (getPieceAtPosition(pos) != null)
            return false;
        return true;
    }

    private Position whereIsTheKing(ConcretePiece[][] find) {
        for (int i = 0; i < find.length; i++)
            for (int j = 0; j < find.length; j++)
                if (find[i][j] instanceof King) {
                    Position p = new Position(i, j);
                    return p;
                }
        return null;
    }

private void checkKilling(Position a, Position b) {

    Position xl2 = new Position(b.getX() - 2, b.getY());
    Position xr2 = new Position(b.getX() + 2, b.getY());
    Position yu2 = new Position(b.getX(), b.getY() - 2);
    Position yd2 = new Position(b.getX(), b.getY() + 2);

    Position xl1 = new Position(xl2.getX() + 1, xl2.getY());
    Position xr1 = new Position(xr2.getX() - 1, xr2.getY());
    Position yu1 = new Position(yu2.getX(), yu2.getY() + 1);
    Position yd1 = new Position(yd2.getX(), yd2.getY() - 1);

    if (!(board[a.getX()][a.getY()] instanceof King)) {
        if (b.getX() != 0) {
            if (xl1.getX() == 0 || (board[xl2.getX()][xl2.getY()] != null && (board[xl2.getX()][xl2.getY()] instanceof Pawn) && board[xl2.getX()][xl2.getY()].getOwner().isPlayerOne() == board[a.getX()][a.getY()].getOwner().isPlayerOne()) || sides(xl2)) {
                if (board[xl1.getX()][xl1.getY()] instanceof Pawn && board[xl1.getX()][xl1.getY()].getOwner().isPlayerOne() != board[a.getX()][a.getY()].getOwner().isPlayerOne()) {
                    list.add(xl1);
                    ((Pawn) board[a.getX()][a.getY()]).kill();
                    for (int i=0; i<piecesOnTheBoard.size(); i++){
                        if (board[xl1.getX()][xl1.getY()]== piecesOnTheBoard.get(i))
                            piecesOnTheBoard.remove(i);
                    }
                    board[xl1.getX()][xl1.getY()] = null;
                }
            }
        }
        if (xr1.getX() == 10 || (xr2.getX() <11 && board[xr2.getX()][xr2.getY()] != null && board[xr2.getX()][xr2.getY()] instanceof Pawn && board[xr2.getX()][xr2.getY()].getOwner().isPlayerOne() == board[a.getX()][a.getY()].getOwner().isPlayerOne()) || sides(xr2)) {
            if (board[xr1.getX()][xr1.getY()] instanceof Pawn && board[xr1.getX()][xr1.getY()].getOwner().isPlayerOne() != board[a.getX()][a.getY()].getOwner().isPlayerOne()) {
                list.add(xr1);
                ((Pawn) board[a.getX()][a.getY()]).kill();
                for (int i=0; i<piecesOnTheBoard.size(); i++){
                    if (board[xr1.getX()][xr1.getY()] == piecesOnTheBoard.get(i))
                        piecesOnTheBoard.remove(i);
                }
                board[xr1.getX()][xr1.getY()] = null;
            }
        }

        if (b.getY() != 0) {
            if (yu1.getY() == 0 || (board[yu2.getX()][yu2.getY()] != null && board[yu2.getX()][yu2.getY()] instanceof Pawn && board[yu2.getX()][yu2.getY()].getOwner().isPlayerOne() == board[a.getX()][a.getY()].getOwner().isPlayerOne()) || sides(yu2)) {
                if (board[yu1.getX()][yu1.getY()] instanceof Pawn && board[yu1.getX()][yu1.getY()].getOwner().isPlayerOne() != board[a.getX()][a.getY()].getOwner().isPlayerOne()) {
                    list.add(yu1);
                    ((Pawn) board[a.getX()][a.getY()]).kill();
                    for (int i=0; i<piecesOnTheBoard.size(); i++){
                        if (board[yu1.getX()][yu1.getY()] == piecesOnTheBoard.get(i))
                            piecesOnTheBoard.remove(i);
                    }
                    board[yu1.getX()][yu1.getY()] = null;
                }
            }
        }
        if (yd1.getY() == 10 || (yd2.getY()< 11 && board[yd2.getX()][yd2.getY()] != null && board[yd2.getX()][yd2.getY()] instanceof Pawn && board[yd2.getX()][yd2.getY()].getOwner().isPlayerOne() == board[a.getX()][a.getY()].getOwner().isPlayerOne()) || sides(yd2)) {
            if (board[yd1.getX()][yd1.getY()] instanceof Pawn && board[yd1.getX()][yd1.getY()].getOwner().isPlayerOne() != board[a.getX()][a.getY()].getOwner().isPlayerOne()) {
                list.add(yd1);
                ((Pawn) board[a.getX()][a.getY()]).kill();
                for (int i=0; i<piecesOnTheBoard.size(); i++){
                    if (board[yd1.getX()][yd1.getY()]  == piecesOnTheBoard.get(i))
                        piecesOnTheBoard.remove(i);
                }
                board[yd1.getX()][yd1.getY()] = null;
            }
        }
    }
}
    private boolean legal (Position a, Position b){
        if (isEmptyAtPosition(a) || !isEmptyAtPosition(b) || (a.getX()!=b.getX() && a.getY()!=b.getY()) || (a.getX()==b.getX() && a.getY()==b.getY()))
            return false;
        if (board[a.getX()][a.getY()] instanceof Pawn && ( (b.getX()==0 && b.getY()==0) || (b.getX()==0 && b.getY()==10) || (b.getX()==10 && b.getY()==0) || (b.getX()==10 && b.getY()==10)) )
            return false;

        if (a.getX()==b.getX()) {
            if (a.getY() < b.getY()) {
                for (int i = a.getY() + 1; i <= b.getY(); i++) {
                    if (board[a.getX()][i] != null) {
                        return false;
                    }
                }
            } else {
                for (int i = a.getY() - 1; i >= b.getY(); i--) {
                    if (board[a.getX()][i] != null) {
                        return false;
                    }
                }
            }
        }
        if (a.getY()==b.getY()) {
            if (a.getX() < b.getX()) {
                for (int i = a.getX() + 1; i <= b.getX(); i++) {
                    if (board[i][a.getY()] != null) {
                        return false;
                    }
                }
            } else {
                for (int i = a.getX() - 1; i >= b.getX(); i--) {
                    if (board[i][a.getY()] != null) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private ConcretePlayer isFinish(Position b){
        Position king= whereIsTheKing (board);
        ConcretePlayer whoIsTheWinner=null;
        boolean fl= true;
        for (int i=0; i<piecesOnTheBoard.size(); i++){ // check if the defender eat all the attacker
            if (!piecesOnTheBoard.get(i).getOwner().isPlayerOne())
                fl=false;
        }
        if (fl){
            whoIsTheWinner= this.defender;
            this.finish = true;
        }
        else {
            if (sides(king)) {
                whoIsTheWinner = this.defender;
                this.finish = true;
            } else {
                Position lKing = new Position(king.getX() - 1, king.getY());
                Position rKing = new Position(king.getX() + 1, king.getY());
                Position uKing = new Position(king.getX(), king.getY() - 1);
                Position dKing = new Position(king.getX(), king.getY() + 1);

                if (king.getX() == 0) {
                    if (king.getY() == 1) {
                        if (whoIsTheOwnerPawm(rKing) == "black" && whoIsTheOwnerPawm(dKing) == "black") {
                            whoIsTheWinner = this.attacker;
                            this.finish = true;
                        }
                    } else {
                        if (king.getY() == 9) {
                            if (whoIsTheOwnerPawm(rKing) == "black" && whoIsTheOwnerPawm(uKing) == "black") {
                                whoIsTheWinner = this.attacker;
                                this.finish = true;
                            }
                        } else {
                            if (whoIsTheOwnerPawm(rKing) == "black" && whoIsTheOwnerPawm(dKing) == "black" && whoIsTheOwnerPawm(uKing) == "black") {
                                whoIsTheWinner = this.attacker;
                                this.finish = true;
                            }
                        }
                    }
                } else {
                    if (king.getX() == 10) {
                        if (king.getY() == 1) {
                            if (whoIsTheOwnerPawm(lKing) == "black" && whoIsTheOwnerPawm(dKing) == "black") {
                                whoIsTheWinner = this.attacker;
                                this.finish = true;
                            }
                        } else {
                            if (king.getY() == 9) {
                                if (whoIsTheOwnerPawm(lKing) == "black" && whoIsTheOwnerPawm(uKing) == "black") {
                                    whoIsTheWinner = this.attacker;
                                    this.finish = true;
                                }
                            } else {
                                if (whoIsTheOwnerPawm(lKing) == "black" && whoIsTheOwnerPawm(dKing) == "black" && whoIsTheOwnerPawm(uKing) == "black") {
                                    whoIsTheWinner = this.attacker;
                                    this.finish = true;
                                }
                            }
                        }
                    } else {
                        if (king.getY() == 0) {
                            if (king.getX() == 1) {
                                if (whoIsTheOwnerPawm(rKing) == "black" && whoIsTheOwnerPawm(dKing) == "black") {
                                    whoIsTheWinner = this.attacker;
                                    this.finish = true;
                                }
                            } else {
                                if (king.getX() == 9) {
                                    if (whoIsTheOwnerPawm(lKing) == "black" && whoIsTheOwnerPawm(dKing) == "black") {
                                        whoIsTheWinner = this.attacker;
                                        this.finish = true;
                                    }
                                } else {
                                    if (whoIsTheOwnerPawm(rKing) == "black" && whoIsTheOwnerPawm(lKing) == "black" && whoIsTheOwnerPawm(dKing) == "black") {
                                        whoIsTheWinner = this.attacker;
                                        this.finish = true;
                                    }
                                }
                            }
                        } else {
                            if (king.getY() == 10) {
                                if (king.getX() == 1) {
                                    if (whoIsTheOwnerPawm(rKing) == "black" && whoIsTheOwnerPawm(uKing) == "black") {
                                        whoIsTheWinner = this.attacker;
                                        this.finish = true;
                                    }
                                } else {
                                    if (king.getX() == 9) {
                                        if (whoIsTheOwnerPawm(lKing) == "black" && whoIsTheOwnerPawm(uKing) == "black") {
                                            whoIsTheWinner = this.attacker;
                                            this.finish = true;
                                        }
                                    } else {
                                        if (whoIsTheOwnerPawm(rKing) == "black" && whoIsTheOwnerPawm(lKing) == "black" && whoIsTheOwnerPawm(uKing) == "black") {
                                            whoIsTheWinner = this.attacker;
                                            this.finish = true;
                                        }
                                    }
                                }
                            } else {
                                if (whoIsTheOwnerPawm(lKing) == "black" && whoIsTheOwnerPawm(rKing) == "black" && whoIsTheOwnerPawm(uKing) == "black" && whoIsTheOwnerPawm(dKing) == "black") {
                                    whoIsTheWinner = this.attacker;
                                    this.finish = true;
                                } else {
                                    this.finish = false;
                                }
                            }

                        }
                    }

                }
            }
        }
        return whoIsTheWinner;
    }




    private boolean sides (Position p){
        if ((p.getX() == 0 && p.getY() == 0) ||
                (p.getX() == 0 && p.getY() == 10) ||
                (p.getX() == 10 && p.getY() == 0) ||
                (p.getX() == 10 && p.getY() == 10)) {
            return true;
        }
        return false;
    }

    private String whoIsTheOwnerPawm (Position p){
        if(board[p.getX()][p.getY()] instanceof Pawn){
            return ((Pawn) board[p.getX()][p.getY()]).colorType();
        }
        return null;
    }

    private ConcretePiece[][] getBoard (){ return this.board; }

    private void printComperator (){
        Comperator_distance comperator_distance= new Comperator_distance ();
        Comperator_kills comperator_kills= new Comperator_kills ();
        Comperator_steps comperator_steps= new Comperator_steps ();
        Comperator_position Comperator_position= new Comperator_position ();

        Collections.sort(pieces,comperator_steps);
        for (int i=0; i< pieces.size(); i++){
            if (pieces.get(i).getNumberSteps()>1) {
                System.out.print(pieces.get(i).getName() + ": [");
                for (int j = 0; j < pieces.get(i).getSteps().size(); j++) {
                    System.out.print("(" + pieces.get(i).getSteps().get(j).getX() + ", " + pieces.get(i).getSteps().get(j).getY() + ")");
                    if (pieces.get(i).getSteps().size()-j>1)
                        System.out.print(", ");
                }
                System.out.print("]");
                System.out.println("");
            }
        }
        System.out.println("***************************************************************************");

        Collections.sort(pieces,comperator_kills);
        for (int i=0; i< pieces.size(); i++){
            if (pieces.get(i).getKills()>0) {
                System.out.print(pieces.get(i).getName() + ": ");
                System.out.print(pieces.get(i).getKills() + " kills");
                System.out.println("");
            }
        }
        System.out.println("***************************************************************************");

        Collections.sort(pieces,comperator_distance);
        for (int i=0; i< pieces.size(); i++){
            if (pieces.get(i).getNumberSteps()>1){
                System.out.print(pieces.get(i).getName()+ ": ");
                System.out.print(pieces.get(i).distance() + " squares");
                System.out.println("");
            }
        }

        System.out.println("***************************************************************************");

        for (int i=0; i<position.length; i++){
            for (int j=0; j<position.length; j++){
                int size= positionBoard[i][j].size();
                position[i][j].setNumPieces(size);
                listPosition.add(position[i][j]);
            }
        }

        Collections.sort(listPosition,Comperator_position);
        for (int i=0; i< listPosition.size(); i++){
            if (listPosition.get(i).getNumPieces()>1){
                System.out.print("("+ listPosition.get(i).getX()+ ", "+ listPosition.get(i).getY()+ ")");
                System.out.print(listPosition.get(i).getNumPieces() + " pieces");
                System.out.println("");
            }
        }

        System.out.println("***************************************************************************");
    }
}