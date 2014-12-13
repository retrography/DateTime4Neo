Regx4Neo
========

A simple Neo4j server plugin that makes it possible to execute server-side time-related calculations on date-time values saved as text properties in Neo4j database. This fills a void in Cypher Query Language that I am sure will be addressed soon.

### Installation 

In order to install the plugin, copy the [`jar` file][1] to `plugins` directory of your Neo4j installation and subsequently restart Neo4j server. This will add three methods to the REST API of Neo4j. To learn more about server plugins and how they work check the [server plugins page][2] from Neo4j's manual.

### Use

First make sure the plugin has been installed correctly by running the following in Neo4j browser:

    // REST API
    :GET /db/data

The response must include the following lines:

    "extensions": {
      "Dater": {
        "dt_period": "http://localhost:7474/db/data/ext/Dater/graphdb/dt_period",
      }
    }

Sending a GET request with each of this addresses will inform you about the supported parameters. A POST request along with parameters will run the method.

For instance running `:GET /db/data/ext/Dater/graphdb/dt_period` will return the following:

    {
      "extends" : "graphdb",
      "description" : "Calculates the length of a period and returns or saves the result.",
      "name" : "dt_period",
      "parameters" : [
        {
          "optional" : false,
          "description" : "The label by which nodes should be filtered.",
          "type" : "string",
          "name" : "label"
        },
        {
          "optional" : false,
          "description" : "The property pointing to the beginning of the period.",
          "type" : "string",
          "name" : "start"
        },
        {
          "optional" : false,
          "description" : "The property pointing to the end of the period.",
          "type" : "string",
          "name" : "end"
        },
        {
          "optional" : false,
          "description" : "The date pattern used. (See http://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html)",
          "type" : "string",
          "name" : "format"
        },
        {
          "optional" : true,
          "description" : "The time unit for the results (YEARS, MONTHS, WEEKS or DAYS). Defaults to days.",
          "type" : "string",
          "name" : "unit"
        },
        {
          "optional" : true,
          "description" : "The property that must be populated with the results.",
          "type" : "string",
          "name" : "output"
        }
      ]
    }

And accordingly, a sample split query would be as follows:

    // REST API
    :POST /db/data/ext/Dater/graphdb/dt_period
    {"target":"/db/data", "label":"Project", "start":"since", "end":"until", "format":"yyyyMMdd", "unit":"DAYS", "output":"activity_period"}

This will return the number of days a project has been active.

The server response will be similar to the following:

    [
      "681 properties modified."
    ]

If `output` is omitted, server will simply return the results to the REST client. 

### To-Do

1. Implement a duration function that calculates less-than-a-day periods
2. Implement a range overlap function that calculates whether two ranges have an overlap and how long the overlap is


  [1]: https://github.com/retrography/DateTime4Neo/releases
  [2]: http://docs.neo4j.org/chunked/stable/server-plugins.html
