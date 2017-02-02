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

/**
 * Game resources from a player's point of view!
 * 		// 12 Pentaminos : I, F, L, N, P, T, U, V, W, X, Y, Z
 *		X5: 5, 8, 1
 *		W5: 5, 7, 4
 *		F5: 5, 7, 8
 *		T5: 5, 6, 4
 *		Z5: 5, 6, 4
 *		Y5: 5, 6, 8
 *		N5: 5, 6, 8
 *		U5: 5, 5, 4
 *		V5: 5, 5, 4
 *		P5: 5, 5, 8
 *		L5: 5, 5, 8 
 *		I5: 5, 4, 2
 *		// 5 Tetramonos : I, O, T, L, S
 *		O4: 4, 4, 1
 *		S4: 4, 6, 4
 *		T4: 4, 6, 4
 *		L4: 4, 5, 8
 *		I4: 4, 4, 2
 *		// 2 Triominos : I, L
 *		L3: 3, 5, 4
 *		I3: 3, 4, 2
 *		// Domono and Monomino
 *		I2: 2, 4, 2
 *		O1: 1, 4, 1
 */
public class Board {
	public static final String tag = "sc";
	public int color;
	public int size = 20;
	int[][] ij = new int [20][20];
	public List<Piece> pieces = new ArrayList<Piece>();
	public int nbPieces;
	public int score;
	public boolean over=false;

	public Board(int color) {
		this.color = color;
		if (color==0) ij[0][0]=1;
		if (color==1) ij[size-1][0]=1;
		if (color==2) ij[size-1][size-1]=1;
		if (color==3) ij[0][size-1]=1;

		pieces.add( new Piece(color, 3, "X5", 1, 1).add(0,0).add(-1,0).add(0,-1).add(1,0).add(0,1));
		pieces.add( new Piece(color, 3, "W5", 4, 1).add(0,0).add(0,-1).add(1,-1).add(-1,0).add(-1,1));
		pieces.add( new Piece(color, 3, "F5", 4, 2).add(0,0).add(0,-1).add(1,-1).add(-1,0).add(0,1));
		pieces.add( new Piece(color, 3, "T5", 4, 1).add(-1,-1).add(0,-1).add(1,-1).add(0,0).add(0,1));
		pieces.add( new Piece(color, 3, "Z5", 2, 2).add(-1,-1).add(0,-1).add(0,0).add(0,1).add(1,1));

		pieces.add( new Piece(color, 4, "Y5", 4, 2).add(0,-1).add(0,0).add(0,1).add(0,2).add(1,0));
		pieces.add( new Piece(color, 4, "N5", 4, 2).add(0,-1).add(0,0).add(1,0).add(1,1).add(1,2));
		
		pieces.add( new Piece(color, 3, "U5", 4, 1).add(1,-1).add(0,-1).add(0,0).add(0,1).add(1,1));
		pieces.add( new Piece(color, 3, "V5", 4, 1).add(1,-1).add(0,-1).add(-1,-1).add(-1,0).add(-1,1));
		pieces.add( new Piece(color, 3, "P5", 4, 2).add(0,-1).add(0,0).add(0,1).add(1,-1).add(1,0));

		pieces.add( new Piece(color, 4, "L5", 4, 2).add(1,-1).add(1,0).add(1,1).add(1,2).add(0,2));

		pieces.add( new Piece(color, 5, "I5", 2, 1).add(0,-2).add(0,-1).add(0,0).add(0,1).add(0,2));
		
		pieces.add( new Piece(color, 2, "O4", 1, 1).add(0,0).add(1,0).add(0,1).add(1,1) );
		
		pieces.add( new Piece(color, 3, "S4", 2, 2).add(-1,-1).add(-1,0).add(0,0).add(0,1) );
		pieces.add( new Piece(color, 3, "T4", 4, 1).add(-1,-1).add(-1,0).add(-1,1).add(0,0) );
		pieces.add( new Piece(color, 3, "L4", 4, 2).add(0,-1).add(0,0).add(0,1).add(1,1));

		pieces.add( new Piece(color, 4, "I4", 2, 1).add(0,-1).add(0,0).add(0,1).add(0,2));
		
		pieces.add( new Piece(color, 3, "I3", 2, 1).add(0,-1).add(0,0).add(0,1));

		pieces.add( new Piece(color, 2, "L3", 4, 1).add(0,0).add(0,1).add(1,1));
		pieces.add( new Piece(color, 2, "I2", 2, 1).add(0,0).add(0,1));

		pieces.add( new Piece(color, 1, "O1", 1, 1).add(0,0));
		nbPieces = pieces.size();
	}
	
