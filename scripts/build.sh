#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
mkdir -p build/classes dist/lib
find build/classes -type f -delete
find src -name '*.java' -print | sort > build/sources.txt
javac --release 17 -encoding UTF-8 -cp 'lib/*' -d build/classes @build/sources.txt
cp -R src/resources build/classes/
cp lib/jfreechart-1.5.4.jar dist/lib/
printf 'Manifest-Version: 1.0\nMain-Class: vn.edu.ptit.oop.nhom04.Main\nClass-Path: lib/jfreechart-1.5.4.jar\n\n' > build/MANIFEST.MF
jar --create --file dist/MiniBank.jar --manifest build/MANIFEST.MF -C build/classes .
printf 'Đã tạo dist/MiniBank.jar. Chạy: java -jar dist/MiniBank.jar\nGiữ thư mục dist/lib cạnh JAR khi sao chép sang máy khác.\n'
