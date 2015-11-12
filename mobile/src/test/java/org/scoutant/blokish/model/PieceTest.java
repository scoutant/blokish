package org.scoutant.blokish.model;

import android.util.Log;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PieceTest {

	public static final String tag = "sc";
	Piece I3;
	Piece L4;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		I3 = new Piece(3, "I3", 2, 1).add(0,-1).add(0,0).add(0,1);
		L4 = new Piece(3, "L4", 4, 2).add(0,-1).add(0,0).add(0,1).add(1,1);
	}

	@Test
	public void testAdd() {
		Log.d(tag, ""+I3);
//		fail("Not yet implemented");
	}

	@Test
	public void testRotate() {
		I3.rotate(1);
		Log.d(tag, "" + I3);
//		Log.d(tag, "L4 : \n" + L4 );

		L4.rotate(1);
//		Log.d(tag, "L4 rotated : \n" + L4 );
	}

	@Test
	public void testFlip() {
		Log.d(tag, ""+L4 );
		L4.flip();
		Log.d(tag, ""+L4 );
	}

	@Test
	public void testEquals() {
		assertTrue( I3==I3);
		assertFalse( I3==L4);
	}
	@Test
	public void testTouches() {
		assertTrue( I3.touches(-1, -1));
		Square s = new Square(1, 1);
		assertTrue(I3.touches(s.i, s.j));
		assertFalse(L4.touches(s.i, s.j));
	}
	@Test
	public void testCrosses() {
		Square s = new Square(1, 1);
		assertFalse( I3.crosses(s.i, s.j));
		Log.d(tag, ""+L4 );
		assertFalse(L4.crosses(s.i, s.j));
		assertTrue(L4.crosses(-1, -2));
		assertFalse(L4.crosses(-2, -2));
	}

	@Test
	public void testSeeds() {
		Log.d(tag, ""+L4.seeds() );
		assertTrue(L4.seeds().size()==5);
	}
	@Test
	public void testSquares() {
		Log.d(tag, ""+L4.squares() );
		assertTrue(L4.squares().size()==4);
	}
	@Test
	public void testSquaresAndEdges() {
		Log.d(tag, ""+L4.squares(0) );
		assertTrue(L4.squares(0).size()==13);
	}
	
	
}
