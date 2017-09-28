class Meteors {
  float x, y, oldx=0, oldy=0, size, Mspeed, speed, minspeed = 1.01, maxspeed =1.025;
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

  void update() {
    if (explode==true&&buff==false) {
      explosionAnimation();
    } else {
      if (buff==false) {
        PImage current = rock.get(int(frameCount/4.5) % rock.size());
        image(current, x, y, size*3, size);
      } else {
        PImage current = buffs.get(buffCode);
        image(current, x, y, 50, 50);
      }
      Mspeed = Mspeed * speed;
      x = x - Mspeed;
    }
  }

  void incspeed() {
    minspeed = minspeed + 0.015;
    maxspeed = maxspeed + 0.015;
  }

  void collision() {
    closestdistance = size/2 + 108/2;
    hitdistance = dist((R.x + robotpos), R.y, x-size, y);
    if (hitdistance <= closestdistance) {
      gothit = true;
    }
  }

  void scoreclear() {
    scorecount = 0;
  }

  void speedreset() {
    minspeed = 1.01;
    maxspeed = 1.025;
  }

  void explosionAnimation() {
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

  void contact() {
    x = width + (size*2);
    speed = 0;
    gothit=false;
    respawn = true;
  }

  void respawn() {
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

  void buffgenerator() {
    buffRandomizer = int(random(1, 20));
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

  void rollincollision() {
    robotpos = 0;
  }

  void idlecollision() {
    robotpos = 21;
  }
}