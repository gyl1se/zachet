#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os
import sys
import re
import requests
import pypdf
from datetime import datetime

# ----------------------------------------------------------------------
# 1. НАСТРОЙКИ (ЗАМЕНИТЕ НА СВОИ)
# ----------------------------------------------------------------------
API_KEY = "sk-4ece6708f9d243d9b8795264d870d0a8"          # <- замените на свой ключ
PDF_PATH = r"E:\Java\test\src\Java_Ex_v1.pdf"
PROMPT = """Реши вариант 15 из файла.
Выведи только код на Java, без пояснений, без маркдауна (без обратных кавычек).
Каждый класс должен быть в отдельном блоке и рассписан полный путь до файла и название файла который нужно создать.
Используй правильный синтаксис Java.
"""

# Папка для выходных файлов
OUTPUT_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "output")
os.makedirs(OUTPUT_DIR, exist_ok=True)

# Имена файлов
LOG_FILE = os.path.join(OUTPUT_DIR, "log.txt")
TEXT_FILE = os.path.join(OUTPUT_DIR, "extracted_text.txt")
ANSWER_FILE = os.path.join(OUTPUT_DIR, "answer.txt")
COMBINED_FILE = os.path.join(OUTPUT_DIR, "combined_java.txt")  # один файл с подписями

# ----------------------------------------------------------------------
# 2. ЛОГГЕР
# ----------------------------------------------------------------------
def log_and_print(msg, level="INFO"):
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    line = f"[{timestamp}] [{level}] {msg}"
    print(line)
    with open(LOG_FILE, "a", encoding="utf-8") as f:
        f.write(line + "\n")

def log_info(msg):
    log_and_print(msg, "INFO")

def log_error(msg):
    log_and_print(msg, "ERROR")

# ----------------------------------------------------------------------
# 3. ПРОВЕРКА API-КЛЮЧА
# ----------------------------------------------------------------------
def validate_api_key(key):
    try:
        key.encode('ascii')
    except UnicodeEncodeError:
        log_error("API-ключ содержит не-ASCII символы (возможно, русские буквы).")
        log_error(f"Первые 10 символов ключа: {key[:10]}")
        log_error("Пожалуйста, замените на корректный ключ из панели DeepSeek.")
        sys.exit(1)
    if len(key) < 10:
        log_error("API-ключ слишком короткий. Проверьте, что вы вставили полный ключ.")
        sys.exit(1)
    log_info("API-ключ корректен (ASCII, длина > 10).")

# ----------------------------------------------------------------------
# 4. ИЗВЛЕЧЕНИЕ ТЕКСТА ИЗ PDF
# ----------------------------------------------------------------------
def extract_text_from_pdf(pdf_path):
    log_info(f"Начинаю извлечение текста из {pdf_path}")
    if not os.path.exists(pdf_path):
        raise FileNotFoundError(f"Файл не найден: {pdf_path}")
    log_info("Файл существует, проверяю размер...")
    if os.path.getsize(pdf_path) == 0:
        raise ValueError("Файл пустой")
    log_info(f"Размер файла: {os.path.getsize(pdf_path)} байт")

    text_parts = []
    with open(pdf_path, 'rb') as f:
        reader = pypdf.PdfReader(f)
        log_info(f"PDF содержит {len(reader.pages)} страниц")
        for i, page in enumerate(reader.pages, 1):
            log_info(f"Обрабатываю страницу {i}...")
            page_text = page.extract_text()
            if page_text:
                text_parts.append(page_text)
                log_info(f"  Страница {i}: извлечено {len(page_text)} символов")
            else:
                log_info(f"  Страница {i}: текст отсутствует")
    full_text = "\n".join(text_parts)
    log_info(f"Всего извлечено {len(full_text)} символов")
    if not full_text.strip():
        raise RuntimeError("Не удалось извлечь текст (PDF может содержать только изображения).")
    return full_text

