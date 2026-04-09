import java.util.Scanner;

public class Main {
    public static char[][][] map7 = {
            {
                    // MAP
            }
    };

    static int floor = 2;
    static int x = 1;
    static int y = 1;
    static int oxygen = 30;
    static char under = '_';
    static boolean hasDecryptor = true;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while (oxygen > 0){
            stats();
            printmap();
            System.out.println("Enter you command:");
            char act = sc.nextLine().toLowerCase().charAt(0);

            if (act == 'w' || act == 'a' || act == 's' || act == 'd') {
                move(act);
            } else if(act == 'e'){
                System.out.println("Enter SPYSK: ");
                char dir  = sc.nextLine().toLowerCase().charAt(0);
                downstairs(dir);
            } else if(act == 'h') {
                System.out.println("Enter napravlenie: ");
                char dir = sc.nextLine().toLowerCase().charAt(0);
                hacker(dir);
            }
        }
        System.out.println("You lose ");
        System.exit(0);
    }
    static void move(char dir){
        int ny = y;
        int nx = x;

        if (dir == 'w') ny--;
        if (dir == 'a') nx--;
        if (dir == 's') ny++;
        if (dir == 'd') nx++;

        if (!proverka(ny, nx)) {
            System.out.println("Pysto");
            return;
        }
        if (map7[floor][ny][nx] == 'O'){
            oxygen += 10;
            oxygen--;
            System.out.println("Oxygen +10");

            map7[floor][y][x] = under;
            map7[floor][ny][nx] = 'P';
            under = '_';
            y = ny;
            x = nx;
        }
        else if (map7[floor][ny][nx] == 'F'){
            System.out.println("You win! ");
            System.exit(0);
        }
        else if (map7[floor][ny][nx] == '_'){
            oxygen--;
            map7[floor][ny][nx] = 'P';
            map7[floor][ny][nx] = under;
            under = '_';
            y = ny;
            x = nx;
        }
        else {
            System.out.println("Proxod zakrit.");
            return;
        }
    }
    static void hacker(char dir) {
        if (oxygen < 7){
            System.out.println("Vam ne xvatit.");
            return;
        }
        if (hasDecryptor) {
            System.out.println("otsytstvyet");
            return;
        }

        int ny = y;
        int nx = x;
        if (dir == 'w') ny--;
        if (dir == 'a') nx--;
        if (dir == 's') ny++;
        if (dir == 'd') ny++;

        if (!proverka(ny, nx)) {
            System.out.println("hack no");
            return;
        }
        if (map7[floor][ny][nx] == 'X') {
            oxygen -= 7;
            map7[floor][ny][nx] = '_';
        } else {
            System.out.println("No");
            return;
        }
    }
    static void downstairs(char dir) {
        int ny = y;
        int nx = x;
        if (dir == 'w') ny--;
        if (dir == 'a') nx--;
        if (dir == 's') ny++;
        if (dir == 'd') ny++;

        if (!proverka(ny, nx)) {
            System.out.println("No ");
            return;
        }
        if (map7[floor][ny][nx] == 'D') {
            oxygen--;
            map7[floor][ny][nx] = under;
            floor--;
            findstairs('U');
        }
        else if (map7[floor][ny][nx] == 'U') {
            oxygen--;
            map7[floor][y][x] = under;
            floor++;
            findstairs('D');
        }
        else {
            System.out.println("No   ");
            return;
        }
    }
    static boolean proverka(int ny, int nx) {
        return ny >= 0 && nx >= 0 && ny < map7[floor].length && nx < map7[floor][ny].length;
    }
    static void findstairs(char lift) {
        for (int i = 0; i < map7[floor].length; i++) {
            for (int j = 0; j < map7[floor][i].length; j++) {
                if (map7[floor][i][j] == lift) {
                    y = i;
                    x = j;
                    under = lift;
                    map7[floor][y][x] = 'P';
                    return;
                }
            }
        }
    }
    static void printmap() {
        for (int i = 0; i < map7[floor].length; i++) {
            for (int j = 0; j < map7[floor][i].length; j++) {
                System.out.println("[" + map7[floor][i][j] + "]");
            }
            System.out.println();
        }
    }
    static void stats() {
        System.out.println("Floor:" + floor + "X:" + x + "Y:" + y + "Oxygen left:" + oxygen);
    }
}
