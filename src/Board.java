import java.awt.*;

public class Board {
    //Campo para a pontuacao
    public static int clearedFrames = 0;

    //Array que guarda o valor da cor da peça numa denominada posição
    private static int[][][] pieceColor = new int[ColorFrames.BOARD_DIM][ColorFrames.BOARD_DIM][ColorFrames.FRAMES_DIM];

    public static void saveColor (int gridNum, int frame, int color) {
        Coord coord = getBoardCoord(gridNum);
        pieceColor[coord.x][coord.y][frame] = color;
    }

    public static void init() {
        Panel.printLevel(1);
        for (int x = 0; x < ColorFrames.BOARD_DIM ; ++x)
            for (int y = 0; y < ColorFrames.BOARD_DIM; y++)
                for (int f = 0; f < ColorFrames.FRAMES_DIM; ++f)
                    pieceColor[x][y][f] = ColorFrames.NO_FRAME;
    }

    //Verifica se uma peça cabe no indice do tabuleiro.
    public static boolean canFit(int[] piece, int gridNum) {
        boolean canFit = true;
        Coord coord = getBoardCoord(gridNum);
        for (int f = 0; f < ColorFrames.FRAMES_DIM; f++)
            if (piece[f] != ColorFrames.NO_FRAME && pieceColor[coord.x][coord.y][f] != ColorFrames.NO_FRAME)
            { canFit=false; break; }
        return (canFit);
    }

    public static boolean canFitOnBoard(int[] piece) {
        for (int i = 1; i <= ColorFrames.BOARD_PLACES ; i++)
            if(canFit(piece, i)) return true;
        return false;
    }

    public static boolean isFull() {
        int fullFrames = 0;
        for (int x = 0; x < ColorFrames.BOARD_DIM ; x++)
            for (int y = 0; y < ColorFrames.BOARD_DIM ; y++)
                for (int f = 0; f < ColorFrames.FRAMES_DIM ; f++)
                    if (pieceColor[x][y][f] != ColorFrames.NO_FRAME) ++fullFrames;
        return (fullFrames == (ColorFrames.BOARD_PLACES*ColorFrames.FRAMES_DIM));
    }


    public static void sameColor(int gridNum, int[] piece) {
        Coord coord = getBoardCoord(gridNum);
        int x = coord.x;
        int y = coord.y;

        //Ver célula
        if (checkCell(x,y)) doClearCell(x, y);

        //Para cada frame da peça que pusemos, fazer uma verificaçao
        for (int p = 0; p < ColorFrames.FRAMES_DIM ; p++) {
            if (piece[p]!=ColorFrames.NO_FRAME) {
                //Ver linhas
                if (checkLine(x,y,piece[p])) doClearLine(y, piece[p]);
                //Ver colunas
                if (checkCol(x,y,piece[p])) doClearCol(x, piece[p]);
                //Ver diagonal principal
                if (x==y && checkMainDiag(x,piece[p])) doClearMainDiag(piece[p]);
                //Ver diagonal secundaria
                if (checkSecondDiag(x,y,piece[p])) doClearSecondDiag(piece[p]);

                Panel.printScore(clearedFrames);
                Panel.printLevel(level());
            }
        }
    }

    private static boolean checkSecondDiag(int x, int y, int color) {
        int num = 0;
        int k = ColorFrames.BOARD_DIM-1;
        for (int i = 0 ; i < ColorFrames.BOARD_DIM && k >= 0; i++, --k) {
            if (i!=x && k!=y) {
                for (int j = 0; j < ColorFrames.FRAMES_DIM; j++) {
                    if (pieceColor[i][k][j] == color) {
                            ++num;
                            break;
                    }
                }
            }
        }
        return(num==ColorFrames.BOARD_DIM-1);
    }

    private static int level () {
        if (clearedFrames < 25) return 1;
        else if (clearedFrames < 50) {ColorFrames.MAX_COLORS=5; return 2;}
        else if (clearedFrames < 100) {ColorFrames.MAX_COLORS=6; return 3;}
        else if (clearedFrames < 200) {ColorFrames.MAX_COLORS=7; return 4;}
        else if (clearedFrames < 400) {ColorFrames.MAX_COLORS=8; return 5;}
        else {ColorFrames.MAX_COLORS=9; return 6;}
    }

