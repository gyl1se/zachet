//первая способность
import java.util.Scanner;
import java.util.Random;

public class SingularityGame {
    private static char[][][] network;
    private static int z, y, x;
    private static int energy;
    private static Random rand = new Random();
    private static final int COLLAPSE_COST = 12;

    public static void main(String[] args) {
        // Карта из варианта 1 (стр. 7 PDF)
        network = new char[][][] {
            // Кластер 0: 3x4
            {
                {'#', '#', '#', '#'},
                {'#', 'P', '_', '#'},
                {'#', '#', '#', '#'}
            },
            // Кластер 1: 2x6
            {
                {'#', '#', '_', '#', '#', '#'},
                {'#', '#', '_', '_', '+', '#'}
            },
            // Кластер 2: 4x3
            {
                {'#', '#', '#'},
                {'#', '_', '#'},
                {'#', 'F', '#'},
                {'#', '#', '#'}
            }
        };

        // Найти начальную позицию P
        for (int i = 0; i < network.length; i++) {
            for (int j = 0; j < network[i].length; j++) {
                for (int k = 0; k < network[i][j].length; k++) {
                    if (network[i][j][k] == 'P') {
                        z = i; y = j; x = k;
                        break;
                    }
                }
            }
        }

        energy = 30;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Игра началась! Ваш персонаж — Сингулярность.");
        while (true) {
            printStatus();
            printMap();
            System.out.print("Введите команду: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;
            char cmd = input.charAt(0);

            boolean actionDone = false;
            switch (cmd) {
                case 'w': actionDone = move(-1, 0); break;
                case 's': actionDone = move(1, 0); break;
                case 'a': actionDone = move(0, -1); break;
                case 'd': actionDone = move(0, 1); break;
                case 'q': actionDone = moveLayer(-1); break;
                case 'e': actionDone = moveLayer(1); break;
                case 'c': actionDone = useCollapse(); break;
                default:
                    System.out.println("Неизвестная команда. Используйте w/a/s/d, q/e, c.");
                    continue;
            }

            // После каждого действия проверяем победу/поражение
            if (energy < 0) {
                System.out.println("Я застрял в бесконечно малом цикле. Тьма.");
                break;
            }
            if (network[z][y][x] == 'F') {
                System.out.println("Горизонт событий пройден. Ядро в моих руках.");
                break;
            }
        }
        scanner.close();
    }

    private static void printStatus() {
        System.out.printf("Статус: Кластер %d | Позиция [Y: %d, X: %d] | Энергия: %d%n", z, y, x, energy);
    }

    private static void printMap() {
        System.out.println("Карта текущего кластера:");
        for (int i = 0; i < network[z].length; i++) {
            for (int j = 0; j < network[z][i].length; j++) {
                char cell = network[z][i][j];
                if (i == y && j == x && cell != 'F') cell = 'P';
                System.out.print("[" + cell + "] ");
            }
            System.out.println();
        }
    }

    private static boolean move(int dy, int dx) {
        int newY = y + dy;
        int newX = x + dx;
        if (newY >= 0 && newY < network[z].length && newX >= 0 && newX < network[z][newY].length) {
            if (network[z][newY][newX] == '#') {
                System.out.println("Препятствие / Пустота");
                energy--;
                return false;
            }
            // Перемещение
            if (network[z][y][x] == 'P') network[z][y][x] = '_';
            y = newY; x = newX;
            energy--;
            // Бонус
            if (network[z][y][x] == '+') {
                int bonus = rand.nextInt(11) + 10;
                energy += bonus;
                System.out.printf("Найден бонус! +%d энергии.%n", bonus);
                network[z][y][x] = '_';
            }
            return true;
        } else {
            System.out.println("Препятствие / Пустота");
            energy--;
            return false;
        }
    }

    private static boolean moveLayer(int dz) {
        int newZ = z + dz;
        if (newZ < 0 || newZ >= network.length) {
            System.out.println("Препятствие / Пустота");
            energy--;
            return false;
        }
        if (y >= network[newZ].length || x >= network[newZ][y].length) {
            System.out.println("Препятствие / Пустота");
            energy--;
            return false;
        }
        if (network[newZ][y][x] == '#') {
            System.out.println("Препятствие / Пустота");
            energy--;
            return false;
        }
        // Перемещение
        if (network[z][y][x] == 'P') network[z][y][x] = '_';
        z = newZ;
        energy--;
        // Бонус на новом слое
        if (network[z][y][x] == '+') {
            int bonus = rand.nextInt(11) + 10;
            energy += bonus;
            System.out.printf("Найден бонус! +%d энергии.%n", bonus);
            network[z][y][x] = '_';
        }
        return true;
    }

    private static boolean useCollapse() {
        if (energy < COLLAPSE_COST) {
            System.out.println("Недостаточно энергии для сингулярности!");
            return false;
        }
        // Текущий слой: 8 соседей (все, кроме центра)
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dy == 0 && dx == 0) continue;
                int ny = y + dy;
                int nx = x + dx;
                if (ny >= 0 && ny < network[z].length && nx >= 0 && nx < network[z][ny].length) {
                    if (network[z][ny][nx] == '#')
                        network[z][ny][nx] = '_';
                }
            }
        }
        // Слой выше (z-1) и ниже (z+1): квадрат 3x3
        for (int deltaZ : new int[]{-1, 1}) {
            int targetZ = z + deltaZ;
            if (targetZ >= 0 && targetZ < network.length) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        int ny = y + dy;
                        int nx = x + dx;
                        if (ny >= 0 && ny < network[targetZ].length && nx >= 0 && nx < network[targetZ][ny].length) {
                            if (network[targetZ][ny][nx] == '#')
                                network[targetZ][ny][nx] = '_';
                        }
                    }
                }
            }
        }
        energy -= COLLAPSE_COST;
        System.out.println("-> Пространство свернуто! Дефрагментация прошла успешно.");
        return true;
    }
}
//вторая способность
import java.util.Scanner;
import java.util.Random;

