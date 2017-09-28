import ddf.minim.*;
Minim minim;
AudioPlayer introMusic;
AudioPlayer loadAndPauseMusic;
AudioPlayer gameplayMusic;
AudioPlayer explosionSound;
AudioPlayer shootingSound;
AudioPlayer gameOverMusic;
AudioPlayer lootBuffSound;
ArrayList<Meteors> M;
ArrayList<Lasers> shot;
Robot R;
PImage Background, invincibility;
float minspeed = 1.01, maxspeed = 1.025;
int backgroundcolor;
boolean hit = false, pause = false;
boolean safetime = true;
int safetimer = 360, score = 0;
int MAsize = 5, shotAsize = 0;
int respawntimer = 0, seconds = 0, updates = 0, speedinctimer = 0, retryCount=1;
int BGnumber = int(random(1, 18));
int timer=0;
boolean startingGame=false, spawnlaser=false, lose=false, intro=true, bombBuff=false;
PFont titleFont, gameFont, pointsFont;

void setup() {
  size(1280, 720);
  minim= new Minim(this);
  gameFont = loadFont("gameFont.vlw");
  titleFont = loadFont("Title.vlw");
  pointsFont = loadFont("Points.vlw");
  R = new Robot (200, displayHeight/2);
  M = new ArrayList<Meteors>();
  Background = loadImage("Background_Space_" + BGnumber + ".jpg");
  invincibility = loadImage("meteor/buff0002.png");
  shot = new ArrayList<Lasers>();
  introMusic = minim.loadFile("2001- A Space Odyssey Theme song.mp3");
  loadAndPauseMusic = minim.loadFile("Fallout 2 Klamath Falls music.mp3");
  gameplayMusic = minim.loadFile("Halo 3 - One Final Effort - Soundtrack.mp3");
  gameOverMusic = minim.loadFile("Pier to Nowhere (dark, eerie ambient music) - Charlie Spring.mp3");
  explosionSound = minim.loadFile("explosion.mp3");
  lootBuffSound = minim.loadFile("Super Mario Bros.-Coin Sound Effect.mp3");
  shootingSound= minim.loadFile("laser.mp3");
  for (int i = 0; i < 12; i++) {
    M.add(new Meteors(random(height), random(50, 80), random(minspeed, maxspeed)));
  }
  timer=0;
  score=0;
  imageMode(CENTER);
}

void draw() {
  background(Background);
  if (startingGame==false) {
    introMusic.play();
    textFont(titleFont);
    textAlign(CENTER);
    textSize(100);
    fill(255, 255, 100);
    text("2015", width/2, height/2);
    fill(255);
    textSize(60);
    textFont(gameFont);
    text("a space oddity", width/2, height/2+60);
    textSize(30);
    text("press any key to begin", width/2, height/2+200);
  } else if (intro==true&&timer<=10) {
    introMusic.close();
    loadAndPauseMusic.play();
    textAlign(CENTER);
    textSize(45);
    text("many explorers will grow weary of an easy journey...", width/2, height/2);
    text("but space - Space offers no such thing.", width/2, height/2+100);
    textSize(30);
    text("dodge the meteors using the arrow keys or", width/2, height/2+225);
    text("shoot the meteors using the space bar", width/2, height/2+250);
    timer+=1;
    if (timer==600) {
      intro=false;
    }
  } else {
    if (pause) {
      pause();
    } else {
      loadAndPauseMusic.close();
      gameplayMusic.play();
      if (lootBuffSound.isPlaying()==false) {
        lootBuffSound.rewind();
      }
      if (gameplayMusic.isPlaying()==false) {
        gameplayMusic.rewind();
      }
      if (explosionSound.isPlaying()==false) {
        explosionSound.rewind();
      }
      updates = updates + 1;
      if ( updates == 60) {
        updates = 0;
        seconds = seconds + 1;
        respawntimer = respawntimer + 1;
        speedinctimer = speedinctimer + 1;
      }
      if (respawntimer == 8) {
        for (int i = 0; i < M.size (); i++) {
          if (M.get(i).respawn == true) {
            M.get(i).respawntimer = true;
          }
        }
        respawntimer = 0;
      }
      if (R.shoot) {
        shotAsize = shotAsize + 1;
        shot.add(new Lasers(R.x, R.y));
        shootingSound.rewind();
        shootingSound.play();
        R.shoot=false;
      }
      R.update();
      R.lifeupdate();
      R.life();
      R.variedCollision();     
      R.lifeup();
      for (int c = 0; c < MAsize; c++) {
        M.get(c).collision();
        M.get(c).update();
        M.get(c).respawn();
        score = score + M.get(c).scorecount;
        M.get(c).scorecount = 0;
      }
      for (int i = shot.size () - 1; i >= 0; i--) {
        shot.get(i).update();
        for (int c = MAsize; c >= 0; c--) {
          if (dist(shot.get(i).x + 25, shot.get(i).y, M.get(c).x, M.get(c).y) <= (M.get(c).size/2 + 10) && M.get(c).buff==false) {
            score+=10;
            M.get(c).explode=true;
            if (explosionSound.isPlaying()==false) {
              explosionSound.play();
            }
            M.get(c).oldx=M.get(c).x;
            M.get(c).oldy=M.get(c).y;
            M.get(c).contact();
          }
        }
        if (shot.get(i).x> width) {
          shot.remove(i);
        }
      }
      if (bombBuff) {
        for (int i = 0; i < MAsize; i++) {
          if (M.get(i).buff==false) {
            M.get(i).explode=true;
            explosionSound.play();
            M.get(i).oldx=M.get(i).x;
            M.get(i).oldy=M.get(i).y;
            M.get(i).contact();
          }
        }
        score+=MAsize*10;
        bombBuff=false;
      }
      counter();
      collision();
      Text();
      if (R.gameover) {
        gameover();
      }
      if (minspeed > 1.04) {
        minspeed = 1.039;
      }
      if (maxspeed > 1.075) {
        maxspeed = 1.074;
      }
      if (speedinctimer == 20 && MAsize > 10) {
        seconds=0; 
        minspeed = minspeed + 0.015;
        maxspeed = maxspeed + 0.015;
        speedinctimer = 0;
        MAsize = MAsize +1;
        for (int c = 0; c < MAsize; c++) {
          if (minspeed < 1.04 && maxspeed < 1.075) {
            M.get(c).incspeed();
          }
        }
      }
    }
  }
}

