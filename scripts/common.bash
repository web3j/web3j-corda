set -eo pipefail

[[ "$TRACE" ]] && set -x

ensure_version() {
    if [[ -z "$VERSION" ]]; then
        VERSION="${TRAVIS_BRANCH//release\/}"
    fi

    if [[ "$VERSION" = "" ]]; then
        echo "ERROR: Missing VERSION specify it using an env variable"
        exit 1
    fi
}

ensure_product() {
    if [[ -z "$PRODUCT" ]]; then
        PRODUCT="${TRAVIS_REPO_SLUG//release\/}"
    fi

    if [[ "$VERSION" = "" ]]; then
        echo "ERROR: Missing PRODUCT specify it using an env variable"
        exit 1
    fi
}
