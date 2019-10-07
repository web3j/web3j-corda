#!/bin/bash
web3j_corda_version="0.1.0-SNAPSHOT"
installed_flag=0
update_flag=0
local=~/

check_if_installed() {
  if [ -x "$(command -v web3j-corda)" ] &>/dev/null; then
    echo 'Web3j-Corda is installed on you system.'
    installed_flag=1
  fi
}

completed() {
  echo "To start using the CLI type web3j_corda"
  exit 0
}

download_web3j_corda() {
  echo "Downloading Web3j-Corda ..."
  mkdir "${local}.web3j_corda"
  if [[ $(curl --write-out %{http_code} --silent --output /dev/null "https://oss.sonatype.org/service/local/repositories/snapshots/content/org/web3j/corda/web3j-corda-console/0.1.0-SNAPSHOT/web3j-corda-console-0.1.0-20191002.151748-1.zip") -eq 200 ]]; then
    curl -# -L -o "$HOME/.web3j_corda/web3j_corda-${web3j_corda_version}.zip" "https://oss.sonatype.org/service/local/repositories/snapshots/content/org/web3j/corda/web3j-corda-console/0.1.0-SNAPSHOT/web3j-corda-console-0.1.0-20191002.151748-1.zip"
    echo "Installing Web3j Corda..."
    unzip "$HOME/.web3j_corda/web3j_corda-${web3j_corda_version}" -d "$HOME/.web3j_corda"
    echo "export PATH=\$PATH:$HOME/.web3j_corda/web3j-corda-${web3j_corda_version}/bin" >"$HOME/.web3j_corda/source.sh"
    chmod +x "$HOME/.web3j_corda/source.sh"
    #echo "Removing zip file ..."
    #rm "$HOME/.web3j_corda/web3j_corda-${web3j_corda_version}.zip"
 else
  echo "Looks like there was an error while trying to download web3j_corda"
  exit 0
 fi
}

check_version() {
  version_string=`web3j-corda --version`
  echo $version_string
  # FIXME: the version of cli reflect web3j-version
  if [[ $version_string < "1.0" ]] ; then
    echo "Your web3j_corda version is not up to date."
    get_user_input
  else
    echo "You have the latest version of web3j_corda. Exiting."
    exit 0
  fi
}

get_user_input() {
  while read -p "Would you like to update web3j_corda ? [y]es | [n]o : " user_input ; do
    case $user_input in
    y)
      echo "Updating web3j_corda ..."
      break
      ;;
    n)
      echo "Aborting instalation ..."
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
    touch ${file}
    if ! grep -qc '.web3j_corda/source.sh' "${file}"; then
      echo "Adding source string to ${file}"
      printf "$SOURCE_WEB3J_CORDA\n" >>"${file}"
    else
      echo "Skipped update of ${file} (source string already present)"
    fi
  fi
}

clean_up() {
  if [ -d "${local}.web3j_corda" ] ; then
  rm -r "${local}.web3j_corda" &>/dev/null
  echo "Deleting older installation ..."
  fi
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