void keyReleased() {
  if (R.fireShots==false&&R.rocketJump==false) {
    if (keyCode==RIGHT||keyCode==LEFT||keyCode==DOWN) {
      R.inputCommands.clear();
      R.rollin=false;
      R.ballout=true;
    }
  }
  if (keyCode==UP) {
    R.removeSpecifiedInput("up");
    R.startLanding=true;
  }
  if (key==' ') {
    R.removeSpecifiedInput("space");
  }
}

void keyPressed() {
  if (startingGame==false) {
    startingGame=true;
  }
  if (key=='p'||key=='P') {
    if (pause) {
      gameplayMusic = minim.loadFile("Halo 3 - One Final Effort - Soundtrack.mp3");
      loop();
      pause=false;
    } else {
      pause=true;
    }
  }
  if (R.gameover==true&&key == 'Y' || R.gameover==true&&key == 'y') {
    gameOverMusic.close();
    retryCount+=1;
    R.gameover = false;
    MAsize = 5;
    setup();
    loop();
  }
}

void counter() {
  if (safetime == true) {
    safetimer = safetimer -1;
    image(invincibility, 450, height-50, 50, 50);
  }
  if (safetimer <= 0) {
    safetime = false;
    safetimer = 90;
    for (int i = 0; i < M.size (); i++) {
      M.get(i).gothit = false;
      hit = false;
    }
  }
  if (hit == true && safetime == false) {
    R.lifecount = R.lifecount -1;
    safetime = true;
  }
}

void collision() {
  for (int i = 0; i < M.size (); i++) {
    if (M.get(i).gothit == true) {
      if (M.get(i).buff==false&&M.get(i).respawn==false) {
        M.get(i).explode=true;
        explosionSound.play();
        M.get(i).oldx=M.get(i).x;
        M.get(i).oldy=M.get(i).y;
        hit = true;
      } else if (M.get(i).buff==true) {
        lootBuffSound.play();
        if (M.get(i).buffType=="shield") {
          safetime=true;
          safetimer=300;
        } else if (M.get(i).buffType=="life"&&R.lifecount<5) {
          R.heal = true;
        } else if (M.get(i).buffType=="bomb") {
          bombBuff=true;
        }
      }
      M.get(i).contact();
    }
  }
}   

void pause() {
  if (loadAndPauseMusic.isPlaying()==false) {
    loadAndPauseMusic.rewind();
  }
  gameplayMusic.close();
  loadAndPauseMusic = minim.loadFile("Fallout 2 Klamath Falls music.mp3");
  loadAndPauseMusic.play();
  textSize(50);
  text("you've decided to take a break in your journey", width/2, height/2);
  text("perhaps it's best to not venture on", width/2, height/2+100);
  noLoop();
}

void Text() {
  rectMode(CENTER);
  noFill();
  strokeWeight(6);
  stroke(255);
  rect(width/2+45, height-60, 150, 40, 15);
  textFont(pointsFont);
  textSize(32);
  fill(255);
  noStroke();
  text("SCORE", width/2+25, height-50);
  text(score, width/2+80, height-50);
  textFont(gameFont);
}

void gameover() {
  gameplayMusic.close();
  gameOverMusic.play();
  if (gameOverMusic.isPlaying()==false) {
    gameOverMusic.rewind();
  }
  background(Background);
  textSize(128);
  textFont(titleFont);
  fill(255, 0, 0);
  text("GAME OVER", width/2, height/2);
  fill(255);
  textSize(45);
  textFont(gameFont);
  text("we sacrificed " + retryCount + " to the exploration of space,", width/2, height/2+100);
  text("and for what was to gain?", width/2, height/2+150);
  textSize(30);
  text("despite your previous arbitrary efforts, do you still continue? (Y for yes)", width/2, height/2+225);
  Text();
  for (int i = 0; i < shot.size (); i++) {
    shot.clear();
  }
  for (int i = 0; i < M.size (); i++) {
    M.get(i).scoreclear();
    M.get(i).speedreset();
    M.remove(i);
  }
  noLoop();
}