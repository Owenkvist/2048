package task3513;

public class MoveEfficiency implements Comparable<MoveEfficiency> {
    private int numberOfEmptyTiles;
    private int score;
    private Move move;

    public Move getMove() {
        return move;
    }

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }


    public int compareTo(MoveEfficiency o) {
        int result = Integer.compare(numberOfEmptyTiles, o.numberOfEmptyTiles);
        if (result != 0) return result;

        return Integer.compare(score,o.score);
    }


}
