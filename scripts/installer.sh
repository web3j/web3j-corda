#!/bin/bash

installed_flag=0
local=~/

web3j_corda_version="0.2.8"

check_if_installed() {
  if [ -x "$(command -v web3j-corda)" ] &>/dev/null; then
    echo 'web3j-corda is installed on you system.'
    installed_flag=1
  fi
}

download_web3j_corda() {
  echo "Downloading web3j-corda ..."
  mkdir "${local}.web3j_corda"
  if [[ $(curl --write-out %{http_code} --silent --output /dev/null "https://github.com/web3j/web3j-corda/releases/download/v${web3j_corda_version}/web3j-corda-${web3j_corda_version}.tar") -eq 302 ]]; then
    curl -# -L -o "$HOME/.web3j_corda/web3j-corda-${web3j_corda_version}.tar" "https://github.com/web3j/web3j-corda/releases/download/v${web3j_corda_version}/web3j-corda-${web3j_corda_version}.tar"
    echo "Installing Web3j Corda..."
    tar -xf "$HOME/.web3j_corda/web3j-corda-${web3j_corda_version}.tar" -C "$HOME/.web3j_corda"
    echo "export PATH=\$PATH:$HOME/.web3j_corda/web3j-corda-${web3j_corda_version}/bin" >"$HOME/.web3j_corda/source.sh"
    chmod +x "$HOME/.web3j_corda/source.sh"
    echo "Removing tar file ..."
    rm "$HOME/.web3j_corda/web3j-corda-${web3j_corda_version}.tar"
  else
    echo "Looks like there was an error while trying to download web3j-corda"
    exit 0
  fi
}

check_version() {
  version_string=$(web3j-corda --version)
  echo "$version_string"
  if [[ $version_string < ${web3j_corda_version} ]]; then
    echo "Your web3j-corda version is not up to date."
    get_user_input
  else
    echo "You have the latest version of web3j-corda. Exiting."
    exit 0
  fi
}

get_user_input() {
  while read -p "Would you like to update web3j-corda ? [y]es | [n]o : " user_input; do
    case $user_input in
    y)
      echo "Updating web3j-corda ..."
      break
      ;;
    n)
      echo "Aborting installation ..."
      exit 0
      ;;
    esac
  done
}

source_web3j_corda() {
  SOURCE_WEB3J_CORDA="\n[ -s \"$HOME/.web3j_corda/source.sh\" ] && source \"$HOME/.web3j_corda/source.sh\""

  files=("$HOME/.bashrc")

  if [ -f "$HOME/.bash_profile" ]; then
    files+=("$HOME/.bash_profile")
  elif [ -f "$HOME/.bash_login" ]; then
    files+=("$HOME/.bash_login")
  elif [ -f "$HOME/.profile" ]; then
    files+=("$HOME/.profile")
  else
    files+=("$HOME/.bash_profile")
  fi

  for file in "${files[@]}"; do
    touch "${file}"
    if ! grep -qc '.web3j_corda/source.sh' "${file}"; then
      echo "Adding source string to ${file}"
      printf "$SOURCE_WEB3J_CORDA\n" >>"${file}"
    else
      echo "Skipped update of ${file} (source string already present)"
    fi
  done

  if [ -f "$(command -v zsh 2>/dev/null)" ]; then
    file="$HOME/.zshrc"
    touch "${file}"
    if ! grep -qc '.web3j_corda/source.sh' "${file}"; then
      echo "Adding source string to ${file}"
      printf "$SOURCE_WEB3J_CORDA\n" >>"${file}"
    else
      echo "Skipped update of ${file} (source string already present)"
    fi
  fi
}

clean_up() {
  if [ -d "${local}.web3j_corda" ]; then
    rm -r "${local}.web3j_corda" &>/dev/null
    echo "Deleting older installation ..."
  fi
}

completed() {
  printf '\033[32m'
  echo "web3j-corda was succesfully installed"
  echo "To get started you will need web3j-corda's bin directory in your PATH enviroment variable."
  echo "When you open a new terminal window this will be done automatically."
  echo "To see what web3j-corda's CLI can do you can check the documentation below."
  echo "https://corda.web3j.io "
  echo "To use web3j-corda in your current shell run:"
  echo "source \$HOME/.web3j_corda/source.sh "
  printf '\033[0m'
  exit 0
}

main() {
  check_if_installed
  if [ $installed_flag -eq 1 ]; then
    check_version
    clean_up
  fi
  download_web3j_corda
  source_web3j_corda
  completed
}

main
