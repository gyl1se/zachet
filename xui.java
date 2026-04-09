import java.util.Scanner;
public class xui {
    public static char[][][] map7 = {
            // === ПАЛУБА 0 (3x5) ===
            {
                    {'#', '#', '#', '#', '#'},
                    {'F', 'X', '_', 'U', '#'},
                    {'#', '#', '#', '#', '#'}
            },
            // === ПАЛУБА 1 (4x4) ===
            {
                    {'#', '#', '#', '#'},
                    {'#', '_', '_', 'D'},
                    {'#', 'U', '_', '#'},
                    {'#', '#', '#', '#'}
            },
            // === ПАЛУБА 2 (4x5) ===
            {
                    {'#', '#', '#', '#', '#'},
                    {'#', 'P', '_', 'O', '#'},
                    {'#', 'D', '#', '#', '#'},
                    {'#', '#', '#', '#', '#'}
            }
    };
    static int floor = 2;
    static int x = 1;
    static int y = 1;
    static int oxygen = 30;
    static char under = '_';
    static boolean hasDecryptor = true;
    static Scanner sc = new Scanner(System.in);
    public static void main(String[] args){
        while(oxygen > 0){
            stats();
            printmap();
            System.out.println("Введите команду: ");
            char act = sc.nextLine().toLowerCase().charAt(0);
            if ( act == 'w' || act == 'a' || act == 's' || act == 'd'){
                move(act);
            }else if (act == 'e'){
                System.out.println("Укажите направление спуска: ");
                char dir = sc.nextLine().toLowerCase().charAt(0);
                downstairs(dir);
            }else if (act == 'h'){
                System.out.println("Укажите направление: ");
                char dir = sc.nextLine().toLowerCase().charAt(0);
                hacker(dir);
            }
        }
        System.out.println("Тревожный писк скафандра стих. Тьма поглотила ваше \n" +
                "сознание.");
        System.exit(0);
    }
    static void move(char dir){
        int ny = y;
        int nx = x;
        if (dir == 'w') ny--;
        if (dir == 'a') nx--;
        if (dir == 's') ny++;
        if (dir == 'd') nx++;
        if (!proverka(ny, nx)){
            System.out.println("Там пустота, туда нельзя!");
            return;}
        if (map7[floor][ny][nx] == 'O'){
            oxygen += 10;
            oxygen--;
            System.out.println("Вы нашли баллон с кислородом! +10!");
            map7[floor][y][x] = under;
            map7[floor][ny][nx] = 'P';
            under = '_';
            y =  ny;
            x = nx;
        } else if (map7[floor][ny][nx] == 'F'){
            System.out.println("Байты сложились в путь к спасению. Спасательная шлюпка \n" +
                    "отстрелена.");
            System.exit(0);
        }else if (map7[floor][ny][nx] == '_'){
            oxygen--;
            map7[floor][ny][nx] = 'P';
            map7[floor][y][x] = under;
            under = '_';
            y = ny;
            x = nx;
        } else {
            System.out.println("Вы не можете туда пройти");
            return;
        }
    }
    static void hacker(char dir){
        if (oxygen < 7){
            System.out.println("Вам не хватит кислорода");
            return;
        }
        if (!hasDecryptor){
            System.out.println("У вас отсутствует дишифратор!");
            return;
        }
        int ny = y;
        int nx = x;
        if (dir == 'w') ny--;
        if (dir == 'a') nx--;
        if (dir == 's') ny++;
        if (dir == 'd') nx++;
        if (!proverka(ny, nx)){
            System.out.println("Там пустота, хакать нечего!");
            return;}
        if (map7[floor][ny][nx] == 'X'){
            oxygen -= 7;
            map7[floor][ny][nx] = '_';
        }else{
            System.out.println("Это нельзя взломать");
            return;
        }
    }
    static void downstairs(char dir){
        int ny = y;
        int nx = x;
        if (dir == 'w') ny--;
        if (dir == 'a') nx--;
        if (dir == 's') ny++;
        if (dir == 'd') nx++;
        if (!proverka(ny, nx)){
            System.out.println("Вы не можете спуститься в пустоту");
            return;
        }
        if (map7[floor][ny][nx] == 'D'){
            oxygen--;
            map7[floor][y][x] = under;
            floor--;
            findstairs('U');
        }
        else if (map7[floor][ny][nx] == 'U'){
            oxygen--;
            map7[floor][y][x] = under;
            floor++;
            findstairs('D');
        }else{
            System.out.println("Вы не можете спуститься/подняться тут");
            return;
        }
    }
    static boolean proverka(int ny, int nx){
        return ny >= 0 && nx >= 0 && ny < map7[floor].length && nx < map7[floor][ny].length;
    }
    static void findstairs(char lift){
        for (int i = 0; i < map7[floor].length; i++) {
            for (int j = 0; j < map7[floor][i].length; j++) {
                if (map7[floor][i][j] == lift){
                    y = i;
                    x = j;
                    under = lift;
                    map7[floor][y][x] = 'P';
                    return;
                }
            }
        }
    }
    static void printmap(){
        for (int i = 0; i < map7[floor].length; i++){
            for ( int j = 0; j < map7[floor][i].length; j++){
                System.out.print("[" + map7[floor][i][j] + "]");
            }
            System.out.println();
        }
    }
    static void stats(){

        System.out.println("Floor:" + floor + " X:" + x + " Y:" + y + " Oxygen left:" + oxygen);
    }
}

