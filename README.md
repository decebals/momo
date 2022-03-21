Momo
=====================
A tiny JCR (Java Content Repository) browser in Swing.

Current build status: [![Build Status](https://buildhive.cloudbees.com/job/decebals/job/momo/badge/icon)](https://buildhive.cloudbees.com/job/decebals/job/momo/)

Features/Benefits
-------------------
Momo is an open source (Apache license) tiny JCR Browser.
With Momo you can browse a JCR, you can view the item's properties. Momo comes with built-in viewers for images and xml files.
Also, you can query the JCR using XPath, SQL, JCR_SQL2 and JCR_JQOM. A query history is stored in the query-history.xml file and you can navigate in the query history from application.
All you must to do is to set some properties in the momo.properties file.

For the moment only (Local) Jackrabbit is supported (this is the only one I have been using) but I think that I can easily add support for ModeShape or other JCR implementations.
All operations are read-only in general (except for renaming or deleting a node from hierarchy tab).

How to use
-------------------

Build the distibution file (zip file) with:
```
mvn clean package
```

Above command creates a file `momo-*.zip` in target folder.

To run momo application you can follow these steps:

```
mkdir dist
cd dist
cp ../target/momo-*.zip .
unzip momo-*.zip
java -jar momo-*.jar
```

DON'T FORGET
You must create a file momo.properties from momo-sample.properties

To make a query:
- go to Query tab
- choose the query type (`XPath`, `SQL`, `JCR_SQL2` and `JCR_JQOM`); XPath, SQL are deprecated in JCR2
- enter the query text
- press Run button

The query result contains a list with nodes. Double click on a node or press Enter key to "jump" to view that node in the Hierarchy tab.

You can see some screenshots from application in [wiki page] (https://github.com/decebals/momo/wiki).

License
--------------
Copyright 2013 Decebal Suiu

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with
the License. You may obtain a copy of the License in the LICENSE file, or at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