# ----------------------------------------------------------------------
# 5. ЗАПРОС К DEEPSEEK API
# ----------------------------------------------------------------------
def ask_deepseek(prompt, pdf_text):
    log_info("Подготавливаю запрос к DeepSeek API...")
    url = "https://api.deepseek.com/chat/completions"

    validate_api_key(API_KEY)

    headers = {
        "Authorization": f"Bearer {API_KEY}",
        "Content-Type": "application/json; charset=utf-8"
    }

    full_prompt = f"{prompt}\n\nСодержимое файла:\n{pdf_text}"
    log_info(f"Длина промпта: {len(full_prompt)} символов")

    payload = {
        "model": "deepseek-v4-pro",
        "messages": [
            {"role": "user", "content": full_prompt}
        ]
    }

    log_info("Отправляю POST-запрос к API...")
    try:
        response = requests.post(url, headers=headers, json=payload, timeout=120)
        log_info(f"Получен ответ, статус-код: {response.status_code}")
        response.raise_for_status()
    except requests.exceptions.RequestException as e:
        log_error(f"Ошибка HTTP-запроса: {e}")
        log_error(f"Заголовки: {headers}")
        log_error(f"Промпт (первые 200 символов): {full_prompt[:200]}...")
        raise

    data = response.json()
    if "choices" in data and len(data["choices"]) > 0:
        answer = data["choices"][0]["message"]["content"]
        log_info(f"Ответ получен, длина: {len(answer)} символов")
        return answer
    else:
        log_error(f"Неожиданный ответ API: {data}")
        raise RuntimeError(f"Неожиданный ответ API: {data}")

# ----------------------------------------------------------------------
# 6. ОЧИСТКА JAVA-КОДА ОТ МАРКДАУНА И ЛИШНИХ СИМВОЛОВ
# ----------------------------------------------------------------------
def clean_java_code(text):
    # Удаляем блоки ```java ... ``` или ``` ... ```
    text = re.sub(r'```(?:java)?\s*(.*?)\s*```', r'\1', text, flags=re.DOTALL)
    # Удаляем одиночные обратные кавычки
    text = re.sub(r'`([^`]+)`', r'\1', text)

    # Оставляем только строки, начиная с объявления класса или import/package
    lines = text.splitlines()
    code_lines = []
    inside_class = False
    for line in lines:
        stripped = line.strip()
        # Если строка начинается с объявления класса/интерфейса/enum
        if re.match(r'^(public\s+)?(abstract\s+)?(final\s+)?(class|interface|enum)\s+', stripped):
            inside_class = True
        # Если внутри класса, сохраняем все строки
        if inside_class:
            code_lines.append(line)
        # Если видим import или package до начала класса, тоже сохраняем
        elif re.match(r'^(import|package)\s+', stripped):
            code_lines.append(line)
        # Иначе пропускаем (пояснения)
    if not code_lines:
        return text
    return '\n'.join(code_lines)

