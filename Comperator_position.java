import java.util.ArrayList;
import java.util.Comparator;

public class Comperator_position implements Comparator<Position> {
    @Override
    public int compare(Position a, Position b) {

        if (a.getNumPieces() > b.getNumPieces()) {
            return -1;
        } else {
            if (a.getNumPieces() < b.getNumPieces()) {
                return 1;
            } else {
                if (a.getX() > b.getX()) {
                    return 1;
                } else {
                    if (a.getX() < b.getX()) {
                        return -1;
                    } else {
                        if (a.getY() > b.getY()) {
                            return 1;
                        } else {
                            if (a.getY() < b.getY()) {
                                return -1;
                            }
                        }
                    }
                }
            }
        }
        return 1;
    }
}