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

public class Square implements Comparable<Square> {
	
	public int i;
	public int j;
	public int value;
	
	public Square(int i, int j) {
		this.i=i;
		this.j=j;
	}
	public Square(int i, int j, int value) {
		this(i,j);
		this.value = value;
	}
	
	public String toString() {
		return "("+i+", "+j+") ";
	}

	// TODO add criteria including max distance from origin corner : strategy is encouraging invading other's camp before filling one's own area
	/** So as to order square against center proximity */
	public int compareTo(Square that) {
		return this.distance()-that.distance();
	}
	private int distance() {
		return (i-10)*(i-10)+(j-10)*(j-10);
	}
	
}
