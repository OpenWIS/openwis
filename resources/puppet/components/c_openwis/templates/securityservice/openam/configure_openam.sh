#!/bin/bash

export COOKIES=none
export EXPECTED_RESPONSE=""
export STEP_NUM=0
export STEP_NAME=""

function do_curl(){
	STEP_NUM=$((STEP_NUM + 1))
	RESPONSE_FILE=$(printf "%02d" ${STEP_NUM})_${STEP_NAME}_response.txt
	echo "" > ${RESPONSE_FILE}

	case ${COOKIES} in
		read)
			COOKIES_CMD="--cookie cookies.txt"
			;;
		write)
			COOKIES_CMD="--cookie-jar cookies.txt"
			;;
		write)
			COOKIES_CMD=""
			;;
	esac

	echo $1
curl -s -o ${RESPONSE_FILE} -L ${COOKIES_CMD} http://<%= @auth_service_host %>:8080/openam/$@

	RESPONSE="$(cat ${RESPONSE_FILE})"
	ERROR_MSG=""

	case ${EXPECTED_RESPONSE} in
		HTML)
			if [ -z "$(echo ${RESPONSE} | egrep '<div|<html')" ]
			then
				ERROR_MSG="Expected HTML but got '${RESPONSE}'"
			fi
			;;
		none)
			if [ -n "${RESPONSE}" ]
			then
				ERROR_MSG="Expected no response but got '${RESPONSE}'"
			fi
			;;
		*)
			if [ "${RESPONSE}" != "${EXPECTED_RESPONSE}" ]
			then
				ERROR_MSG="Expected '${EXPECTED_RESPONSE}' but got '${RESPONSE}'"
			fi
			;;
	esac

	if [ -n "${ERROR_MSG}" ]
	then
			echo "Error Running '$@':" >&2
			echo "${ERROR_MSG}" >&2
			exit 1
	fi
}

STEP_NAME=start
EXPECTED_RESPONSE=HTML
do_curl

STEP_NAME=resetSessionAttributes
EXPECTED_RESPONSE=none
do_curl 'config/options.htm?actionLink=resetSessionAttributes'

COOKIES=write
STEP_NAME=checkPasswords
EXPECTED_RESPONSE="true"
do_curl 'config/wizard/step1.htm?actionLink=checkPasswords' \
		--data 'confirm=<%= @openam_admin_password %>&password=<%= @openam_admin_password %>&otherPassword=x&type=admin'

COOKIES=read
STEP_NAME=step2
EXPECTED_RESPONSE=HTML
do_curl 'config/wizard/step2.htm?locale='

STEP_NAME=validateInput_serverURL
EXPECTED_RESPONSE="true"
do_curl 'config/wizard/step2.htm?actionLink=validateInput&key=serverURL&value=<%= @openam_server_url %>'

STEP_NAME=validateCookieDomain
EXPECTED_RESPONSE="true"
do_curl 'config/wizard/step2.htm?actionLink=validateCookieDomain&domain=<%= @openam_cookie_domain %>&serverurl=http://<%= @openam_server_url %>'

STEP_NAME=validateInput_platformLocale
EXPECTED_RESPONSE="true"
do_curl 'config/wizard/step2.htm?actionLink=validateInput&key=platformLocale&value=<%= @openam_locale %>'

STEP_NAME=validateConfigDir
EXPECTED_RESPONSE="true"
do_curl 'config/wizard/step2.htm?actionLink=validateConfigDir&dir=<%= @openam_dir %>'

STEP_NAME=step3
EXPECTED_RESPONSE=HTML
do_curl 'config/wizard/step3.htm?locale='

STEP_NAME=setConfigType
EXPECTED_RESPONSE=HTML
do_curl 'config/wizard/step3.htm?actionLink=setConfigType&type=embedded'

STEP_NAME=validateLocalPort
EXPECTED_RESPONSE="ok"
do_curl 'config/wizard/step3.htm?actionLink=validateLocalPort&port=50389'

STEP_NAME=validateLocalAdminPort
EXPECTED_RESPONSE="ok"
do_curl 'config/wizard/step3.htm?actionLink=validateLocalAdminPort&port=5444'

STEP_NAME=validateLocalJmxPort
EXPECTED_RESPONSE="ok"
do_curl 'config/wizard/step3.htm?actionLink=validateLocalJmxPort&port=2689'

STEP_NAME=step4
EXPECTED_RESPONSE=HTML
do_curl 'config/wizard/step4.htm?locale='

STEP_NAME=resetUMEmbedded
EXPECTED_RESPONSE=none
do_curl 'config/wizard/step4.htm?actionLink=resetUMEmbedded'

STEP_NAME=setStoreType
EXPECTED_RESPONSE="ok"
do_curl 'config/wizard/step4.htm?actionLink=setStoreType&type=LDAPv3ForOpenDS'

STEP_NAME=setHost
EXPECTED_RESPONSE="ok"
do_curl 'config/wizard/step4.htm?actionLink=setHost&host=<%= @opendj_host %>'

STEP_NAME=setPort
EXPECTED_RESPONSE="ok"
do_curl 'config/wizard/step4.htm?actionLink=setPort&port=1389'

STEP_NAME=setRootSuffix
EXPECTED_RESPONSE="ok"
do_curl 'config/wizard/step4.htm?actionLink=setRootSuffix&rootsuffix=dc%3Dopensso%2Cdc%3Djava%2Cdc%3Dnet'

STEP_NAME=setPassword
EXPECTED_RESPONSE="ok"
do_curl 'config/wizard/step4.htm?actionLink=setPassword' \
		--data 'password=<%= @opendj_root_password %>'

STEP_NAME=validateUMHost
EXPECTED_RESPONSE="ok"
do_curl 'config/wizard/step4.htm?actionLink=validateUMHost'

STEP_NAME=step5
EXPECTED_RESPONSE=HTML
do_curl 'config/wizard/step5.htm?locale='

STEP_NAME=step6
EXPECTED_RESPONSE=HTML
do_curl 'config/wizard/step6.htm?locale='

STEP_NAME=checkPasswords
EXPECTED_RESPONSE="true"
do_curl 'config/wizard/step6.htm?actionLink=checkPasswords' \
		--data 'confirm=<%= @openam_url_password %>&password=<%= @openam_url_password %>&otherPassword=<%= @openam_admin_password %>&type=agent'

STEP_NAME=step7
EXPECTED_RESPONSE=HTML
do_curl 'config/wizard/step7.htm?locale='

STEP_NAME=createConfig
EXPECTED_RESPONSE=HTML
do_curl 'config/wizard/wizard.htm?actionLink=createConfig'
