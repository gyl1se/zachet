#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os
import sys
import re
import base64
import getpass
import subprocess
from datetime import datetime


def ensure_packages():
    """Ставит pypdf, requests, cryptography, если их ещё нет."""
    import importlib

    needed = [
        ("pypdf", "pypdf"),
        ("requests", "requests"),
        ("cryptography", "cryptography"),
    ]
    missing = []
    for module_name, pip_name in needed:
        try:
            importlib.import_module(module_name)
        except ImportError:
            missing.append(pip_name)

    if not missing:
        return

    print("Не найдены библиотеки:", ", ".join(missing))
    print("Устанавливаю автоматически...\n")
    cmd = [sys.executable, "-m", "pip", "install", *missing]
    try:
        subprocess.check_call(cmd)
    except subprocess.CalledProcessError:
        print("\nНе удалось установить пакеты. В терминале IDEA выполните:")
        print("  pip install pypdf requests cryptography")
        sys.exit(1)
    print("\nБиблиотеки установлены.\n")


ensure_packages()

import requests
import pypdf
from cryptography.fernet import Fernet, InvalidToken
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC
from cryptography.hazmat.primitives import hashes

# ----------------------------------------------------------------------
# 1. НАСТРОЙКИ
# ----------------------------------------------------------------------
# Зашифрованный ключ хранится ЗДЕСЬ ЖЕ (это не сам API-ключ).
# Заполняется командой: python zalupa2.py --encrypt
ENC_SALT = "3c72332a959258024b75465afc168e31"
ENC_TOKEN = "gAAAAABqvsk0Ji04Rf1RQyO7q9VLOsVUgkhSqiVivknvruw8Nwjlxs4IG-s04rpmRFd5MyEqpnrLptt7UzE3zHWydsRIlfz-rZrzecIh5_K7rflnn9sH9lCwTtTOTcQZLsfe1Z-Lxpzo"
KDF_ITERATIONS = 480_000

PDF_PATH = r"Java_Ex_v3.pdf"

PROMPT = """Реши вариант 15 из файла.
Выведи только код на Java, без пояснений, без маркдауна (без обратных кавычек).
Выведи сразу все файлы с необходимыми названиями.

КРИТИЧЕСКИ ВАЖНО:
1. Для КАЖДОГО файла обязательно укажи строку package (например, package models; или package main;) в зависимости от расположения файла.
2. Перед КАЖДЫМ классом/интерфейсом укажи все необходимые import-ы (например, import java.io.Serializable;).
3. Обязательно выводи код ВСЕХ интерфейсов и вспомогательных классов, которые используются в задании (например, интерфейс Manageable, если он нужен).
4. Не пиши никакого текста между классами. Только код.
5. После каждого действия должна выводиться краткая сводка информации.
6. Добавь пункт с общей сводкой информации.
"""

OUTPUT_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "output")
os.makedirs(OUTPUT_DIR, exist_ok=True)

LOG_FILE = os.path.join(OUTPUT_DIR, "log.txt")
TEXT_FILE = os.path.join(OUTPUT_DIR, "extracted_text.txt")
ANSWER_FILE = os.path.join(OUTPUT_DIR, "answer.txt")
COMBINED_FILE = os.path.join(OUTPUT_DIR, "combined_java.txt")

# ----------------------------------------------------------------------
# 2. ЛОГГЕР
# ----------------------------------------------------------------------
def log_and_print(msg, level="INFO"):
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    line = f"[{timestamp}] [{level}] {msg}"
    print(line)
    with open(LOG_FILE, "a", encoding="utf-8") as f:
        f.write(line + "\n")

def log_info(msg):  log_and_print(msg, "INFO")
def log_error(msg): log_and_print(msg, "ERROR")

# ----------------------------------------------------------------------
# 3. ШИФРОВАНИЕ API-КЛЮЧА
# ----------------------------------------------------------------------
def _fernet_from_password(password: str, salt: bytes) -> Fernet:
    kdf = PBKDF2HMAC(
        algorithm=hashes.SHA256(),
        length=32,
        salt=salt,
        iterations=KDF_ITERATIONS,
    )
    key = base64.urlsafe_b64encode(kdf.derive(password.encode("utf-8")))
    return Fernet(key)


