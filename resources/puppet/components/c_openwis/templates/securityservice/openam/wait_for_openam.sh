#!/bin/bash -x

while [ -z "$(curl -L -s 'http://<%= @auth_service_host %>:8080/openam')" ]
do
	sleep 6
done
