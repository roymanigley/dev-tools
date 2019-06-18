# dev-tools
> Webapplication as standalone .jar and deployable .war (Tomcat) with features like FileWalker, SQL editor **with recursive DBUnit exporter by SQL query**, web shell and a Groovy editor  


## Installation

#### Requirements
- Java 8 (JDK)
- Apache Maven
- Web Browser


#### Build with maven
  - Build using Maven
```mvn clean install```

## Start the application
  - Run as JAR (parameters are optional)
```
java -jar target/dev-tools\#\#1.0.jar -httpPort=7070 \  
 -Dsqleditor-jdbc-url=jdbc:mysql://localhost:3306/classicmodels \
 -Dsqleditor-username=root \
 -Dsqleditor-password=toor
 ```
  - Deploy as WAR
```target/dev-tools##1.0.war```

#### Use the application
  - Login to the application [http://localhost:7070/dev-tools](http://localhost:7070/dev-tools)
```username.equals(password)```

## Description
- Authentication
  - Login / Password 
- FileWalker
  - Browsing the servers Filesystem
  - Read/Download Files
  - Edit Files
  - Delete Files
  - Upload Files
- Groovy Editor
  - Execute Groovy scripts
  - return value is displayed
  - stdout is displayed
  - execution can be canceled (runs in a thread)
- SQL Editor
  - Tested on MsSql 2012, MySql, Oracel 10g (need to comment out in pom for oracle)
  - SQL Autocomplete
  - Dataexporter: CSV, XLS, XML, **DbUnit** ; ) 
- Terminal
  - Execute system commands
  - StdError and StdOut are displayed

## To do
- Better Authentication
- SQLEditor Better detection if DDL, DML, DCL
- i18n

## SQL Executionplan

### MsSQL
```sql
-- don't work in dev-tools
SET STATISTICS PROFILE ON

SELECT * FROM MY_TABLE WHERE MY_TABLE.MY_COL IS NULL

SET STATISTICS PROFILE OFF

```

### MySQL
```sql
EXPLAIN SELECT * FROM MY_TABLE WHERE MY_TABLE.MY_COL IS NULL

```

### Oracle
```sql
EXPLAIN PLAN FOR
SELECT * FROM MY_TABLE WHERE MY_TABLE.MY_COL IS NULL

select plan_table_output from table(dbms_xplan.display())
```
