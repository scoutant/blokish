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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.util.Log;

public class AI  {

	public static final String tag = "sc";
	private static final int SIZE_WEIGHT = 5;
	private static final int CENTER_WEIGHT = 1;
	private static final int SEEDS_WEIGHT = 3;
	private static final int ENEMY_SEEDS_WEIGHT = 1;
	private static final int CHAINING_WEIGHT = 3;
	public Game game;
	private Random random = new Random();
	
	private int[] maxMoves = { 40, 100, 250, 10000 };

	public int adaptedLevel = 3;

	public AI(Game game) {
		this.game = game;
	}

	public boolean hasMove(int color) {
		Board board = game.boards.get(color);
		for (Square seed : board.seeds()) {
			for (int p=0; p<board.pieces.size(); p++) {
				Piece piece = board.pieces.get(p);
				// Fixing issue #3, changing order rotate/flip
				for( int f=0; f<piece.flips; f++, piece.flip()) {
					for (int r=0; r<piece.rotations; r++, piece.rotate(1)) {
						for (Square s : piece.squares()) {
							int i = seed.i - s.i;
							int j = seed.j - s.j;
							if ( !board.outside(s, i, j) && game.fits(piece, i, j)) {
								Log.d(tag, "possible move : " + new Move(piece, i, j));
								game.boards.get(color).over = false;
								return true;
							}
						}
					}
				}
			}
		}
		game.boards.get(color).over = true;
		return false;
	}
	
	public Move think(int color, int level) {
		if (game.boards.get(color).pieces.isEmpty()) {
			Log.d(tag, "no more pieces for player : " + color);
			// no big deal, AI will continue for the other players. At the very end current player will be granted the winning message.
			game.boards.get(color).over = true;
			return null;
		}
		Log.d(tag, "--------------------------------------------------------------------------------");
		level = Math.min(level, adaptedLevel);
		// reinforce player 1 compared to player 2 and 3
		if (level>1 && color!=1 ) level--;
		Log.d(tag, "thinking for player : " + color + ", upto # moves : " + maxMoves[level]);
		List<Move> moves = thinkUpToNMoves(color, level);
		Log.d(tag, "# moves : " + moves.size());
		if (moves.size()==0) {
			game.boards.get(color).over = true;
			return null;
		}
		Collections.sort(moves);
		Collections.reverse(moves);
		Move move = moves.get( 0);
		Log.d(tag, "best move actually is : " + move);
		// TODO may be many moves with O1 and I2. But only 1 or 2 nice moves!!
		if (moves.size()>20) {
			// let's prune trivial moves
			for (int k=moves.size()-1; k>=2; k--) {
				if (moves.get(k).piece.count<=2) moves.remove(k);
			}
			// Now if we do have a significant # of moves, lets randomized among the very best ones.
			if (moves.size()> 10) {
				move = moves.get( random.nextInt(3));
			} else {
				Log.d(tag, "keeping best move!");
			}
		}
		move.piece.reset(move.ghost);
		return move;
	}
	protected List<Move> thinkUpToNMoves(int color, int level) {
		List<Move> moves = new ArrayList<Move>();
		Board board = game.boards.get(color);
		// Most of time , in the middle of the game, any player has about 10 to 20 seeds.
		int nbSeeds = board.seeds().size();
		Log.d(tag, "# of seeds : " + nbSeeds);
		if (nbSeeds==0) return moves;
		long startedAt = new Date().getTime();
		List<Square> seeds = board.seeds();
		Collections.sort(seeds);
		if (board.pieces.size()> board.nbPieces - 4) {
			seeds = seeds.subList(0, Math.min(seeds.size(), 2));
		}
		for (Square seed : seeds) {
			int movesAgainstSeed=0;
			Log.d(tag, "---- seed : " + seed);
			int maxMovesAgainstSeed = maxMoves[level] / nbSeeds;
//			Log.d(tag, "considering # of moves : " + maxMovesAgainstSeed );
			for (int p=0; p<board.pieces.size() && movesAgainstSeed<maxMovesAgainstSeed; p++) {
				Piece piece = board.pieces.get(p);
				for (int r=0; r<piece.rotations; r++, piece.rotate(1)) {
					for( int f=0; f<piece.flips; f++, piece.flip()) {
						for (Square s : piece.squares()) {
							int i = seed.i - s.i;
							int j = seed.j - s.j;
							if ( !board.outside(s, i, j) && game.fits(piece, i, j)) {
								Move move = new Move(piece, i, j);
								if (!game.valid(move)) {
									Log.e(tag, "Inconsistant ! "+move);
								}
								int score = SIZE_WEIGHT * piece.count;
								if (board.pieces.size()> board.nbPieces-5) {
									// encourage moving to the center, this extra bonus only for pentaminos
									int io = game.size/2 - i;
									int jo = game.size/2 - j;
									score -= CENTER_WEIGHT * (io*io + jo*jo);
								}
								int seedsIfAdding = board.scoreSeedsIfAdding(piece, i, j);
								score += SEEDS_WEIGHT * seedsIfAdding ;
								int enemyscore = game.scoreEnemySeedsIfAdding(board.color, piece, i, j);
								score -= ENEMY_SEEDS_WEIGHT * enemyscore;
								// Endgame deep thinking
								if (board.pieces.size() < 9) {
									score += CHAINING_WEIGHT*chainingScore(color, move);
								}
								move.score = score;
//								Log.d(tag, ""+move);
								if (board.pieces.size()<= board.nbPieces-4 || piece.count>=5) {
									moves.add(move);
								}
								
								movesAgainstSeed++;
								if (moves.size()>= maxMoves[level]) {
									autoAdaptLevel(startedAt);
									return moves;
								}
							}
						}
					}
				}
			}
		}
		autoAdaptLevel(startedAt);
		return moves;
	}

