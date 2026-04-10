import java.util.Scanner;

public class Main {
    public static char[][][] tower = {
            {
                    {'#', '#', '#', '#'},
                    {'#', 'F', '#', '#'},
                    {'#', 'X', '#', '#'},
                    {'#', '_', 'O', '_'},
                    {'#', '#', '#', '#'}
            },
            {
                    {'#', '#', '#', '#', '#'},
                    {'#', '_', '_', '_', '#'},
                    {'#', '#', '_', '_', '#'},
                    {'#', '_', '_', '_', '#'}
            },
            {
                    {'#', '#', '#', '#', '#', '#'},
                    {'#', '_', '_', '_', 'P', '#'},
                    {'#', '#', '#', '#', '#', '#'},
                    {'_', '_', '_', '_', '_', '_'}
            }
    };

    public static int currentFloor = 2;
    public static int playerY = 1;
    public static int playerX = 4;
    public static String[] floorNames = {"палуба 0","палуба 1", "палуба 2"};
    public static Scanner scanner = new Scanner(System.in);
    public static boolean end = false;
    public static int charge = 5;
    public static int energy = 50;
    public static int packets = 4;


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
            System.out.print(" | Энергия: " + energy);
            System.out.print(" | Заряд:" + charge);
            System.out.println();

            System.out.println("карта");
            printMap();

            System.out.print("введите команду: ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.isEmpty()) continue;
            char cmd = input.charAt(0);
            if (cmd == 'w' || cmd == 's' ||  cmd == 'a' || cmd == 'd') {
                movePlayer(cmd);
            } else if (cmd == 'e') {
                interactDown();
            } else if (cmd == 'q') {
                interactUp();
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

    public static void interactDown() {
        int nextFloor = currentFloor - 1;
        if (nextFloor < 0) {
            System.out.println("ниже этажа нет");
            return;
        }
        try {
            char cell = tower[nextFloor][playerY][playerX];
            if (cell != '#') {
                currentFloor = nextFloor;
                energy -= 1;
                System.out.println("вы опустились");
            } else {
                System.out.println("нельзя спуститься");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("нельзя спуститься: размеры палуб не совпадают");
        }
    }

    public static void interactUp() {
        int nextFloor = currentFloor + 1;
        if (nextFloor >= tower.length) {
            System.out.println("выше этажа нет");
            return;
        }
        try {
            char cell = tower[nextFloor][playerY][playerX];
            if (cell != '#') {
                currentFloor = nextFloor;
                energy -= 1;
                System.out.println("вы поднялись");
            } else {
                System.out.println("нельзя подняться");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("нельзя подняться: размеры палуб не совпадают");
        }
    }

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
