package UI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CursorTest {
    private Cursor cursor;

    @BeforeEach
    public void setUp() {
        cursor = new Cursor(2, 2, true, 5, 5);
    }

    @Test
    public void testInitialization() {
        assertEquals(2, cursor.getCursorX());
        assertEquals(2, cursor.getCursorY());
        assertTrue(cursor.isCursorActive());
        // Проверяем, что курсор не выходит за границы (если moveCursor работает)
        cursor.moveCursor(10, 10); // Пытаемся выйти за пределы
        assertEquals(2, cursor.getCursorX()); // Остаётся в пределах
        assertEquals(2, cursor.getCursorY());
    }

    @Test
    public void testMoveCursorWithinBounds() {
        cursor.moveCursor(1, 1);
        assertEquals(3, cursor.getCursorX());
        assertEquals(3, cursor.getCursorY());
    }

    @Test
    public void testMoveCursorOutOfBounds() {
        cursor.moveCursor(-3, -3); // Попытка выйти за границы (x=2-3=-1, y=2-3=-1)
        assertEquals(2, cursor.getCursorX()); // X не должен измениться
        assertEquals(2, cursor.getCursorY()); // Y не должен измениться
    }

    @Test
    public void testMoveCursorUp() {
        cursor.MoveCursorUp();
        assertEquals(2, cursor.getCursorX());
        assertEquals(1, cursor.getCursorY());
    }

    @Test
    public void testMoveCursorUpAtTopBoundary() {
        cursor.setCursorY(0);
        cursor.MoveCursorUp();
        assertEquals(0, cursor.getCursorY()); // Должен остаться на 0
    }

    @Test
    public void testMoveCursorDown() {
        cursor.MoveCursorDown();
        assertEquals(2, cursor.getCursorX());
        assertEquals(3, cursor.getCursorY());
    }

    @Test
    public void testMoveCursorDownAtBottomBoundary() {
        cursor.setCursorY(4);
        cursor.MoveCursorDown();
        assertEquals(4, cursor.getCursorY()); // Должен остаться на 4
    }

    @Test
    public void testMoveCursorLeft() {
        cursor.MoveCursorLeft();
        assertEquals(1, cursor.getCursorX());
        assertEquals(2, cursor.getCursorY());
    }

    @Test
    public void testMoveCursorLeftAtLeftBoundary() {
        cursor.setCursorX(0);
        cursor.MoveCursorLeft();
        assertEquals(0, cursor.getCursorX()); // Должен остаться на 0
    }

    @Test
    public void testMoveCursorRight() {
        cursor.MoveCursorRight();
        assertEquals(3, cursor.getCursorX());
        assertEquals(2, cursor.getCursorY());
    }

    @Test
    public void testMoveCursorRightAtRightBoundary() {
        cursor.setCursorX(4);
        cursor.MoveCursorRight();
        assertEquals(4, cursor.getCursorX()); // Должен остаться на 4
    }

    @Test
    public void testSetCursorActive() {
        cursor.setCursorActive(false);
        assertFalse(cursor.isCursorActive());
    }

    @Test
    public void testSetTerrainUnderCursor() {
        cursor.setTerrainUnderCursor("Grass");
        assertEquals("Grass", cursor.getTerrainUnderCursor());
    }

}