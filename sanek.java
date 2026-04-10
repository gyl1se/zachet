static void doubleStep(String dir){
    if (energy < 2){
        System.out.println("Недостаточно энергии!");
        return;
    }

    int nx = x;
    int ny = y;

    // --- ШАГ 1 ---
    if (dir.equals("w")) ny--;
    if (dir.equals("s")) ny++;
    if (dir.equals("a")) nx--;
    if (dir.equals("d")) nx++;

    // проверка границ
    if (proverka(ny, nx)){
        System.out.println("Граница!");
        return;
    }

    // если стена — не двигаемся
    if (map5[floor][ny][nx] == '#'){
        System.out.println("Первая клетка — стена!");
        return;
    }

    // запоминаем первую клетку
    int firstX = nx;
    int firstY = ny;

    // --- ШАГ 2 ---
    if (dir.equals("w")) ny--;
    if (dir.equals("s")) ny++;
    if (dir.equals("a")) nx--;
    if (dir.equals("d")) nx++;

    // если граница — идём на 1
    if (proverka(ny, nx)){
        moveTo(firstY, firstX);
        energy -= 2;
        return;
    }

    // если стена — идём на 1
    if (map5[floor][ny][nx] == '#'){
        System.out.println("Вторая клетка — стена, идём на 1");
        moveTo(firstY, firstX);
        energy -= 2;
        return;
    }

    // иначе идём на 2
    moveTo(ny, nx);
    energy -= 2;
}

static void moveTo(int ny, int nx){
    map5[floor][y][x] = under;

    y = ny;
    x = nx;

    if (map5[floor][y][x] == 'F'){
        System.out.println("Победа!");
        System.exit(0);
    }

    under = '_';
    map5[floor][y][x] = 'P';
}


-----heal over time-------

static boolean heal = false;
static int turns = 0;

static void activateHeal(){
    if (energy < 3){
        System.out.println("Недостаточно энергии!");
        return;
    }

    energy -= 3;
    heal = true;
    turns = 3;

    System.out.println("Регенерация активирована на 3 хода!");
}


// добавить в (move)
if (heal){
    energy += 2;
    turns--;

    System.out.println("Регенерация: +2 энергии");

    if (turns == 0){
        heal = false;
        System.out.println("Регенерация закончилась");
    }
}

case "b":
    activateHeal();
    break;

---push-----

static void push(String dir){
    if (energy < 3){
        System.out.println("Недостаточно энергии!");
        return;
    }

    int nx = x;
    int ny = y;

    // шаг 1 — куда толкаем
    if (dir.equals("w")) ny--;
    if (dir.equals("s")) ny++;
    if (dir.equals("a")) nx--;
    if (dir.equals("d")) nx++;

    // проверка границ
    if (proverka(ny, nx)){
        System.out.println("Граница карты!");
        return;
    }

    // должна быть стена
    if (map5[floor][ny][nx] != '#'){
        System.out.println("Тут нечего толкать!");
        return;
    }

    // шаг 2 — куда сдвигается стена
    int nx2 = nx;
    int ny2 = ny;

    if (dir.equals("w")) ny2--;
    if (dir.equals("s")) ny2++;
    if (dir.equals("a")) nx2--;
    if (dir.equals("d")) nx2++;

    // проверка границ
    if (proverka(ny2, nx2)){
        System.out.println("Некуда толкать — граница!");
        return;
    }

    // проверка, что за стеной пусто
    if (map5[floor][ny2][nx2] != '_'){
        System.out.println("Нельзя толкнуть — занято!");
        return;
    }

    map5[floor][ny2][nx2] = '#';

    // игрок идёт на место стены
    map5[floor][y][x] = under;
    y = ny;
    x = nx;
    map5[floor][y][x] = 'P';

    energy -= 3;

    System.out.println("Стена сдвинута!");
}
