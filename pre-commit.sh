#!/bin/bash

source bash/common.sh

lein with-profile quality do clean, deps

lein ancient

lein test
unit=$?
echo_result "Unit Tests" $unit

lein quality
quality=$?
echo_result "Quality Tests" $quality
