# See here for image contents: https://github.com/microsoft/vscode-dev-containers/tree/v0.245.2/containers/java/.devcontainer/base.Dockerfile

# [Choice] Java version: 21, 25 (append -bookworm or -trixie to pin the OS)
ARG VARIANT="25"
FROM mcr.microsoft.com/devcontainers/java:${VARIANT}

RUN su vscode -c "umask 0002 && . /usr/local/sdkman/bin/sdkman-init.sh && sdk install scala 3.9.0"
RUN su vscode -c "umask 0002 && . /usr/local/sdkman/bin/sdkman-init.sh && sdk install sbt 2.0.9"
