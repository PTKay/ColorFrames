import isel.leic.pg.Console;
import static java.awt.event.KeyEvent.*;

public class ColorFrames {
    public static int MAX_COLORS = 4;  // [1..9] Color used to generate random piece
    public static final int BOARD_DIM = 4;  // [2..4] Dimension (lines and columns) of board
    public static final int FRAMES_DIM = 3; // [1..4] Number of frames in each place of board
    public static final int NO_FRAME = -1;  // Special color to mark frame absence

    public static final int BOARD_PLACES = BOARD_DIM*BOARD_DIM;

    /**
     * Random generated piece.
     * Each position has the color of each frame [0 .. MAX_COLORS-1] or NO_FRAME indicates that frame does not exist.
     * Each index corresponds to one dimension of the frame.
     */
    private static int[] piece = new int[FRAMES_DIM];

    /**
     * Flag to finish the game
     */
    private static boolean terminate = false;
    private static boolean isFull = false;

    public static void main(String[] args) {
        Panel.init();
        Board.init();
        Panel.printMessage("Welcome");
        playGame();
        if (isFull) Panel.printMessageAndWait("GAME;OVER");
        else Panel.printMessageAndWait("BYE");
        Panel.end();
    }

    private static void playGame() {
        int key;
        generatePiece();
        printPiece();
        do {
            key = Console.waitKeyPressed(5000);
            if (key>0) {
                Console.waitKeyReleased(key);
                processKey(key);
            } else Panel.printMessage("");  // Clear last message
        } while( !terminate );
    }

    private static void processKey(int key) {
        if (key == VK_ESCAPE) terminate = true;
        int gridNum = 0;
        if (key >= VK_1 && key <= VK_9)
            gridNum = key-VK_1+1;
        else if (key >= VK_NUMPAD1 && key <= VK_NUMPAD9 )
            gridNum = key-VK_NUMPAD1+1;
        else if (key >= VK_A && key <= VK_Z)
            gridNum = key-VK_A+10;
        if (gridNum>=1 && gridNum<=BOARD_PLACES) {
            if (Board.canFit(piece, gridNum)) {
                putPieceInBoard(gridNum);
                Panel.printMessage("Ok");
                if (!Board.isFull()) {
                    generatePiece();
                    printPiece();
                } else { isFull = true ; terminate = true ; }
            } else Panel.printMessage("Invalid;Move");
        }
    }

    private static void putPieceInBoard(int gridNum) {
        for (int f = 0; f < FRAMES_DIM; ++f) { // For each frame dimension
            int color = piece[f];
            if (color!=NO_FRAME) {
                Panel.printFrame(f,color,gridNum); // Displays the frame with (f) dimension and (color)
                Board.saveColor(gridNum,f,color);
                Panel.clearFrame(f,0);             // Clean the frame of piece.
            }
        }
        Board.sameColor(gridNum, piece);
    }

    private static void generatePiece() {
        do {
            for (int f = 0; f < FRAMES_DIM; ++f)  // Removes all frames
                piece[f] = NO_FRAME;
            int numOfFrames = 1 + (int) (Math.random()* (FRAMES_DIM-1)); // Frames to generate
            for(int i=0 ; i<numOfFrames ; ++i) {
                int frameSize;
                do frameSize = (int) (Math.random() * FRAMES_DIM); // Selects a free random dimension
                while (piece[frameSize] != NO_FRAME);
                piece[frameSize] = (int) (Math.random() * MAX_COLORS); // Generate random color
            }
        } while(!Board.canFitOnBoard(piece));
    }

    private static void printPiece() {
        for (int f = 0; f < FRAMES_DIM; ++f) {  // For each frame dimension
            int color = piece[f];
            if (color==NO_FRAME) Panel.clearFrame(f,0);  // Clean if does not exist with (f) dimension
            else Panel.printFrame(f,color,0);      // or Displays the frame with (f) dimension and (color)
        }
    }
}
