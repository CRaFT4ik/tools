#!/bin/bash

###
### Запускает обновление модуля связи после обновления ядра.
### @author Eldar Timraleev
###

file_kernels="kernel.version.txt"

kernels_cached=$(cat $file_kernels)
kernels_actual=$(uname -r)
working_dir=$(pwd)

if [ "$kernels_cached" == "$kernels_actual" ]; then
    echo "No kernel changes detected, the wireless module will NOT be reinstalled."
else
	echo "Kernel changes detected, reinstalling wireless module..."
	
	cd ../
	/bin/bash driver_install.sh
	
	cd $working_dir
	printf "$kernels_actual" > $file_kernels
fi