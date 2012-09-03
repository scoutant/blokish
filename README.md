
![blokish](http://blokish.scoutant.org/blokish-21.png)

#Blokish

Open-source Android implementation of the popular board game Blokus. With very smooth Drag & Drop user interface.

Live on Google Play : http://play.google.com/store/apps/details?id=org.scoutant.blokish. More than 50.000 downloads.

Blokus is family game involving four players and a board with 20 x 20 squares. Like chess or checkers, it's a game for which at any time you can see your opponent's pieces.

Each player starts with 21 pieces : 1 monomino, 1 domono, 2 triominos, 5 tetraminos and 12 pentaminos. It may recall you the Tetris polyominos...

"For first round, I must place a piece so that it touches one of the corners of the board"

"Next, a piece must be placed so that it touches a corner of one of my own pieces.
It may touch several corners, but never any side/edge of my pieces!" Look at screenshots.

The game has a nice and natural User Interface : just Drag & Drop the piece with the finger. Rotate a piece likewise. And return a piece with a long press.

"If a cannot place any more piece I pass my turn."

The end of the game is reached when every player passes.
"My score is : addition of the squares of the pieces I placed".

Refer to 'Menu > Help' inside the app. With several screenshots.

You can safely quit at any time : your current game will be restored on next startup.

Last piece played is displayed both enlarged and with higher contrast. 

You play against the machine. You can configure the level of corresponding Artificial Intelligence : 4 levels. Refer to Menu > Preferences

You can disable AI for a human play only. Refer to Menu > Preferences.

The software has no unobtrusive banner advertising.
Once installed, you can play off-line.

The game is free. And even more : the code is open-source. 
You can access it at http://github.com/scoutant.
You can reuse and adapt it according to GPL v3 license.

You may post issues at http://github.com/scoutant/blokish/issues

Stephane Coutant, http://scoutant.org

---

# Changelog

## v2.0, 2012/09 : German locale
* added German locale, credits to Sascha Hlusiak
* fix translation for screen "Level", in locales en, fr, es, sv

## v1.9, 2012/07 : Drag and Long-press, tablets
* fixed minor drag inconsistency
* fixed minor long-press inconsistency on tablets. Used to flip a piece. Can still be done using the menu.
* fixed bottom layout : on tablets the score was little bit off its control.
* fixed code warnings revealed by ADT v20.

## v1.8, 2012/04 : Animations
* added a drop animation when a piece is moved, for user to better experience the lasts moves
* removed dependency to heyzap, the social feature that did not meet great interest
* added link to YouTube demo help page. http://www.youtube.com/watch?v=3Q7ow07uaMw

## v1.7, 2012/03 : Swedish locale
* migrated to Android 2.2 
* added social feature, powered by Heyzap : you may checkin and if you win game you may post your score...  
* added local SV_SE

## v1.6, 2011/12 : Locales fr and es
* Added locales FR, ES
* Fixed endgame issue #3 : https://github.com/scoutant/blokish/issues/3
* minor layout refactoring with density-independent pixels

## v1.5, 2011/10 : UI feedbacks
* Added wheel animation to illustrated AI in progress
* Last piece for each player is displayed both enlarged and with higher contrasts 
* vibration feedback whenever AI plays a move
Thanks to those feedbacks, AI thinking duration is set to be longer, resulting in a global higher level.  

## v1.4, 2011/08 : AI with multi-turn processing
* Advanced AI with multi-turn processing better mimicking human strategy.
* Drag & Drop fix for large screens and new app icon.

## v1.3, 2011/06
- Added feature : save game on exit and load it on next start. Splashscreen vanish more quickly when game loaded.
- Added preference 'Exit popup'. Enabling/disable the popup confirmation when exiting.
With those 2 features, you can have a one click close and a quick reload. Suitable in busy situations.
- Minor bug fix : menu option 'back one move' now ok even for fresh new game.

## v1.2, 2011/06
- Added capability to flip a piece to it's mirror position using the menu. As before, can also be done with a long-press gesture directly onto the piece.
The menu alternative may be useful for large devices like tablets where long-press may conflict with DnD gesture... 

## v1.1, 2011/05
- Added capability to disconnect AI, for human play only. AI can be enabled/disabled at any time during the game.
- Added haptic feedback when dropping piece that is valid
- Gracefully adapting AI level to CPU capabilities.
- Refactored AI with initial seeds sort before looping around the best seeds. 

## v1.0, 2011/05
- Added additional AI level
- Fix for small screens : gracefully adapting the bottom pieces stores to be single-line
- Fix bug : I2 and I4 moves happened not to be displayed as valid at the very border (according to orientation). 

## v0.5, 2011/05
First release
