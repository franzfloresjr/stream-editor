package io.franzfloresjr.streameditor

import groovy.util.logging.Slf4j

import java.nio.file.CopyOption
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.time.Instant
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Scan directory and subdirectories for files and replaces match text or pattern
 *
 * @author Francisco Flores jr.
 */
@Slf4j
class StreamEditor {

    File outputFile
    File backupDir
    boolean ignoreCase

    private File targetDirectory
    private List<File> targetFiles = []
    private List<File> modifiedFiles = []
    /**
     * The current working directory
     */
    private static final Path CURRENT_WORKING_DIRECTORY = Path.of(new File(".").canonicalPath)

    def setOutputFile(String outputFile) {
        File file = new File(outputFile)
        if (file.absolute) {
            setOutputFile file
        } else {
            Path resolvedPath = CURRENT_WORKING_DIRECTORY.resolve(file.toPath()).normalize()
            setOutputFile resolvedPath.toFile()
        }
    }

    def setOutputFile(File outputFile) {
        this.outputFile = outputFile
    }

    def setBackupDir(File backupDir) {
        this.backupDir = backupDir
    }

    def setBackupDir(String backupDir) {
        File file = new File(backupDir)

        if (file.absolute) {
            setBackupDir file
        } else {
            Path resolvedPath = CURRENT_WORKING_DIRECTORY.resolve(backupDir).normalize()
            setBackupDir resolvedPath.toFile()
        }
    }

    /**
     * <p>Scans for files recursively in {@code directory}
     * and search for files that contains text or pattern {@code search} replacing with {@code replace}.
     * If a match is found, a backup file will be created</p>
     *
     * @param directory The target to directory to scan for files
     * @param search The text or pattern to search
     * @param replace The replacement text
     */
    def replace(String directory, String search, String replace) {
        Path filepath = Path.of(directory)

        if (filepath.absolute) {
            this.targetDirectory = filepath.toFile()
        } else {
            Path resolvedPath = CURRENT_WORKING_DIRECTORY.resolve(directory).normalize()

            this.targetDirectory = resolvedPath.toFile()
        }

        if (!search || search.empty) {
            throw new NullPointerException("search must not ne null")
        }

        log.info("Searching for '{}' in files in '{}'", search, this.targetDirectory.path)
        scanForFiles this.targetDirectory
        doReplace search, replace
    }

    /**
     * Scan all files in the directory and all subdirectories
     * @param directory The target directory for scan
     */
    def scanForFiles(File directory) {
        directory.listFiles().each {
            if (it.directory) {
                // scan for files in the subdirectory as well
                scanForFiles it
            } else {
                targetFiles << it
            }
        }
    }

    /**
     * <p>Searches for files that contains text or pattern {@code search} replacing with {@code replace}.
     * If a match is found, a backup file will be created</p>
     *
     * @param search The text or pattern to search
     * @param replace The replacement text
     */
    private def doReplace(String search, String replace) {

        int flag = 0

        if (ignoreCase) {
            flag |= Pattern.CASE_INSENSITIVE
        }

        // Using Pattern.compile() is more efficient than using String.matches()
        // since we have to compile the pattern once
        Pattern pattern = Pattern.compile(search, flag)

        targetFiles.each {

            // Since we might be searching for a pattern that might includes line feed in between the text, we should retrieve whole content
            // For example for the given text:
            //
            //     Hello
            //     World
            //
            // reading the file by each line wont match the pattern 'Hello\nWorld'
            def content = it.text

            Matcher matcher = pattern.matcher(content)

            // only find the first occurrence to improve performance
            if (!matcher.find()) {
                return
            }

            backup it
            it.text = matcher.replaceAll(replace)
            log.info "Found '{}' in '{}'", search, it.path

            modifiedFiles << it
        }

        if (modifiedFiles.size() == 0) {
            log.info "No match found."
        }

        if (!outputFile) {
            return
        }

        if (modifiedFiles.empty) {
            return
        }

        // create output file if not exist
        if (!outputFile.exists()) {
            log.info("Output file does not exists. Creating in '{}'", outputFile.path)
            File parent = outputFile.parentFile

            if (!parent.exists()) {
                Files.createDirectories parent.toPath()
                parent.writable = true
            }

            Files.createFile outputFile.toPath()
            outputFile.writable = true
        } else if (outputFile.directory) {
            this.outputFile = new File(outputFile.path, "output.txt")
        }

        // list all modified files in the output file
        modifiedFiles.each {
            // use operating system line separator
            def filename = it.path + System.getProperty("line.separator")
            outputFile.append filename
        }
        log.info("Updated {} files", modifiedFiles.size())
        log.info("Writing list of modified files into output file: {}", outputFile.path)
    }

    private def backup(File source) {
        File target
        if (!backupDir) {
            // Backup directory is not set.
            // Backup files in the current directory of the file to be modified
            // Append '.orig' suffix
            target = new File(source.path + ".orig")
        } else {
            // create backup directory if not exists
            if (!this.backupDir.exists()) {
                Path backupDir = Files.createDirectories this.backupDir.toPath()
                this.backupDir.writable = true
                log.info("Backup directory created: {}", backupDir)
            }

            target = new File(backupDir.path, source.name + "." + Instant.now().getEpochSecond())
        }

        CopyOption[] copyOptions = [StandardCopyOption.REPLACE_EXISTING]
        Files.copy(source.toPath(), target.toPath(), copyOptions)
    }

}
