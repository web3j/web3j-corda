Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
$PSDefaultParameterValues['*:ErrorAction']='Stop'

$web3j_corda_version="0.2.8"

[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
$ProgressPreference = 'SilentlyContinue'

New-Item -Force -ItemType directory -Path "${env:USERPROFILE}\.web3j_corda" | Out-Null
$url = "https://github.com/web3j/web3j-corda/releases/download/v${web3j_corda_version}/web3j-corda-${web3j_corda_version}.zip"
$output = "${env:USERPROFILE}\.web3j_corda\web3j_corda.zip"
Write-Output "Downloading Web3j Corda version ${web3j_corda_version}..."
Invoke-WebRequest -Uri $url -OutFile $output
Write-Output "Extracting web3j-corda..."
Expand-Archive -Path "${env:USERPROFILE}\.web3j_corda\web3j_corda.zip" -DestinationPath "${env:USERPROFILE}\.web3j_corda\" -Force
$CurrentPath = [Environment]::GetEnvironmentVariable("Path", [EnvironmentVariableTarget]::User)

if (!($CurrentPath -match $web3j_corda_version)) {
    [Environment]::SetEnvironmentVariable(
            "Path",
            $CurrentPath + ";${env:USERPROFILE}\.web3j_corda\web3j-corda-${web3j_corda_version}\bin",
            [EnvironmentVariableTarget]::User)
    Write-Output "web3j-corda has been added to your PATH variable. You will need to open a new CMD/PowerShell instance to use it."
}

Write-Output "web3j-corda has been successfully installed (assuming errors were printed to your console)."
