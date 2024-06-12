#### PROGRAM CHALLENGE ###
Time completed: 7hr 8min

## STREAM EDITOR ##
<p>Searches and replaces text or pattern<p></p>

### HOW-TO
<p>To run the application run command in project root</p>
<code>./gradlew run --args="[OPTIONS]"</code>

<p>Test files can be found in <code>[PROJECT_ROOT]/files</code></p>

<p>To search and replace for text:</p>
<code>./gradlew run --args="--directory=[DIRECTORY] --search=[TEXT] --replace=[REPLACEMENT]"</code>

<p>To ignore case:</p>
<code>./gradlew run --args="--directory=[DIRECTORY] --search=[TEXT] --replace=[REPLACEMENT] --ignore-case"</code>

<p>To search and replace for pattern:</p>
<code>./gradlew run --args="--directory=[DIRECTORY] --search=[REGEX_PATTERN] --replace=[REPLACEMENT]"</code>

<p>To backup files use <code>--backup</code> option</p>
<code>./gradlew run --args="--directory=[DIRECTORY] --search=[TEXT] --replace=[REPLACEMENT] --backup=[BACKUP_DIR]"</code>

<p>To list all modified files use <code>--output</code> option</p>
<code>./gradlew run --args="--directory=[DIRECTORY] --search=[TEXT] --replace=[REPLACEMENT] --output=[OUTPUT_FILE]"</code>

<p>To see the list of options:</p>
<code>./gradlew run --args="--help"</code>

    OPTIONS:
    -d, --directory             The target directory to search for files
    -s, --search                Text or pattern to search
    -r, --replace               The text to replace the searched pattern. Default is empty string
    -i, --ignore-case           Perform case-insensitive search
    -o, --output                Output file to write list of modified files. If output is directory, output.txt filename will be used. If not specified, no output file is created
    -b, --backupDir             Directory where to backup files. Default is the target directory for search
    -h, --help                  Display help
