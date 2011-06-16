/*
* Copyright (C) 2011- stephane coutant
*
* This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
* See the GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>
*/

package org.scoutant.blokish.model;

import java.util.ArrayList;
import java.util.List;

public class Piece {
	public String type;
	public int size;
	public int color = 0;
	private int[][] a;
	private int[][] b;
	private boolean actual = true;
	public int rotations = 4;
	public int flips = 2;
	public int count=0;
	private int h;
	private int r;
	private int f;
	private boolean odd;

	public static final String tag = "sc";

	public Piece( int color, int size, String type, int rotations, int flips ) {
		this(size, type, rotations, flips);
		this.color = color;
	}

	public Piece( int size, String type, int rotations, int flips ) {
		this.size = size;
		this.type = type;
		this.rotations = rotations;
		this.flips = flips;
		h = (size+1)/2 -1;
		odd = (size % 2) == 1; 
		reset();
	}
	public void reset() {
		r=0;
		f=0;
		a = new int[size][size];
		b = new int[size][size];
		actual = true;
		count=0;
	}
	
	public void reset(Piece ghost) {
		reset();
		for (Square s : ghost.squares()) {
			add(s.i, s.j);
		}
	}

	public Piece clone(){
		Piece clone = new Piece(size, type, rotations, flips);
		for (Square s : squares()) clone.add(s);
		return clone;
	}
	
	public Piece add(Square s) {
		return add(s.i, s.j);
	}

	public Piece add(int x, int y) {
		int i=x+h;
		int j=y+h;
		if (i<0 || i>= size) throw new IllegalArgumentException(); 
		if (j<0 || j>= size) throw new IllegalArgumentException(); 
		if (a[i][j]>0) throw new IllegalArgumentException(); 
		a[i][j] = 1;
		count++;
		return this;
	}

	private int[][] v() {
		return ( actual ? a : b);
	}
	private int[][] w() {
		return ( actual ? b : a);
	}

	private void toggle() {
		actual = ! actual;
	}
	
	private int get(int x, int y) {
		return v()[x+h][y+h];
	}
	private void set(int x, int y, int value) {
		w()[x+h][y+h] = value;
	}
	
	public int getValue(int x, int y) {
		if (x<-h || x>=-h+size || y<-h || y>=-h+size ) return 0;
		return get(x,y);
	}
	public boolean isValue(int x, int y) {
		return getValue(x, y)>0;
		
	}
	
	public Piece rotate(int dir) {
		for (int y=-h; y<-h+size; y++) {
			for (int x=-h; x<-h+size; x++) {
				if (odd) set(x,y, (dir>0 ? get(y,-x) : get(-y,x)) );
				else {
					if (dir<0) set(x,y, get(-y+1,x) );
					else set(x,y, get( y, -x+1) );
				}
			}
		}
		r = (r+1) % rotations ;
		toggle();
		return this;
	}

	public Piece flip() {
		for (int x=-h; x<-h+size; x++) {
			for (int y=-h; y<-h+size; y++) {
				if (odd) set(x,y, get(-x,y));
				else set(x,y, get(-x+1,y));
			}
		}
		f = (f+1) % flips ;
		toggle();
		return this;
	}

	public String toLabel() {
//		return "[" + type + ", " + r + ", " +f + " ]";
		return type;
	}
	@Override
	public String toString() {
		String str = type + "\n";
		for (int y=-h; y<-h+size; y++) {
			for (int x=-h; x<-h+size; x++) {
				str += get(x,y) + (x==-h+size-1? "\n" : " | ") ; 	
			}
		}
		return str;
		
	}
	
	public boolean touches(int x, int y) {
		if (isValue(x, y)) return false;
		return ( isValue(x-1, y) || isValue(x, y-1) || isValue(x+1, y) || isValue(x, y+1));
	}
	public boolean crosses(int x, int y) {
		if (isValue(x, y)) return false;
		if (touches(x, y)) return false;
		return ( isValue(x-1, y-1) || isValue(x+1, y-1) || isValue(x+1, y+1) || isValue(x-1, y+1));
	}

	public boolean overlaps(Piece that, int X, int Y) {
		if ( Math.abs( X ) > (this.size + that.size)/2 ) return false;  
		if ( Math.abs( Y ) > (this.size + that.size)/2 ) return false;  
		for (int y=-h; y<-h+size; y++) {
			for (int x=-h; x<-h+size; x++) {
				if ( that.isValue(X+x, Y+y)) return true; 	
			}
		}
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		Piece other = (Piece) obj;
		if (other.type != this.type) return false;
		for (int y=-h; y<-h+size; y++) {
			for (int x=-h; x<-h+size; x++) {
				if (this.get(x,y) !=  other.get(x,y)) return false; 	
			}
		}
		return true;
	}

	public List<Square> squares() {
		List<Square> list = new ArrayList<Square>();
		for (int y=-h; y<-h+size; y++) {
			for (int x=-h; x<-h+size; x++) {
				if ( isValue(x, y)) list.add(new Square(x, y, 3)); 	
			}
		}
		return list;
	}
	
	public List<Square> squares(int color) {
		List<Square> list = new ArrayList<Square>();
		if (color != this.color) {
			return squares();
		} else {
			for (int y=-h-1; y<-h+size+1; y++) {
				for (int x=-h-1; x<-h+size+1; x++) {
					if ( isValue(x, y)) list.add(new Square(x, y, 3)); 	
					else if ( touches(x, y)) list.add(new Square(x, y, 2)); 	
				}
			}
		}
		return list;
	}

	public List<Square> seeds() {
		List<Square> list = new ArrayList<Square>();
		for (int y=-h-1; y<-h+size+1; y++) {
			for (int x=-h-1; x<-h+size+1; x++) {
				if ( crosses(x, y)) list.add(new Square(x, y)); 	
			}
		}
		return list;
	}
	
	/** @return a represention of the piece, like this sample : 2:I3:0,-1:0,0:0,1 */
	public static String serialize(Piece piece) {
		String msg = "" + piece.color;
		msg += ":"+piece.type;
		for (Square s : piece.squares()) {
			msg+=":"+s.i+","+s.j;
		}
		return msg;
	}
	
}