//static void moveUp(){
//    if (floor == map5.length-1){
//        System.out.println("Такого этажа несуществует");
//        return;
//    }
//    if (y >= map5[floor+1].length || x >= map5[floor+1][y].length){
//        System.out.println("Нет такой клетки на след этаже");
//        return;
//    }
//    if (map5[floor+1][y][x] == '#'){
//        System.out.println("На след этаже на этих координатах стена");
//        return;
//    }
//    energy--;
//    map5[floor][y][x] = '_';
//    floor++;
//    map5[floor][y][x] = 'P';
//    System.out.println("Вы поднялись на этаж " + floor);
//
//}
//static void moveDown(){
//    if (floor == 0){
//        System.out.println("Такого этажа несуществует");
//        return;
//    }
//    if (y >= map5[floor-1].length || x >= map5[floor-1][y].length){
//        System.out.println("Нет такой клетки на след этаже");
//        return;
//    }
//    if (map5[floor-1][y][x] == '#'){
//        System.out.println("На след этаже на этих координатах стена");
//        return;
//    }
//    energy--;
//    map5[floor][y][x] = '_';
//    floor--;
//    map5[floor][y][x] = 'P';
//    System.out.println("Вы поднялись на этаж " + floor);
//
//}

//static void laser(String dir) {
//    // Проверка энергии и буров
//    if (energy < 10) {
//        System.out.println("Недостаточно энергии! Нужно 10 единиц. У вас " + energy);
//        return;
//    }
//    if (drills < 1) {
//        System.out.println("Недостаточно алмазных буров! У вас " + drills);
//        return;
//    }
//
//    // Тратим ресурсы
//    energy -= 10;
//    drills--;
//
//    int destroyed = 0;
//    int step = 1;
//
//    switch (dir) {
//        case "w":
//            while (destroyed < 3){
//                int targetY = y - step;
//                if (targetY < 0){
//                    System.out.println("Луч достиг границы карты");
//                    break;
//                }
//                if (x >= map5[floor][targetY].length){
//                    System.out.println("Луч достиг границы карты");
//                    break;
//                }
//                if (map5[floor][targetY][x] == '#'){
//                    map5[floor][targetY][x] = '_';
//                    destroyed++;
//                }
//                step++;
//            }
//            break;
//        case "s": // Юг
//            while (destroyed < 3) {
//                int targetY = y + step;
//                if (targetY >= map5[floor].length) {
//                    System.out.println("Луч достиг границы карты!");
//                    break;
//                }
//                if (x >= map5[floor][targetY].length) {
//                    System.out.println("Луч достиг границы карты!");
//                    break;
//                }
//                if (map5[floor][targetY][x] == '#') {
//                    map5[floor][targetY][x] = '_';
//                    destroyed++;
//                    System.out.println("Стена уничтожена на юге, шаг " + step);
//                }
//                step++;
//            }
//            break;
//
//        case "a": // Запад
//            while (destroyed < 3) {
//                int targetX = x - step;
//                if (targetX < 0) {
//                    System.out.println("Луч достиг границы карты!");
//                    break;
//                }
//                if (map5[floor][y][targetX] == '#') {
//                    map5[floor][y][targetX] = '_';
//                    destroyed++;
//                    System.out.println("Стена уничтожена на западе, шаг " + step);
//                }
//                step++;
//            }
//            break;
//
//        case "d": // Восток
//            while (destroyed < 3) {
//                int targetX = x + step;
//                if (targetX >= map5[floor][y].length) {
//                    System.out.println("Луч достиг границы карты!");
//                    break;
//                }
//                if (map5[floor][y][targetX] == '#') {
//                    map5[floor][y][targetX] = '_';
//                    destroyed++;
//                    System.out.println("Стена уничтожена на востоке, шаг " + step);
//                }
//                step++;
//            }
//            break;
//
//        case "q": // Вверх (увеличение этажа)
//            while (destroyed < 3) {
//                int targetFloor = floor + step;
//                if (targetFloor >= map5.length) {
//                    System.out.println("Луч достиг границы этажей!");
//                    break;
//                }
//                if (y >= map5[targetFloor].length || x >= map5[targetFloor][y].length) {
//                    System.out.println("На этаже выше нет такой позиции!");
//                    break;
//                }
//                if (map5[targetFloor][y][x] == '#') {
//                    map5[targetFloor][y][x] = '_';
//                    destroyed++;
//                    System.out.println("Стена уничтожена на этаже выше, шаг " + step);
//                }
//                step++;
//            }
//            break;
//
//        case "e": // Вниз (уменьшение этажа)
//            while (destroyed < 3) {
//                int targetFloor = floor - step;
//                if (targetFloor < 0) {
//                    System.out.println("Луч достиг границы этажей!");
//                    break;
//                }
//                if (y >= map5[targetFloor].length || x >= map5[targetFloor][y].length) {
//                    System.out.println("На этаже ниже нет такой позиции!");
//                    break;
//                }
//                if (map5[targetFloor][y][x] == '#') {
//                    map5[targetFloor][y][x] = '_';
//                    destroyed++;
//                    System.out.println("Стена уничтожена на этаже ниже, шаг " + step);
//                }
//                step++;
//            }
//            break;

