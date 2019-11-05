DOCKER_HOST=unix:///var/run/docker.sock DOCKER_TLS_VERIFY=0 docker run -t --rm \
          -v "$HOME/.gradle":/root/.gradle/ \
          -v /var/run/docker.sock:/var/run/docker.sock \
          -v "$(pwd)":"$(pwd)" \
          -w "$(pwd)" \
          openjdk:8-jdk-alpine \
          ./gradlew --no-daemon spotlessCheck check integrationTest jacocoTestReport -Pversion="${TRAVIS_BRANCH#'release/'}"
