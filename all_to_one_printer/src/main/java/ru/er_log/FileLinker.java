package ru.er_log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

import org.apache.commons.cli.*;

public class FileLinker
{
    private static final String appName = "AllToOnePrinter";
    private static final String appTask = "Print all contents into file";

    private String stringMask = null;
    private String stringDirectory = null;
    private String stringOutFile = null;
    private File fileDirectory = null;

    public void launch(String[] args)
    {
        parseArgs(args);

        Log.debug("Args:", ", ", this.stringMask, this.stringDirectory, this.stringOutFile);

        final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + this.stringMask);
        FileFilter fileFilter = new FileFilter()
        {
            @Override
            public boolean accept(File pathname)
            {
                if (pathname.isDirectory() || matcher.matches(Paths.get(pathname.getName())))
                    return true;

                return false;
            }
        };

        this.fileDirectory = new File(this.stringDirectory);
        String readText = read(this.fileDirectory, fileFilter);

        if (write(new File(this.stringOutFile), readText))
            Log.out("Operation succeed! Total length: " + readText.length() + " ch");
        else
            Log.err("Operation not completed");
    }

    private boolean write(File output, String text)
    {
        if (output == null || text.isEmpty())
        {
            Log.warn("Nothing to write");
            return false;
        }

        if (!output.getParentFile().exists())
            output.getParentFile().mkdirs();

        if (!output.exists())
            try { output.createNewFile(); }
            catch (IOException e) { Log.fatal("Failed to create file " + output); }

        try
        {
            FileWriter fileWriter = new FileWriter(output);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(text);
            bufferedWriter.close();

            return true;
        } catch (IOException e)
        {
            Log.fatal("Failed to write in file " + output);
            return false;
        }
    }

    private String read(File directory, FileFilter filter)
    {
        if (!directory.isDirectory())
            Log.fatal(directory + " is not directory or doesn't exist!");

        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder lineBuilder = new StringBuilder();

        for (File file : directory.listFiles(filter))
        {
            if (file.isDirectory())
                stringBuilder.append(read(file, filter));

            if (file.isFile())
                try
                {
                    FileReader fileReader = new FileReader(file);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);

                    String line;
                    while ((line = bufferedReader.readLine()) != null)
                        lineBuilder.append(line).append("\n");

                    bufferedReader.close();
                    putFormattedString(stringBuilder, relativizePath(this.fileDirectory, file), lineBuilder.toString());
                    lineBuilder.setLength(0);
                } catch (IOException e)
                {
                    Log.warn("Can't read file: " + file);
                    continue;
                }
        }

        return stringBuilder.toString();
    }

    private String relativizePath(File base, File relative)
    {
        return base.toURI().relativize(relative.toURI()).getPath();
    }

    private void putFormattedString(StringBuilder stringBuilder, String filename, String text)
    {
        String filenameSeparator = new String();
        for (int i = 0; i < 80; i++)
            filenameSeparator = filenameSeparator + "+";

        String string = String.format("\n%s\n %s (length: %d ch)\n%s\n\n%s\n", filenameSeparator, filename, Integer.valueOf(text.length()), filenameSeparator, text.trim());
        stringBuilder.append(string);
    }

    void parseArgs(String[] args)
    {
        Options options = new Options();

        Option optionMask = new Option("m", "mask", true, "file mask, glob pattern (see for details: https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob)");
        optionMask.setRequired(true);
        options.addOption(optionMask);

        Option optionDir = new Option("d", "directory", true, "scanning directory");
        optionDir.setRequired(true);
        options.addOption(optionDir);

        Option optionOut = new Option("o", "output", true, "output filename");
        optionOut.setRequired(true);
        options.addOption(optionOut);

        DefaultParser defaultParser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try
        {
            cmd = defaultParser.parse(options, args);
        } catch (ParseException e)
        {
            formatter.printHelp(appName.toLowerCase(), options);
            Log.fatal(e.getMessage());
            System.exit(1);
        }

        this.stringMask = cmd.getOptionValue("mask");
        this.stringDirectory = cmd.getOptionValue("directory");
        this.stringOutFile = cmd.getOptionValue("output");
    }

    public static void main(String[] args)
    {
        new FileLinker().launch(args);
    }
}
