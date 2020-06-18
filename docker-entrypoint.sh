#!/bin/sh

APP_URL=${HOST_URL}

cp public/401.bak.html public/401.html
sed -i "s/{HOST_URL}/${APP_URL}/g" public/401.html

echo "Starting application..."

exec java \
    -jar LocalAuth.jar