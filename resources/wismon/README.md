# OpenWIS Monitoring Tool for WMO Common Dashboard Pilot

## Introduction
The WMO Common Dashboard (WCD) pilot project requires each participating centre
to provide three JSON files containing messages with a format conforming to the
spec. 
The three JSON files are `monitor.json`, `centres.json` and `events.json`. 

The OpenWIS Monitoring Tool (`wismon`) is a command line utility that helps
creating the necessary JSON files to feed the WCD pilot.
Though majority part of the metrics are optional except three
elements in `monitor.json`, the tool generates all three JSON files with all
elements to maximize contributions from OpenWIS centres.

The three JSON files are the final output from `wismon`. The tool does not
attempt to serve the files on web. It is up to users how to expose the JSON
files so they can be accessed through HTTP.

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
4. To check whether the installation is successful, type and run `wismon -h`
   and it should display help messages.
    * If installed using user scheme, the full path may be required to call the
      command, such as `~/.local/bin/wismon -h`

### Manual Installation
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
    |-- WisMon.py
    |-- db.py
    `-- templates.py
```
Be sure to copy the **inner** `wismon` folder into `PYTHONPATH`.

Once installed, type and run `python -m wismon -h` or 
`python -m wismon.__main__ -h` (Python 2.6) 
and ensure help messages are displayed. To avoid typing the long command
repetitively, an **alias** can be created such as 
`alias wismon="python -m wismon"`.

The term `wismon` will be used hereafter to refer both the script installed
by setuptools and the alias created via manual installation.

### Upgrade
Upgrade from an existing installation of `wismon` is essentially no different
from a fresh installation. Simply repeat what has been done for the previous
installation to overwrite old files (uninstallation is NOT required).


## Usage
In general, the `wismon` command is invoked as follows:
```bash
wismon -d PATH_TO_WORKING_DIRECTORY [-l] SUB-COMMAND [OPTIONS]
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
  [ISO 8601](http://en.wikipedia.org/wiki/ISO_8601)) 
    - `init` - Initialize a working directory with a template of the
      configuration file and other necessary folder structure. More details can
      be found in the [Setup](#setup) section.
    - `json-gen` - Generate today's monitoring JSON files. 
        * This is the core sub-command that is assumed to run daily.
        * **Does nothing if JSON files of the day already exist**.
    - `json-get` - Retrieve JSON message of the given Date and Name
        * Date is of format YYYY-MM-DD (default is today)
        * Name is the JSON message names, i.e. `monitor`, `centres` or `events`
          (default is `monitor`)
    - `json-del` - Delete JSON messages of the given Date
        * It deletes all three JSON messages for the given Date
        * It only deletes entries in the local SQLite database and does NOT
          remove the JSON files (since the given Date may not be the present day
          whose data the JSON files are always based on).
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
      Datetime
        * Datetime is of format YYYY-MM-DDThh:mm:ssZ (default is now)
    - `event-del` - Delete an event with the given event ID
        * The event ID can be checked using the `event-get` sub-command
    - `remarks-set` - Set text of the `remarks` field in `monitor.json` with
      the given string
        * Content of the remarks is included in `monitor.json`when `json-gen` runs.
        * Set the remarks to empty if no string is given 
    - `remarks-get` - Get the content of the current remarks

## <a id="setup"></a>Setup
Once `wismon` is installed, a setup process is required to make the tool to
perform the day-to-day monitoring task. 
* To generate the monitoring JSON files every 24 hours as required by the WCD
  pilot project, a **Cron** job needs to be created to run `wismon` each day at
  00 UTC. Such a Cron job can be created as something like the follows:

  ```
  00 00 * * * wismon -d PATH_TO_WORKING_DIRECTORY json-gen
  ```
    - If `wismon` is installed manually, a crob job may not recognize the alias
      (depending on where the alias is set), hence the full command may be
      necessary:

      ```
      00 00 * * * python -m wismon -d PATH_TO_WORKING_DIRECTORY json-gen
      ```
      For Python 2.6, append the object name to the module:

      ```
      00 00 * * * python -m wismon.__main__ -d PATH_TO_WORKING_DIRECTORY json-gen
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
      |   `-- wismon.cfg
      |-- data
      |   `-- JSON/       <-- where JSON files are stored
      `-- logs/
      ```
    * The configuration file has quite a few options to be filled. Fortunately,
      most of them are very straightforward. **Detailed explanation of each option
      can be found in the template file**. 
    * The three **JSON files are saved in** `PATH_TO_WORKING_DIRECTORY/data/JSON/`.
      When `json-gen` task runs, these files are updated to hold the most recent
      stats.
    * JSON files of previous days are not lost. All data are archived in a
      SQLite database under the data directory, i.e.
      `PATH_TO_WORKING_DIRECTORY/data/wismon.sqlite3`. 
        - Use `json-get` sub-command to retrieve JSON message of a given date
          and name
    * The log files are saved in `PATH_TO_WORKING_DIRECTORY/logs/`. 


## Program Internals
This section provides some more explanations about how `wismon` works
internally. 

* To calculate monitoring stats, especially the numeric ones, `wismon` needs to
  send **one** query to the OpenWIS database and save the results as a snapshot
  into local SQLite database for further calculations. 
* The query to OpenWIS database only runs when performing the `json-gen`
  sub-command. Normally it runs only every 24 hours. The query 
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
    - Note that the previous snapshot is often the one from the previous day (24
      hours apart if a Crob job runs normally). However it is not the case when
      `json-del` is used. For an example, if JSON files are already generated for
      the day, i.e. `json-gen` has run once and the snapshot is taken.
      When `json-del` runs, it deletes only the JSON messages, NOT the snapshot.
      When `json-gen` runs again after `json-del`, the previous snapshot is the
      one taken by the first run of `json-gen`, hence NOT from the previous day.
* List of files
    - `db.py` - It defines the SQL queries needed for the monitoring task and also
      handles the connections to OpenWIS and `wismon` local database. 
    - `WisMon.py` - It performs the actual monitoring task and includes a
      command line interface.
    - `templates.py` - It provides helper utilities for creating the JSON files.
      It also includes the template of the configuration file.
    - `__main__.py` - It makes `wismon` runnable as a module.
    - `__init__.py` - It is what makes a regular folder to a Python module.