    private static boolean checkMainDiag(int x, int color) {
        int num = 0;
        for (int i = 0; i < ColorFrames.BOARD_DIM; i++) {
            if (i != x)
                for (int j = 0; j < ColorFrames.FRAMES_DIM; j++) {
                    if (pieceColor[i][i][j] == color) {
                        ++num;
                        break;
                    }
                }
        }
        return(num == ColorFrames.BOARD_DIM - 1);
    }

    private static boolean checkCol(int x, int y, int color) {
        int num = 0;
        for (int i = 0; i < ColorFrames.BOARD_DIM; i++) {
            if (i != y)
                for (int j = 0; j < ColorFrames.FRAMES_DIM; j++) {
                    if (pieceColor[x][i][j] == color) {
                        ++num;
                        break;
                    }
                }
        }
        return(num == ColorFrames.BOARD_DIM - 1);
    }

    private static boolean checkCell(int x, int y) {
        int num=0;
        int colorGrid=pieceColor[x][y][0];
        for (int i = 1; i < ColorFrames.FRAMES_DIM; i++)
            if(pieceColor[x][y][i] == colorGrid) ++num;
        return (num==ColorFrames.FRAMES_DIM-1);
    }

    private static boolean checkLine(int x, int y, int color) {
        int num = 0;
        for (int i = 0; i < ColorFrames.BOARD_DIM; i++) {
            if (i != x)
                for (int j = 0; j < ColorFrames.FRAMES_DIM; j++) {
                    if (pieceColor[i][y][j] == color) {
                        ++num;
                        break;
                    }
                }
        }
        return (num == ColorFrames.BOARD_DIM - 1);
    }

    private static void doClearSecondDiag(int color) {
        int y = ColorFrames.BOARD_DIM-1;
        for (int x = 0; x < ColorFrames.BOARD_DIM && y >= 0; x++, y--) {
            for (int f = 0; f < ColorFrames.FRAMES_DIM; f++) {
                if (pieceColor[x][y][f] == color) {
                        Panel.clearFrame(f, getBoardNum(x,y));
                        pieceColor[x][y][f] = ColorFrames.NO_FRAME;
                        ++clearedFrames;
                }
            }
        }
    }

    private static void doClearMainDiag(int color) {
        for (int x = 0; x < ColorFrames.BOARD_DIM; x++) {
            for (int f = 0; f < ColorFrames.FRAMES_DIM; f++) {
                if(pieceColor[x][x][f]==color) {
                    Panel.clearFrame(f, getBoardNum(x, x));
                    pieceColor[x][x][f] = ColorFrames.NO_FRAME;
                    ++clearedFrames;
                }
            }
        }
    }

    private static void doClearCol(int x, int color) {
        for (int y = 0; y < ColorFrames.BOARD_DIM; y++) {
            for (int f = 0; f < ColorFrames.FRAMES_DIM; f++) {
                if(pieceColor[x][y][f]==color) {
                    Panel.clearFrame(f, getBoardNum(x, y));
                    pieceColor[x][y][f]=ColorFrames.NO_FRAME;
                    ++clearedFrames;
                }
            }
        }
    }

    private static void doClearCell(int x, int y) {
        for (int f = 0; f < ColorFrames.FRAMES_DIM; ++f) {
            Panel.clearFrame(f, getBoardNum(x, y));
            pieceColor[x][y][f]=ColorFrames.NO_FRAME;
            ++clearedFrames;
        }
    }

    private static void doClearLine(int y, int color) {
        for (int x = 0; x < ColorFrames.BOARD_DIM; x++) {
            for (int f = 0; f < ColorFrames.FRAMES_DIM; f++) {
                if(pieceColor[x][y][f]==color) {
                    Panel.clearFrame(f, getBoardNum(x, y));
                    pieceColor[x][y][f]=ColorFrames.NO_FRAME;
                    ++clearedFrames;
                }
            }
        }
    }


    //Converte o indice da grid numa coordenada (x,y)
    private static Coord getBoardCoord(int gridNum) {
        int x = ((gridNum-1)%ColorFrames.BOARD_DIM);
        int y = ((gridNum-1)/ColorFrames.BOARD_DIM);
        return new Coord(x,y);
    }

    //Converte uma coordenada num indice do tabuleiro
    private static int getBoardNum(int x, int y) {
        return y*ColorFrames.BOARD_DIM+x+1;
    }
}