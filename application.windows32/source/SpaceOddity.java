import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class SpaceOddity extends PApplet {


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
float minspeed = 1.01f, maxspeed = 1.025f;
int backgroundcolor;
boolean hit = false, pause = false;
boolean safetime = true;
int safetimer = 360, score = 0;
int MAsize = 5, shotAsize = 0;
int respawntimer = 0, seconds = 0, updates = 0, speedinctimer = 0, retryCount=1;
int BGnumber = PApplet.parseInt(random(1, 18));
int timer=0;
boolean startingGame=false, spawnlaser=false, lose=false, intro=true, bombBuff=false;
PFont titleFont, gameFont, pointsFont;

public void setup() {
  
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

public void draw() {
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
      if (minspeed > 1.04f) {
        minspeed = 1.039f;
      }
      if (maxspeed > 1.075f) {
        maxspeed = 1.074f;
      }
      if (speedinctimer == 20 && MAsize > 10) {
        seconds=0; 
        minspeed = minspeed + 0.015f;
        maxspeed = maxspeed + 0.015f;
        speedinctimer = 0;
        MAsize = MAsize +1;
        for (int c = 0; c < MAsize; c++) {
          if (minspeed < 1.04f && maxspeed < 1.075f) {
            M.get(c).incspeed();
          }
        }
      }
    }
  }
}