	private int[][] ij = new int [20][20];
	
	/**
	 * Considering we play given @param move. 
	 * @return true if we may place a piece on newly created seeds.
	 */
	protected int chainingScore(int color, Move move) {
		Board board = game.boards.get(color);
		Piece played = move.piece;
		List<Piece> pieces = new ArrayList<Piece>();
		for (Piece piece : board.pieces) {
			if (!piece.equals(played)) {
				pieces.add( piece.clone());
			}
		}
		
		for (int j=0;j<20;j++) {
			for (int i=0;i<20;i++) {
				ij[i][j] = board.ij[i][j];
			}
		}
		// let's place 'played' onto board.
		for(Square s : played.squares(color)) {
			int I = move.i+s.i;
			int J = move.j+s.j;
			if (I>=0 && I<20 && J>=0 && J<20) ij[I][J] = s.value;
		}

		int score = 0;
		Move second = null;
		Log.d(tag, "considering # of pieces : " + pieces.size());
		for (Square seed : played.seeds()) {
			for (Piece piece : pieces) {
				for (int r=0; r<piece.rotations; r++, piece.rotate(1)) {
					for( int f=0; f<piece.flips; f++, piece.flip()) {
						for (Square s : piece.squares()) {
							int i = move.i + seed.i - s.i;
							int j = move.j + seed.j - s.j;
							boolean overlaps = overlaps(color, piece, i, j);
							boolean outside = board.outside(s, i, j); 
							boolean fits =  game.fits(piece, i, j);
							if ( !overlaps && !outside && fits ){
								if (piece.count > score) {
									second = new Move(piece, i, j);
									score = piece.count;
								}
								// TODO also include # of promissing seeds?
							}
						}
					}
				}
			}
		}
		if (score>1) Log.d(tag, "may CHAIN with : " + second);
		return score;
	}
	
	public boolean overlaps( int color, Piece piece, int i, int j) {
		for(Square s : piece.squares()) {
			int I = i+s.i;
			int J = j+s.j;
			if ( I>=0&&I<20&&J>=0&&J<20 &&  ij[I][J] > 1 ) return true;
		}
		return false;
	}

	
	private void autoAdaptLevel(long startedAt) {
		long duration = new Date().getTime()- startedAt;
		Log.d(tag, "lasted : " + duration );
		if (duration > 2500 && adaptedLevel>0) {
			Log.i(tag, "Decreasing AI level! * * * * * * * * * * * * * * * * * * * *");
			adaptedLevel --;
		}
	}
}
