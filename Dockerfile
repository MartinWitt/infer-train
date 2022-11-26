FROM debian:bullseye-slim AS compilator

# mkdir the man/man1 directory due to Debian bug #863199
RUN apt-get update && \
  mkdir -p /usr/share/man/man1 && \
  apt-get install --yes --no-install-recommends \
  autoconf \
  automake \
  bzip2 \
  cmake \
  curl \
  g++ \
  gcc \
  git \
  libc6-dev \
  libgmp-dev \
  libmpfr-dev \
  libsqlite3-dev \
  make \
  opam \
  openjdk-11-jdk-headless \
  patch \
  patchelf \
  pkg-config \
  python3 \
  python3-distutils \
  unzip \
  xz-utils \
  zlib1g-dev && \
  rm -rf /var/lib/apt/lists/*

# Disable sandboxing
# Without this opam fails to compile OCaml for some reason. We don't need sandboxing inside a Docker container anyway.
RUN opam init --reinit --bare --disable-sandboxing --yes --auto-setup

# Download the latest Infer from git
RUN cd / && \
  git clone --depth 1 https://github.com/facebook/infer/

# Build opam deps first, then infer. This way if any step fails we
# don't lose the significant amount of work done in the previous
# steps.
RUN cd /infer && ./build-infer.sh java --only-setup-opam
RUN cd /infer && ./build-infer.sh java

# Generate a release
RUN cd /infer && \
  make install-with-libs \
  BUILD_MODE=opt \
  PATCHELF=patchelf \
  DESTDIR="/infer-release" \
  libdir_relative_to_bindir="../lib"

# Get the infer release
COPY --from=compilator /infer-release/usr/local /infer

# Install infer
ENV PATH /infer/bin:${PATH}
FROM registry.access.redhat.com/ubi8/ubi-minimal:8.6
WORKDIR /work/
RUN chown 1001 /work \
  && chmod "g+rwX" /work \
  && chown 1001:root /work
COPY --chown=1001:root build/*-runner /work/application

EXPOSE 8080
USER 1001

CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
