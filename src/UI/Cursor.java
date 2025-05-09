package UI;

import java.util.Scanner;
import java.io.Serializable;

public class Cursor implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int cursorX;
    private int cursorY;
    private boolean cursorActive;
    private int cursorZoneWidth;
    private int cursorZoneHeight;
    private String terrainUnderCursor;

    public Cursor(int x, int y, boolean cursorActive, int width, int height) {
        this.cursorX = x;
        this.cursorY = y;
        this.cursorActive = cursorActive;
        this.cursorZoneWidth = width;
        this.cursorZoneHeight = height;
    }

    public void setCursorY(int cursorY) {
        this.cursorY = cursorY;
    }

    public void setCursorX(int cursorX) {
        this.cursorX = cursorX;
    }

    public int getCursorY() {
        return cursorY;
    }

    public int getCursorX() {
        return cursorX;
    }

    public void setCursorActive(boolean cursorActive) {
        this.cursorActive = cursorActive;
    }

    public boolean isCursorActive() {
        return cursorActive;
    }

    public void selectCursorDirection() {
        System.out.println("Выберите направление перемещения (W - вверх, S - вниз, A - влево, D - вправо, F - начать движение):");
        Scanner scanner = new Scanner(System.in);
        char command = scanner.next().charAt(0);
        switch (command) {
            case 'w':
            case 'W':
                MoveCursorUp();
                break;
            case 'S':
            case 's':
                MoveCursorDown();
                break;

            case 'a':
            case 'A':
                MoveCursorLeft();
                break;

            case 'd':
            case 'D':
                MoveCursorRight();
                break;

            case 'f':
            case 'F':
                setCursorActive(false);
                break;
        }
    }

    public void moveCursor(int dx, int dy) {
        int newX = cursorX + dx;
        int newY = cursorY + dy;

        if (newX >= 0 && newX < cursorZoneWidth && newY >= 0 && newY < cursorZoneHeight) {
            cursorX = newX;
            cursorY = newY;
        }
    }

    public void MoveCursorUp() {
        if (cursorY > 0) {
            cursorY--;
        }
    }

    public void MoveCursorLeft() {
        if (cursorX > 0) {
            cursorX--;
        }
    }

    public void MoveCursorRight() {
        if (cursorX < cursorZoneWidth - 1) {
            cursorX++;
        }
    }

    public void MoveCursorDown() {
        if (cursorY < cursorZoneHeight - 1) {
            cursorY++;
        }
    }

    public void setTerrainUnderCursor(String terrainUnderCursor) {
        this.terrainUnderCursor = terrainUnderCursor;
    }

    public String getTerrainUnderCursor() {
        return terrainUnderCursor;
    }
}