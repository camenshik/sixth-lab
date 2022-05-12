cd server
mkdir -p bin-server
chmod u=rwx ./libs/commons-lang3-3.12.0.jar ./libs/opencsv-5.6.jar
javac -classpath "libs/*:." -d ./bin-server `find ./src-server -type f -name *.java`
javac -d ./response `find ./response -type f -name *.java`
echo "Main-Class: server.Main\nClass-Path: libs/commons-lang3-3.12.0.jar libs/opencsv-5.6.jar response/TypeResponse.class response/Response.class response/ bin-server/\n" > MANIFEST.mf
jar -cvfm server.jar MANIFEST.mf -C bin-server/ response/ .
cd ../client
mkdir -p bin-client
javac -d ./bin-client `find ./src-client -type f -name *.java`
javac -d ./response `find ./response -type f -name *.java`
echo "Main-Class: client.Main\nClass-Path: response/TypeResponse.class response/Response.class response/\n" > MANIFEST.mf
jar -cvfm client.jar MANIFEST.mf -C bin-client/ response/ bin-client/ .