# ----------------------------------------------------------------------
# 7. СОХРАНЕНИЕ В ОДИН ФАЙЛ С ПОДПИСЯМИ ИМЁН ФАЙЛОВ
# ----------------------------------------------------------------------
def save_combined_java(text, output_path):
    """
    Находит все классы/интерфейсы/enum в тексте и сохраняет их в один файл,
    предваряя каждый блок комментарием с именем файла (например, // === MyClass.java ===).
    """
    log_info(f"Собираю все классы в один файл: {output_path}")
    # Ищем объявления классов/интерфейсов/enum
    pattern = re.compile(
        r'(?P<header>(?:public\s+)?(?:abstract\s+)?(?:final\s+)?(?:class|interface|enum)\s+(?P<name>[A-Za-z_]\w*)\s*(?:<[^>]*>)?\s*(?:extends\s+\w+\s*)?(?:implements\s+[^{]+)?\s*\{)',
        re.MULTILINE
    )

    matches = list(pattern.finditer(text))
    if not matches:
        log_info("Классы не найдены, сохраняю весь текст как один блок без имени файла.")
        with open(output_path, "w", encoding="utf-8") as f:
            f.write(text)
        return

    parts = []
    # Идём по найденным заголовкам
    for i, match in enumerate(matches):
        start = match.start()
        end = matches[i+1].start() if i+1 < len(matches) else len(text)
        class_code = text[start:end].strip()
        class_name = match.group("name")
        # Если в имени есть .java – убираем для чистоты
        if class_name.endswith(".java"):
            class_name = class_name[:-5]
        # Заменяем недопустимые символы в имени файла (на всякий случай)
        safe_name = re.sub(r'[\\/*?:"<>|]', "_", class_name)
        parts.append((safe_name, class_code))
        log_info(f"  Найден класс {safe_name}, длина кода {len(class_code)} символов")

    # Формируем содержимое одного файла
    combined_lines = []
    for name, code in parts:
        combined_lines.append(f"// === {name}.java ===")
        combined_lines.append(code)
        combined_lines.append("")  # пустая строка для разделения

    # Если есть текст до первого класса – сохраняем как преамбулу
    if matches[0].start() > 0:
        preamble = text[:matches[0].start()].strip()
        if preamble:
            combined_lines.insert(0, "// === preamble (текст до первого класса) ===")
            combined_lines.insert(1, preamble)
            combined_lines.insert(2, "")

    with open(output_path, "w", encoding="utf-8") as f:
        f.write("\n".join(combined_lines))
    log_info(f"Единый файл сохранён: {output_path}")

# ----------------------------------------------------------------------
# 8. ОСНОВНАЯ ФУНКЦИЯ
# ----------------------------------------------------------------------
def main():
    with open(LOG_FILE, "w", encoding="utf-8") as f:
        f.write(f"=== Запуск {datetime.now().strftime('%Y-%m-%d %H:%M:%S')} ===\n")

    print("\n" + "="*60)
    print("🚀 ЗАПУСК СКРИПТА (ВСЁ В ОДИН ФАЙЛ С ПОДПИСЯМИ)")
    print("="*60 + "\n")

    log_info(f"PDF-файл: {PDF_PATH}")
    log_info(f"Выходная папка: {OUTPUT_DIR}")

    try:
        # Шаг 1: извлечение текста
        log_info("Шаг 1/3: Извлечение текста из PDF")
        pdf_text = extract_text_from_pdf(PDF_PATH)
        with open(TEXT_FILE, "w", encoding="utf-8") as f:
            f.write(pdf_text)
        log_info(f"Текст сохранён в {TEXT_FILE}")

        # Шаг 2: запрос к DeepSeek
        log_info("Шаг 2/3: Отправка запроса к DeepSeek API")
        answer = ask_deepseek(PROMPT, pdf_text)

        # Очистка
        answer_cleaned = clean_java_code(answer)

        # Сохраняем очищенный ответ целиком (на всякий случай)
        with open(ANSWER_FILE, "w", encoding="utf-8") as f:
            f.write(answer_cleaned)
        log_info(f"Очищенный ответ сохранён в {ANSWER_FILE}")

        # Шаг 3: сохранение в один файл с подписями
        log_info("Шаг 3/3: Формирование единого файла с подписями")
        save_combined_java(answer_cleaned, COMBINED_FILE)

        log_info("✅ Скрипт успешно завершён!")
        print("\n" + "="*60)
        print(f"✅ ВСЕ ГОТОВО! Результаты в папке: {OUTPUT_DIR}")
        print(f"   - extracted_text.txt – текст из PDF (для справки)")
        print(f"   - answer.txt – очищенный ответ модели (весь код)")
        print(f"   - combined_java.txt – ЕДИНЫЙ ФАЙЛ с подписями имён файлов")
        print(f"   - log.txt – подробный лог")
        print("="*60 + "\n")
        
    except Exception as e:
        log_error(f"❌ Критическая ошибка: {e}")
        print("\n" + "="*60)
        print(f"❌ ОШИБКА! Смотрите лог: {LOG_FILE}")
        print("="*60 + "\n")
        sys.exit(1)

if __name__ == "__main__":
    main()