def encrypt_api_key_interactive():
    """Один раз: спрашивает API-ключ и пароль, вписывает шифротекст в этот же файл."""
    print("\n=== Шифрование API-ключа ===")
    print("API-ключ в git НЕ попадёт. В файл запишется только шифротекст.\n")

    api_key = getpass.getpass("Вставьте DeepSeek API-ключ (не отображается): ").strip()
    if len(api_key) < 10:
        print("Ключ слишком короткий.")
        sys.exit(1)
    try:
        api_key.encode("ascii")
    except UnicodeEncodeError:
        print("API-ключ содержит не-ASCII символы.")
        sys.exit(1)

    pw1 = getpass.getpass("Придумайте пароль для расшифровки (НЕ сам API-ключ): ")
    pw2 = getpass.getpass("Повторите пароль: ")
    if not pw1:
        print("Пароль пустой.")
        sys.exit(1)
    if pw1 != pw2:
        print("Пароли не совпадают.")
        sys.exit(1)
    if pw1 == api_key:
        print("Пароль не должен совпадать с API-ключом.")
        sys.exit(1)

    salt = os.urandom(16)
    token = _fernet_from_password(pw1, salt).encrypt(api_key.encode("utf-8"))
    salt_hex = salt.hex()
    token_str = token.decode("ascii")

    here = os.path.abspath(__file__)
    with open(here, encoding="utf-8") as f:
        src = f.read()

    src_new, n1 = re.subn(
        r"^ENC_SALT = \".*\"$",
        f'ENC_SALT = "{salt_hex}"',
        src,
        count=1,
        flags=re.MULTILINE,
    )
    src_new, n2 = re.subn(
        r"^ENC_TOKEN = \".*\"$",
        f'ENC_TOKEN = "{token_str}"',
        src_new,
        count=1,
        flags=re.MULTILINE,
    )
    if n1 != 1 or n2 != 1:
        print("Не удалось вписать шифротекст в zalupa2.py. Не меняйте строки ENC_SALT / ENC_TOKEN.")
        sys.exit(1)

    with open(here, "w", encoding="utf-8") as f:
        f.write(src_new)

    print(f"\nГотово. Шифротекст записан в этот же файл:\n  {here}")
    print("Файл МОЖНО коммитить в открытый GitHub.")
    print("Пароль запомните. На другом ПК вводите его в консоли при запуске.\n")


def load_api_key_from_secret():
    """Расшифровывает ключ паролем из консоли. Шифротекст берётся из ENC_SALT / ENC_TOKEN."""
    if not ENC_SALT or not ENC_TOKEN:
        log_error("Ключ ещё не зашифрован. Один раз запустите: python zalupa2.py --encrypt")
        sys.exit(1)

    try:
        salt = bytes.fromhex(ENC_SALT)
        token = ENC_TOKEN.encode("ascii")
    except ValueError as e:
        log_error(f"Повреждены ENC_SALT / ENC_TOKEN: {e}")
        sys.exit(1)

    password = getpass.getpass("Пароль для API-ключа: ")
    try:
        api_key = _fernet_from_password(password, salt).decrypt(token).decode("utf-8")
    except InvalidToken:
        log_error("Неверный пароль или повреждён шифротекст в файле.")
        sys.exit(1)

    return api_key


def validate_api_key(key):
    try:
        key.encode("ascii")
    except UnicodeEncodeError:
        log_error("API-ключ содержит не-ASCII символы.")
        sys.exit(1)
    if len(key) < 10:
        log_error("API-ключ слишком короткий.")
        sys.exit(1)

