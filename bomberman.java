length && x >= 0 && x < map7[floor - 1][y].length) {
                if (map7[floor - 1][y][x] == '#') {
                    map7[floor - 1][y][x] = '_';
                    destroyed++;
                }
            }
        }
        
        // ШАГ 5: списываем энергию и выводим результат
        if (destroyed > 0) {
            energy -= 5;
            System.out.println("Бомба взорвалась! Разрушено стен: " + destroyed);
        } else {
            System.out.println("Бомба не разрушила ни одной стены!");
        }
    }

    // ======================= БЛОК 7: ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ =======================
    
    // проверка границ (из оригинального кода)
    static boolean proverka(int ny, int nx) {
        return ny >= 0 && nx >= 0 &&
               ny < map7[floor].length &&
               nx < map7[floor][ny].length;
    }

    // отрисовка карты (из оригинального кода)
    static void printmap() {
        for (int i = 0; i < map7[floor].length; i++) {
            for (int j = 0; j < map7[floor][i].length; j++) {
                System.out.print("[" + map7[floor][i][j] + "]");
            }
            System.out.println();
        }
    }

    // вывод статистики (изменён под новое ТЗ)
    static void stats() {
        System.out.println("Слой: " + floor + " | Y: " + y + " X: " + x + " | Энергия: " + energy);
    }
}
