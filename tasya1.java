```java
import java.util.Scanner;

public class Main {

    public static int currentFloor = 2;
    public static int playerY = 1;
    public static int playerX = 4;
    public static String[] floorNames = {"палуба 0","палуба 1", "палуба 2"};
    public static Scanner scanner = new Scanner(System.in);
    public static boolean end = false;
    public static int charge = 5;
    public static int energy = 50;
    public static int packets = 4;
    public static char[][][] tower 

    public static void main(String[] args) {
        System.out.println("игра началась");
        gameLoop();
        scanner.close();
    }

    public static void gameLoop() {
        while (!end) {

            if (energy <= 0) {
                System.out.println("Индикатор энергии мигнул в последний раз. Наступила\n" +
                        "вечная тишина.");
                return;
            }


            System.out.print("Статус | ");
            System.out.print("Этаж: " + floorNames[currentFloor]);
            System.out.print(" | Позиция игрока Y:" + playerY + " X:" + playerX);
            System.out.print(" | Заряд кулака: " + charge);
            System.out.print(" | Энергия: " + energy);

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




            } else if (cmd == 'c') {
                singularity();

            } else if (cmd == 'p') {
                phantomBurst();


            } else if (cmd == 'r') {
                if (input.length() >= 2) {
                    char dir = input.charAt(1);
                    if (dir == 'w' || dir == 's' || dir == 'a' || dir == 'd') {
                        cascadeResonance(dir);
                    } else {
                        System.out.println("для резонанса укажите направление (w,a,s,d)");
                    }
                } else {
                    System.out.println("недостаточно аргументов для резонанса (пример: r w)");
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
            energy += 10;
            tower[currentFloor][ny][nx] = '_';
            System.out.println("вы подобрали энергию: " + energy);
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
                            energy--;
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
                            energy--;
                            found = true;
                            System.out.println("вы поднялись");
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


    public static void singularity() {
        int cost = 12;
        if (energy < cost) {
            System.out.println("Недостаточно энергии для сингулярности (требуется " + cost + ")");
            return;
        }

        System.out.println("Активация протокола «Сингулярность»... Пространство схлопывается.");
        energy -= cost;


        for (int z = currentFloor - 1; z <= currentFloor + 1; z++) {
            if (z < 0 || z >= tower.length) continue;


            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    int ny = playerY + dy;
                    int nx = playerX + dx;

                    if (ny >= 0 && ny < tower[z].length && nx >= 0 && nx < tower[z][ny].length) {
                        char cell = tower[z][ny][nx];
                        if (cell == '#' || cell == 'X') {
                            tower[z][ny][nx] = '_';
                        }
                    }
                }
            }
        }
        System.out.println("Дефрагментация завершена.");
    }


    public static void phantomBurst() {
        int energyCost = 7;
        int packetCost = 1;

        if (energy < energyCost || packets < packetCost) {
            System.out.println("Недостаточно ресурсов (Нужно: " + energyCost + " эн., " + packetCost + " пак.)");
            return;
        }

        System.out.println("Запуск «Фантомного всплеска»...");
        energy -= energyCost;
        packets -= packetCost;

        int[] offsetsY = {-1, -1, 0, 1, 1};
        int[] offsetsX = {-1, 1, 0, -1, 1};

        int[] targetFloors = {currentFloor - 1, currentFloor + 1};

        for (int z : targetFloors) {
            if (z < 0 || z >= tower.length) continue;

            for (int i = 0; i < offsetsY.length; i++) {
                int ny = playerY + offsetsY[i];
                int nx = playerX + offsetsX[i];

                if (ny >= 0 && ny < tower[z].length && nx >= 0 && nx < tower[z][ny].length) {
                    char cell = tower[z][ny][nx];
                    if (cell == '#' || cell == 'X') {
                        tower[z][ny][nx] = '_';
                    }
                }
            }
        }
        System.out.println("Всплеск произошел на смежных уровнях.");
    }


    public static void cascadeResonance(char dir) {
        int cost = 10;
        if (energy < cost) {
            System.out.println("Недостаточно энергии для резонанса (требуется " + cost + ")");
            return;
        }

        int dy = 0;
        int dx = 0;
        switch (dir) {
            case 'w': dy = -1; break;
            case 's': dy = 1; break;
            case 'a': dx = -1; break;
            case 'd': dx = 1; break;
            default:
                System.out.println("Неверное направление для резонанса");
                return;
        }

        System.out.println("Инициирован каскадный резонанс...");
        energy -= cost;

        for (int step = 1; step <= 3; step++) {
            int targetFloor = currentFloor + (step - 1);

            if (targetFloor < 0 || targetFloor >= tower.length) {
                System.out.println("Волна ушла в неизведанные глубины (этаж " + targetFloor + " недоступен)");
                continue;
            }

            int ny = playerY + (dy * step);
            int nx = playerX + (dx * step);

            if (ny >= 0 && ny < tower[targetFloor].length && nx >= 0 && nx < tower[targetFloor][ny].length) {
                char cell = tower[targetFloor][ny][nx];
                if (cell == '#' || cell == 'X') {
                    tower[targetFloor][ny][nx] = '_';
                    System.out.println("Блок разрушен на уровне " + targetFloor + " (смещение " + step + ")");
                } else {
                    System.out.println("На уровне " + targetFloor + " преграда не обнаружена или цель недосягаема.");
                }
            } else {
                System.out.println("Волна ударилась о границу мира на уровне " + targetFloor);
            }
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
```
