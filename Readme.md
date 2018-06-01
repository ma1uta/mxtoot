# MXToot - Matrix-Mastodon bot written on java.

## Overview

* [Usage](#Usage)
* [Registration](#Registration)
* [Certificate](#Certificate)
* [Configuration](#Configuration)
* [Commands](#Commands)
* [Compile](#Compile)

## Usage

### Requirements:

* openjdk 8 (openjdk 9, 10 don't supported, I need time to test it).

### Run bot

Command:
```
java -jar mxtoot-X.X.X.jar check mxtoot.yaml
```
will check you configuration.

Command:
```
java -Xmx100m -jar mxtoot-X.X.X.jar server mxtoot.yaml
```
will run appplication service.

## Registration 

TODO

## Certificate

TODO

## Configuration

There is only an one configuration yaml-file which include all settings.

### homeserverUrl
Url of the matrix homeserver. For example "https://matrix.org:8448"

### displayName
Initial bot's displayName. If command is enabled it is possible to change this name.
Can be invoked only by owner. For example, "mxtoot".

### prefix
Initial command's prefix. If command is enabled it is possible to change prefix.
Can be invoked only by owner. For example, "!".

DO NOT USE "/". This character is internal used by Web Riot and all commands with "/" prefix will be
invoked only by Riot not bot. Workaround ro execute commands: place one or more spaces before prefix.

Also there is a special placeholder {{display_name}} which will be replaced by current bot's name.
You can set prefix as "{{display_name}}:" and all commands should be execute by bot's mention.
For example, "mxtoot: help".

### commands
There is a list of all enabled commands described by command's class.
See [more](#Commands).

### runState
There are two run states: APPLICATION_SERVICE and STANDALONE.

In STANDALONE mode each of the bots will run in separated thread and receive events from /sync request
like a common client.

In APPLICATION_SERVICE mode bots wont'be run in threads and will receive events only from
application service via /transaction endpoint. This state is recommended because reduces load
of the homeserver.

### strictMode
May be `true` or `false`.

When Application service receive events via /transaction endpoint it will validate event.

When strictMode is enabled if unknown field is found application service will throw exception
and stop it process.

When strictMode is disabled it will skip event's validations and all unknown fields will be ignored.

### postFormat
### replyFormat
### boostFormat

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

### dateTimeFormat

Defines how display date and time from all Mastodon's messages. 
See more on the [https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html]()
in the Section "Patterns for Formatting and Parsing".
 
### dateTimeLocale

Defines locale of the localized date terms in the [dateTimeFormat](#datetimeformat) template.

For example, you can set `dateTimeFormat: "MMM, dd, yyyy"` and `dateTimeLocale: "ru"`. Then date
will be rendered as `Май, 02, 2018` because the Russian locale was defined.

### fetchMissingStatuses

May be `true` or `false`.

TODO: write about dropwizard's settings.

## Commands

TODO: some introduction.

### io.github.ma1uta.matrix.bot.command.Leave

Leave current room. If bot isn't available in any rooms it will delete itself.

### io.github.ma1uta.matrix.bot.command.NewName

Set new name.

### io.github.ma1uta.matrix.bot.command.Pong

Just pong.

### io.github.ma1uta.matrix.bot.command.SetAccessPolicy

Set or show access policy. All commands are divided into two categories: commands which can be invoked
only by owner (who invited bot) or category which is set by this command.

Possible values: OWNER or ALL.

### io.github.ma1uta.matrix.bot.command.Join

Rejoin to a new room. Bot will leave from old room.

### io.github.ma1uta.matrix.bot.command.Prefix

Set or show command prefix, override [the prefix setting](#prefix).

### io.github.ma1uta.matrix.bot.command.DefaultCommand

Set default command. If invoked unknown commands or entered some text
it would invoke the default command.

For example, if invoke `!default toot` every message will be tooted into the Mastodon.

### io.github.ma1uta.matrix.bot.command.Help

Show all commands.

### io.github.ma1uta.mxtoot.matrix.command.RegisterMastodonClient

Start registration flow.

Syntax: `!reg mastodon.server.tld` where mastodon.server.tld is you mastodon server (mstdn.jp,
mastodon.social, etc.)

### io.github.ma1uta.mxtoot.matrix.command.AuthorizeMastodonClient

Validate auth code.

### io.github.ma1uta.mxtoot.matrix.command.Timeline

Start or stop timeline. Bot create connection to the streaming resource and will receive all
new messages.

Possible value: `on`, `off`, `auto` (autorestart after application service's restart).

### io.github.ma1uta.mxtoot.matrix.command.Toot

Post a new message to the Mastodon.

### io.github.ma1uta.mxtoot.matrix.command.Reply

Reply to the message from Mastodon.

### io.github.ma1uta.mxtoot.matrix.command.FetchStatuses

Some messages from Mastodon has only identifier of the statuses or accounts. If this parameter is `true`
then bot will retrieve this statuses or accounts by id.

### io.github.ma1uta.mxtoot.matrix.command.Format

Set or show post, reply or boost message templates.

### io.github.ma1uta.mxtoot.matrix.command.Boost

Boost(reblog) message.

## Compile

To build you need jdk 8 (oracle or openjdk) and apache maven 3.5.2 or higher.

1. clone repository `got clone https://github.com/ma1uta/jeon`
2. `cd jeon`
3. compile and install core libraries `mvn clean install`
4. clone repository `git clone https://github.com/ma1uta/mxtoot`
5. `cd mxtoot`
6. build common modules and mxtoot `mvn clean package`
