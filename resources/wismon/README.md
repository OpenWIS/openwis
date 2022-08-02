# OpenWIS Monitoring Tool for WMO WIS Monitoring Project

**NOTE: The command line usage is changed with the new version (0.3.0) as a
result of implementing the new monitoring spec.** 
Mostly notable changes are:

* All `json-xxx` sub-commands now requires a name to specify which monitoring
  message (e.g. `monitor`, `cache`, `centres`, `events`) to generate.
* Centres JSON and Events JSON are generated separately from Monitor JSON and 
  Cache JSON. This often means two Cron jobs are needed to schedule the file
  creations.
Details can be found in the [Usage](#usage) and [Setup](#setup) sections.

## Introduction
The WMO Common Dashboard (WCD) pilot project requires each participating centre
to provide a few JSON files containing messages with a format conforming to the
spec. 
The JSON files are `monitor.json`, `cache.json`, `centres.json` and `events.json`. 

The OpenWIS Monitoring Tool (`wismon`) is a command line utility that helps
creating necessary JSON files for downstream aggregation applications to consume.
The JSON files are the final output from `wismon`. The tool does not
attempt to serve the files on web. It is up to users how to expose the JSON
files so they can be accessed through HTTP per required by the monitoring spec.

The program has been tested and verified to work with OpenWIS version 3.12 and
3.13. Tests are not performed with earlier versions of OpenWIS and it is likely
that some of them are not supported (e.g. the tool does not run with OpenWIS
3.5). 

## Installation
Since `wismon` needs to query OpenWIS database to calculate the metrics, it
**must be installed on a machine where it has access to the database server**.
A few possible candidates for installation are the Admin Service Server, the
Portal Server or the Database Server itself. The tool does NOT access any
OpenWIS log files.

The tool requires **Python version 2.6 or 2.7**.
Installation can be handled automatically by Python
[setuptools](https://pypi.python.org/pypi/setuptools/) (easy and recommended)
or users can manually install it by copying a handful of files.
A privileged account is NOT required for installation.

### With Python setuptools
1. Download and extract the source code
2. On a terminal, navigate into the source code folder, 
   e.g. `cd /Path_To_Extraction/wismon`
3. Type `python setup.py install`
    * If install with an unprivileged account, type `python setupt.py install
      --user` to install using user scheme (i.e. it installs everything under
      `~/.local/` on Linux)
4. To check whether the installation is successful, type and run `wismon -v`
   and it should output `wismon: v0.3.0`
    * If installed using user scheme, the full path may be required to call the
      command, such as `~/.local/bin/wismon -v`

### Manual installation
For manual installation, users have to manually install following two
pure-Python modules as dependencies:

* [`pg8000`](https://pypi.python.org/pypi/pg8000) to interface
  OpenWIS Postgres database. Latest release (1.10.1) is required.

* [`argpase`](https://pypi.python.org/pypi/argparse) is required for Python 2.6.
  It is not needed for Python 2.7 as it is part of the standard
  libraries. Version 1.1 or up is required.

The `wismon` tool itself is also a Python module that needs to be installed
manually after above dependencies are satisfied. 

Manual installation of pure-Python modules really is just about copying the
module folder into system's `PYTHONPATH`. If `PYTHONPATH` is not defined, it
can be added as `export PYTHONPATH=/home/foo/pylibs`. 
The modules can then be placed inside `/home/foo/pylibs` as shown in following
folder structure:

```
/home/foo/pylibs/
|-- MODULE_FOLDER
|   |-- __init__.py
|   `-- ...
`-- ANOTHER_MODULE_FOLDER
    |-- __init__.py
    `-- ...
```
Note that MODULE_FOLDER is the **nested folder** in the module source tree
after extraction, **NOT the topmost folder** that contains all source files
(these two folders often have the same name). For an example, the folder
structure of the `wismon` module after extraction is as follows:

```
wismon      <-- topmost folder
|-- README.md
|-- requirements.txt
|-- setup.py
`-- wismon      <-- this is the module folder
    |-- __init__.py
    |-- __main__.py
    |-- wismon.py
    |-- db.py
    |-- templates.py
    `-- config_templates.cfg
```
Be sure to copy the **inner** `wismon` folder into `PYTHONPATH`.

Once installed, type and run `python -m wismon -h` or 
`python -m wismon.__main__ -h` (Python 2.6) 
and ensure help messages are displayed. To avoid typing the long command
repetitively, an **alias** can be created such as 
`alias wismon="python -m wismon"`.

The term `wismon` will be used hereafter to refer both the script installed
by setuptools and the alias created via manual installation.

## Upgrade
Upgrade from an existing installation of `wismon` is essentially no different
from a fresh installation. Simply repeat what has been done for the previous
installation to overwrite old files. 

If the previous version was installed with setuptools and pip is available on
the system, it can be uninstalled with `pip uninstall wismon` first. 
However uninstallation is NOT required.

### Database migration (Optional)
This step is optional and necessary if previously generated JSON messages need
to be retained. 

1. Locate the sqlite database file used by `wismon` (it is under the `data`
   sub-folder of the wismon working directory)
2. Navigate into the `data` folder and launch sqlite session with `sqlite
   wismon.sqlite3`.
3. Run following SQL commands for data migration.
    ```sql
    CREATE TABLE wismon_named_json (
            id INTEGER NOT NULL PRIMARY KEY,
            datetime DATETIME NOT NULL,
            name VARCHAR(255) NOT NULL,
            content TEXT NOT NULL,
            timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
        );

    INSERT INTO wismon_named_json (datetime, name, content) 
    SELECT DATE || 'T00:00:00Z', 'monitor', monitor_json FROM wismon_json;

    INSERT INTO wismon_named_json (datetime, name, content) 
    SELECT DATE || 'T00:00:00Z', 'cache', centres_json FROM wismon_json;

    INSERT INTO wismon_named_json (datetime, name, content) 
    SELECT DATE || 'T00:00:00Z', 'events', events_json FROM wismon_json;

    DROP TABLE wismon_json;
    ```
4. Type `.exit` to quit the sqlite session.


## Usage
In general, the `wismon` command is invoked as follows:
```bash
wismon -d PATH_TO_WORKING_DIRECTORY [-l] SUB-COMMAND [OPTIONS] ...
```
Users can always type `wismon -h` to display built-in help messages.

* `PATH_TO_WORKING_DIRECTORY` is the directory where input configuration file
  is read and output files (JSON files, log files etc.) are stored. 
    - The configuration file contains OpenWIS Postgres database account
      information and metadata about WIS monitoring. Detailed instruction for
      setting up the working directory can be found in the [Setup](#setup)
      section.
    - `PATH_TO_WORKING_DIRECTORY` **must be specified each time the command is
      called** (except when just displaying help messages with the `-h` option.
      Aliases can be created to reduce repetitive typings).

* The logging messages are kept in log files which rotate every one MB (a
  maximum of five log files are archived). When the `-l` option is set, 
  the logging message will also be printed to the console. 

* `SUB-COMMAND` is the task to be performed by `wismon`. Type `wismon
  SUB-COMMAND -h` also shows usage information about a sub-command. Note that
  all datetime values must be in **UTC** and conforms to 
  [ISO 8601](http://en.wikipedia.org/wiki/ISO_8601)) (e.g. `YYYY-MM-DD` or `YYYY-MM-DDTHH:MM:SSZ`)
    - `init` - Initialize a working directory with a template of the
      configuration file and other necessary folder structure. More details can
      be found in the [Setup](#setup) section.

    - `json-gen` - Generate monitoring JSON message of the given name (`monitor`, 
        `cache`, `centres`, `events`). 
        * This is the main sub-command that should run on a schedule.
        * Monitor and Cache JSON messages are always generated together.
          This means that the names of `monitor` and `cache` are essentially
          identical for program execution.
        * **Does nothing if required JSON file already exist
          (unless forced with the `-f` flag)**.

    - `json-get` - Retrieve JSON message of the given name and datetime
      (default to the most recent entry).

    - `json-del` - Delete JSON message of the given name and datetime from the
      database (default to the most recent entry).
        * It only deletes the entry in the local SQLite database and does NOT
          remove any JSON files.

    - `event-add` - Add an event by specifying its title and start/end datetime
        * When `json-gen`runs, it searches for events that are in range of the
          present day and adds them to `events.json`.
        * Following command is an example for adding a new event: 

        ```bash
        wismon -d PATH_TO_WORKING_DIRECTORY event_add \
                -t "Maintenance" \
                -d "Cache and metadata catalogue cleanup" \
                -s 2015-02-01T00:00:00Z \
                -e 2015-02-01T01:30:00Z
        ```
    - `event-get` - List all events that have end datetime later than the given
      datetime (default to now)

    - `event-del` - Delete an event with the given event ID
        * The event ID can be checked using the `event-get` sub-command

    - `remarks-set` - Set text of the `remarks` field in `monitor.json` with
      the given string
        * Content of the remarks is included in `monitor.json` the next the message is generated. 
        * Set the remarks to empty if no string is given 

    - `remarks-get` - Get the content of the current remarks

## Setup
Once `wismon` is installed, a setup process is required to configure the tool to
perform the actual monitoring tasks. 

* First a working directory must be initialized as described in the
  [Usage](#usage) section.

* To generate the monitoring JSON files on a regular time basis,
  two **Cron** job needs to be created to run `wismon`. 
  One job runs every 24 hours to generate Monitor and Cache JSON files and one
  job runs every 10 minutes to generate Centres JSON file.
  00 UTC. Such Cron jobs can be created like the follows:

  ```
  00 00 * * * wismon -d PATH_TO_WORKING_DIRECTORY json-gen monitor
  0,10,20,30,40,50 */1 * * * wismon -d PATH_TO_WORKING_DIRECTORY json-gen centres
  ```

    - If `wismon` is installed manually, a crob job may not recognize the alias
      (depending on where the alias is set), hence the full command may be
      necessary:

      ```
      00 00 * * * python -m wismon -d PATH_TO_WORKING_DIRECTORY json-gen monitor
      0,10,20,30,40,50 */1 * * * python -m wismon -d PATH_TO_WORKING_DIRECTORY json-gen centres
      ```
      For Python 2.6, append the object name to the module:

      ```
      00 00 * * * python -m wismon.__main__ -d PATH_TO_WORKING_DIRECTORY json-gen monitor
      0,10,20,30,40,50 */1 * * * python -m wismon -d PATH_TO_WORKING_DIRECTORY json-gen centres
      ```
* A working directory is where input configuration file and output files are
  stored. It has to be correctly initialised before any meaningful work can be
  done. 
    * To initialize a working directory, type and run 
      `wismon -d PATH_TO_WORKING_DIRECTORY init`. 
      This command creates the given directory (if not exists already) and
      initialise it with a **template** of the configuration file and required
      folder structure as follows:

      ```
      WORKING_DIRECTORY/
      |-- config
      |   `-- wismon.cfg  <-- a placeholder config file
      |-- data
      |   `-- JSON/       <-- where JSON files are stored
      `-- logs/
      ```
    * The configuration file has quite a few options to be filled. Fortunately,
      most of them are very straightforward. **Detailed explanation of each option
      can be found in the generated placehoder config file**. 
    * The **JSON files are saved in** `PATH_TO_WORKING_DIRECTORY/data/JSON/`.
      When `json-gen` task runs, these files are updated to hold the most recent
      stats.
    * JSON files of previous runs are not lost. All data are archived in a
      SQLite database under the data directory, i.e.
      `PATH_TO_WORKING_DIRECTORY/data/wismon.sqlite3`. 
    * The log files are saved in `PATH_TO_WORKING_DIRECTORY/logs/`. 


## Program Internals
This section provides some more explanations about how `wismon` works
internally. 

* To calculate monitoring stats, especially the numeric ones, `wismon` needs to
  send **one** query to the OpenWIS database and save the results as a snapshot
  into local SQLite database for further calculations. 
* The query to OpenWIS database only runs when performing the `json-gen`
  sub-command and . Normally it runs only every 24 hours. The query 
  finishes in 10 - 20 seconds from establishing the connection and
  disconnecting from the server. Therefore, it should have very minimal
  performance impact on the OpenWIS system.
* The statistics are calculated using the snapshot saved in the local SQLite
  database. Hence this calculation dot not affect OpenWIS system at all. It
  takes 30-40 seconds to finish. So the total time needed for an complete
  `json-gen` run is under one minute.
    - Detailed time information is available in the log files.
    - Number of metadata changes are calculated by comparing today's snapshot
      against the **previoius snapshot**.
* List of files
    - `db.py` - It abstracts the database operations for both OpenWIS database
      and `wismon` local database. 
    - `wismon.py` - The main program that implements the montoring logic.
    - `templates.py` - It handles creation and formatting of the JSON messages.
    - `__init__.py` - It provides a command line interface to the tool and is
      also what makes a regular folder to a Python module.
    - `__main__.py` - It makes `wismon` runnable as a module.


