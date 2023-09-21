#!/bin/bash

cert_name="er-log.ru"
cert_path="/etc/letsencrypt/live/$cert_name"
conf_file="./.secrets/cloudflare.ini"

# Install Certbot
sudo apt update
sudo apt -y install certbot

# Install DNS CloudFlare plugin
sudo apt -y install python3-certbot-dns-cloudflare

# This directory may not exist yet
sudo mkdir -p $cert_path
sudo mkdir -p $(dirname "$conf_file")

# Create file with the Cloudflare API token
[[ -f $conf_file ]] \
|| (echo "dns_cloudflare_api_token = AN_API_TOKEN_HERE" > "$conf_file" \
    && printf "\nWARN: Config file was not exist but stub was created at '$(realpath $conf_file)', exiting now!\n" \
    && exit -1)

# Secure that file (otherwise certbot yells at you)
sudo chmod 0700 $(dirname "$conf_file")
sudo chmod 0400 "$conf_file"

# Create a certificate
printf "\nRunning certbot...\n\n"
sudo certbot certonly -d er-log.ru,*.er-log.ru,*.tg.er-log.ru \
    --cert-path         "$cert_path/cert.pem" \
    --key-path          "$cert_path/privkey.pem" \
    --chain-path        "$cert_path/chain.pem" \
    --fullchain-path    "$cert_path/fullchain.pem" \
    --dns-cloudflare --dns-cloudflare-credentials "$conf_file" \
    --preferred-challenges dns-01 \
    --non-interactive --agree-tos --expand \
    --post-hook "sudo docker container restart service_nginx" \
    --email eldar.tim@gmail.com
