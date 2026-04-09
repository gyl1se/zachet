import java.util.Scanner;

public class main {
    private static char[][][] map;
    private static int floor;
    private static int playerY;
    private static int playerX;
    private static int oxygen = 30;
    private static boolean gameRunning = true;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initMap();
        findPlayer();

        while (gameRunning) {
            displayHUD();
            displayMap();
            System.out.print("Введите команду: ");
            String input = scanner.nextLine().toLowerCase();

            if (input.length() == 1) {
                char command = input.charAt(0);
                if (command == 'w' || command == 'a' || command == 's' || command == 'd') {
                    moveCommand(command);
                }
            } else if (input.length() == 2) {
                if (input.charAt(0) == 'j') { // Команда jump
                    jumpCommand(input.charAt(1));
                } else if (input.charAt(0) == 'e') {
                    interact(input.charAt(1));
                }
            }
            checkOxygen();
            checkWin();
        }

        scanner.close();
    }

    private static void initMap() {
        // Используем карту из варианта 10 (PDF страница 18)
        map = new char[][][] {
                // ПАЛУБА 0 (5x4)
                {
                        {'#', '#', '#', '#'},
                        {'#', 'F', '#', '#'},
                        {'#', 'X', '#', '#'},
                        {'#', '_', 'U', '#'},
                        {'#', '#', '#', '#'}
                },
                // ПАЛУБА 1 (4x6)
                {
                        {'#', '#', '#', '#', '#', '#'},
                        {'#', 'U', '_', 'O', '_', '#'},
                        {'#', '_', '#', '#', '#', '#'},
                        {'#', '_', 'D', '#', '#', '#'}
                },
                // ПАЛУБА 2 (3x5)
                {
                        {'#', '#', '#', '#', '#'},
                        {'#', 'D', '_', 'P', '#'},
                        {'#', '#', '#', '#', '#'}
                }
        };
        floor = 2; // Начинаем с верхней палубы
    }

    private static void findPlayer() {
        for (int y = 0; y < map[floor].length; y++) {
            for (int x = 0; x < map[floor][y].length; x++) {
                if (map[floor][y][x] == 'P') {
                    playerY = y;
                    playerX = x;
                    map[floor][y][x] = '_'; // Убираем P, но запоминаем координаты
                    return;
                }
            }
        }
    }

    private static void displayHUD() {
        System.out.println("\n=== СТАТУС ===");
        System.out.println("Палуба: " + floor);
        System.out.println("Позиция [Y: " + playerY + ", X: " + playerX + "]");
        System.out.println("Кислород: " + oxygen);
        System.out.println("==============");
    }

    private static void displayMap() {
        for (int y = 0; y < map[floor].length; y++) {
            for (int x = 0; x < map[floor][y].length; x++) {
                if (y == playerY && x == playerX) {
                    System.out.print("[P]");
                } else {
                    System.out.print("[" + map[floor][y][x] + "]");
                }
            }
            System.out.println();
        }
    }

    private static void moveCommand(char direction) {
        int newY = playerY;
        int newX = playerX;

        switch (direction) {
            case 'w': newY--; break;
            case 's': newY++; break;
            case 'a': newX--; break;
            case 'd': newX++; break;
        }

        // Проверка границ
        if (newY < 0 || newY >= map[floor].length ||
                newX < 0 || newX >= map[floor][newY].length) {
            System.out.println("Вы упали в бездну! Игра окончена.");
            gameRunning = false;
            return;
        }

        char targetCell = map[floor][newY][newX];

        // Проверка на стены
        if (targetCell == '#') {
            System.out.println("Путь преграждают обломки!");
            return;
        }

        // Перемещаем игрока
        playerY = newY;
        playerX = newX;

        // Обрабатываем новую клетку
        if (targetCell == 'O') {
            oxygen += 10;
            System.out.println("Подобран кислородный баллон! +10 кислорода");
            map[floor][playerY][playerX] = '_'; // Баллон исчезает
        }

        oxygen--;
    }

    private static void jumpCommand(char direction) {
        // Проверка кислорода
        if (oxygen < 5) {
            System.out.println("Недостаточно кислорода для фазового рывка! Требуется 5 единиц.");
            return;
        }

        int targetY = playerY;
        int targetX = playerX;

        // Рассчитываем целевую клетку (прыжок на 2 клетки)
        switch (direction) {
            case 'w': targetY -= 2; break;
            case 's': targetY += 2; break;
            case 'a': targetX -= 2; break;
            case 'd': targetX += 2; break;
            default:
                System.out.println("Неверное направление! Используйте w/a/s/d");
                return;
        }

        // Проверка границ целевой клетки
        if (targetY < 0 || targetY >= map[floor].length ||
                targetX < 0 || targetX >= map[floor][targetY].length) {
            System.out.println("Нельзя прыгнуть за пределы корабля!");
            return;
        }

        // Проверяем, что целевая клетка проходима
        char targetCell = map[floor][targetY][targetX];
        if (targetCell == '#') {
            System.out.println("Нельзя приземлиться в стену!");
            return;
        }

        // Выполняем прыжок
        System.out.println("Совершён фазовый рывок!");
        playerY = targetY;
        playerX = targetX;
        oxygen -= 5;

        // Обрабатываем клетку приземления
        if (targetCell == 'O') {
            oxygen += 10;
            System.out.println("Подобран кислородный баллон! +10 кислорода");
            map[floor][playerY][playerX] = '_';
        }
        // Если приземлились на F - победа будет обработана в checkWin()
    }

    private static void interact(char direction) {
        int checkY = playerY;
        int checkX = playerX;

        switch (direction) {
            case 'w': checkY--; break;
            case 's': checkY++; break;
            case 'a': checkX--; break;
            case 'd': checkX++; break;
            default:
                System.out.println("Неверное направление! Используйте w/a/s/d");
                return;
        }

        // Проверяем, не вышли ли за границы
        if (checkY < 0 || checkY >= map[floor].length ||
                checkX < 0 || checkX >= map[floor][checkY].length) {
            System.out.println("Нельзя взаимодействовать за пределами корабля!");
            return;
        }

        char cell = map[floor][checkY][checkX];

        if (cell == 'D') {
            // Спуск вниз
            if (floor > 0) {
                System.out.println("Использован гравилифт вниз!");
                floor--;

                boolean found = false;
                for (int y = 0; y < map[floor].length; y++) {
                    for (int x = 0; x < map[floor][y].length; x++) {
                        if (map[floor][y][x] == 'U') {
                            playerY = y;
                            playerX = x;
                            found = true;
                            System.out.println("Вы появились на лифте U на палубе " + floor);
                            break;
                        }
                    }
                    if (found) break;
                }

                if (!found) {
                    System.out.println("Ошибка: не найден соответствующий лифт U!");
                }
                oxygen--;
            } else {
                System.out.println("Нельзя спуститься ниже - это самый нижний этаж!");
            }
        } else if (cell == 'U') {
            // Подъем вверх
            if (floor < map.length - 1) {
                System.out.println("Использован гравилифт вверх!");
                floor++;

                boolean found = false;
                for (int y = 0; y < map[floor].length; y++) {
                    for (int x = 0; x < map[floor][y].length; x++) {
                        if (map[floor][y][x] == 'D') {
                            playerY = y;
                            playerX = x;
                            found = true;
                            System.out.println("Вы появились на лифте D на палубе " + floor);
                            break;
                        }
                    }
                    if (found) break;
                }

                if (!found) {
                    System.out.println("Ошибка: не найден соответствующий лифт D!");
                }
                oxygen--;
            } else {
                System.out.println("Нельзя подняться выше - это самый верхний этаж!");
            }
        } else {
            System.out.println("На этой клетке нет гравилифта! Здесь: " + cell);
        }
    }

    private static void checkOxygen() {
        if (oxygen <= 0) {
            System.out.println("\nРезервный баллон пуст. Вы стали лишь еще одной мертвой деталью этого корабля.");
            gameRunning = false;
        }
    }

    private static void checkWin() {
        if (map[floor][playerY][playerX] == 'F') {
            System.out.println("\nПространство изогнулось, пропуская вас к спасению.");
            gameRunning = false;
        }
    }
}