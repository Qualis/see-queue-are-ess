#!/bin/bash

source bash/common.sh

lein do clean, deps

lein ancient

MIDJE_COLORIZE=NONE lein test
unit=$?
echo_result "Unit Tests" $unit

lein quality
quality=$?
echo_result "Quality Tests" $quality

lein cloverage --runner :midje --lcov
python bash/lcov_cobertura.py target/coverage/lcov.info --output target/coverage/coverage.xml
