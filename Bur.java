import java.util.Scanner;

class driller{
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
    static int floor = 0;
    static int x = 0;
    static int y = 0;
    static int energy = 30;
    static int drills = 3;

    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);
        try {
            while(energy > 0){
                printmap();
                info();
                System.out.println("Введите действие: w/a/s/d - движение, q - вверх, e - вниз, l - лазер (стоит 10 энергии и 1 бур): ");
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
                    case "l":
                        System.out.println("Введите направление лазера (w - север, s - юг, a - запад, d - восток, q - вверх, e - вниз): ");
                        String dir = scan.nextLine().toLowerCase();
                        laser(dir);
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

        if (floor == map5.length - 1) {
            System.out.println("Это самый верхний этаж! Нельзя подняться выше!");
            return;
        }


        int newY = y;
        int newX = x;


        if (newY >= map5[floor + 1].length || newX >= map5[floor + 1][newY].length) {
            System.out.println("На этаже выше нет такой позиции! Нельзя подняться!");
            return;
        }

        if (map5[floor + 1][newY][newX] == '#') {
            System.out.println("На этаже выше на этой позиции стена! Нельзя подняться!");
            return;
        }


        energy--;
        map5[floor][y][x] = under;
        under = '_';
        floor++;


        map5[floor][y][x] = 'P';
        System.out.println("Вы поднялись на этаж " + floor);
    }

    static void moveDown(){

        if (floor == 0) {
            System.out.println("Это самый нижний этаж! Нельзя спуститься ниже!");
            return;
        }


        int newY = y;
        int newX = x;


        if (newY >= map5[floor - 1].length || newX >= map5[floor - 1][newY].length) {
            System.out.println("На этаже ниже нет такой позиции! Нельзя спуститься!");
            return;
        }


        if (map5[floor - 1][newY][newX] == '#') {
            System.out.println("На этаже ниже на этой позиции стена! Нельзя спуститься!");
            return;
        }


        energy--;
        map5[floor][y][x] = under;
        under = '_';
        floor--;


        map5[floor][y][x] = 'P';
        System.out.println("Вы спустились на этаж " + floor);
    }

    static void laser(String dir) {

        if (energy < 10) {
            System.out.println("Недостаточно энергии! Нужно 10 единиц. У вас " + energy);
            return;
        }
        if (drills < 1) {
            System.out.println("Недостаточно алмазных буров! У вас " + drills);
            return;
        }


        energy -= 10;
        drills--;

        int destroyed = 0;
        int step = 1;

        switch (dir) {
            case "w":
                while (destroyed < 3){
                    int targetY = y - step;
                    if (targetY < 0){
                        System.out.println("Луч достиг границы карты");
                        break;
                    }
                    if (x >= map5[floor][targetY].length){
                        System.out.println("Луч достиг границы карты");
                        break;
                    }
                    if (map5[floor][targetY][x] == '#'){
                        map5[floor][targetY][x] = '_';
                        destroyed++;
                    }
                    step++;
                }
                break;
            case "s":
                while (destroyed < 3) {
                    int targetY = y + step;
                    if (targetY >= map5[floor].length) {
                        System.out.println("Луч достиг границы карты!");
                        break;
                    }
                    if (x >= map5[floor][targetY].length) {
                        System.out.println("Луч достиг границы карты!");
                        break;
                    }
                    if (map5[floor][targetY][x] == '#') {
                        map5[floor][targetY][x] = '_';
                        destroyed++;
                        System.out.println("Стена уничтожена на юге, шаг " + step);
                    }
                    step++;
                }
                break;
            case "a":
                while (destroyed < 3) {
                    int targetX = x - step;
                    if (targetX < 0) {
                        System.out.println("Луч достиг границы карты!");
                        break;
                    }
                    if (map5[floor][y][targetX] == '#') {
                        map5[floor][y][targetX] = '_';
                        destroyed++;
                        System.out.println("Стена уничтожена на западе, шаг " + step);
                    }
                    step++;
                }
                break;

            case "d":
                while (destroyed < 3) {
                    int targetX = x + step;
                    if (targetX >= map5[floor][y].length) {
                        System.out.println("Луч достиг границы карты!");
                        break;
                    }
                    if (map5[floor][y][targetX] == '#') {
                        map5[floor][y][targetX] = '_';
                        destroyed++;
                        System.out.println("Стена уничтожена на востоке, шаг " + step);
                    }
                    step++;
                }
                break;

            case "q":
                while (destroyed < 3) {
                    int targetFloor = floor + step;
                    if (targetFloor >= map5.length) {
                        System.out.println("Луч достиг границы этажей!");
                        break;
                    }
                    if (y >= map5[targetFloor].length || x >= map5[targetFloor][y].length) {
                        System.out.println("На этаже выше нет такой позиции!");
                        break;
                    }
                    if (map5[targetFloor][y][x] == '#') {
                        map5[targetFloor][y][x] = '_';
                        destroyed++;
                        System.out.println("Стена уничтожена на этаже выше, шаг " + step);
                    }
                    step++;
                }
                break;

            case "e": 
                while (destroyed < 3) {
                    int targetFloor = floor - step;
                    if (targetFloor < 0) {
                        System.out.println("Луч достиг границы этажей!");
                        break;
                    }
                    if (y >= map5[targetFloor].length || x >= map5[targetFloor][y].length) {
                        System.out.println("На этаже ниже нет такой позиции!");
                        break;
                    }
                    if (map5[targetFloor][y][x] == '#') {
                        map5[targetFloor][y][x] = '_';
                        destroyed++;
                        System.out.println("Стена уничтожена на этаже ниже, шаг " + step);
                    }
                    step++;
                }
                break;

            default:
                System.out.println("Неверное направление! Лазер не активирован.");
                energy += 10;
                drills++;
                return;
        }

        System.out.println("Лазер завершил работу. Уничтожено стен: " + destroyed);
        System.out.println("Осталось энергии: " + energy + ", буров: " + drills);
    }

    static void info(){
        System.out.println("X:" + x + " Y:" + y + " Floor:" + floor + " Energy: " + energy);
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