package org.scoutant.blokish.model;

import android.util.Log;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BoardTest {

	public static final String tag = "sc";
	Board board;
	Piece L4;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		board = new Board(0);
		L4 = new Piece(3, "L4", 4, 2).add(0,-1).add(0,0).add(0,1).add(1,1);
	}

	@Test
	public void testOverlaps() {
		assertTrue( board.overlaps(0, L4, 0, 0));
		assertFalse( board.overlaps(0, L4, 0, 1));
	}

	@Test
	public void testFits() {
		assertFalse( board.fits(0, L4, 0, 0));
		assertTrue( board.fits(0, L4, 0, 1));
		assertTrue( board.fits(0, L4, 0, 18));
		assertFalse( board.fits(0, L4, 0, 19));
		assertTrue( board.fits(0, L4, 18, 1));
		assertFalse( board.fits(0, L4, 19, 1));
	}

	@Test
	public void testToString() {
		assertTrue( board.fits(0, L4, 0, 1));
		board.add(L4, 0, 1);
		assertFalse( board.fits(0, L4, 1, 3));
		assertFalse( board.fits(0, L4, 1, 4));
		assertFalse( board.fits(0, L4, 2, 3));
		assertTrue( board.fits(0, L4, 2, 4));
		board.add(L4, 2, 4);
		Log.d(tag, board.toString(8));
	}

	@Test
	public void testOnseed() {
		assertTrue( board.onseed(L4, 0, 1));		
		assertFalse( board.onseed(L4, 2, 4));		
		board.add(L4, 0, 1);
		assertTrue( board.onseed(L4, 2, 4));		
	}
	

}
