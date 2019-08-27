cd out/test/resources/nodes
nodeFolder=$1
legalName=$2
port=$3

echo 'devMode=true' >> $nodeFolder/node.conf
echo 'myLegalName="'$legalName'"' >> $nodeFolder/node.conf
echo 'notary {\n\tvalidating=false\n}' >> $nodeFolder/node.conf
echo 'p2pAddress="localhost:'$port'"' >> $nodeFolder/node.conf
port=$(($port+1))
echo 'rpcSettings {' >> $nodeFolder/node.conf
echo '  address="localhost:'$port'"' >> $nodeFolder/node.conf
port=$(($port+1))
echo '  adminAddress="localhost:'$port'"' >> $nodeFolder/node.conf
port=$(($port+1))
echo '}' >> $nodeFolder/node.conf
echo 'security {\n\tauthService {\n\t\tdataSource {\n\t\t\ttype=INMEMORY\n\t\t\tusers=[ {\n\t\t\t\tpassword=test\n\t\t\t\tpermissions=[ALL]\n\t\t\t\tuser=user1\n\t\t\t} ]\n\t\t}\n\t}\n}' >> $nodeFolder/node.conf
echo 'compatibilityZoneURL="http://'$4':8080"' >> $nodeFolder/node.conf
echo 'devModeOptions.allowCompatibilityZone=true' >> $nodeFolder/node.conf

cp ../corda.jar $nodeFolder
cp -R ../cordapps $nodeFolder