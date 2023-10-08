#!/bin/bash
[ "$UID" -eq 0 ] || echo "$1" | exec sudo -S "$0" "$@"

echo "$PWD"

#ln -s ../src/test/resources/assets/sanlib ../src/main/resources/assets/sanlib
#ln -s ../src/test/resources/assets/sanplayermodel ../src/main/resources/assets/sanplayermodel