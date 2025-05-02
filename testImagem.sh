#!/bin/bash
name=webpublico_test
image=repository.webpublico.com.br/webpublico-rb:$TAG

echo "Testando Imagem $image"

docker stop $name||true
docker rm -v $name ||true
docker pull $name
docker run --name $name \
-e DB_SID=wpub2 -e DB_USER=outraatualizacaosemanal \
 -d $image

echo "Aguarda 5 minutos"
sleep 300;

ip=$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' $name)


status_code=$(curl -s -o /dev/null -I -w '%{http_code}' --connect-timeout 10 -X GET http://${ip}:8080/spring/aplicacao/status/)

if [[ "$status_code" != "200" ]]; then
    echo "Container not respond endpoint IMAGE: $TAG on /spring/aplicacao/status/. Aborting with failure error."
    exit 1;
else
  echo "Everything seems ok."
fi
#Limpando teste
echo "Shutdown e clean test."
docker stop $name||true
docker rm -v $name ||true
