import java.util.Scanner;

public class PPP1 {
    public static char[][][] map = {
            // === ПАЛУБА 0 (4x6) ===
            {
                    {'#', '#', '#', '#', '#', '#'},
                    {'_', '_', '_', '_', '_', 'F'},
                    {'_', '#', '#', '#', '#', '#'},
                    {'_', '_', '_', '_', '_', '_'}
            },
            // === ПАЛУБА 1 (4x5) ===
            {
                    {'#', '_', '#', '#', '#'},
                    {'#', '_', '_', '_', '#'},
                    {'#', '#', '#', '_', '#'},
                    {'#', '_', '_', '_', '#'}
            },
            // === ПАЛУБА 2 (3x6) ===
            {
                    {'#', '_', '_', '_', '_', '#'},
                    {'#', '#', '_', '+', '#', '#'},
                    {'P', '_', '_', '_', '_', '_'}
            }
    };
    static int x = 2, y = 0, floor = 2, energy = 100;
    static String fall = "fall", win = "win", energyOut = "energyOUT";
    static boolean gameOver = false;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while(!gameOver) {
            if (energy <= 0) {
                System.out.println(energyOut);
                break;
            }
            if (map[floor][x][y] == 'F') {
                System.out.println(win);
                break;
            }
            System.out.println("энергия: " + energy);
            System.out.println("x: " + x);
            System.out.println("y: " + y);
            System.out.println("этаж: " + floor);
            print();
            System.out.println("команда: ");
            handle(scanner.nextLine().trim());
        }
    }
    static boolean inside(int x, int y, int floor) {
        return floor >= 0 && floor < map.length &&
                x >= 0 && x < map[floor].length &&
                y >= 0 && y <map[floor][x].length;
    }
    static void print() {
        for (int i = 0; i < map[floor].length; i++) {
            for (int j = 0; j < map[floor][i].length; j++) {
                if (i == x && j == y) System.out.print("[P]");
                else System.out.print("[" + map[floor][i][j] + "]");
            }
            System.out.println();
        }
        System.out.println();
    }
    static void handle(String cmd) {
        if (cmd.isEmpty()) return;
        int dx = 0, dy = 0, dz = 0;
        char c = cmd.charAt(cmd.length() - 1);
        if (c == 'w') dx = -1;
        else if (c == 's') dx= 1;
        else if (c == 'a') dy = -1;
        else if (c == 'd') dy = 1;
        else if (c == 'q') dz = -1;
        else if (c == 'e') dz = 1;
        else if (c == 'b') bomb();

        if (cmd.length() == 2) {
            if (cmd.charAt(0) == 'l') lazer(dx, dy, dz);
        }
        else if (c == 'w' || c == 's' || c == 'a' || c == 'd') move(dx, dy);
        else if (c == 'q' || c =='e') nextLayer(dz);
    }
    static void move(int dx, int dy) {
        int nx = dx + x, ny = dy + y;
        if (!inside(nx, ny, floor)) return;
        char m = map[floor][nx][ny];
        if (m == '#') {
            energy--;
            return;
        }
        if (m == '+') {
            energy += 10;
        }
        map[floor][x][y] = '_';
        energy--;
        x = nx;
        y = ny;
    }
    static void nextLayer(int dz) {
        int nf = floor + dz;
        if (!inside(x, y, nf)) return;
        if (map[nf][x][y] == '#') {
            System.out.println("стена");
            energy--;
            return;
        }
        floor = nf;
    }
    static void lazer(int dx, int dy, int dz) {
        if (energy <= 5) return;
        for (int step = 1; step <= 3; step++) {
            int nx = x + dx*step;
            int ny = y + dy*step;
            int nz = floor + dz*step;
            if (inside(nx, ny, nz)) {
                if (map[nz][nx][ny] == '#') {
                    map[nz][nx][ny] = '_';
                }
            }
        }
    }
    static void bomb() {
        if (energy <= 10) return;
        int[][] directions = {
                {1, 0, 0},
                {-1, 0, 0},
                {0, 1, 0},
                {0, -1, 0},
                {0, 0, 1},
                {0, 0, -1}
        };
        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            int nz = floor + dir[2];
            if (inside(nx, ny, nz)) {
                if (map[nz][nx][ny] == '#') {
                    map[nz][nx][ny] = '_';
                }
            }
        }
    }
}