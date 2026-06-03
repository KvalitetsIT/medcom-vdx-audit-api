#!/bin/sh

# Build inside docker container
docker run -e VERSION=$VERSION -e GITHUB_TOKEN=$GITHUB_TOKEN -e MAVEN_USERNAME=$MAVEN_USERNAME -e MAVEN_PASSWORD=$MAVEN_PASSWORD -e SIGN_KEY_PASS=$SIGN_KEY_PASS -e SIGN_KEY_ID=$SIGN_KEY_ID -e SIGN_KEY="$SIGN_KEY" -v /var/run/docker.sock:/var/run/docker.sock -v $(pwd):/src -v $HOME/.m2:/root/.m2 maven:3-ibm-semeru-21-noble /src/build/maven-deploy.sh