# ----------------------------------------------------------------------
# 4. ИЗВЛЕЧЕНИЕ ТЕКСТА ИЗ PDF
# ----------------------------------------------------------------------
def extract_text_from_pdf(pdf_path):
    log_info(f"Извлекаю текст из {pdf_path}")
    if not os.path.exists(pdf_path):
        raise FileNotFoundError(f"Файл не найден: {pdf_path}")
    text_parts = []
    with open(pdf_path, "rb") as f:
        reader = pypdf.PdfReader(f)
        for i, page in enumerate(reader.pages, 1):
            page_text = page.extract_text()
            if page_text:
                text_parts.append(page_text)
    full_text = "\n".join(text_parts)
    if not full_text.strip():
        raise RuntimeError("Не удалось извлечь текст.")
    return full_text

# ----------------------------------------------------------------------
# 5. ЗАПРОС К DEEPSEEK API
# ----------------------------------------------------------------------
def ask_deepseek(prompt, pdf_text, api_key):
    url = "https://api.deepseek.com/chat/completions"
    validate_api_key(api_key)
    headers = {
        "Authorization": f"Bearer {api_key}",
        "Content-Type": "application/json; charset=utf-8"
    }
    full_prompt = f"{prompt}\n\nСодержимое файла:\n{pdf_text}"
    payload = {
        "model": "deepseek-v4-pro",
        "messages": [{"role": "user", "content": full_prompt}]
    }
    log_info("Отправляю запрос к DeepSeek...")
    response = requests.post(url, headers=headers, json=payload, timeout=120)
    log_info(f"Статус: {response.status_code}")
    response.raise_for_status()
    data = response.json()
    if "choices" in data and len(data["choices"]) > 0:
        answer = data["choices"][0]["message"]["content"]
        log_info(f"Получен ответ, {len(answer)} символов")
        return answer
    raise RuntimeError(f"Неожиданный ответ API: {data}")

# ----------------------------------------------------------------------
# 6. ОЧИСТКА ОТ МАРКДАУНА
# ----------------------------------------------------------------------
def clean_java_code(text):
    text = re.sub(r"```(?:java)?\s*(.*?)\s*```", r"\1", text, flags=re.DOTALL)
    text = re.sub(r"`([^`]+)`", r"\1", text)
    return text.strip()

# ----------------------------------------------------------------------
# 7. +++ ПОДСЧЁТ КОНЦА КЛАССА ПО СКОБКАМ +++
# ----------------------------------------------------------------------
def find_class_end(text, start):
    """Находит позицию закрывающей } класса, начиная с { в позиции start."""
    brace = 0
    i = start
    in_str = in_chr = in_lc = in_bc = False
    while i < len(text):
        c = text[i]
        n = text[i + 1] if i + 1 < len(text) else ""
        if in_lc:
            if c == "\n":
                in_lc = False
        elif in_bc:
            if c == "*" and n == "/":
                in_bc = False
                i += 1
        elif in_str:
            if c == "\\":
                i += 1
            elif c == '"':
                in_str = False
        elif in_chr:
            if c == "\\":
                i += 1
            elif c == "'":
                in_chr = False
        else:
            if c == "/" and n == "/":
                in_lc = True
            elif c == "/" and n == "*":
                in_bc = True
            elif c == '"':
                in_str = True
            elif c == "'":
                in_chr = True
            elif c == "{":
                brace += 1
            elif c == "}":
                brace -= 1
                if brace == 0:
                    return i + 1
        i += 1
    return -1

# ----------------------------------------------------------------------
# 8. +++ УМНЫЙ ПАРСЕР С ПОДДЕРЖКОЙ package/import ДЛЯ КАЖДОГО КЛАССА +++
# ----------------------------------------------------------------------
def package_to_dir(package_line):
    if not package_line:
        return ""
    m = re.match(r"package\s+([\w.]+)\s*;", package_line.strip())
    if not m:
        return ""
    return m.group(1).replace(".", os.sep)


