/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package put.ai.games.handsomeplayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;

public class handsomeplayermonte extends Player {
    private static final int MAX_ITERATIONS = 1000;
    private static final double UCT_CONSTANT = 1.414;
    // monte carlo tree search
    private Random random = new Random(0xdeadbeef);
    private boolean firstRund = true;
    private int size;
    private long timeOfRound;
    private Player.Color oponentColor;
    private Node root;
    private class Node {
        Board board;
        Move move;
        Node parent;
        List<Node> children;
        int winCount;
        int visitCount;
        int wining;
    

        Node(Board board, Move move, Node parent) {
            this.board = board;
            this.move = move;
            this.parent = parent;
            this.children = new ArrayList<>();
            this.wining = 0;
        }
        void addChild(Node child) {
            children.add(child);
        }

        // Aktualizacja statystyk węzła
        void updateStats(int result) {
            visitCount++;
            winCount += result;
        }
        double uctValue() {
            if (visitCount == 0) return Double.MAX_VALUE;
            return ((double) winCount / visitCount) + UCT_CONSTANT * Math.sqrt(Math.log(parent.visitCount) / visitCount);
        }
        
    }

    // Główna metoda MCTS
    public void MTSC(Board currentBoard, Color playerColor) {
        

        while (System.currentTimeMillis() < timeOfRound-200){
            Node selectedNode = selectNode(root);
            int result = simulateRandomPlayout(selectedNode, playerColor);
            backPropagate(selectedNode, result);
        }

       
    }
    public int simulateRandomPlayout(Node node, Color playerColor) {
        Node tempNode = node;
        Board tempBoard = tempNode.board.clone();
        int boardSize = tempBoard.getSize();
        int tempColor = playerColor.ordinal();
        int tempOpponentColor = oponentColor.ordinal();
        int tempResult = 0;
        while (tempBoard.getWinner(playerColor) == null) {
            List<Move> moves = tempBoard.getMovesFor(playerColor);
            tempBoard.doMove(moves.get(random.nextInt(moves.size())));
            tempColor = (tempColor + 1) % 2;
            tempOpponentColor = (tempOpponentColor + 1) % 2;
        }
        if (tempBoard.getWinner(playerColor) == playerColor) {
            tempResult = 1;
        } else if (tempBoard.getWinner(playerColor) == oponentColor) {
            tempResult = 0;
        }
        return tempResult;
    }
    

    private void backPropagate(Node node, int result) {
        while (node != null) {
            node.updateStats(result);
            node = node.parent;
        }
    }

    private Move getBestMove(Node rootNode) {
        return rootNode.children.stream()
                .max((node1, node2) -> Double.compare(node1.uctValue(), node2.uctValue()))
                .map(node -> node.move)
                .orElse(null);
    }

    private Node selectNode(Node rootNode) {
        Node currentNode = rootNode;
        while (!currentNode.children.isEmpty()) {
            currentNode = currentNode.children.stream()
                    .max((node1, node2) -> Double.compare(node1.uctValue(), node2.uctValue()))
                    .orElseThrow(RuntimeException::new);
        }
        return currentNode;
    }









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
        for (Node child : root.children) {
            if (child.board.equals(b)) {
                root = child;
                root.parent = null;
                break;
            }
        }
        
        MTSC(b, getColor());
        
        return getBestMove(root);
    }
}
