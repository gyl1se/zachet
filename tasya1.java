import java.util.Scanner;

public class Main {

    public static int currentFloor = 2;
    public static int playerY = 1;
    public static int playerX = 4;
    public static int oxygen = 300;
    public static String[] floorNames = {"палуба 0","палуба 1", "палуба 2"};
    public static Scanner scanner = new Scanner(System.in);
    public static boolean end = false;
    public static int charge = 5;


    public static void main(String[] args) {
        System.out.println("игра началась");
        gameLoop();
        scanner.close();
    }

    public static void gameLoop() {
        while (!end) {
            if(oxygen <= 0) {
                System.out.println("Индикатор кислорода мигнул в последний раз. Наступила\n" +
                        "вечная тишина.");
                return;
            }

            System.out.print("Статус | ");
            System.out.print("Этаж: " + floorNames[currentFloor]);
            System.out.print(" | Позиция игрока Y:" + playerY + "X:" + playerX);
            System.out.print(" | Кислород: " + oxygen);
            System.out.print(" | задяд:" + charge);
            System.out.println();

            System.out.println("карта");
            printMap();

            System.out.print("введите команду: ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.isEmpty()) continue;

            char cmd = input.charAt(0);

            if (cmd == 'w' || cmd == 's' || cmd == 'a' || cmd == 'd') {
                movePlayer(cmd);
            } else if (cmd == 'e') {
                System.out.println("введите направление: ");
                String input2 = scanner.nextLine().trim().toLowerCase();
                if (input2.isEmpty()) continue;

                char dir = input2.charAt(0);
                if (dir == 'w' || dir == 's' || dir == 'a' || dir == 'd') {
                    interact(dir);
                } else {
                    System.out.println("неизвестное направление");
                }
            } else if (cmd == 'b' && input.length() >= 2) {
                char dir = input.charAt(1);
                if (dir == 'w' || dir == 's' || dir == 'a' || dir == 'd') {
                    cyborg(dir);
                }
            } else {
                System.out.println("неверная команда");
            }
        }

    }


    public static void movePlayer(char dir) {
        int ny = playerY;
        int nx = playerX;

        switch (dir) {
            case'w':ny--;break;
            case's':ny++;break;
            case'a':nx--;break;
            case'd':nx++;break;
        }

        char target;
        try {
            target = tower[currentFloor][ny][nx];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("вы в бездне");
            end = true;
            return;
        }

        if (target == 'F') {
            System.out.println("«Пространство изогнулось, пропуская вас к спасению.»");
            end = true;
            return;
        }

        if (target == '#') {
            System.out.println("стена");
            return;
        }

        if (target == 'O') {
            oxygen += 10;
            tower[currentFloor][ny][nx] = '_';
            System.out.println("вы подобрали кислород: " + oxygen);
        }

        if (target == 'X') {
            System.out.println("искореженный металл, пройти нельзя");
            return;
        }

        playerY = ny;
        playerX = nx;
        oxygen--;
    }

    public static void interact(char dir) {
        int ny = playerY;
        int nx = playerX;

        switch (dir) {
            case'w':ny--;break;
            case's':ny++;break;
            case'a':nx--;break;
            case'd':nx++;break;
        }

        char cell;
        try {
            cell = tower[currentFloor][ny][nx];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("в этом направлении нет лестницы");
            return;
        }

        if (cell == 'D'){
            int nextFloor = currentFloor - 1;
            boolean found = false;
            for (int y = 0; y < tower[nextFloor].length && !found ;y++) {
                for (int x = 0; x < tower[nextFloor][y].length;x++) {
                    try {
                        if (tower[nextFloor][y][x] == 'U') {
                            playerX = x;
                            playerY = y;
                            currentFloor = nextFloor;
                            oxygen--;
                            found = true;
                            System.out.println("вы опустились");
                            break;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("ошибка поиска лестницы");
                        return;
                    }
                }
            }
            return;
        }
        if (cell == 'U'){
            int nextFloor = currentFloor + 1;
            boolean found = false;
            for (int y = 0; y < tower[nextFloor].length && !found ;y++) {
                for (int x = 0; x < tower[nextFloor][y].length;x++) {
                    try {
                        if (tower[nextFloor][y][x] == 'D') {
                            playerX = x;
                            playerY = y;
                            currentFloor = nextFloor;
                            oxygen--;
                            found = true;
                            System.out.println("вы опустились");
                            break;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("ошибка поиска лестницы");
                        return;
                    }
                }
            }
            return;
        }
        System.out.println("в этом направлении нет лестницы");
    }


    public static void cyborg(char dir) {
        if (charge <= 0) {
            System.out.println("заряд пневмокулака исчерпан");
            return;
        }

        int ny = playerY;
        int nx = playerX;

        switch (dir) {
            case'w':ny--;break;
            case's':ny++;break;
            case'a':nx--;break;
            case'd':nx++;break;
        }

        char target;
        try {
            target = tower[currentFloor][ny][nx];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("В этом направении ничего нет");
            return;
        }
        if (target == '#' || target == 'X') {
            tower[currentFloor][ny][nx] = '_';
            charge-=1;
            oxygen-=10;
            System.out.println("вы сломали");
            return;
        }
        System.out.println("в этом направлении нечего ломать");
    }


    public static void printMap() {
        char[][] floor = tower[currentFloor];
        for (int y = 0; y < floor.length;y++) {
            for(int x = 0; x < floor[y].length;x++) {
                if(playerY == y && playerX == x) {
                    System.out.print("[P]");
                } else {
                    System.out.print("[" + floor[y][x] + "]");
                }
            }
            System.out.println();
        }
        System.out.println();
    }


}
