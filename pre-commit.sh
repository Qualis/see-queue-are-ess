#!/bin/bash

source bash/common.sh

lein do clean, deps

lein test
unit=$?
echo_result "Unit tests" $unit