def extract_java_blocks(text):
    blocks = []
    matches = list(re.finditer(
        r"^(?:public\s+)?(?:abstract\s+)?(?:final\s+)?(?:class|interface|enum)\s+([A-Za-z_]\w*)[^{]*\{",
        text, re.MULTILINE
    ))
    if not matches:
        return blocks

    top_region = text[:matches[0].start()]
    top_packages = [l.strip() for l in top_region.splitlines()
                    if l.strip().startswith("package ")]
    top_imports = [l.strip() for l in top_region.splitlines()
                   if l.strip().startswith("import ")]

    prev_end = 0
    for idx, m in enumerate(matches):
        start = m.start()
        class_name = m.group(1)
        end = find_class_end(text, start)
        if end == -1:
            log_error(f"Не найден конец класса {class_name}")
            continue

        region = text[prev_end:start]
        region_packages = [l.strip() for l in region.splitlines()
                           if l.strip().startswith("package ")]
        region_imports = [l.strip() for l in region.splitlines()
                          if l.strip().startswith("import ")]

        packages = region_packages if region_packages else top_packages
        imports = list(dict.fromkeys(region_imports + top_imports))

        class_code = text[start:end].rstrip()

        parts = []
        for p in packages:
            parts.append(p)
        if packages and imports:
            parts.append("")
        for imp in imports:
            parts.append(imp)
        if packages or imports:
            parts.append("")
        parts.append(class_code)
        file_content = "\n".join(parts).strip() + "\n"

        if class_name.endswith(".java"):
            class_name = class_name[:-5]
        safe_name = re.sub(r'[\\/*?:"<>|]', "_", class_name)

        pkg_dir = package_to_dir(packages[0]) if packages else ""

        blocks.append((pkg_dir, safe_name, file_content))
        log_info(f"  Класс {safe_name}: package={pkg_dir or '(default)'}, "
                 f"imports={len(imports)}")
        prev_end = end

    return blocks

# ----------------------------------------------------------------------
# 9. СОХРАНЕНИЕ
# ----------------------------------------------------------------------
def save_individual_java_files(blocks, output_dir):
    saved = []
    for pkg_dir, name, code in blocks:
        if pkg_dir:
            target_dir = os.path.join(output_dir, pkg_dir)
            os.makedirs(target_dir, exist_ok=True)
        else:
            target_dir = output_dir

        path = os.path.join(target_dir, f"{name}.java")
        with open(path, "w", encoding="utf-8") as f:
            f.write(code)
        log_info(f"  Сохранён {path}")
        saved.append(path)
    return saved


def save_combined_java(blocks, output_path):
    with open(output_path, "w", encoding="utf-8") as f:
        for pkg_dir, name, code in blocks:
            rel = os.path.join(pkg_dir, f"{name}.java") if pkg_dir else f"{name}.java"
            f.write(f"// === {rel} ===\n")
            f.write(code)
            f.write("\n")

# ----------------------------------------------------------------------
# 10. ОСНОВНАЯ ФУНКЦИЯ
# ----------------------------------------------------------------------
def main():
    if "--encrypt" in sys.argv or "--setup-key" in sys.argv:
        encrypt_api_key_interactive()
        return

    with open(LOG_FILE, "w", encoding="utf-8") as f:
        f.write(f"=== Запуск {datetime.now().strftime('%Y-%m-%d %H:%M:%S')} ===\n")

    print("\n" + "=" * 60)
    print("ЗАПУСК С ПОДДЕРЖКОЙ package / import + авто-папки")
    print("=" * 60 + "\n")

    try:
        api_key = load_api_key_from_secret()

        pdf_text = extract_text_from_pdf(PDF_PATH)
        with open(TEXT_FILE, "w", encoding="utf-8") as f:
            f.write(pdf_text)

        answer = ask_deepseek(PROMPT, pdf_text, api_key)
        answer_cleaned = clean_java_code(answer)
        with open(ANSWER_FILE, "w", encoding="utf-8") as f:
            f.write(answer_cleaned)

        blocks = extract_java_blocks(answer_cleaned)
        if not blocks:
            log_error("Классы не найдены!")
            sys.exit(1)

        save_combined_java(blocks, COMBINED_FILE)
        saved = save_individual_java_files(blocks, OUTPUT_DIR)

        print("\n" + "=" * 60)
        print(f"ГОТОВО! Файлов: {len(saved)}")
        for p in saved:
            rel = os.path.relpath(p, OUTPUT_DIR)
            print(f"   • {rel}")
        print("=" * 60 + "\n")

    except Exception as e:
        log_error(f"Критическая ошибка: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main()
