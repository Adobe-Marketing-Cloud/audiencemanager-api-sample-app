#!/bin/bash

read -p 'Audience Manager Access Token:' access_token

echo 'Creating Data Source'
echo '===================='
curl -v -H "Authorization: Bearer $access_token" -H 'Accept: application/json' -H 'Content-Type: application/json' -X POST --data '{"name":"blog","uniqueTraitIntegrationCodes":false,"uniqueSegmentIntegrationCodes":false}' https://api.demdex.com/v1/datasources/
echo

echo
echo 'Creating Blog Post Reader Folder'
echo '================================'
curl -v -H "Authorization: Bearer $access_token" -H 'Accept: application/json' -H 'Content-Type: application/json' -X POST --data '{"name":"Blog Post Reader","parentFolderId":0}' https://api.demdex.com/v1/folders/traits/
echo

echo
echo 'Creating Blog Post Commenter Folder'
echo '==================================='
curl -v -H "Authorization: Bearer $access_token" -H 'Accept: application/json' -H 'Content-Type: application/json' -X POST --data '{"name":"Blog Post Commenter","parentFolderId":0}' https://api.demdex.com/v1/folders/traits/
echo

echo
echo 'Creating Blog Post Tags Folder'
echo '=============================='
curl -v -H "Authorization: Bearer $access_token" -H 'Accept: application/json' -H 'Content-Type: application/json' -X POST --data '{"name":"Blog Post Tags","parentFolderId":0}' https://api.demdex.com/v1/folders/traits/
echo
