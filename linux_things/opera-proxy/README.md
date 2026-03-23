## Opera Proxy

Systemd-сервис для [opera-proxy](https://github.com/Alexey71/opera-proxy/) — локальный HTTP-прокси на `127.0.0.1:18080` через VPN-инфраструктуру Opera.

Автоматический перезапуск при падении. По умолчанию выход через EU.

### Установка

1. Установить opera-proxy (например, `snap install opera-proxy`)
2. Скопировать сервис:
```bash
cp opera-proxy.service /etc/systemd/system/
systemctl enable --now opera-proxy.service
```
