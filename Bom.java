import java.util.Scanner;

class Bomb{
    public static char[][][] map5 = {
// === ЭТАЖ 0 (самый нижний) (4x4) ===
            {
                    {'P', '_', '#', '#'},
                    {'_', '#', '_', '#'},
                    {'_', '_', '_', '#'},
                    {'#', '#', '_', '_'}
            },
// === ЭТАЖ 1 (средний) (5x5) ===
            {
                    {'#', '#', '#', '#', '#'},
                    {'#', '_', '_', '_', '+'},
                    {'#', '_', '#', '#', '#'},
                    {'#', '_', '_', '_', '_'},
                    {'#', '#', '#', '#', '#'}
            },
// === ЭТАЖ 2 (самый верхний) (3x5) ===
            {
                    {'#', '#', '#', '#', '#'},
                    {'#', '_', '_', '_', 'F'},
                    {'#', '#', '#', '#', '#'}
            }
    };
    static char under = '_';
    static int floor = 0; // 0 - самый нижний этаж
    static int x = 0;
    static int y = 0;
    static int energy = 30;// Начальная энергия для способностей

    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);
        try {
            while(energy > 0){
                printmap();
                info();
                System.out.println("Введите действие: w/a/s/d - движение, q - вверх, e - вниз, b - бомба (стоит 5 энергии): ");
                String act = scan.nextLine().toLowerCase();
                switch (act){
                    case "w":
                    case "s":
                    case "a":
                    case "d":
                        move(act);
                        break;
                    case "q":
                        moveUp();
                        break;
                    case "e":
                        moveDown();
                        break;
                    case "b":
                        bomb();
                        break;
                    default:
                        System.out.println("Неверная команда!");
                        break;
                }
            }
            System.out.println("Game Over! Кислород закончился!");
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Взрывная волна вышла за пределы массива!");
            System.exit(0);
        }
    }

    static void move(String dir){
        int nx = x;
        int ny = y;
        if (dir.equals("w")){ny--;}
        if (dir.equals("s")){ny++;}
        if (dir.equals("a")){nx--;}
        if (dir.equals("d")){nx++;}

        // Проверка выхода за границы массива
        if (ny < 0 || ny >= map5[floor].length || nx < 0 || nx >= map5[floor][ny].length) {
            System.out.println("Нельзя выйти за пределы этажа!");
            return;
        }

        if(map5[floor][ny][nx] == '_'){
            energy--;
            map5[floor][ny][nx] = 'P';
            map5[floor][y][x] = under;
            under = '_';
            y = ny;
            x = nx;
        } else if (map5[floor][ny][nx] == '#') {
            System.out.println("Там стена! Нельзя пройти!");
        } else {
            System.out.println("Туда нельзя пройти!");
        }
    }

    static void moveUp(){
        // Вверх - к большему номеру этажа (с 0 на 1, с 1 на 2)
        if (floor == map5.length - 1) {
            System.out.println("Это самый верхний этаж! Нельзя подняться выше!");
            return;
        }

        // Запоминаем текущие координаты
        int newY = y;
        int newX = x;

        // Проверяем, не выходят ли координаты за границы этажа выше
        if (newY >= map5[floor + 1].length || newX >= map5[floor + 1][newY].length) {
            System.out.println("На этаже выше нет такой позиции! Нельзя подняться!");
            return;
        }

        // Проверяем, не является ли позиция на этаже выше стеной
        if (map5[floor + 1][newY][newX] == '#') {
            System.out.println("На этаже выше на этой позиции стена! Нельзя подняться!");
            return;
        }
// Поднимаемся (на этаж с большим номером)
        energy--;
        map5[floor][y][x] = under;
        under = '_';
        floor++;

        // Устанавливаем игрока на те же координаты
        map5[floor][y][x] = 'P';
        System.out.println("Вы поднялись на этаж " + floor);
    }

    static void moveDown(){
        // Вниз - к меньшему номеру этажа (с 2 на 1, с 1 на 0)
        if (floor == 0) {
            System.out.println("Это самый нижний этаж! Нельзя спуститься ниже!");
            return;
        }

        // Запоминаем текущие координаты
        int newY = y;
        int newX = x;

        // Проверяем, не выходят ли координаты за границы этажа ниже
        if (newY >= map5[floor - 1].length || newX >= map5[floor - 1][newY].length) {
            System.out.println("На этаже ниже нет такой позиции! Нельзя спуститься!");
            return;
        }

        // Проверяем, не является ли позиция на этаже ниже стеной
        if (map5[floor - 1][newY][newX] == '#') {
            System.out.println("На этаже ниже на этой позиции стена! Нельзя спуститься!");
            return;
        }

        // Спускаемся (на этаж с меньшим номером)
        energy--;
        map5[floor][y][x] = under;
        under = '_';
        floor--;

        // Устанавливаем игрока на те же координаты
        map5[floor][y][x] = 'P';
        System.out.println("Вы спустились на этаж " + floor);
    }

    static void bomb() {
        // Проверка энергии
        if (energy < 5) {
            System.out.println("Недостаточно энергии! Нужно 5 единиц. У вас " + energy);
            return;
        }

        System.out.println("💣 Взрыв! Крестовая волна уничтожает стены вокруг!");

        // Тратим энергию
        energy -= 5;

        // Взрываем центральную клетку (где стоит игрок) - БЕЗ ПРОВЕРКИ
        if (map5[floor][y][x] == '#') {
            map5[floor][y][x] = '_';
        }

        // 6 направлений для взрыва - БЕЗ ПРОВЕРОК ГРАНИЦ (это вызовет краш!)

        // Север (y-1) - может выйти за границу
        if (map5[floor][y-1][x] == '#') {
            map5[floor][y-1][x] = '_';
            System.out.println("Стена на севере разрушена!");
        }

        // Юг (y+1) - может выйти за границу
        if (map5[floor][y+1][x] == '#') {
            map5[floor][y+1][x] = '_';
            System.out.println("Стена на юге разрушена!");
        }

        // Запад (x-1) - может выйти за границу
        if (map5[floor][y][x-1] == '#') {
            map5[floor][y][x-1] = '_';
            System.out.println("Стена на западе разрушена!");
        }

        // Восток (x+1) - может выйти за границу
        if (map5[floor][y][x+1] == '#') {
            map5[floor][y][x+1] = '_';
            System.out.println("Стена на востоке разрушена!");
        }

        // Верх (этаж выше, floor+1) - может выйти за границу массива этажей
        if (map5[floor+1][y][x] == '#') {
            map5[floor+1][y][x] = '_';
            System.out.println("Стена на этаже выше разрушена!");
        }

        // Низ (этаж ниже, floor-1) - может выйти за границу массива этажей
        if (map5[floor-1][y][x] == '#') {
            map5[floor-1][y][x] = '_';
            System.out.println("Стена на этаже ниже разрушена!");
        }

        // Проверка на кислород (взрыв отнимает кислород)
        energy--;
        System.out.println("Осталось энергии: " + energy);
    }
    static void info(){
        System.out.println("X:" + x + " Y:" + y + " Floor:" + floor + " Oxygen: " + energy);
    }
    static void printmap(){
        for (int i = 0;i < map5[floor].length;i++){
            for(int j = 0;j < map5[floor][i].length;j++){
                System.out.print("[" +map5[floor][i][j]+ "]");
            }
            System.out.println();
        }
    }
}