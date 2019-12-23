import static isel.leic.pg.Console.*;

public class Panel {
    // Colors used for frames [0..8]
    public static final int[] COLORS = { RED, GREEN, BLUE, YELLOW, CYAN, PINK, MAGENTA, WHITE, ORANGE };

    private static final int
        FRAMES_DIM = ColorFrames.FRAMES_DIM,
        BOARD_DIM = ColorFrames.BOARD_DIM,
        GRID_SIZE = FRAMES_DIM*2+1,
        STATUS_LINES = Math.max(GRID_SIZE,4)+2,
        SCORE_COLS = 6,
        MESSAGE_COLS = 6,
        BOARD_SIZE = (GRID_SIZE+1) * BOARD_DIM +1,
        LINES = BOARD_SIZE + STATUS_LINES,
        COLS = Math.max(BOARD_SIZE, SCORE_COLS+GRID_SIZE+MESSAGE_COLS),
        FONT_SIZE = 18;

    /**
     * Open console window and print initial panel
     */
    public static void init() {
        fontSize(FONT_SIZE);
        open("PG Color Frames", LINES, COLS);
        enableMouseEvents(false);
        printGrid();
    }

    /**
     * Close console window
     */
    public static void end() {
        close();
    }

    /**
     * Print initial panel
     */
    public static void printGrid() {
        clearRect(0,0,BOARD_SIZE,BOARD_SIZE,DARK_GRAY);
        char name = '1';
        for (int p = 1; p <= BOARD_DIM*BOARD_DIM; p++) {
            cursor(gridLine(p),gridCol(p));
            color(WHITE,DARK_GRAY);
            print(name);
            if (name=='9') name='A'; else ++name;
            for (int f = 0; f < FRAMES_DIM; f++) {
                clearFrame(f, p);
                sleep(30);      // dynamic effect
            }
        }
        printLabel("Score",1); printScore(0);
        printLabel("Level",4); printValue(0,5);
        clearArea();
    }

    private static final int COL_SCORE = Math.max(BOARD_SIZE/2-3*GRID_SIZE/2,1);
    private static void printLabel(String txt, int line) {
        cursor(BOARD_SIZE+line,COL_SCORE);
        color(GRAY,BLACK);
        print(center(txt,SCORE_COLS));
    }
    private static void printValue(int value, int line) {
        cursor(BOARD_SIZE+line,COL_SCORE);
        color(WHITE,BLACK);
        print(center(""+value,SCORE_COLS));
    }

    /**
     * Print score value
     * @param score
     */
    public static void printScore(int score) { printValue(score,2); }

    /**
     * Print score value
     * @param level
     */
    public static void printLevel(int level) { printValue(level,5); }

    /**
     * Print one frame with the indicated size, color and position
     * @param frame size of frame [0 .. FRAME_DIM-1]
     * @param color color of frame [0 .. MAX_COLORS-1]
     * @param gridNum position of frame [1 .. BOARD_DIM*BOARD_DIM-1] or 0 (piece position)
     */
    public static void printFrame(int frame, int color, int gridNum) {
        // Test arguments validation
        if (frame<0 || frame>=FRAMES_DIM || color<0 || color>=COLORS.length || gridNum<0 || gridNum>BOARD_DIM*BOARD_DIM)
            throw new IllegalArgumentException("frame="+frame+", color="+color+", gridNum="+gridNum);
        color(BLACK,COLORS[color]);
        printFrame(frame, gridNum, '+', '-', '|');
    }

    /**
     * Clear the frame with the indicated size and position
     * @param frame size of frame [0 .. FRAME_DIM-1]
     * @param gridNum position of frame [1 .. BOARD_DIM*BOARD_DIM-1] or 0 (piece position)
     */
    public static void clearFrame(int frame, int gridNum) {
        // Test arguments validation
        if (frame<0 || frame>=FRAMES_DIM || gridNum<0 || gridNum>BOARD_DIM*BOARD_DIM)
            throw new IllegalArgumentException("frame="+frame+", gridNum="+gridNum);
        setBackground(BLACK);
        printFrame(frame, gridNum, ' ', ' ', ' ');
    }

    private static void printFrame(int frame, int gridNum, char corner, char top, char side) {
        int size = (frame + 1) * 2 + 1; // 3,5,7,...
        int line = gridLine(gridNum) - frame - 1;
        int col = gridCol(gridNum) - frame - 1;
        cursor(line, col);  printTopLine(size, corner, top);
        for (int l = 1; l < size - 1; ++l) {
            cursor(++line, col); print(side);
            cursor(line, col + size - 1); print(side);
        }
        cursor(++line, col); printTopLine(size, corner, top);
    }

    private static void printTopLine(int size, char corner, char fill) {
        print(corner); printRepeat(fill, size - 2); print(corner);
    }

    private static final int COL_PIECE = Math.max(BOARD_SIZE/2, SCORE_COLS+GRID_SIZE/2);
    private static int gridCol(int p) {
        return p==0 ? COL_PIECE : (GRID_SIZE+1)*((p-1)%BOARD_DIM) + GRID_SIZE/2 +1;
    }
    private static int gridLine(int p) {
        return p==0 ? BOARD_SIZE+GRID_SIZE/2+1 : BOARD_SIZE-GRID_SIZE/2-1 - (GRID_SIZE + 1) * ((p-1)/BOARD_DIM) -1;
    }

    private static void clearRect(int lin, int col, int height, int width, int color) {
        setBackground(color);
        for(int i=0 ; i<height ; ++i) {
            cursor(lin + i, col);
            printRepeat(' ',width);
        }
    }

    private static void printRepeat(char c, int times) {
        for (; times>0 ; times--) print(c);
    }

    private static final int LINE_MESSAGE = BOARD_SIZE + 1;
    private static final int COL_MESSAGE = COL_PIECE +GRID_SIZE/2+1;
    private static final int LEN_MESSAGE = COLS-COL_MESSAGE;

    private static final void clearArea() {
        clearRect(LINE_MESSAGE,COL_MESSAGE,GRID_SIZE,LEN_MESSAGE,BLACK);
    }

    /**
     * Show the message
     * @param msg Text to print. ';' is the change the line char.
     */
    public static void printMessage(String msg) {
        int key;
        while ((key= getKeyPressed())!=NO_KEY) { // Wait to release all keys
            if (key >= 0) waitKeyReleased(key);
            else getMouseEvent();
        }
        clearArea();
        setForeground(YELLOW);
        String[] lines = msg.split(";");
        int l = LINE_MESSAGE;
        for (String line : lines) {
            if (line.length()>LEN_MESSAGE) line = line.substring(0,LEN_MESSAGE);
            cursor(l++, COL_MESSAGE);
            print(center(line,LEN_MESSAGE));
        }
        cursor(LINE_MESSAGE+lines.length,COL_MESSAGE);
    }

    /**
     * Write the message and return when a key is pressed or after four seconds.
     * @param msg Text to print. ';' is the change the line charw.
     */
    public static void printMessageAndWait(String msg) {
        printMessage(msg);
        int key;
        do {
            key = waitKeyPressed(4000);     // Wait a time or a key
            if (key >= 0) waitKeyReleased(key);
            getMouseEvent();
        } while(key == MOUSE_EVENT );
        clearArea();
    }

    private static String center(String txt, int len) {
        while( txt.length() <= len-2)
            txt = ' '+txt+' ';
        if (txt.length()<len) txt += ' ';
        return txt;
    }
}
