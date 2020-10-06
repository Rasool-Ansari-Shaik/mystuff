#!/bin/bash
##
# @Description: Steps to pass multiple parameters in shell script
# Take single argument
##

function show_usage (){
    printf "Usage: $0 [options [parameters]]\n"
    printf "\n"
    printf "Options:\n"
    printf " -c|--create, Create Event Stream\n"
    printf " -n|--name [event_stream_name], Provide Event Stream Name\n"
	printf " -p|--plan [plan_type], Provide Plan type\n"
	printf " -r|--region [region], Provide Region\n"
	printf " -a|--apikey-name [api-key-name], Provide API Key Name\n"
	printf " -o|--role-name [role-name], Provide Role Name\n"
    printf " -h|--help, Print help\n"
	printf " -t|--topic-id [topic_id], Provide Topic Id\n"

#return 0
exit 1
}

function logout_ibmcloud() {
	echo "Logout from IBMCloud if already logged in"
	ibmcloud logout
}

function login_ibmcloud() {
	# Logout from IBMCloud if already logged in
	logout_ibmcloud
	
	echo "Login to IBM Cloud..."
	ibmcloud login --apikey {api-key}
	
	echo "Target Resource group, Organization and Space"
	ibmcloud target -g Default -o rashaik4@in.ibm.com -s dev
}

function create_eventstream() {
	echo "Started Creating Event Stream"
	
	# Login to IBMCloud CLI
	login_ibmcloud

	# Validate Plan, Region, Role name and check event_stream_name already exist
	validate
	local IS_VALID=$?
	echo "is valid: ${IS_VALID}"
	check_es_exist
	local ES_EXIST=$?
	echo "es exist: ${ES_EXIST}"
	
	if [ "${IS_VALID}" -eq 0 ] && [ "${ES_EXIST}" -eq 31 ]
	then
	
		# To create an instance from the CLI
		echo "Creating Event Stream..."
		ibmcloud resource service-instance-create ${EVENT_STREAM_NAME} messagehub $PLAN $REGION
		
		if [ $? -eq 0 ]
		then
			echo "Event Stream Service instance created successfully"
		else
			echo "Event Stream Service instance not created"
			exit 5
		fi
	
		# Create a service API key for this instance
		echo "Creating Event Stream's API Key..."
		ibmcloud resource service-key-create ${KEY_NAME} ${ROLE_NAME} --instance-name ${EVENT_STREAM_NAME}

		if [ $? -eq 0 ]
		then
			echo "Event Stream API Key created successfully"
		else
			echo "Event Stream API Key not created"
			exit 6
		fi
	
	fi
	
	# Logout from IBMCloud
	logout_ibmcloud
	
	exit 0
}

function delete_eventstream() {
	echo "Started Deleting Event Stream"

	# Login to IBMCloud CLI
	login_ibmcloud

	# Validate Plan, Region, Role name and check event_stream_name already exist
	validate
	local IS_VALID=$?
	echo "is valid: ${IS_VALID}"
	check_es_exist
	local ES_EXIST=$?
	echo "es exist: ${ES_EXIST}"
	
	if [ "${IS_VALID}" -eq 0 ] && [ "${ES_EXIST}" -eq 0 ]
	then
	
		# To delete an instance using CLI
		echo "Deleting Event Stream..."
		ibmcloud resource service-instance-delete ${EVENT_STREAM_NAME} --force --recursive
		
		if [ $? -eq 0 ]
		then
			echo "Event Stream Service instance deleted successfully"
		else
			echo "Event Stream Service instance not deleted"
			exit 5
		fi
	
	fi
	
	# Logout from IBMCloud
	logout_ibmcloud
	
	exit 0

}

function validate() {
	echo "Started validating input arguments"
#	PLAN_LIST=("enterprise" "standard" "free")
	
	# Validate plan
	echo -n "Validating Plan..."
	PLAN_LC=${PLAN,,}
	# Find the input plan in the list
	FIND_PLAN=`ibmcloud catalog service messagehub | grep -w "${PLAN_LC}"`
	if [[ -z "${FIND_PLAN}" ]]
	then
		echo "Plan not found: $PLAN"
		return 21
	else 
		echo "success"
	fi
#	for i in "${PLAN_LIST[@]}"
#	do
#		if [ "$i" != "${PLAN_LC}" ] ; then
#			echo "Plan not found: $PLAN"
#			exit 2
#		fi
#	done
	
	# Validate region
	echo -n "Validating Region..."
	REGION_LC=${REGION,,}
	# Find the input region in the list
	FIND_REGION=`ibmcloud regions | grep -w "${REGION_LC}"`
	if [[ -z "${FIND_REGION}" ]]
	then
		echo "Region not found: $REGION"
		return 22
	else
		echo "success"
	fi
	
	# Validate Role name
	echo -n "Validating Role name..."
	FIND_ROLE=`ibmcloud iam roles --service messagehub | grep -w "${ROLE_NAME}"`
	if [[ -z "${FIND_ROLE}" ]]
	then
		echo "Role not found: ${ROLE_NAME}"
		return 23
	else
		echo "success"
	fi
	
	return 0
#	exit 0
}

function check_es_exist() {
	# Check whether the event_stream_name already exists
	echo -n "Checking whether the event stream name already exists..."
	FIND_ES=`ibmcloud resource service-instances | grep -w "${EVENT_STREAM_NAME}"`
	if [[ ! -z "${FIND_ES}" ]]
	then
		echo "Event Stream name already exist: ${EVENT_STREAM_NAME}"
		return 0
	else
		echo "not found"
	fi
	return 31
}

if [[ $# -eq 0 ]] || [[ "$1" == "--help" ]] || [[ "$1" == "-h" ]]
then
	show_usage
fi

while [ ! -z "$1" ]; do
  case "$1" in
     --create|-c)         
        echo "You are asked for creation of Event Stream"
		CREATE=true
        ;;
	 --delete|-d)         
        echo "You are asked to delete the Event Stream"
		DELETE=true
        ;;
     --name|-n)
        shift
        echo "Event Stream name: $1"
		EVENT_STREAM_NAME=$1
        ;;
     --topic-id|-t)
        shift
        echo "Topic Id: $1"
		TOPIC_ID=$1
        ;;
     --plan|-p)
        shift
        echo "Selected plan: $1"
		PLAN=$1
        ;;
     --region|-r)
        shift
        echo "Selected region: $1"
		REGION=$1
        ;;		
     --apikey-name|-a)
        shift
        echo "API Key Name: $1"
		KEY_NAME=$1
        ;;
     --role-name|-o)
        shift
        echo "Role Name: $1"
		ROLE_NAME=$1
        ;;		
     *)
        show_usage
        ;;
  esac
shift
done

if [[ "$CREATE" = "true" && ! -z "${EVENT_STREAM_NAME}" && ! -z "$REGION" && ! -z "$PLAN" && 
		! -z "${KEY_NAME}" && ! -z "${ROLE_NAME}" ]]
then
	create_eventstream
elif [[ "$DELETE" = "true" && ! -z "${EVENT_STREAM_NAME}" ]]
then
	delete_eventstream	
else
	echo "Incorrect input arguments"
	show_usage
fi
