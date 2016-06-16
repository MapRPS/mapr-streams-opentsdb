This is a custom agent which is able fetch OPC events from streams and write it to OpenTSDB

More information: http://www.opcdatahub.com/WhatIsOPC.html

Used library: https://openscada.atlassian.net/wiki/display/OP/HowToStartWithUtgard

OPC server trial: https://www.softwaretoolbox.com/topserver/ (Windows required)


Generate IDE files

```
gradle idea

 or

gradle eclipse
```

Build with

```
gradle clean build distZip
```

Find an example config in `sample_config.yml` and run `mapr-streams-opentsdb <config_file.yml>`