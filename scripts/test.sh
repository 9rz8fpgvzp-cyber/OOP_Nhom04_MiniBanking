#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
./scripts/build.sh
mkdir -p build/tests
find tests -name '*.java' -print | sort > build/test-sources.txt
test_cp_separator=':'
case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) test_cp_separator=';' ;; esac
test_main_cp="build/classes${test_cp_separator}lib/*"
test_full_cp="build/tests${test_cp_separator}${test_main_cp}"
javac --release 17 -encoding UTF-8 -cp "$test_main_cp" -d build/tests @build/test-sources.txt
for test_class in \
  repository.AccountRepositoryTest model.FixedTermAccountTest \
  service.PinServiceTest service.BankIntegrationTest util.DemoDataFactoryTest; do
  java -Djava.awt.headless=true -cp "$test_full_cp" "vn.edu.ptit.oop.nhom04.$test_class"
done
java -Djava.awt.headless=true -cp "$test_main_cp" vn.edu.ptit.oop.nhom04.test.BankV2Test
if [ "${1:-}" = '--gui' ]; then
  java -cp "$test_full_cp" vn.edu.ptit.oop.nhom04.ui.dialogs.LoginDialogTest
  java -cp "$test_full_cp" vn.edu.ptit.oop.nhom04.ui.BankControllerTest
  printf 'Premain-Class: vn.edu.ptit.oop.nhom04.ui.JarLaunchSmoke\n\n' > build/agent.MF
  jar --create --file build/jar-smoke-agent.jar --manifest build/agent.MF -C build/tests vn/edu/ptit/oop/nhom04/ui/JarLaunchSmoke.class
  test_project_root="$PWD"
  test_package_dir="$(mktemp -d)"
  trap 'rm -rf "$test_package_dir"' EXIT
  cp -R dist "$test_package_dir/app"
  (
    cd "$test_package_dir"
    java -javaagent:"$test_project_root/build/jar-smoke-agent.jar" -jar app/MiniBank.jar
  )
fi
