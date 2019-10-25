
[![GitHub Release](https://img.shields.io/github/release/codemonstur/filldb.svg)](https://github.com/codemonstur/filldb/releases) 
[![Build Status](https://travis-ci.org/codemonstur/filldb.svg?branch=master)](https://travis-ci.org/codemonstur/filldb)
[![Maintainability](https://api.codeclimate.com/v1/badges/cf3bf5cb7c1ae4f9f359/maintainability)](https://codeclimate.com/github/codemonstur/filldb/maintainability)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/dwyl/esta/issues)
[![HitCount](http://hits.dwyl.com/codemonstur/filldb.svg)](http://hits.dwyl.com/codemonstur/filldb)
[![Coverage Status](https://coveralls.io/repos/github/codemonstur/filldb/badge.svg?branch=master)](https://coveralls.io/github/codemonstur/filldb?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/0f23dda61a2f4ec4909827462b6aa47e)](https://www.codacy.com/manual/codemonstur/filldb)
[![Sputnik](https://sputnik.ci/conf/badge)](https://sputnik.ci/app#/builds/codemonstur/filldb)
[![MIT Licence](https://badges.frapsoft.com/os/mit/mit.svg?v=103)](https://opensource.org/licenses/mit-license.php)

## A tool for filling a Relational database

I looked around and couldn't find a decent database filling tool.
So I wrote one.

This tool only works with MariaDB.
Extending it to support other databases should not be hard, but I have no need at the moment.
Anyone that would like to implement it can help.

Meant to be used from the command line.

### Status

A little rough around the edges but generally functional.
Still missing:
- Decent command line help output
- Dealing with primary keys that span multiple columns (may or may not fail with the current code)

### Current features

- Schema agnostic, point it to a DB and it will figure things out from there
- Supports text, varchar, bigint, int and bit columns
- Can generate test data of various field types:
  street, city, state, country, email, name, first/middle/last name, hex, 
  phone number, timestamp, 
- Can download various lorum ipsums from generators online
- Can download various cartoons from webcomics

### Future features

There are some listed in `src/main/docs/ideas.md`.
More ideas are welcome.
As are bugs, comments and anything else really.

### Installation

1. Check out the code `git clone https://github.com/codemonstur/filldb.git`
2. Run `make install`
3. You can now run the code with `java -jar target/filldb.jar`
4. \[Optional] Copy the code to `/usr/local/bin`
5. \[Optional] Create an alias `alias filldb='java -jar /usr/local/bin/filldb.jar'`

The code requires Java 12.