	public Piece findPieceByType(String type) {
		for (Piece piece:pieces) {
			if (piece.type.equals(type)) return piece;
		}
		return null;
	}
	
	public void add( Piece piece, int i, int j) {
		for(Square s : piece.squares(this.color)) {
			// TODO refactor without try / catch
//			try { ij[i+s.i][j+s.j] = s.value; } catch (Exception e) {}
			int I = i+s.i;
			int J = j+s.j;
			if (I>=0 && I<size && J>=0 && J<size) ij[I][J] = s.value;
		}
		
		if (piece.color == this.color) {
			pieces.remove( piece);
			score += piece.count;
			for(Square seed : piece.seeds()) {
				try { if (ij[i+seed.i][j+seed.j] ==0 ) ij[i+seed.i][j+seed.j] = 1; } catch (Exception e) {}   
			}
		}
	}

	int[][] ab = new int [20][20];
	/**
	 * @return # of seeds if actually adding @param piece at @param i, @param j.
	 */
	public int scoreSeedsIfAdding(Piece piece, int i, int j) {
		int result=0;
		for (int b=0; b<20; b++) for (int a=0; a<20; a++) ab[a][b] = ij[a][b];
		for(Square s : piece.squares(this.color)) {
			try { ab[i+s.i][j+s.j] = s.value; } catch (Exception e) {}
		}
		for(Square seed : piece.seeds()) {
			try { if (ab[i+seed.i][j+seed.j] ==0 ) ab[i+seed.i][j+seed.j] = 1; } catch (Exception e) {}   
		}
		for (int b=0; b<20; b++) for (int a=0; a<20; a++) if (ab[a][b]==1) result++;
		return result;
	}
	
	
	public boolean outside(Square s, int i, int j) {
		return ( s.i+i<0 || s.i+i>=size || s.j+j<0 || s.j+j>=size );
	}
	
	public boolean overlaps( int color, Piece piece, int i, int j) {
		for(Square s : piece.squares()) {
			if (outside(s, i, j)) return true;
			if (ij[i+s.i][j+s.j] > (piece.color == color ? 1: 2) ) return true;
		}
		return false;
	}
	
	public boolean fits( int color, Piece piece, int i, int j) {
		if (i<-1 || i> size || j<-1 || j>size) return false; 
		return ! overlaps( color, piece, i, j);
	}
	
	public boolean onseed( Piece piece, int i, int j) {
		for(Square s : piece.squares()) {
			if ( !outside(s, i, j) && ij[i+s.i][j+s.j]==1) return true;
		}
		return false;
	}
	
	public String toString() {
		return toString(size);
	}
	public String toString(int jmax) {
		String str = "";
		for (int j=0; j<jmax; j++) {
			for (int i=0; i<size; i++) {
				str += ij[i][j] + (i==size-1 ? "\n" : " | ");
			}
		}
		return str;
	}

	public List<Square> seeds() {
		List<Square> list = new ArrayList<Square>();
		for (int j=0; j<size; j++) {
			for (int i=0; i<size; i++) {
				if ( ij[i][j]==1 ) list.add(new Square(i, j)); 	
			}
		}
		return list;
	}
}
