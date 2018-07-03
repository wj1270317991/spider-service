#!/bin/sh
appName=$1
datasource=$2
templateName="template"

if [[ $appName = "" ]]; then
    echo "缺少参数 appName 未设置, 格式：sh init [appName] [是否多数据源：Y/N] 示例：sh init.sh zeus Y"
    exit 0
elif [[ $datasource = "" ]]; then
    echo "缺少参数 是否多数据源未设置, 格式：sh init [appName] [是否多数据源：Y/N] 示例：sh init.sh zeus Y"
    exit 0
else
    cd ..
    mv saluki2-template $appName-service
    cd $appName-service

    sed -i "" "s/$templateName/$appName/g" *.xml

    mv dao/src/main/java/com/quancheng/$templateName/ dao/src/main/java/com/quancheng/$appName/
    mv service/src/main/java/com/quancheng/$templateName/ service/src/main/java/com/quancheng/$appName/

    sed -i "" "s/$templateName/$appName/g" dao/src/main/java/com/quancheng/$appName/dataobject/*.java
    sed -i "" "s/$templateName/$appName/g" dao/src/main/resources/sqlmapper/datasource1/*.xml
    sed -i "" "s/$templateName/$appName/g" dao/src/main/resources/sqlmapper/datasource2/*.xml
    sed -i "" "s/$templateName/$appName/g" dao/src/main/resources/sqlmapper/*.xml
    sed -i "" "s/$templateName/$appName/g" dao/src/main/java/com/quancheng/$appName/dao/datasource1/*.java
    sed -i "" "s/$templateName/$appName/g" dao/src/main/java/com/quancheng/$appName/dao/datasource2/*.java
    sed -i "" "s/$templateName/$appName/g" dao/src/main/java/com/quancheng/$appName/dao/*.java
    sed -i "" "s/$templateName/$appName/g" service/src/main/java/com/quancheng/$appName/*/*.java
    sed -i "" "s/$templateName/$appName/g" service/src/main/java/com/quancheng/application/Application.java

    sed -i "" "s/$templateName/$appName/g" service/src/main/resources/application.properties
    sed -i "" "s/com.quancheng.template.dao/com.quancheng.$appName.dao/g" service/src/main/java/com/quancheng/$appName/api/DemoController.java

    sed -i "" "s/$templateName/$appName/g" *.xml
    sed -i "" "s/$templateName/$appName/g" dao/*.xml
    sed -i "" "s/$templateName/$appName/g" interface/*.xml
    sed -i "" "s/$templateName/$appName/g" service/*.xml

    mv interface/src/main/proto/$templateName/ interface/src/main/proto/$appName/


    sed -i "" "s/$templateName/$appName/g" .gitlab-ci.yml
    sed -i "" "s/$templateName/$appName/g" Dockerfile
    sed -i "" "s/$templateName/$appName/g" interface/package.json
    sed -i "" "s/$templateName/$appName/g" service/src/main/resources/datasource1.xml
    sed -i "" "s/$templateName/$appName/g" service/src/main/resources/datasource2.xml

    rm -rf dao/*.iml
    rm -rf interface/*.iml
    rm -rf service/*.iml
    rm -rf *.iml
    rm -rf .git
fi

if [[ $datasource = "Y" ]]; then
    rm -rf dao/src/main/resources/sqlmapper/*.xml
    rm -rf dao/src/main/java/com/quancheng/$appName/dao/*.java
    rm -rf service/src/main/resources/datasource1.xml
    mv service/src/main/resources/datasource2.xml service/src/main/resources/datasource.xml
else
    rm -rf dao/src/main/resources/sqlmapper/datasource1/
    rm -rf dao/src/main/resources/sqlmapper/datasource2/
    rm -rf dao/src/main/java/com/quancheng/$appName/dao/datasource1/
    rm -rf dao/src/main/java/com/quancheng/$appName/dao/datasource2/
    rm -rf service/src/main/resources/datasource2.xml
    mv service/src/main/resources/datasource1.xml service/src/main/resources/datasource.xml
fi



