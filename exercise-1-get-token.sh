#!/bin/bash

read -p 'Audience Manager OAuth2 Client ID:' client_id
read -p 'Audience Manager OAuth2 Client Secret:' client_secret
read -p 'Audience Manager Username:' user
read -p 'Audience Manager Password:' password

curl -v --user $client_id:$client_secret -X POST --data-urlencode "grant_type=password" --data-urlencode "username=$user" --data-urlencode "password=$password" https://api.demdex.com/oauth/token
#CURL doesn't output a final newline -- do it here to keep console clean
echo 
