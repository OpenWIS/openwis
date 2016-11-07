#!/bin/bash

cd <%= @ssoadmintools_dir %>/openam/bin

./ssoadm delete-svc -u amAdmin -f passwd -s iPlanetAMUserService

./ssoadm create-svc -u amAdmin -f passwd --xmlfile amUser.xml

./ssoadm update-datastore -u amAdmin -f passwd -e / -m OpenDJ -D attrs.properties
