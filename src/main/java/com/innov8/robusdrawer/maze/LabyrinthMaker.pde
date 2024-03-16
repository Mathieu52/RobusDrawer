/*======================================================================
                          Initialization methods
========================================================================*/

public void generateMaze(Cell[][] labyrinth, int size, int startX, int startY) {
  generateMaze(labyrinth, size, size, startX, startY);
}

public void generateMaze(Cell[][] labyrinth, int sizeX, int sizeY, int startX, int startY) {
  for (int x = 0; x < sizeX; x++) {
    for (int y = 0; y < sizeY; y++) {
      labyrinth[x][y] = new Cell();
    }
  }
  
  labyrinth[startX][startY].visited = true;
  
  generateMazeRecursive(labyrinth, new Point(startX, startY), sizeX, sizeY); // Start the generation process
}

public void generateMaze(Cell[][] labyrinth, int size) {
  generateMaze(labyrinth, size, size);
}


public void generateMaze(Cell[][] labyrinth, int sizeX, int sizeY) {
  int startX = (int) random(sizeX);
  int startY = (int) random(sizeY);
  
  generateMaze(labyrinth, sizeX, sizeY, startX, startY);
}

/*======================================================================
                          Generation methods
========================================================================*/

// Represents all the possible directions of a cell's neighbour
//    [ ]
//   []*[]
//    [ ]

private Point[] directions = {Point.UP, Point.RIGHT, Point.DOWN, Point.LEFT};

// This method take in the grid and a position and return the the indexes of the free neighbours at this position
public NeighbourPoints getFreeNeighbours(Cell[][] labyrinth, Point position, int w, int h) {
  NeighbourPoints freeNeighbours = new NeighbourPoints();
  
  for (Point direction : directions) {
    Point neighbourPosition = Point.add(position, direction);
    
    if (isPointInsideGrid(neighbourPosition, w, h) && isCellFree(labyrinth, neighbourPosition)) {  // Make sure the neighbour is part of the grid and is free
      //freeNeighbours.add(neighbourPosition);
      freeNeighbours.neighbours[freeNeighbours.count++] = neighbourPosition;
    }
  }
  
  return freeNeighbours;
}

// A cell is free if doesn't exist in the grid
public boolean isCellFree(Cell[][] labyrinth, Point position) {
  return !labyrinth[position.x][position.y].visited;
}

public boolean isPointInsideGrid(Point point, int w, int h) {
  return point.x >= 0 && point.x <= w - 1 && point.y >= 0 && point.y <= h - 1;
}

public void generateMazeRecursive(Cell[][] labyrinth, Point position, int w, int h) {
  Point newCellPosition;
  
  for (int i = 0; i < 4; i++) { // Loop for times at max (Their cannot be more than 4 neighbours)
    NeighbourPoints freeNeighbours = getFreeNeighbours(labyrinth, position, w, h);
    if (freeNeighbours.count == 0) {
      return; // If you don't have any neighbour (stuck) return to your caller (the previous cell)
    }
  
    int index = (int) random(freeNeighbours.count);
    
    newCellPosition = freeNeighbours.neighbours[index];
    
    // Get the current cell and create the new one
    Cell currentCell = labyrinth[position.x][position.y];
    
    // Place the new cell in the grid
    Cell newCell = labyrinth[newCellPosition.x][newCellPosition.y];
    newCell.visited = true;;
    
    // Connect both cells (remove the wall between them)
    currentCell.connect(position, newCellPosition);
    newCell.connect(newCellPosition, position);
    
    // Move on to the new cell
    generateMazeRecursive(labyrinth, newCellPosition, w, h);
  }
}

public void openLabyrinth(Cell[][] labyrinth, int w, int h) {
  labyrinth[0][0].wallUp = false;
  labyrinth[w - 1][h - 1].wallDown = false;
}
