/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package put.ai.games.naiveplayer;

import java.util.List;
import java.util.Random;
import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;
import java.util.ArrayList;

public class NaivePlayer extends Player {

    public static void main(String[] args) {}
    private static final int MAX_ITERATIONS = 100;
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
        Color moveOf;
    

        Node(Board board, Move move, Node parent) {
            this.board = board;
            this.move = move;
            this.parent = parent;
            if (parent != null) {
                this.moveOf = getOpponent(parent.moveOf);
            } else {
                this.moveOf = getColor();
            }
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
    public void MTSC(Board currentBoard) {
        
        
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            
            
            Node selectedNode = selectNode(root);
            expansion(selectedNode);
            Node nodeToExplore = selectedNode.children.get(random.nextInt(selectedNode.children.size()));
            int result = simulateRandomPlayout(selectedNode);  
            backPropagate(selectedNode, result);
        }

       
    }
    public int simulateRandomPlayout(Node node) {
        Node tempNode = node;
        Board tempBoard = tempNode.board.clone();
        int boardSize = tempBoard.getSize();
        Color tempColor = tempNode.moveOf;
        Color pponentColor = getOpponent(getColor());
        
        int tempResult = 0;
        while (tempBoard.getWinner(getColor()) == null ) {
            List<Move> moves = tempBoard.getMovesFor(tempColor);
            tempBoard.doMove(moves.get(random.nextInt(moves.size())));
            if (tempColor == getColor()) {
                tempColor = getOpponent(getColor());
            } else {
                tempColor = getColor();
            }
            
        }
        if (tempBoard.getWinner(tempColor) == getColor()) {
            tempResult = 1;
        } else if (tempBoard.getWinner(tempColor) == oponentColor) {
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
        Move bestmove = null;
        double best = -1;
        for (Node child : rootNode.children) {
            if (child.uctValue() > best) {
                
                bestmove = child.move;
            }
        }
        System.out.println("koniec generowania");
        System.out.println(bestmove);
        // System.out.println(rootNode.children);
        return bestmove;
    }

    private Node selectNode(Node rootNode) {
        Node node = rootNode;
        while (node.children.size() != 0) {
            node = node.children.stream()
                    .max((node1, node2) -> Double.compare(node1.uctValue(), node2.uctValue()))
                    .orElseThrow(IllegalStateException::new);
        }
        // System.out.println("koniec wyboru");
        // System.out.println(node);
        return node;
    }
    private void expansion(Node node) {
        List<Move> moves = node.board.getMovesFor(node.moveOf);
        for (Move move : moves) {
            Board tempBoard = node.board.clone();
            tempBoard.doMove(move);
            Node newNode = new Node(tempBoard, move, node);
            node.addChild(newNode);
        }
    }









    @Override
    public String getName() {
        return "Jan Skowron 151802 Kacper Mokrzycki 151893";
    }

   



    @Override
    public Move nextMove(Board b) {
        System.out.println("hello");
        if (firstRund){
            
            size = b.getSize();
            firstRund = false;
            timeOfRound = this.getTime();
            oponentColor = getOpponent(getColor());
            root = new Node(b, null, null);
            
        }
        for (Node child : root.children) {
            if (child.board.getMovesFor(getColor()).equals(b.getMovesFor(getColor()))) {
                root = child;
                root.parent = null;
                System.out.println("mleko");
                break;
            }
        }
        
        MTSC(b);
        System.out.println("hello");
        Move bestMove = getBestMove(root);
        System.out.println(root);
        System.out.println(root.winCount);
        System.out.println(root.visitCount);
        System.out.println("mefe");
        if (bestMove == null || !b.getMovesFor(getColor()).contains(bestMove)) {
            System.out.println("Blad");
            // System.out.println(bestMove);
            // for (Node child : root.children) {
            //     System.out.println(child.move);
            // }
            List<Move> moves = b.getMovesFor(getColor());
            bestMove = moves.get(random.nextInt(moves.size()));
            
            firstRund = true;
            return bestMove;
        }
        System.out.println(getColor());
        Board whatNext = b.clone();
        whatNext.doMove(bestMove);
        System.out.println("Maslo");
        for (Node child : root.children) {
            if (child.move.equals(bestMove)) {
                System.out.println("Maciej");
                System.out.println(child);
                root = child;
                root.parent = null;
                break;
            }
        }
        System.out.println(root);
        return bestMove;
    }
}