# MXToot - Matrix-Mastodon bot written on java.

## Overview

* [Usage](#Usage)
* [Configuration](#Configuration)
* [Commands](#Commands)
* [Compile source code](#Compile source code)

### Usage



#### Requirements:

* openjdk 8 (openjdk 9, 10 don't supported, I need time to test it).

#### Run bot

Command:
```
java -jar mxtoot-X.X.X.jar check mxtoot.yaml
```
will check you configuration.

Command:
```
java -Xmx100m -jar mxtoot-X.X.X.jar sever mxtoot.yaml
```
will run appplication service.

### Configuration

There is only an one configuration yaml-file which include all settings:

#### homeserverUrl
Url of the matrix homeserver. For example "https://matrix.org:8448"

#### displayName
Initial bot's displayName. If command is enabled it is possible to change this name.
Can be invoked only by owner. For example, "mxtoot".

#### prefix
Initial command's prefix. If command is enabled it is possible to change prefix.
Can be invoked only by owner. For example, "!".

DO NOT USE "/". This character is internal used by Web Riot and all commands with "/" prefix will be
invoked only by Riot not bot. Workaround ro execute commands: place one or more spaces before prefix.

Also there is a special placeholder {{display_name}} which will be replaced by current bot's name.
You can set prefix as "{{display_name}}:" and all commands should be execute by bot's mention.
For example, "mxtoot: help".

#### commands
There is a list of all enabled commands described by command's class.
See [more](#Commands).

#### runState
There are two run states: APPLICATION_SERVICE and STANDALONE.

In STANDALONE mode each of the bots will run in separated thread and receive events from /sync request
like a common client.

In APPLICATION_SERVICE mode bots wont'be run in threads and will receive events only from
application service via /transaction endpoint. This state is recommended because reduces load
of the homeserver.

#### strictMode
May be `true` or `false`.

When Application service receive events via /transaction endpoint it will validate event.

When strictMode is enabled if unknown field is found application service will throw exception
and stop it process.

When strictMode is disabled it will skip event's validations and all unknown fields will be ignored.

#### postFormat
#### replyFormat
#### boostFormat

Initial template of the post, reply and boost messages which come from Mastodon. To create messages uses
[jmustache](https://github.com/samskivert/jmustache) library, it is another java implementation 
of the logic-less templating engine [mustache](https://mustache.github.io/).

For example, how you can define template:
```
postFormat: >
    <hr/>[{{id}}]: <a href="{{url}}">{{url}}</a>:<br/>
    {{account.acct}} at {{created_at}} wrote:<br/>
    {{content}} 
```  
When bot receive message from the Mastodon it fill all placeholders `{{value}}` by values and write
the message to the room.
So, portFormat describe how display general toots from the Mastodon, replyFormat - replies,
boostFormat - boost/reblog messages.

TODO: write available placeholders.

#### dateTimeFormat

Defines how display date and time from all Mastodon's messages. 
See more on the [https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html]()
in the Section "Patterns for Formatting and Parsing".
 
#### dateTimeLocale

Defines locale of the localized date terms in the [dateTimeFormat](#dateTimeFormat) template.

For example, you can set `dateTimeFormat: "MMM, dd, yyyy"` and `dateTimeLocale: "ru"`. Then date
will be rendered as `Май, 02, 2018` because the Russian locale was defined.

#### fetchMissingStatuses

May be `true` or `false`.

### Commands

### Compile source code

To build you need jdk 8 (oracle or openjdk) and apache maven 3.5.2 or higher.

1. clone repository `git clone https://github.com/ma1uta/jeon.git`
2. `cd jeon`
3. build common modules and mxtoot `mvn -P mxtoot package`
