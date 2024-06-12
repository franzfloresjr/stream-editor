package io.franzfloresjr.streameditor

import groovy.util.logging.Slf4j
import org.apache.commons.cli.*

/**
 * <p>Application entry point</p?
 * <p>Utilizes Apache Commons CLI for ease of supplying command-line arguments</p>
 * @author Francisco Flores Jr.
 */
@Slf4j
class Main {
    /**
     * Command-line options
     */
    private static final Options options = new Options()
            .addOption('d', 'directory', true, 'The target directory to search for files')
            .addOption('s', 'search', true, 'Text or pattern to search')
            .addOption('r', 'replace', true, 'The text to replace the searched pattern. Default is empty string')
            .addOption('i', 'ignore-case', false, 'Perform case-insensitive search')
            .addOption('o', 'output', true, 'Output file to write list of modified files. If output is directory, output.txt filename will be used. If not specified, no output file is created')
            .addOption('b', 'backupDir', true, 'Directory where to backup files. Default is the target directory for search')
            .addOption('h', 'help', false, 'Display help')

    static void main(String[] args) {
        log.info('Starting application...')
        try {
            CommandLineParser parser = new DefaultParser()
            CommandLine cmd = parser.parse(options, args)

            // if help option is present, display help
            if (cmd.hasOption('h')) {
                displayHelp()
            }

            if (!cmd.hasOption('d')) {
                throw new MissingOptionException('directory must not be null. Please specify target directory for search')
            }

            if (!cmd.hasOption('s')) {
                throw new MissingOptionException('search must not be blank. Please specify the text or pattern to search')
            }
            def editor = new StreamEditor()
            editor.ignoreCase = cmd.hasOption('i')

            if(cmd.hasOption('b')) {
                editor.backupDir = cmd.getOptionValue('b')
            }

            if(cmd.hasOption('o')) {
                editor.outputFile = cmd.getOptionValue('o')
            }

            def directory = cmd.getOptionValue('d')
            def search = cmd.getOptionValue('s')

            // Default will replace search with empty string
            def replace = cmd.getOptionValue('r', '')

            // search for files recursively in the 'directory' replacing the 'search' with 'replace'
            editor.replace(directory, search, replace)

        } catch (UnrecognizedOptionException | MissingOptionException e) {
            log.error(e.getMessage())
            displayHelp()

        } catch (Exception e) {
            log.error(e.message, e)
        }finally {
            log.info('Exiting application...')
        }
    }

    static void displayHelp() {
        println '\nCommand:'
        println './gradlew run --args="[OPTIONS]"\n'
        println 'Options:'
        options.getOptions().each {
            println "    -${it.opt}${it.hasLongOpt() ? ', --' + it.longOpt : ''}\t\t${it.description}"
        }
    }
}