package org.scoutant.blokish.model;

import android.util.Log;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AITest {

	public static final String tag = "ai";
	int color=0;
	AI ai;
	Game game;
	Board board;
	List<Piece> pieces;
	boolean valid = false;
	protected Piece L4 = new Piece(color, 3, "L4", 4, 2).add(0,-1).add(0,0).add(0, 1).add(1,1);
	protected Piece P5 = new Piece(color, 3, "P5", 4, 2).add(0,-1).add(0,0).add(0,1).add(1,-1).add(1,0);
	protected Piece I3 = new Piece(color, 3, "I3", 2, 1).add(0,-1).add(0,0).add(0, 1);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		game = new Game();
		board = game.boards.get(color);
		ai = new AI(game);
		pieces = board.pieces;
    pieces.clear();

//    L4 = board.findPieceByType("L4");
//		P5 = board.findPieceByType("P5");
//		I3 = board.findPieceByType("I3");
	}

	@Test
	public void testThinkEmptyBoard() {
		pieces.add( L4);
		assertNotNull( ai.think(0,0));
	}

	@Test
	public void testThinkWithL4() {
		pieces.add( L4);
		List<Move> moves = ai.thinkUpToNMoves(0,0);
		assertTrue(moves.size()== 6);
	}

	@Test
	public void testThinkWithP5() {
		pieces.add( P5);
		List<Move> moves = ai.thinkUpToNMoves(0,3);
		assertTrue(moves.size()== 6);
	}

	@Test
	public void testThinkWithP5AndL4() {
		Move move = new Move( P5, 0, 1);
    game.play(move);
    assertTrue (board.seeds().size() == 2);
		pieces.add( L4 );
		List<Move> moves = ai.thinkUpToNMoves(0,3);
		assertTrue(moves.size() ==17);
		game.play(new Move(L4, 1,4));
    Log.d(tag, "" + board);
    assertTrue (board.seeds().size() == 4);
	}

	@Test
	public void testThinkWithP5L4I3() {
		game.play(new Move(P5, 0, 1));
		game.play(new Move(L4, 1,4));
		pieces.add( I3 );
		List<Move> moves = ai.thinkUpToNMoves(0, 3);
		assertTrue(board.seeds().size() == 4);
    Log.d(tag, "#moves : " + moves.size());
		assertTrue(moves.size()== 6);
		I3.rotate(1);
		valid =  game.play( new Move(I3, 4, 6));
    assertTrue(valid);
    Log.d(tag, "" + board);
    assertTrue (board.seeds().size() == 6);
	}
	@Test
	public void testThinkWithP5L4I3P5() {
		game.play( new Move( P5, 0, 1));
		game.play( new Move(L4, 1,4));
		pieces.add( I3 );
		I3.rotate(1);
		valid =  game.play( new Move(I3, 4,6));
		assertTrue(valid);
		assertTrue (board.seeds().size() == 6);
		pieces.add( P5 );
		List<Move> moves = ai.thinkUpToNMoves(0, 3);
		System.out.println("move : " + moves.size());
		assertTrue(moves.size()== 37);
	}
	
}