public class PhantomBurstGame {
    private static char[][][] network;
    private static int z, y, x;
    private static int energy;
    private static int packets;
    private static Random rand = new Random();
    private static final int BURST_COST_ENERGY = 7;

    public static void main(String[] args) {
        // Карта из варианта 9 (стр. 15 PDF) – для класса B
        network = new char[][][] {
            // Кластер 0: 5x2
            {
                {'#', '#'},
                {'#', 'P'},
                {'#', '_'},
                {'#', '#'},
                {'#', '#'}
            },
            // Кластер 1: 1x6
            {
                {'#', '_', '_', '_', '+', '#'}
            },
            // Кластер 2: 4x4
            {
                {'#', '#', '#', '#'},
                {'#', '_', '_', '#'},
                {'#', '#', 'F', '#'},
                {'#', '#', '#', '#'}
            }
        };

        // Найти P
        for (int i = 0; i < network.length; i++) {
            for (int j = 0; j < network[i].length; j++) {
                for (int k = 0; k < network[i][j].length; k++) {
                    if (network[i][j][k] == 'P') {
                        z = i; y = j; x = k;
                        break;
                    }
                }
            }
        }

        energy = 30;
        packets = 4;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Игра началась! Ваш персонаж — Фантомный всплеск.");
        while (true) {
            printStatus();
            printMap();
            System.out.print("Введите команду: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;
            char cmd = input.charAt(0);

            switch (cmd) {
                case 'w': move(-1, 0); break;
                case 's': move(1, 0); break;
                case 'a': move(0, -1); break;
                case 'd': move(0, 1); break;
                case 'q': moveLayer(-1); break;
                case 'e': moveLayer(1); break;
                case 'p': usePhantomBurst(); break;
                default:
                    System.out.println("Неизвестная команда. Используйте w/a/s/d, q/e, p.");
                    continue;
            }

            if (energy < 0) {
                System.out.println("Фантом рассеялся. Мой след потерян.");
                break;
            }
            if (network[z][y][x] == 'F') {
                System.out.println("Всплеск прошел сквозь фильтры. Ядро дешифровано.");
                break;
            }
        }
        scanner.close();
    }

    private static void printStatus() {
        System.out.printf("Статус: Кластер %d | Позиция [Y: %d, X: %d] | Энергия: %d | Пакеты: %d%n", z, y, x, energy, packets);
    }

    private static void printMap() {
        System.out.println("Карта текущего кластера:");
        for (int i = 0; i < network[z].length; i++) {
            for (int j = 0; j < network[z][i].length; j++) {
                char cell = network[z][i][j];
                if (i == y && j == x && cell != 'F') cell = 'P';
                System.out.print("[" + cell + "] ");
            }
            System.out.println();
        }
    }

    private static void move(int dy, int dx) {
        int newY = y + dy;
        int newX = x + dx;
        if (newY >= 0 && newY < network[z].length && newX >= 0 && newX < network[z][newY].length) {
            if (network[z][newY][newX] == '#') {
                System.out.println("Препятствие / Пустота");
                energy--;
                return;
            }
            if (network[z][y][x] == 'P') network[z][y][x] = '_';
            y = newY; x = newX;
            energy--;
            if (network[z][y][x] == '+') {
                int bonus = rand.nextInt(11) + 10;
                energy += bonus;
                System.out.printf("Найден бонус! +%d энергии.%n", bonus);
                network[z][y][x] = '_';
            }
        } else {
            System.out.println("Препятствие / Пустота");
            energy--;
        }
    }

    private static void moveLayer(int dz) {
        int newZ = z + dz;
        if (newZ < 0 || newZ >= network.length) {
            System.out.println("Препятствие / Пустота");
            energy--;
            return;
        }
        if (y >= network[newZ].length || x >= network[newZ][y].length) {
            System.out.println("Препятствие / Пустота");
            energy--;
            return;
        }
        if (network[newZ][y][x] == '#') {
            System.out.println("Препятствие / Пустота");
            energy--;
            return;
        }
        if (network[z][y][x] == 'P') network[z][y][x] = '_';
        z = newZ;
        energy--;
        if (network[z][y][x] == '+') {
            int bonus = rand.nextInt(11) + 10;
            energy += bonus;
            System.out.printf("Найден бонус! +%d энергии.%n", bonus);
            network[z][y][x] = '_';
        }
    }

    private static void usePhantomBurst() {
        if (energy < BURST_COST_ENERGY) {
            System.out.println("Недостаточно энергии для фантомного всплеска!");
            return;
        }
        if (packets < 1) {
            System.out.println("Нет пакетов данных!");
            return;
        }
        // Уничтожаем блоки в форме X на слоях выше и ниже
        for (int deltaZ : new int[]{-1, 1}) {
            int targetZ = z + deltaZ;
            if (targetZ >= 0 && targetZ < network.length) {
                int[] dyList = {0, -1, -1, 1, 1};
                int[] dxList = {0, -1, 1, -1, 1};
                for (int i = 0; i < dyList.length; i++) {
                    int ny = y + dyList[i];
                    int nx = x + dxList[i];
                    if (ny >= 0 && ny < network[targetZ].length && nx >= 0 && nx < network[targetZ][ny].length) {
                        if (network[targetZ][ny][nx] == '#')
                            network[targetZ][ny][nx] = '_';
                    }
                }
            }
        }
        energy -= BURST_COST_ENERGY;
        packets--;
        System.out.println("-> Фантомный всплеск активирован! Блоки на соседних слоях уничтожены.");
    }
}
//тертья способность
import java.util.Scanner;
import java.util.Random;

public class CascadeResonanceGame {
    private static char[][][] network;
    private static int z, y, x;
    private static int energy;
    private static Random rand = new Random();
    private static final int RESONANCE_COST = 10;

