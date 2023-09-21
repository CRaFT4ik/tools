#!/bin/bash

service_name=letencrypt-renewal
abs_path=$(realpath .)
script_path="$abs_path/run_update_certs.sh"

[[ -f $script_path ]] || (echo "Script $script_path not found" && exit -1)

sudo echo "
[Unit]
Description=Automatically reniew letencrypt certificates
After=network.target

[Service]
User=root
Group=root
WorkingDirectory=$abs_path
ExecStart=/bin/bash $script_path

[Install]
WantedBy=multi-user.target
" > /etc/systemd/system/$service_name.service

sudo echo "
[Unit]
Description=Periodically work for '$service_name.service' service

[Timer]
OnBootSec=120s
OnUnitActiveSec=1w

[Install]
WantedBy=timers.target
" > /etc/systemd/system/$service_name.timer

sudo systemctl enable $service_name.timer
sudo systemctl daemon-reload
sudo systemctl start $service_name.timer

printf "\nOk. Run 'systemctl status $service_name.timer' or 'systemctl status $service_name.service' for details\n"
printf "\nYou may also want to disable default certbot autorenewal service:
    systemctl disable certbot.timer
    systemctl stop certbot.timer\n\n"