//static void bomb() {
//    // Проверка энергии
//    if (energy < 5) {
//        System.out.println("Недостаточно энергии! Нужно 5 единиц. У вас " + energy);
//        return;
//    }
//
//    System.out.println("💣 Взрыв! Крестовая волна уничтожает стены вокруг!");
//
//    // Тратим энергию
//    energy -= 5;
//
//    // Взрываем центральную клетку (где стоит игрок) - БЕЗ ПРОВЕРКИ
//    if (map5[floor][y][x] == '#') {
//        map5[floor][y][x] = '_';
//    }
//
//    // 6 направлений для взрыва - БЕЗ ПРОВЕРОК ГРАНИЦ (это вызовет краш!)
//
//    // Север (y-1) - может выйти за границу
//    if (map5[floor][y-1][x] == '#') {
//        map5[floor][y-1][x] = '_';
//        System.out.println("Стена на севере разрушена!");
//    }
//
//    // Юг (y+1) - может выйти за границу
//    if (map5[floor][y+1][x] == '#') {
//        map5[floor][y+1][x] = '_';
//        System.out.println("Стена на юге разрушена!");
//    }
//
//    // Запад (x-1) - может выйти за границу
//    if (map5[floor][y][x-1] == '#') {
//        map5[floor][y][x-1] = '_';
//        System.out.println("Стена на западе разрушена!");
//    }
//
//    // Восток (x+1) - может выйти за границу
//    if (map5[floor][y][x+1] == '#') {
//        map5[floor][y][x+1] = '_';
//        System.out.println("Стена на востоке разрушена!");
//    }
//
//    // Верх (этаж выше, floor+1) - может выйти за границу массива этажей
//    if (map5[floor+1][y][x] == '#') {
//        map5[floor+1][y][x] = '_';
//        System.out.println("Стена на этаже выше разрушена!");
//    }
//
//    // Низ (этаж ниже, floor-1) - может выйти за границу массива этажей
//    if (map5[floor-1][y][x] == '#') {
//        map5[floor-1][y][x] = '_';
//        System.out.println("Стена на этаже ниже разрушена!");
//    }
//
//    // Проверка на кислород (взрыв отнимает кислород)
//    energy--;
//    System.out.println("Осталось энергии: " + energy);
//}

