void fillTable(Cell[][] labyrinth, Vertex[][] table, int w, int h) { 
  for (int x = 0; x <= w; x++) {
    for (int y = 0; y <= h; y++) {
      table[x][y] = new Vertex();
    }
  }
  
  for (int x = 0; x <= w; x++) {
    for (int y = 0; y <= h; y++) {
      table[x][y].up = isPointConnected(labyrinth, w, h, x, y, 0, -1);
      table[x][y].down = isPointConnected(labyrinth, w, h, x, y, 0, 1);
      table[x][y].right = isPointConnected(labyrinth, w, h, x, y, 1, 0);
      table[x][y].left = isPointConnected(labyrinth, w, h, x, y, -1, 0);
    }
  }
}

boolean isPointConnected(Cell[][] labyrinth, int w, int h, int x, int y, int dx, int dy) {  
  if (dx == 1 && x < w) {
    if (y == h) {
      return labyrinth[x][y - 1].wallDown;
    } else {
      return labyrinth[x][y].wallUp;
    }
  } else if (dx == -1 && x - 1 >= 0) {
    if (y == w) {
      return labyrinth[x - 1][y - 1].wallDown;
    } else {
      return labyrinth[x - 1][y].wallUp;
    }
  } else if (dy == 1 && y < h) {
    if (x == w) {
      return labyrinth[x - 1][y].wallRight;
    } else {
      return labyrinth[x][y].wallLeft;
    }
  } else if (dy == -1 && y - 1 >= 0) {
    if (x == w) {
      return labyrinth[x - 1][y - 1].wallRight;
    } else {
      return labyrinth[x][y - 1].wallLeft;
    }
  }
  
  return false;
}

void drawLabyrinth(Cell[][] labyrinth, Vertex[][] table, int w, int h, float cellWidth, float cellHeight) {
  
  int x = 0;
  int y = 0;
  while(true) {
    if (table[x][y].right) {
      table[x][y].right = false;
      if (x != w) {
        table[x + 1][y].left = false;
      }
      line(x * cellWidth, y * cellHeight, (x + 1) * cellWidth, y * cellHeight);
      x++;
      continue;
    }
    
    if (table[x][y].down) {
      table[x][y].down = false;
      if (y != h) {
        table[x][y + 1].up = false;
      }
      line(x * cellWidth, y * cellHeight, x * cellWidth, (y + 1) * cellHeight);
      y++;
      continue;
    }
    
    if (table[x][y].left) {
      table[x][y].left = false;
      if (x != 0) {
        table[x - 1][y].right = false;
      }
      line(x * cellWidth, y * cellHeight, (x - 1) * cellWidth, y * cellHeight);
      x--;
      continue;
    }
    
    if (table[x][y].up) {
      table[x][y].up = false;
      if (y != 0) {
        table[x][y - 1].down = false;
      }
      line(x * cellWidth, y * cellHeight, x * cellWidth, (y - 1) * cellHeight);
      y--;
      continue;
    }
    
    int X = -1;
    int Y = -1;
    float minDistance = Float.MAX_VALUE;
    for (int xx = 0; xx <= w; xx++) {
      for (int yy = 0; yy <= h; yy++) {
        if ((xx != x || yy != y) && table[xx][yy].hasConnexions()) {
          float distance = (x - xx) * (x - xx) + (y - yy) * (y - yy);
          
          if (distance < minDistance) {
            X = xx;
            Y = yy;
            minDistance = distance;
          }
        }
      }
    }
    
    if (X == -1 && Y == -1) {
      break;
    }
    
    x = X;
    y = Y;
  }
}
