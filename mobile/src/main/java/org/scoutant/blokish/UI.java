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

package org.scoutant.blokish;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.scoutant.blokish.model.Move;
import org.scoutant.blokish.model.Piece;
import org.scoutant.blokish.model.Square;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class UI extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {
	private static final int MENU_ITEM_HISTORY = 99;
	private static final int MENU_ITEM_REPLAY = 101;
	private static final int MENU_ITEM_BACK = 102;
	private static final int MENU_ITEM_NEW = 5;
	private static final int MENU_ITEM_THINK=10;
	private static final int MENU_ITEM_PREFERENCES=-1;
	private static final int MENU_ITEM_HELP = 9;
	private static final int MENU_ITEM_PASS_TURN = 12;
	private static final int MENU_ITEM_FLIP = 15;

	private static String tag = "activity";
	public GameView game;
	public boolean devmode=false;
	private SharedPreferences prefs;
	private Vibrator vibrator;
	private Resources rs;
	private boolean back_pressed;
	private DrawerLayout drawer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rs = getResources();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

		newgame();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		sourceFromMovesFile();

		AppRater.app_launched( this);
	}

	private void newgame() {
		game = new GameView(UI.this);
//		setContentView(game);
		setContentView( R.layout.activity_main);
		FrameLayout container = (FrameLayout) findViewById(R.id.container);
		container.addView( game);
		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
		drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {}
			@Override
			public void onDrawerOpened(View drawerView) {
				navigationView.getMenu().findItem(R.id.item_flip).setVisible( game.selected!=null);
			}
			@Override
			public void onDrawerClosed(View drawerView) {}
			@Override
			public void onDrawerStateChanged(int newState) {}
		});

	}

	@Override
	public void onRefresh() {

	}

	// menu in not show since 3.1, refactored to drawing menu
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		if (game.selected!=null) {
			menu.add(Menu.NONE, MENU_ITEM_FLIP, Menu.NONE, R.string.flip).setIcon(android.R.drawable.ic_menu_set_as);
		}
		menu.add(Menu.NONE, MENU_ITEM_BACK, Menu.NONE, R.string.undo).setIcon( R.drawable.left_48);
		menu.add(Menu.NONE, MENU_ITEM_NEW, Menu.NONE, R.string.new_game).setIcon( R.drawable.restart_48);

		menu.add(Menu.NONE, MENU_ITEM_HELP, Menu.NONE, R.string.help).setIcon( R.drawable.help_48);
		menu.add(Menu.NONE, MENU_ITEM_PREFERENCES, Menu.NONE, R.string.preferences).setIcon( R.drawable.preferences_48);

		if (devmode) {
			menu.add(Menu.NONE, MENU_ITEM_THINK, Menu.NONE, "AI").setIcon(android.R.drawable.ic_menu_manage);
			menu.add(Menu.NONE, MENU_ITEM_HISTORY, Menu.NONE, "hist").setIcon(android.R.drawable.ic_menu_recent_history);
		}
		if (!prefs.getBoolean("ai", true)) {
			menu.add(Menu.NONE, MENU_ITEM_PASS_TURN, Menu.NONE, R.string.i_pass).setIcon( R.drawable.checkmark_48);			
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		if (item.getItemId() == MENU_ITEM_HELP) {
			startActivity(new Intent(this, Help.class));
		}
		if (item.getItemId() == MENU_ITEM_PREFERENCES) {
			startActivity(new Intent(this, Settings.class));
		}
		if (item.getItemId() == MENU_ITEM_HISTORY) {
			Log.d(tag, "" + game.game);
		}
		if (item.getItemId() == MENU_ITEM_REPLAY) {
			GameView old = game;
			newgame();
			Log.d(tag, "replay # moves : " + old.game.moves.size());
			game.replay(old.game.moves);
		}
		if (item.getItemId() == MENU_ITEM_BACK) {
			List<Move> moves = game.game.moves;
			int length = moves.size();
			if (length>=4) {
				length -= 4;
			}
			moves = moves.subList(0, length);
			newgame();
			Log.i(tag, "replay # moves : " + length);
			game.replay( moves);			
		}
		if (item.getItemId() == MENU_ITEM_NEW) {
			final AlertDialog dialog =
			new AlertDialog.Builder(this)
			.setMessage(rs.getString(R.string.new_game) + "?")
			.setCancelable(false)
			.setPositiveButton(" ", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					newgame();
				}
			})
			.setNegativeButton(" ", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			})
			.create();
			dialog.setOnShowListener(new DialogInterface.OnShowListener() {
				@Override
				public void onShow(DialogInterface dialogInterface) {
					setButtonImage(dialog, AlertDialog.BUTTON_POSITIVE, R.drawable.checkmark);
					setButtonImage(dialog, AlertDialog.BUTTON_NEGATIVE, R.drawable.cancel);
				}
			});
			dialog.show();
			}
		if (item.getItemId() == MENU_ITEM_THINK) {
			think(0);
		}
		if (item.getItemId() == MENU_ITEM_PASS_TURN) {
				turn = (turn + 1) % 4;
				game.showPieces(turn);
				game.invalidate();
		}
		if (item.getItemId() == MENU_ITEM_FLIP) {
			PieceUI piece = game.selected;
			if (piece!=null) piece.flip();
		}
		return false;
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {

		Log.d("menu", "menu item is : " + item);
		int id = item.getItemId();
		if (id==R.id.item_help) startActivity(new Intent(this, Help.class));
		if (id==R.id.item_preferences) startActivity(new Intent(this, Settings.class));
		if (id==R.id.item_back) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					List<Move> moves = game.game.moves;
					int length = moves.size();
					if (length>=4) {
						length -= 4;
					}
					moves = moves.subList(0, length);
					newgame();
					Log.i(tag, "replay # moves : " + length);
					game.replay( moves);
				}
			}, 500);
		}
		if (id==R.id.item_new) {

			final IconDialog dialog = new IconDialog(this, R.string.new_game);
			dialog.setListener(new IconDialog.OnClick() {
				@Override
				public void onClick() {
					newgame();
				}
			});
			dialog.show();

		}
		if (id==R.id.item_flip) {

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					final PieceUI piece = game.selected;
					if (piece!=null) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								piece.flip();
								boolean okState = game.game.valid(piece.piece, piece.i, piece.j);
								piece.setOkState( okState);
								game.buttons.setOkState( okState);
								game.invalidate();
							}
						});
					}
				}
			}, 500);
		}

		if (id==R.id.more_apps) startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:scoutant.org")));

		drawer.closeDrawer(GravityCompat.START);
		return true;
	}



	private void setButtonImage( AlertDialog dialog, int buttonId, int id ) {
		Button button = dialog.getButton( buttonId);
		Drawable drawable = getResources().getDrawable( id);
		drawable.setBounds(drawable.getIntrinsicWidth()/4, 0, drawable.getIntrinsicWidth()*3/4, drawable.getIntrinsicHeight()/2);
		button.setCompoundDrawables(drawable, null, null, null);
//		button.setBackgroundColor(Color.TRANSPARENT);
	}

	/**
	 * Invokes AI for all players from @param player. Thinking in a background Thread. But one player after the other! 
	 */
	public void think(int player) {
		turn = player;
		new AITask().execute(player);
	}
	
	private int findRequestedLevel() {
		String level = prefs.getString("aiLevel", "0");
		return Integer.valueOf(level);
	}
	
	private int findLevel() {
		String level = prefs.getString("aiLevel", "0");
		int l = Integer.valueOf(level);
		if (l<0 || l>3) l = 1;
		return Math.min(l, game.ai.adaptedLevel);
	}
	
	public int turn = 0;
	private AITask task = null;

	private class AITask extends AsyncTask<Integer, Void, Move> {
		@Override
		protected Move doInBackground(Integer... params) {
			task = this;
			game.thinking=true;
			game.indicator.show();
			return game.ai.think(params[0], findLevel());
		}
		@Override
		protected void onPostExecute(Move move) {
			if (vibrator!=null && !game.redOver) vibrator.vibrate(15);
			if (game.game.over()) {
				displayWinnerDialog();
				return;
			}
			UI.this.game.play( move, true);
			turn++;
			if (turn<4) new AITask().execute(turn);
			if (turn==4) game.indicator.hide();
			if (turn==4 && !game.redOver ) {
				game.thinking=false;
				turn=0;
				game.showPieces(0);
				new CheckTask().execute();
			}
			if (turn==4 && game.redOver ) {
				Log.d(tag, "Red is dead. game.over ? " + game.game.over());
				if (game.game.over()) {
					displayWinnerDialog();					
				} else {
					turn = 1;
					new AITask().execute(turn);
				}
			}
		}
		private void displayWinnerDialog() {
			game.indicator.hide();
			Log.d(tag, "game over !");
			int winner = game.game.winner();
			int score = game.game.boards.get(winner).score;
			String message = "";
			boolean redWins = (winner==0 && prefs.getBoolean("ai", true));
			if (redWins) {
				 message += rs.getString( R.string.congratulations) + " " + score +".";
				 if (findRequestedLevel()<(4-1)) message += "\n" + rs.getString( R.string.try_next);
			} else {
//				message += "Player " + game.game.colors[winner] + " wins with score : " + score;
				message += rs.getString( game.game.colors[winner]);
				message += " " + rs.getString( R.string.wins_with_score) + " : ";
				message += score;
			}
			new EndGameDialog(UI.this, redWins, message, findRequestedLevel()+1, score).show();
		}
	}
	
	private class CheckTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			return !game.ai.hasMove(0);
		}
		@Override
		protected void onPostExecute(Boolean finished) {
			if (finished) {
				game.indicator.hide();
				Log.d(tag, "red over!");
				final AlertDialog dialog =
				new AlertDialog.Builder(UI.this)
				.setMessage( R.string.red_ko)
				.setCancelable(false)
				.setPositiveButton(" ", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						game.redOver = true;
						game.game.boards.get(0).over = true;
						Log.d(tag, "ok!");
						think(1);
						}
					})
				.create();
				dialog.setOnShowListener(new DialogInterface.OnShowListener() {
					@Override
					public void onShow(DialogInterface dialogInterface) {
						setButtonImage( dialog, AlertDialog.BUTTON_POSITIVE, R.drawable.checkmark);
					}
				});
				dialog.show();
			}
		}
	}

	private Toast toast;
	/** Press twice to exit */
	@Override
	public void onBackPressed() {
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
			return;
		}

		if (back_pressed==true) {
			if (toast!=null) toast.cancel();
			super.onBackPressed();
			return;
		}
		toast = Toast.makeText( this, R.string.twice_to_exit, Toast.LENGTH_SHORT);
		toast.show();
		back_pressed = true;
	}

	private void saveToMovesFile() {
		try {
			FileOutputStream fos = openFileOutput("moves.txt", Context.MODE_PRIVATE);
			save(fos);
		} catch (FileNotFoundException e) {
			Log.e(tag, "not found...", e);
		}
	}

	private void save(OutputStream os){
		try {
			if (os==null) return;
			if (!game.game.over()) {
				os.write( game.game.toString().getBytes());
			} // if gave is over we do not save it, so as to open a blank game next time
			os.close();
		} catch (FileNotFoundException e) {
			Log.e(tag, "not found...", e);
		} catch (IOException e) {
			Log.e(tag, "io...", e);
		}
	}


	private void sourceFromMovesFile() {
		try {
			FileInputStream fis = openFileInput("moves.txt");
			source(fis);
		} catch (IOException e) {
			Log.e(tag, "yep error is :", e);
		}
	}



	/** sources a list of representations like this sample : 18|16|2|I3|0,-1|0,0|0,1 */
	private void source(InputStream is) {
		List<Move> list = new ArrayList<Move>();
		try {
			BufferedReader reader = new BufferedReader( new InputStreamReader(is));
			String line;
			reader.readLine(); // first line give the # of moves...
			while ((line = reader.readLine()) != null)   {
				String[] data = line.split(":");
				int i = Integer.valueOf(data[0]);
				int j = Integer.valueOf(data[1]);
				int color = Integer.valueOf(data[2]);
				Piece piece = game.game.boards.get(color).findPieceByType(data[3] );
				piece.reset();
				for (int q = 4; q<data.length; q++) {
					String[] position = data[q].split(",");
					int x = Integer.valueOf( position[0]);
					int y = Integer.valueOf( position[1]);
					piece.add( new Square(x, y ));
				}
				Move move = new Move(piece, i, j);
//				Log.d(tag, "created move : " + move);
				list.add(move);
			}
			newgame();
			game.replay(list);
			game.reorderPieces();
		} catch (Exception e) {
			Log.e(tag, "yep error is :", e);
		}
	}

	@Override
	protected void onStop() {
		if (task!=null) {
			task.cancel(true);
			Log.d(tag, "leaving AI, as activity is brough to background");
		}
			saveToMovesFile();
		super.onStop();
	}
	
}