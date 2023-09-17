#!/usr/bin/env bash

echo "$#"

echo "$@"

echo "docker run --volume /Users/vincentcifello/kotwords/Puzzles:/usr/bin/runner/Puzzles \
--volume /Users/vincentcifello/kotwords/config.json:/usr/bin/runner/config.json \
kotwordclient java -jar run.jar $*"

docker run --volume /Users/vincentcifello/kotwords/Puzzles:/usr/bin/runner/Puzzles \
--volume /Users/vincentcifello/kotwords/config.json:/usr/bin/runner/config.json \
vcifello/kotwordclient java -jar run.jar "$@"




