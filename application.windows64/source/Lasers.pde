class Lasers {
  float x, y;
  float speed = 25;
  // some commented sections feature optional implementations to game
  //boolean supercharged=false;

  Lasers(float originx, float originy) {
    x = originx;
    y = originy;
  }

  void update() {
    x+=speed;
    PImage laserpic = loadImage("projectile attack/laser.png");
    imageMode(CENTER);
    image(laserpic, x, y, 60, 15);
  }
}