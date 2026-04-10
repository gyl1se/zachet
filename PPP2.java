import java.util.Scanner;
public class PPP2 {
    public static char[][][] map = {
            // === ПАЛУБА 0 (4x6) ===
            {
                    {'#', '#', '#', '#', '#', '#'},
                    {'_', '_', 'O', '_', 'X', 'F'},
                    {'_', '#', '#', '#', '#', '#'},
                    {'_', 'U', '_', '_', '_', '_'}
            },
            // === ПАЛУБА 1 (4x5) ===
            {
                    {'#', 'U', '#', '#', '#'},
                    {'#', '_', '_', '_', '#'},
                    {'#', '#', '#', '_', '#'},
                    {'#', 'D', '_', '_', '#'}
            },
            // === ПАЛУБА 2 (3x6) ===
            {
                    {'#', 'D', '_', '_', '_', '#'},
                    {'#', '#', '_', '_', '#', '#'},
                    {'_', '_', '_', '_', '_', 'P'}
            }
    };
    static int floor = 2, x = 2, y = 5, oxygen = 30, charge = 10;
    static boolean gameOver = false;

    static final String oxygen_out = "";
    static final String win = "";
    static final String fall = "";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while(!gameOver){
            if(oxygen <= 0){
                System.out.println(oxygen_out);
                break;
            }
            if(map[floor][x][y] == 'F'){
                System.out.println(win);
                break;
            }
            System.out.println("кислород: " + oxygen);
            System.out.println("заряд: " + charge);

            print();
            System.out.print("Команда: ");
            handle(scanner.nextLine().trim().toLowerCase());
        }
    }

    static void print() {
        for (int i = 0; i < map[floor].length; i++) {
            for (int j = 0; j < map[floor][i].length; j++) {
                if(i == x && j == y) System.out.print("[P] ");
                else
                    System.out.print("[" + map[floor][i][j] + "] ");
            }
            System.out.println();
        }
        System.out.println();
    }

    static void handle(String cmd){
        if(cmd.isEmpty()) return;

        char d = cmd.charAt(cmd.length() - 1);
        int dx = 0, dy = 0;

        if(d == 'w') dx = -1;
        else if(d == 'a') dy = -1;
        else if(d == 's') dx = 1;
        else if(d == 'd') dy = 1;
        else return;

        if(cmd.length() == 1) move(dx, dy);
        else if(cmd.charAt(0) == 'h') unlock(dx, dy);
        else if(cmd.charAt(0) == 'e') stairs(dx, dy);
        else if(cmd.charAt(0) == 'b') hit(dx, dy);
        else if(cmd.charAt(0) == 'j') jump(dx, dy);
    }

    static void move(int dx, int dy){
        int nx = x + dx, ny = y + dy;

        if(!inside(nx, ny)){
            System.out.println(fall);
            gameOver = true;
            return;
        }
        char t = map[floor][nx][ny];

        if(t == '#' || t == 'X'){
            oxygen--;
            return;
        }

        if(t == 'O'){
            oxygen+=10;
        }
        if(map[floor][x][y] != 'U' && map[floor][x][y] != 'D'){
            map[floor][x][y] = '_';
        }

        x = nx;
        y = ny;
        oxygen--;
    }

    static void unlock(int dx, int dy){
        int nx = x + dx, ny = y + dy;

        if(inside(nx, ny) && map[floor][nx][ny] == 'X' && oxygen >= 7){
            map[floor][nx][ny] = '_';
            oxygen -= 7;
        }
    }
    static void hit(int dx, int dy){
        int nx = x + dx, ny = y + dy;

        if(!inside(nx, ny)) return;

        if((map[floor][nx][ny] == 'X' || map[floor][nx][ny] == '#') && charge > 0 && oxygen >= 10){
            map[floor][nx][ny] = '_';
            charge--;
            oxygen-=10;
        }
    }
    static void jump(int dx, int dy){
        int nx = x + dx * 2, ny = y + dy*2;

        if(!inside(nx, ny)){
            System.out.println(fall);
            gameOver = true;
            return;
        }

        char j = map[floor][nx][ny];

        if(j == '#') return;
        if(j == 'X') return;

        if(j == 'O'){
            map[floor][nx][ny] = '_';
            oxygen+=10;
        }

        if(j == '_' || j == 'U' || j == 'D' || j == 'F'){
            if (map[floor][x][y] != 'U' && map[floor][x][y] != 'D') {
                map[floor][x][y] = '_';
            }
        }
        x = nx;
        y = ny;
        oxygen-=5;
    }
    static void stairs(int dx, int dy){
        int nx = x + dx, ny = y + dy;

        if(!inside(nx, ny)) return;

        char s = map[floor][nx][ny];

        if(s == 'U' && floor < map.length - 1) floor++;
        else if(s == 'D' && floor > 0) floor--;
        else return;

        spawn(s == 'U' ? 'D' : 'U');
        oxygen--;
    }

    static void spawn(char stair){
        for (int i = 0; i < map[floor].length; i++) {
            for (int j = 0; j < map[floor][i].length; j++) {
                if(map[floor][i][j] == stair){
                    x = i;
                    y = j;
                    return;
                }
            }
        }
    }
    static boolean inside(int x, int y){
        return x >= 0 && x < map[floor].length &&
                y >= 0 && y < map[floor][x].length;
    }
}
