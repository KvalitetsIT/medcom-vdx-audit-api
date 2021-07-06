#!/bin/sh

# Build inside docker container
docker run -e GITHUB_TOKEN=$GITHUB_TOKEN -v /var/run/docker.sock:/var/run/docker.sock -v $(pwd):/src -v $HOME/.m2:/root/.m2 maven:3-jdk-11 /src/build/maven-deploy.sh
