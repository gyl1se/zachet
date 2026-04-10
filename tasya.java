// без 
import java.util.Scanner;
public class Main {

    };
    public static int currentFloor = 2;
    public static int playerY = 1;
    public static int playerX = 4;
    public static int energy = 25;
    public static String[] floorNames = {"палуба 0","палуба 1", "палуба 2"};
    public static Scanner scanner = new Scanner(System.in);
    public static boolean end = false;
    public static int charge = 5;
    public static int drills = 3;

    public static void main(String[] args) {
        System.out.println("игра началась");
        gameLoop();
        scanner.close();
    }
    public static void gameLoop() {
        while (!end) {
            if (energy <= 0) {
                System.out.println("Индикатор кислорода мигнул в последний раз. Наступила\n" +
                        "вечная тишина.");
                return;
            }
            System.out.print("Статус | ");
            System.out.print("Этаж: " + floorNames[currentFloor]);
            System.out.print(" | Позиция игрока Y:" + playerY + "X:" + playerX);
            System.out.print(" | Кислород: " + energy);
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
            } else if (cmd == 'e' || cmd == 'q') {
                interact(cmd);
            } else if (cmd == 'b' ) {
                bomber();
            } else if (cmd == 'l' && input.length() >= 2) {
                char dir = input.charAt(1);
                if ("wasdeq".indexOf(dir) != -1) {
                    drill(dir);
                } else {
                    System.out.println("Неверное направление для бура");
                }
            } else {
                System.out.println("неизвестная команда");
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
            energy += 10;
            tower[currentFloor][ny][nx] = '_';
            System.out.println("вы подобрали кислород: " + energy);
        }

        if (target == 'X') {
            System.out.println("искореженный металл, пройти нельзя");
            return;
        }
        playerY = ny;
        playerX = nx;
        energy--;
    }
    public static void interact(char dir) {
        char cell;
        if (dir == 'e') {
            try {
                cell = tower[currentFloor - 1][playerY][playerX];
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.print("ниэе этажа нет");
                return;
            }
            if (cell != '#') {
                currentFloor -= 1;
                charge -= 1;
                System.out.print("вы опустилимб");
            }
            if (cell == 'F') {
                System.out.println("«Пространство изогнулось, пропуская вас к спасению.»");
                end = true;
                return;
            }
            else {
                System.out.print("нельзя поднятьс");
                return;
            }
        }
        if (dir == 'q') {
            try {
                cell = tower[currentFloor + 1][playerY][playerX];
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("выше этажа нет");
                return;
            }
            if (cell != '#') {
                currentFloor += 1;
                energy -= 1;
                return;
            } else {
                System.out.println("В эту клетку нельзя подняться");
                return;
            }
        }
        System.out.println("в этом направлени нельзя перместиться");
    }
    // бомбер
    public static void bomber() {
        if (energy < 5) {
            System.out.println("Недостаточно энергии для взрыва (нужно 5).");
            return;
        }
        System.out.println("БА-БАХ! Взрывная волна расходится во все 6 сторон...");
        energy -= 5;

        int[] dy = {-1, 1, 0, 0};
        int[] dx = {0, 0, -1, 1};

        char[][] currentDeck = tower[currentFloor];
        int rows = currentDeck.length;
        int cols = currentDeck[0].length;
        for (int i = 0; i < 4; i++) {
            int ny = playerY + dy[i];
            int nx = playerX + dx[i];

            if (ny >= 0 && ny < rows && nx >= 0 && nx < cols) {
                if (tower[currentFloor][ny][nx] == '#') {
                    tower[currentFloor][ny][nx] = '_';
                    System.out.println("Стена разрушена на текущей палубе.");
                }
            }
        }
        if (currentFloor + 1 < tower.length) {
            char[][] upperDeck = tower[currentFloor + 1];

            if (playerY >= 0 && playerY < upperDeck.length &&
                    playerX >= 0 && playerX < upperDeck[0].length) {

                if (upperDeck[playerY][playerX] == '#') {
                    upperDeck[playerY][playerX] = '_';
                    System.out.println("Стена разрушена на палубе выше!");
                }
            }
        }
        if (currentFloor - 1 >= 0) {
            char[][] lowerDeck = tower[currentFloor - 1];

            if (playerY >= 0 && playerY < lowerDeck.length &&
                    playerX >= 0 && playerX < lowerDeck[0].length) {

                if (lowerDeck[playerY][playerX] == '#') {
                    lowerDeck[playerY][playerX] = '_';
                    System.out.println("Стена разрушена на палубе ниже!");
                }
            }
        }
    }
    // бурильщик
    public static void drill(char dir) {

        if (drills <= 0) {
            System.out.println("Алмазные буры закончились!");
            return;
        }
        if (energy < 10) {
            System.out.println("Недостаточно энергии для бурения (нужно 10).");
            return;
        }
        drills--;
        energy -= 10;
        System.out.println("Запуск бура! Направление: " + dir);

        int dy = 0;
        int dx = 0;
        int dz = 0;

        switch (dir) {
            case 'w': dy = -1; break;
            case 's': dy = 1; break;
            case 'a': dx = -1; break;
            case 'd': dx = 1; break;
            case 'e': dz = -1; break;
            case 'q': dz = 1; break;
            default:
                System.out.println("Неверное направление для бура.");
                drills++;
                energy += 10;
                return;
        }
        int destroyedCount = 0;
        int maxDestroy = 3;

        int curY = playerY + dy;
        int curX = playerX + dx;
        int curZ = currentFloor + dz;
        while (destroyedCount < maxDestroy) {
            if (curZ < 0  || curZ >= tower.length) {
                System.out.println("Луч вышел за пределы шахты (по вертикали).");
                break;
            }
            char[][] currentDeck = tower[curZ];
            if (curY < 0 || curY >= currentDeck.length || curX < 0 || curX >= currentDeck[0].length) {
                System.out.println("Луч упёрся в границу палубы.");
                break;
            }
            char cell = currentDeck[curY][curX];
            if (cell == '#') {
                currentDeck[curY][curX] = '_';
                destroyedCount++;
                System.out.println("Стена разрушена на координатах [Этаж:" + curZ + ", Y:" + curY + ", X:" + curX + "]");
            }
            curY += dy;
            curX += dx;
            curZ += dz;
        }
        if (destroyedCount == 0) {
            System.out.println("Луч не встретил ни одной стены на пути.");
        } else {
            System.out.println("Бурение завершено. Разрушено блоков: " + destroyedCount);
        }
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
import java.util.Scanner;
public class Main {

    public static int currentFloor = 2;
    public static int playerY = 1;
    public static int playerX = 4;
    public static int oxygen = 30;
    public static String[] floorNames = {"палуба 0","палуба 1", "палуба 2"};
    public static Scanner scanner = new Scanner(System.in);
    public static boolean end = false;
    public static boolean hasDecryptor = true;
    public static int charge = 30;

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
            } else if (cmd == 'j' && input.length() >= 2) {
                char dir = input.charAt(1);
                if (dir == 'w' || dir == 's' || dir == 'a' || dir == 'd') {
                    hacker(dir);
                }
            } else {
                System.out.println("неверная команда");
            }
        }

    }
    //фантом
    public static void phantom(char dir) {
        int ny = playerY;
        int nx = playerX;

        switch (dir) {
            case'w':ny-=2;break;
            case's':ny+=2;break;
            case'a':nx-=2;break;
            case'd':nx+=2;break;
        }

        char target;
        try {
            target = tower[currentFloor][ny][nx];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("вы прыгнули в бездну");
            end = true;
            return;
        }

        if (target == '#') {
            System.out.println("вы не можете прыгнуть в стену");
            return;
        }

        if (target == 'F') {
            System.out.println("«Пространство изогнулось, пропуская вас к спасению.»");
            end = true;
            return;
        }

        playerY = ny;
        playerX = nx;
        oxygen -= 5;
        System.out.println("Вы совершили скачок");
    }
    //киборг
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
    //хакер
    public static void hacker(char dir) {
        int ny = playerY;
        int nx = playerX;

        switch (dir) {
            case'w':ny--;break;
            case's':ny++;break;
            case'a':nx--;break;
            case'd':nx++;break;
        }

        char target;

        try{
            target = tower[currentFloor][ny][nx];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("В этом направлении ничего нет");
            return;
        }

        if (target == 'X' && hasDecryptor) {
            tower[currentFloor][ny][nx] = '_';
            hasDecryptor = false;
            oxygen-=7;
            System.out.println("замок взломан! путь открыт.");
            return;
        }
        System.out.println("в этом направлении нет замка");
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
            System.out.println("замок закрыт");
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
