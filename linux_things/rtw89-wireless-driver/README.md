## Wi-Fi драйвер для 8852AE

Скрипты для сборки и установки драйвера [rtw89](https://github.com/lwfinger/rtw89). Включают systemd-сервис, который автоматически пересобирает драйвер после обновления ядра.

**Перед установкой сервиса нужно вручную запустить `driver_install.sh`!**
Убедитесь, что установлены необходимые инструменты сборки.

### Установка

0. Все файлы должны лежать в `/home/rtw89`.
1. Выдать права:
```sh
chmod +x driver_install.sh && chmod +x autoupdate/autoupdate.sh
```
2. Склонировать репозиторий драйвера и переименовать папку в `repo`.
3. Включить автообновление после каждого обновления ядра:
```sh
mv wireless-driver-update.service /etc/systemd/system/
systemctl enable wireless-driver-update.service
```
4. Первый запуск:
```sh
systemctl start wireless-driver-update.service
systemctl status wireless-driver-update.service
```
