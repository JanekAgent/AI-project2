/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package put.ai.games.handsomeplayer;

import java.util.List;
import java.util.Random;
import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;

public class handsomeplayer extends Player {

    private Random random = new Random(0xdeadbeef);
    private boolean firstRund = true;
    private int size;
    private long timeOfRound;
    private Player.Color oponentColor;

    @Override
    public String getName() {
        return "Jan Skowron 151802 Kacper Mokrzycki 151893";
    }




    @Override
    public Move nextMove(Board b) {
        if (firstRund){
            System.out.println(b.getMovesFor(getColor()));
            size = b.getSize();
            firstRund = false;
            timeOfRound = this.getTime();
            oponentColor = getOpponent(getColor());
            
        }

        List<Move> moves = b.getMovesFor(getColor());
        return moves.get(random.nextInt(moves.size()));
    }
}