public void keyReleased() {
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

public void keyPressed() {
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

public void counter() {
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

public void collision() {
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

public void pause() {
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

public void Text() {
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

public void gameover() {
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
class Lasers {
  float x, y;
  float speed = 25;
  // some commented sections feature optional implementations to game
  //boolean supercharged=false;

  Lasers(float originx, float originy) {
    x = originx;
    y = originy;
  }

  public void update() {
    x+=speed;
    PImage laserpic = loadImage("projectile attack/laser.png");
    imageMode(CENTER);
    image(laserpic, x, y, 60, 15);
  }
}
class Meteors {
  float x, y, oldx=0, oldy=0, size, Mspeed, speed, minspeed = 1.01f, maxspeed =1.025f;
  float laserx, lasery, hitdistance, closestdistance;
  ArrayList<PImage> rock;
  boolean rollin = false, rocketJump=false, startLanding=false, gothit = false, respawn=false, respawntimer = false, explode=false, contactexplode = false, buff = false;
  ArrayList<PImage> explosion;
  int frame=0, falsecounter = 2, scorecount, buffRandomizer, buffCode;
  float robotpos = 21;
  String buffType;
  ArrayList<PImage> buffs;

  Meteors(float y_, float s_, float inc) {
    y = y_;
    size = s_;
    x = width;
    Mspeed = 2;
    speed = inc;
    scorecount = scorecount + 0;
    buffs = new ArrayList <PImage>();
    rock = new ArrayList <PImage>();
    explosion= new ArrayList <PImage>();
    for (int i = 0; i<6; i++) {
      rock.add(loadImage("meteor/meteor000"+(i+1)+".png"));
    }
    for (int i= 0; i<16; i++) {
      explosion.add(loadImage("explosion/slice0"+(i+1)+".png"));
    }
    for (int i=0; i<3; i++) {
      buffs.add(loadImage("meteor/buff000"+(i+1)+".png"));
    }
  }

  public void update() {
    if (explode==true&&buff==false) {
      explosionAnimation();
    } else {
      if (buff==false) {
        PImage current = rock.get(PApplet.parseInt(frameCount/4.5f) % rock.size());
        image(current, x, y, size*3, size);
      } else {
        PImage current = buffs.get(buffCode);
        image(current, x, y, 50, 50);
      }
      Mspeed = Mspeed * speed;
      x = x - Mspeed;
    }
  }

  public void incspeed() {
    minspeed = minspeed + 0.015f;
    maxspeed = maxspeed + 0.015f;
  }

  public void collision() {
    closestdistance = size/2 + 108/2;
    hitdistance = dist((R.x + robotpos), R.y, x-size, y);
    if (hitdistance <= closestdistance) {
      gothit = true;
    }
  }

  public void scoreclear() {
    scorecount = 0;
  }

  public void speedreset() {
    minspeed = 1.01f;
    maxspeed = 1.025f;
  }

  public void explosionAnimation() {
    PImage current = explosion.get(frame);
    image(current, oldx-size, oldy);
    if (frameCount%2==0) {
      frame+=1;
    }
    if (frame==16) {
      frame=0;
      explode=false;
    }
  }

  public void contact() {
    x = width + (size*2);
    speed = 0;
    gothit=false;
    respawn = true;
  }

  public void respawn() {
    if (respawn == true && respawntimer == true) {
      speed = random(minspeed, maxspeed);
      Mspeed = 1;
      respawn = false;
      respawntimer = false;
      buff = false;
      buffgenerator();
    } else if (respawn == false) {
      if ( x <= 0-size) {
        x = width+size;
        y = random(height);
        Mspeed = 2;
        size = random(30, 75);
        speed = random(minspeed, maxspeed);
        scorecount = scorecount + 10;
        buff = false;
        buffgenerator();
      }
    }
  }

  public void buffgenerator() {
    buffRandomizer = PApplet.parseInt(random(1, 20));
    if (buffRandomizer==1) {
      buff=true;
      buffCode = floor(random(0, 3));
      if (buffCode==0) {
        buffType = "life";
      } else if (buffCode==1) {
        buffType = "shield";
      } else {
        buffType = "bomb";
      }
    }
  }

  public void rollincollision() {
    robotpos = 0;
  }

  public void idlecollision() {
    robotpos = 21;
  }
}
class Robot {
  boolean reverse =false, shoot=false, rollin = false, ballout = false, fireShots = false, rocketJump=false, startLanding=false, ballform=false, existingCommand=false;
  float x, y, laserx, lasery;
  float xspeed, yspeed, resistance, actualxspeed, actualyspeed;
  float speed = 1.03f;
  boolean lifecount1 = true, lifecount2 = true, lifecount3 = true, lifecount4 = true, lifecount5 = true, gameover = false, transforming = false, heal = false;
  ArrayList <PImage> ballmove;
  ArrayList <PImage> jump;
  ArrayList <PImage> attack;
  ArrayList <PImage> boost;
  ArrayList <PImage> ballup;
  ArrayList <PImage> transformin;
  ArrayList <PImage> rtransformin;
  ArrayList <PImage> transformOut;
  ArrayList <String> inputCommands;
  int lifecount = 5;
  PImage hearts;
  PImage noheart;
  int runFrame=0;
  int delay=0;

  Robot(int retrievex, int retrievey) {
    x = retrievex;
    y = retrievey;
    resistance = 1;
    xspeed = 0;
    yspeed = 0;
    actualxspeed = 0;
    actualyspeed = 0;
    hearts = loadImage("heart.png");
    noheart = loadImage("deadheart.png");
    ballmove = new ArrayList <PImage>();
    jump = new ArrayList <PImage>();
    attack = new ArrayList <PImage>();
    boost = new ArrayList <PImage>();
    ballup = new ArrayList <PImage>();
    transformin = new ArrayList <PImage>();
    transformOut = new ArrayList <PImage>();
    rtransformin= new ArrayList <PImage>();
    inputCommands=new ArrayList <String>();
    for (int i=0; i<17; i++) {
      jump.add(loadImage("Jump/jump000"+(i+1)+".png"));
      //resizing images right when we create object in the beginning because loading time to start game isn't as bad as latency during gameplay
      jump.get(i).resize(150, 0);
    }
    for (int i=0; i<9; i++) {
      attack.add(loadImage("projectile attack/robot attack000"+(i+1)+".png"));
      attack.get(i).resize(0, 200);
    }
    for (int i=0; i<13; i++) {
      boost.add(loadImage("running speed up/Running000"+(i+1)+".png"));
      boost.get(i).resize(0, 200);
    }
    for (int i= 12; i < 16; i++) {
      transformin.add(loadImage("Ball Rolling sliding/Transform00"+(i+1)+".png"));
      //subtractions in the get statement such as (i-12) is to prevent referencing an invalid element, whereas the numbering of transform00 starts at 13, 
      //the imported picture is element 0 in the array
      transformin.get(i-12).resize(0, 100);
    }
    for (int i= 17; i > 13; i--) {
      //much like above scenario, but since our counter counts backwards, we must make sure all negative values after (0) is converted to positive value
      //which can be done through multiplying by *-1
      rtransformin.add(loadImage("Ball Rolling sliding/Transform00"+(i-1)+".png"));
      rtransformin.get((i-17)*-1).resize(0, 100);
    }
    for (int i=11; i>=0; i--) {
      //refer to above comment
      transformOut.add(loadImage("Transform into ball/Transform000"+(i+1)+".png"));
      if (i+1==1) {
        transformOut.get((i-11)*-1).resize(0, 200);
      } 
      else if (i+1==2) {
        transformOut.get((i-11)*-1).resize(0, 196);
      } 
      else if (i+1==3) {
        transformOut.get((i-11)*-1).resize(0, 178);
      } 
      else if (i+1==4) {
        transformOut.get((i-11)*-1).resize(0, 154);
      } 
      else {
        transformOut.get((i-11)*-1).resize(100, 0);
      }
    }
    for (int i=0; i < 12; i++) {
      ballup.add(loadImage("Transform into ball/Transform000"+(i+1)+".png"));
      if (i+1==1) {
        ballup.get(i).resize(0, 200);
      } 
      else if (i+1==2) {
        ballup.get(i).resize(0, 196);
      } 
      else if (i+1==3) {
        ballup.get(i).resize(0, 178);
      } 
      else if (i+1==4) {
        ballup.get(i).resize(0, 154);
      } 
      else {
        ballup.get(i).resize(100, 0);
      }
    }
  }

  public void update() {
    for (int i = 0; i < inputCommands.size (); i++) {
      //println(inputCommands.get(i));
    }
    if (inputCommands.size()==0) {
      //println("none");
    }
    //prevent going off screen
    if (!(x+actualxspeed >= width-46||x+actualxspeed<=46)) {
      x+=actualxspeed;
    }
    if (!(y + actualyspeed >= height-50||y+actualyspeed<=60)) {
      y = y + actualyspeed;
    }
    //physics of game to get sliding motion
    actualyspeed = yspeed;
    actualxspeed = xspeed;
    xspeed = xspeed/resistance;
    yspeed = yspeed/resistance;
    resistance = resistance*resistance;
    //actual animations are determined based on the first inputCommand that is held down to prevent conflicting keyRelease (and crashes) due to key detection functions on processing
    if (ballout==false&&fireShots==false&&rocketJump==false) {
      if (inputCommands.size()>0) {
        if (inputCommands.get(0)=="up") {
          rocketJump=true;
          resistance = 1;
        } 
        else if (inputCommands.get(0)=="down") {
          //variables such as speed and resistance are executed before roll as it allows an easier way of considering different scenarios
          reverse = false;
          yspeed = 7;
          xspeed = 0;
          resistance = 1;
          roll();
        } 
        else if (inputCommands.get(0)=="left") {
          reverse = true;
          xspeed = -7;
          yspeed = 0;
          resistance = 1;
          roll();
        } 
        else if (inputCommands.get(0)=="right") {
          reverse = false;
          xspeed = 7;
          yspeed = 0;
          resistance = 1;
          roll();
        } 
        else if (inputCommands.get(0)=="space") {
          fireShots=true;
        }
      } 
      else {
        resistance = 1.05f;
        //a rounding-esque algrithm to prevent infinitely sliding when you have only miniscule amounts of momentum left
        if (actualxspeed <= 0.1f && actualxspeed >= -0.1f) {
          xspeed = 0;
        }
        if (actualyspeed <= 0.1f && actualyspeed >= -0.1f) {
          yspeed = 0;
        }
        PImage current = loadImage("Jump/jump0001.png");
        current.resize(0, 200);
        image(current, x, y);
      }
    }
    //precisely measuring input ONLY IF no forced animations are currently running
    if (ballout==true) {
      transformout();
    } 
    else if (fireShots==true) {
      shootAnimation();
    } 
    else if (rocketJump==true) {
      jump();
    } 
    else {
      if (keyPressed==true) {
        if (key == ' ') {
          inputCommands.add("space");
        }
        if (key == CODED) {
          if (keyCode == UP) {
            inputCommands.add("up");
          }
          if (keyCode == DOWN) {
            inputCommands.add("down");
          }
          if (keyCode == RIGHT) {
            inputCommands.add("right");
          }
          if (keyCode == LEFT) {
            inputCommands.add("left");
          }
        }
      }
    }
  }
  public void jump() {
    //below if statement is when the movement of the jump actually happens (in sync of when flames appear)
    if (runFrame>=4 && runFrame<=6) {
      yspeed = -8;
      xspeed = 0;
      resistance = 1;
    }
    PImage current = jump.get(runFrame);
    if (runFrame==4||runFrame==7||runFrame==8) {
      //although looks like random frames' y coordinates are modified, these statements simply cater to the rocket boosting images where the robot is higher than its natural state,
      //therefore y was changed ONLY within the outputting of certain frames to ensure that the robot's height is printed consistently
      image(current, x, y+30);
    } 
    else if (runFrame==5||runFrame==6) {
      image(current, x, y+50);
    } 
    else {
      image(current, x, y);
    }
    //frame is moduled in 3 so that if statement runs every 3 seconds
    //stops progressing frames at 5 because 6th frame is the last frame with rocket flame at highest point, allowing users to continue jumping until they release key and descend afterwards
    if (frameCount%3==0&&runFrame<=5||frameCount%3==0&&startLanding==true) {
      runFrame+=1;
    }
    //as this is a forced animation, below if statement allows robot to move freely again
    if (runFrame==17) {
      runFrame=0;
      //reset both verification variables to determine force animation for next time jump is needed
      rocketJump=false;
      startLanding=false;
    }
  }
  public void roll() {
    //did they roll into a ball yet? if not this statement forces them to do so, the transformin method will set rollin to true after transformin's arraylist is finished
    if (rollin==false) {
      transformin();
      transforming = true;
      //yspeed = 0;
      //xspeed = 0;
    }
    //code to make sure that robot ballform moves clockwise when rolling to the right or down, and counterclockwise rolling to the left
    else if (reverse==true) {
      PImage current = rtransformin.get(PApplet.parseInt(frameCount/4.5f) % rtransformin.size());
      image(current, x, y);
    } 
    else if (reverse==false) {
      PImage current = transformin.get(PApplet.parseInt(frameCount/4.5f) % transformin.size());
      image(current, x, y);
    }
  }
  public void transformin() {
    //much like jump()'s forced frames
    PImage current = ballup.get(runFrame);
    image(current, x, y);
    if (frameCount%2==0) {
      runFrame+=1;
    }
    if (runFrame==12) {
      runFrame=0;
      rollin=true;
      ballform=true;
    }
  }
  public void transformout() {
    //executes when an arrow key is released (modifiction of ballout is done in the main class, where keypressed is accessible)
    PImage current = transformOut.get(runFrame);
    image(current, x, y);
    if (frameCount%2==0) {
      runFrame+=1;
    }
    if (runFrame==12) {
      runFrame=0;
      ballout=false;
      ballform=false;
      transforming = false;
    }
  }
  public void shootAnimation() {
    //much like jump() method
    PImage current = attack.get(runFrame);
    image(current, x, y);
    if (frameCount%3==0) {
      runFrame+=1;
    }
    if (runFrame==9) {
      runFrame=0;
      fireShots=false;
    } 
    else if (runFrame==4&&frameCount%3==0) {
      //variable controlling the spawning of laser, which only spawns at the beginning of 5th frame (or rather, the furthest the robot pulls its gun)
      shoot=true;
    }
  }
  //method checks to see if the key is already in the list, although any input other than the first element of an arraylist is disregarded, 
  //the method does prevent HUGE game lag from pressing down one key
  public void addToInput(String addKey) {
    for (int i = 0; i < inputCommands.size (); i++) {
      if (inputCommands.get(i).equals(addKey)) {
        existingCommand=true;
      }
    }
    if (existingCommand==false) {
      inputCommands.add(addKey);
    }
    existingCommand=false;
  }
  public void removeSpecifiedInput(String removeKey) {
    for (int i = 0; i < inputCommands.size (); i++) {
      if (inputCommands.get(i).equals(removeKey)) {
        inputCommands.remove(i);
      }
    }
  }
  public void life() {
    //println(lifecount);
    if (lifecount == 5) {
      lifecount1 = true;
      lifecount2 = true;
      lifecount3 = true;
      lifecount4 = true;
      lifecount5 = true;
    } 
    else if (lifecount == 4) {
      lifecount1 = true;
      lifecount2 = true;
      lifecount3 = true;
      lifecount4 = true;
      lifecount5 = false;
    } 
    else if (lifecount == 3) {
      lifecount1 = true;
      lifecount2 = true;
      lifecount3 = true;
      lifecount4 = false;
      lifecount5 = false;
    } 
    else if (lifecount == 2) {
      lifecount1 = true;
      lifecount2 = true;
      lifecount3 = false;
      lifecount4 = false;
      lifecount5 = false;
    } 
    else if (lifecount == 1) {
      lifecount1 = true;
      lifecount2 = false;
      lifecount3 = false;
      lifecount4 = false;
      lifecount5 = false;
    } 
    else if (lifecount == 0) {
      lifecount1 = false;
      lifecount2 = false;
      lifecount3 = false;
      lifecount4 = false;
      lifecount5 = false;
      gameover = true;
    }
  }

  public void lifeupdate() {
    if (lifecount1 == true) {
      image(hearts, 100, height-50, 50, 50);
    } 
    else if (lifecount1 == false) {
      image(noheart, 100, height-50, 50, 50);
    }
    if (lifecount2 == true) {
      image(hearts, 170, height-50, 50, 50);
    } 
    else if (lifecount2 == false) {
      image(noheart, 170, height-50, 50, 50);
    }
    if (lifecount3 == true) {
      image(hearts, 240, height-50, 50, 50);
    } 
    else if (lifecount3 == false) {
      image(noheart, 240, height-50, 50, 50);
    }
    if (lifecount4 == true) {
      image(hearts, 310, height-50, 50, 50);
    } 
    else if (lifecount4 == false) {
      image(noheart, 310, height-50, 50, 50);
    }
    if (lifecount5 == true) {
      image(hearts, 380, height-50, 50, 50);
    } 
    else if (lifecount5 == false) {
      image(noheart, 380, height-50, 50, 50);
    }
  }

  public void lifeup() {
    if (heal == true) {
    lifecount = lifecount + 1;
    heal = false;
    }
  }

  public void variedCollision() {
    for (int i = 0; i < M.size(); i++) {
      if (rollin == true || transforming == true) {
        M.get(i).rollincollision();
      } 
      else {
        M.get(i).idlecollision();
      }
    }
  }
}
  public void settings() {  size(1280, 720); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "SpaceOddity" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
