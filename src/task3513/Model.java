package task3513;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.*;

public class Model {

    private static final int FIELD_WIDTH = 4;
    private  Tile[][] gameTiles;
    protected int score = 0;
    protected int maxTile = 0;
    private Stack<Tile[][]> previousStates = new Stack();
    private Stack<Integer> previousScores = new Stack();
    private boolean isSaveNeeded = true;


    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    public Model() {
        resetGameTiles();
    }

    private List<Tile> getEmptyTiles(){
        List<Tile> list = new ArrayList<>();
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles[i].length; j++) {
                if (gameTiles[i][j].isEmpty()) list.add(gameTiles[i][j]);
            }
        }
        return list;
    }

    public void addTile(){
        List<Tile> emptyTiles = getEmptyTiles();
        if(emptyTiles.size() > 0)
            emptyTiles.get((int)(emptyTiles.size() * Math.random())).value = (Math.random() < 0.9 ? 2 : 4);
    }

    public void resetGameTiles(){
        this.gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < gameTiles.length; i++){
            for (int j = 0; j < gameTiles[i].length; j++){
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    private boolean compressTiles(Tile[] tiles){
        boolean isCompressed = false;
        int index = 0;
        for(int i = 0; i < tiles.length; i++) {
            if(tiles[i].value == 0) continue;
            if(index == i) {
                index++;
                continue;
            }
            Tile tmp = tiles[index];
            tiles[index] = tiles[i];
            index++;
            tiles[i] = tmp;
            isCompressed = true;
        }
        return isCompressed;
    }

    private boolean mergeTiles(Tile[] tiles){
        boolean isMerged = false;
        for(int i = 0; i < tiles.length - 1; i++) {
            if(tiles[i].value == tiles[i + 1].value && tiles[i].value != 0) {
                tiles[i].value *= 2;
                isMerged = true;
                if (tiles[i].value > maxTile) {
                    maxTile = tiles[i].value;
                }
                score += tiles[i].value;
                tiles[i + 1] = new Tile();
                i++;
            }
        }
        if (isMerged) {
            compressTiles(tiles);
        }
        return isMerged;
    }


    private void rotateClockwise(){
        int [][] newField = new int[FIELD_WIDTH][FIELD_WIDTH];
        for (int x = 0; x < FIELD_WIDTH; x++)
            for (int y = 0; y < FIELD_WIDTH; y++)
                newField[x][y] = gameTiles[FIELD_WIDTH-y-1][x].value;
        for (int x = 0; x < FIELD_WIDTH; x++)
            for (int y = 0; y < FIELD_WIDTH; y++)
                gameTiles[x][y].value = newField[x][y];
    }

    public void left(){
        if (isSaveNeeded) saveState(gameTiles);
        boolean flag = false;
        for (int i=0;i <FIELD_WIDTH;i++){
            if (compressTiles(gameTiles[i])){ flag=true; }
            if (mergeTiles(gameTiles[i])) {
                flag=true;
                compressTiles(gameTiles[i]);
            }
        }
        if (flag) addTile();

        isSaveNeeded = true;
    }

    public void right() {
        saveState(gameTiles);
        rotateClockwise();
        rotateClockwise();
        left();
        rotateClockwise();
        rotateClockwise();
    }

    public void up() {
        saveState(gameTiles);
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
        left();
        rotateClockwise();
    }

    public void down() {
        saveState(gameTiles);
        rotateClockwise();
        left();
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
    }

    public boolean canMove(){
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles[0].length; j++) {
                if (gameTiles[i][j].value == 0)
                    return true;
                if (i != 0 && gameTiles[i - 1][j].value == gameTiles[i][j].value)
                    return true;
                if (j != 0 && gameTiles[i][j - 1].value == gameTiles[i][j].value)
                    return true;
            }
        }
        return false;
    }
    private void saveState (Tile[][]tiles){
        Tile[][] fieldToSave = new Tile[tiles.length][tiles[0].length];
        isSaveNeeded = false;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                fieldToSave[i][j] = new Tile(tiles[i][j].getValue());
            }
        }
        previousStates.push(fieldToSave);
        int scoreToSave = score;
        previousScores.push(scoreToSave);
    }

    public void rollback(){
        if(!previousScores.isEmpty()&&!previousStates.isEmpty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }

    public void randomMove(){
        int n = ((int) (Math.random() * 100)) % 4;
        try {
            Robot rb = new Robot();
            switch (n) {
                case 0: {
                    rb.keyPress(KeyEvent.VK_LEFT);
                    rb.keyRelease(KeyEvent.VK_LEFT);
                }
                break;
                case 1: {
                    rb.keyPress(KeyEvent.VK_RIGHT);
                    rb.keyRelease(KeyEvent.VK_RIGHT);
                }
                break;
                case 2: {
                    rb.keyPress(KeyEvent.VK_UP);
                    rb.keyRelease(KeyEvent.VK_UP);
                }
                break;
                case 3: {
                    rb.keyPress(KeyEvent.VK_DOWN);
                    rb.keyRelease(KeyEvent.VK_DOWN);
                }
                break;
            }
        } catch (AWTException e) {

        }
    }

    public boolean hasBoardChanged(){
        int weightGame = 0;
        int weightStack = 0;
        for (int i = 0; i < gameTiles.length; i++){
            for (int j = 0; j < gameTiles[i].length; j++){
                weightGame += gameTiles[i][j].value;
            }
        }
        Tile[][] tiles = (Tile[][])previousStates.peek();
        for (int i = 0; i < tiles.length; i++){
            for (int j = 0; j < tiles[i].length; j++){
                weightStack += tiles[i][j].value;
            }
        }
        if (weightGame != weightStack)
            return true;
        return false;
    }

    private MoveEfficiency getMoveEfficiency(Move move){
        move.move();
        if (!hasBoardChanged()) {
            rollback();
            return new MoveEfficiency(-1, 0, move);
        }

        int emptyTilesCount = 0;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].isEmpty()) {
                    emptyTilesCount++;
                }
            }
        }

        MoveEfficiency moveEfficiency = new MoveEfficiency(emptyTilesCount, score, move);
        rollback();

        return moveEfficiency;
    }


    public void autoMove(){
        PriorityQueue<MoveEfficiency> queue = new PriorityQueue<>(4, Collections.reverseOrder());
        queue.offer(getMoveEfficiency(this::up));
        queue.offer(getMoveEfficiency(this::down));
        queue.offer(getMoveEfficiency(this::right));
        queue.offer(getMoveEfficiency(this::left));
        queue.peek().getMove().move();


    }
}