    public static void main(String[] args) {
        // Карта из варианта 17 (стр. 23 PDF) – для класса C
        network = new char[][][] {
            // Кластер 0: 5x2
            {
                {'#', '#'},
                {'#', 'P'},
                {'#', '_'},
                {'#', '#'},
                {'#', '#'}
            },
            // Кластер 1: 1x6
            {
                {'#', '_', '_', '_', '+', '#'}
            },
            // Кластер 2: 4x4
            {
                {'#', '#', '#', '#'},
                {'#', '_', '_', '#'},
                {'#', '#', 'F', '#'},
                {'#', '#', '#', '#'}
            }
        };

        // Найти P
        for (int i = 0; i < network.length; i++) {
            for (int j = 0; j < network[i].length; j++) {
                for (int k = 0; k < network[i][j].length; k++) {
                    if (network[i][j][k] == 'P') {
                        z = i; y = j; x = k;
                        break;
                    }
                }
            }
        }

        energy = 30;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Игра началась! Ваш персонаж — Каскадный резонанс.");
        while (true) {
            printStatus();
            printMap();
            System.out.print("Введите команду: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;
            char cmd = input.charAt(0);

            switch (cmd) {
                case 'w': move(-1, 0); break;
                case 's': move(1, 0); break;
                case 'a': move(0, -1); break;
                case 'd': move(0, 1); break;
                case 'q': moveLayer(-1); break;
                case 'e': moveLayer(1); break;
                case 'r':
                    System.out.print("Направление волны (w/a/s/d): ");
                    String dirInput = scanner.nextLine().trim();
                    if (dirInput.isEmpty()) break;
                    char dir = dirInput.charAt(0);
                    useResonance(dir);
                    break;
                default:
                    System.out.println("Неизвестная команда. Используйте w/a/s/d, q/e, r.");
                    continue;
            }

            if (energy < 0) {
                System.out.println("Мой путь оборвался на полпути к финалу. Энергия: 0.");
                break;
            }
            if (network[z][y][x] == 'F') {
                System.out.println("Волна достигла дна. Мастер-ключ у меня.");
                break;
            }
        }
        scanner.close();
    }

    private static void printStatus() {
        System.out.printf("Статус: Кластер %d | Позиция [Y: %d, X: %d] | Энергия: %d%n", z, y, x, energy);
    }

    private static void printMap() {
        System.out.println("Карта текущего кластера:");
        for (int i = 0; i < network[z].length; i++) {
            for (int j = 0; j < network[z][i].length; j++) {
                char cell = network[z][i][j];
                if (i == y && j == x && cell != 'F') cell = 'P';
                System.out.print("[" + cell + "] ");
            }
            System.out.println();
        }
    }

    private static void move(int dy, int dx) {
        int newY = y + dy;
        int newX = x + dx;
        if (newY >= 0 && newY < network[z].length && newX >= 0 && newX < network[z][newY].length) {
            if (network[z][newY][newX] == '#') {
                System.out.println("Препятствие / Пустота");
                energy--;
                return;
            }
            if (network[z][y][x] == 'P') network[z][y][x] = '_';
            y = newY; x = newX;
            energy--;
            if (network[z][y][x] == '+') {
                int bonus = rand.nextInt(11) + 10;
                energy += bonus;
                System.out.printf("Найден бонус! +%d энергии.%n", bonus);
                network[z][y][x] = '_';
            }
        } else {
            System.out.println("Препятствие / Пустота");
            energy--;
        }
    }

    private static void moveLayer(int dz) {
        int newZ = z + dz;
        if (newZ < 0 || newZ >= network.length) {
            System.out.println("Препятствие / Пустота");
            energy--;
            return;
        }
        if (y >= network[newZ].length || x >= network[newZ][y].length) {
            System.out.println("Препятствие / Пустота");
            energy--;
            return;
        }
        if (network[newZ][y][x] == '#') {
            System.out.println("Препятствие / Пустота");
            energy--;
            return;
        }
        if (network[z][y][x] == 'P') network[z][y][x] = '_';
        z = newZ;
        energy--;
        if (network[z][y][x] == '+') {
            int bonus = rand.nextInt(11) + 10;
            energy += bonus;
            System.out.printf("Найден бонус! +%d энергии.%n", bonus);
            network[z][y][x] = '_';
        }
    }

    private static void useResonance(char direction) {
        if (energy < RESONANCE_COST) {
            System.out.println("Недостаточно энергии для каскадного резонанса!");
            return;
        }
        int dy = 0, dx = 0;
        switch (direction) {
            case 'w': dy = -1; break;
            case 's': dy = 1; break;
            case 'a': dx = -1; break;
            case 'd': dx = 1; break;
            default:
                System.out.println("Неверное направление. Используйте w/a/s/d.");
                return;
        }
        // Текущий слой: 1 шаг
        int ny1 = y + dy;
        int nx1 = x + dx;
        if (ny1 >= 0 && ny1 < network[z].length && nx1 >= 0 && nx1 < network[z][ny1].length) {
            if (network[z][ny1][nx1] == '#')
                network[z][ny1][nx1] = '_';
        }
        // Слой z+1: 2 шага
        int nextZ = z + 1;
        if (nextZ < network.length) {
            int ny2 = y + 2*dy;
            int nx2 = x + 2*dx;
            if (ny2 >= 0 && ny2 < network[nextZ].length && nx2 >= 0 && nx2 < network[nextZ][ny2].length) {
                if (network[nextZ][ny2][nx2] == '#')
                    network[nextZ][ny2][nx2] = '_';
            }
        }
        // Слой z+2: 3 шага
        int nextZ2 = z + 2;
        if (nextZ2 < network.length) {
            int ny3 = y + 3*dy;
            int nx3 = x + 3*dx;
            if (ny3 >= 0 && ny3 < network[nextZ2].length && nx3 >= 0 && nx3 < network[nextZ2][ny3].length) {
                if (network[nextZ2][ny3][nx3] == '#')
                    network[nextZ2][ny3][nx3] = '_';
            }
        }
        energy -= RESONANCE_COST;
        System.out.println("-> Каскадный резонанс активирован! Блоки уничтожены лесенкой.");
    }
}
