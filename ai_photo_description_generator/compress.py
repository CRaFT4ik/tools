#!/usr/bin/env python3
"""
Создаёт сжатые копии изображений из указанной папки,
сохраняя структуру подпапок рядом с собой.

Использование:
    python compress.py "D:/Media/Photo archive/2024/Турция 2024"
    python compress.py "D:/Media/Photo archive/2024/Турция 2024" --max-size 800 --quality 50
"""

import argparse
import io
import sys
from pathlib import Path

from PIL import Image

IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".tiff", ".webp"}


def compress_image(src: Path, dst: Path, max_size: int, quality: int) -> None:
    with Image.open(src) as img:
        img = img.convert("RGB") if img.mode in ("RGBA", "P") else img

        # Уменьшаем так, чтобы длинная сторона = max_size
        w, h = img.size
        if max(w, h) > max_size:
            ratio = max_size / max(w, h)
            img = img.resize((int(w * ratio), int(h * ratio)), Image.LANCZOS)

        dst.parent.mkdir(parents=True, exist_ok=True)
        img.save(dst, "JPEG", quality=quality, optimize=True)


def safe_print(text: str) -> None:
    sys.stdout.buffer.write((text + "\n").encode("utf-8", errors="replace"))
    sys.stdout.buffer.flush()


def main() -> None:
    parser = argparse.ArgumentParser(description="Сжатие изображений для анализа ИИ")
    parser.add_argument("source", help="Путь к папке с оригинальными изображениями")
    parser.add_argument("--max-size", type=int, default=640,
                        help="Макс. сторона в пикселях (по умолч. 640)")
    parser.add_argument("--quality", type=int, default=40,
                        help="Качество JPEG 1-100 (по умолч. 40)")
    parser.add_argument("--output-name", type=str, default=None,
                        help="Имя выходной папки (по умолч. = имя исходной)")
    args = parser.parse_args()

    source = Path(args.source).resolve()
    if not source.is_dir():
        safe_print(f"Ошибка: папка не найдена: {source}")
        sys.exit(1)

    folder_name = args.output_name if args.output_name else source.name
    output_root = Path.cwd() / folder_name

    images = [
        f for f in source.rglob("*")
        if f.is_file() and f.suffix.lower() in IMAGE_EXTENSIONS
    ]

    if not images:
        safe_print("Изображения не найдены.")
        sys.exit(0)

    safe_print(f"Найдено {len(images)} изображений")
    safe_print(f"Вывод: {output_root}")
    safe_print(f"Параметры: max_size={args.max_size}px, quality={args.quality}")
    safe_print("")

    for i, src in enumerate(images, 1):
        rel = src.relative_to(source)
        dst = output_root / rel.with_suffix(".jpg")

        try:
            compress_image(src, dst, args.max_size, args.quality)
            src_kb = src.stat().st_size / 1024
            dst_kb = dst.stat().st_size / 1024
            safe_print(f"[{i}/{len(images)}] {rel}  {src_kb:.0f}KB -> {dst_kb:.0f}KB")
        except Exception as e:
            safe_print(f"[{i}/{len(images)}] ОШИБКА {rel}: {e}")

    safe_print(f"\nГотово! Сжатые изображения в: {output_root}")


if __name__ == "__main__":
    main()
