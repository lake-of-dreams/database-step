#!/bin/sh
CLASSPATH=/u01/oracle/oracle_common/modules/jackson-jaxrs-json-provider-2.4.3.jar:/u01/oracle/oracle_common/modules/jackson-dataformat-xml-2.4.3.jar:/u01/oracle/oracle_common/moduls/jackson-module-jsonSchema-2.4.3.jar:/u01/oracle/oracle_common/modules/jackson-jaxrs-base-2.4.3.jar:/u01/oracle/oui/modules/jackson-annotations-2.4.3.jar:/u01/oracle/oui/modules/jackson-databind-2.4.3.jar:/u01/oracle/oui/modules/jackson-core-2.4.3.jar:/u01/oracle/oui/modules/jackson-module-jaxb-annotations-2.4.3.jar:/u01/oracle/oracle_common/modules/oracle.jdbc/ojdbc7.jar

echo "Compiling java class"
cd $WERCKER_STEP_ROOT
javac -cp $CLASSPATH CreateDDL.java
java -cp $CLASSPATH:. CreateDDL