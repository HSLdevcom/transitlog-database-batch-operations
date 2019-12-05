#!/bin/bash

SYNCMACHINEIP=#FILL
SYNCMACHINEUSERNAME=#FILL
SYNCMACHINEPASSWORD=#FILL

function buildDockerContainer {
	echo "Build succesful, build docker container"
	docker build -t image .
	if [ $? -eq 0 ]
	then
		echo "Docker image was built"
		containerHash=`docker images -q image`
		echo "Container hash: $containerHash"
	else
		exit $?
	fi

}

function exportContainerIntoTar {
	#Save container into a tar file
	docker save -o image.tar $containerHash
	if [ $? -eq 0 ]
	then
		echo "Docker save worked"
		echo "Pushing with scp"
	else
		echo "Docker save failed"
		exit $?
	fi
}

function syncContainerWithRSync {
	echo "Syncing saved container with rsync"
	sshpass -p $SYNCMACHINEPASSWORD rsync -P -avzhe ssh image.tar $SYNCMACHINEUSERNAME@$SYNCMACHINEIP:image.tar
}


function remoteLoadDockerContainer {
	echo "Remote loading docker container"
	sshpass -p $SYNCMACHINEPASSWORD ssh -t $SYNCMACHINEUSERNAME@$SYNCMACHINEIP 'echo '$SYNCMACHINEPASSWORD' | sudo -S docker load -i image.tar'
}

function registerCleanupHook {
 	trap "echo "chowning" &&  chown -R $(logname):$(logname) ../" EXIT ERR INT TERM
}

#Check dependencies exist
if [ -z $(command -v sshpass) ]; then sudo apt-get install sshpass; else echo "sshpass is found"; fi

mvn clean install


if [ $? -eq 0 ]
then
  registerCleanupHook
	buildDockerContainer
	exportContainerIntoTar
	syncContainerWithRSync
	remoteLoadDockerContainer
else
	echo "Build failed"
fi


