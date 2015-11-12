package org.scoutant.blokish.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class GameTest {

	public static final String tag = "sc";
	Game board;
	Piece L4;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		board = new Game();
		L4 = new Piece(0, 3, "L4", 4, 2).add(0,-1).add(0,0).add(0,1).add(1,1);
	}

	@Test
	public void testAdd() {
		board.add(L4, 0, 1);
	}

	@Test
	public void testValid() {
		assertTrue( board.valid(L4, 0, 1));
		board.add(L4, 0, 1);
		assertFalse( board.valid(L4, 1, 3));
		assertFalse( board.valid(L4, 1, 4));
		assertFalse( board.valid(L4, 2, 3));
		assertTrue( board.valid(L4, 2, 4));
		board.add(L4, 2, 4);
	}


}
