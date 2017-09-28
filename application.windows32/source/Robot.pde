class Robot {
  boolean reverse =false, shoot=false, rollin = false, ballout = false, fireShots = false, rocketJump=false, startLanding=false, ballform=false, existingCommand=false;
  float x, y, laserx, lasery;
  float xspeed, yspeed, resistance, actualxspeed, actualyspeed;
  float speed = 1.03;
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

  void update() {
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
        resistance = 1.05;
        //a rounding-esque algrithm to prevent infinitely sliding when you have only miniscule amounts of momentum left
        if (actualxspeed <= 0.1 && actualxspeed >= -0.1) {
          xspeed = 0;
        }
        if (actualyspeed <= 0.1 && actualyspeed >= -0.1) {
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
  void jump() {
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
  void roll() {
    //did they roll into a ball yet? if not this statement forces them to do so, the transformin method will set rollin to true after transformin's arraylist is finished
    if (rollin==false) {
      transformin();
      transforming = true;
      //yspeed = 0;
      //xspeed = 0;
    }
    //code to make sure that robot ballform moves clockwise when rolling to the right or down, and counterclockwise rolling to the left
    else if (reverse==true) {
      PImage current = rtransformin.get(int(frameCount/4.5) % rtransformin.size());
      image(current, x, y);
    } 
    else if (reverse==false) {
      PImage current = transformin.get(int(frameCount/4.5) % transformin.size());
      image(current, x, y);
    }
  }
  void transformin() {
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
  void transformout() {
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
  void shootAnimation() {
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
  void addToInput(String addKey) {
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
  void removeSpecifiedInput(String removeKey) {
    for (int i = 0; i < inputCommands.size (); i++) {
      if (inputCommands.get(i).equals(removeKey)) {
        inputCommands.remove(i);
      }
    }
  }
  void life() {
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

  void lifeupdate() {
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

  void lifeup() {
    if (heal == true) {
    lifecount = lifecount + 1;
    heal = false;
    }
  }

  void variedCollision() {
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