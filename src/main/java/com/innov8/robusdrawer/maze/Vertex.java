package com.innov8.robusdrawer.maze;

public class Vertex {
  private boolean up;
  private boolean down;
  private boolean left;
  private boolean right;

  public Vertex() {
    this(false, false, false, false);
  }

  public Vertex(boolean up, boolean down, boolean left, boolean right) {
    this.up = up;
    this.down = down;
    this.left = left;
    this.right = right;
  }

  public int getConnexionCount() {
    return (up ? 1 : 0) + (down ? 1 : 0) + (left ? 1 : 0) + (right ? 1 : 0);
  }
  
  public boolean hasConnexions() {
    return up || down || left || right;
  }

  public boolean isUp() {
    return up;
  }

  public void setUp(boolean up) {
    this.up = up;
  }

  public boolean isDown() {
    return down;
  }

  public void setDown(boolean down) {
    this.down = down;
  }

  public boolean isLeft() {
    return left;
  }

  public void setLeft(boolean left) {
    this.left = left;
  }

  public boolean isRight() {
    return right;
  }

  public void setRight(boolean right) {
    this.right = right;
  }
}
