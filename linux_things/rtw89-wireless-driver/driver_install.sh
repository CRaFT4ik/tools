#!/bin/bash

###
### Автоматически выполняет обновление и переустановку модуля связи.
### @author Eldar Timraleev
###

WIFI_DELAY=12

function wait_any_key {
    while [ true ] ; do
        read -t $WIFI_DELAY -n 1
        if [ $? = 0 ] ; then
	    break
	else
            break #echo "Still waiting for the keypress .. "
        fi
    done
}

function make_install {
    echo " - Unloading rtw_8852ae module..."
    sudo modprobe -rv rtw_8852ae
    sudo modprobe -rv rtw_core
 
    echo " - Compiling the code..."
    make
    
    echo " - Installing..."
    sudo make install
    
    echo " - Loading rtw_8852ae module to kernel..."
    sudo modprobe -v rtw_8852ae
}

echo -e "- - -\nStage 1 of 3: compilation and installing old code\n- - -\n"
cd repo
make_install

echo -e "- - -\nStage 2 of 3: updating the code\n- - -\n"
echo "Please, press any key when WiFi becomes avalible."
echo "It will be done automatically after $WIFI_DELAY seconds."
wait_any_key; echo -e "\n"

make clean
git pull

echo -e "- - -\nStage 3 of 3: compilation and installing new code\n- - -\n"
make_install
cd ../
