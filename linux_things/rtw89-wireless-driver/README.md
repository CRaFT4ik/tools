## Wireless driver for 8852AE network card

Driver repo: https://github.com/lwfinger/rtw89 \
The driver will be automatically updated after kernel update and reboot (it might take some time).

**You must manually run `driver_install.sh` before installing the systemd service!**
Make sure all required build tools are installed.

**Installing:**

0. All files should be placed at `/home/rtw89`.
1. Run:
```sh
chmod +x driver_install.sh && chmod +x autoupdate/autoupdate.sh
```
3. Clone driver repository and rename folder to `repo`.
4. To enable driver automatically after each kernel update run: 
```sh
mv wireless-driver-update.service /etc/systemd/system/
systemctl enable wireless-driver-update.service
```
4. Make first run with:
```sh
systemctl start wireless-driver-update.service
```
and see output with:
```sh
systemctl status wireless-driver-update.service
```