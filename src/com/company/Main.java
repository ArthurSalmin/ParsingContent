package com.company;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;


public class Main {
    private static final String _defaultPath = "/home/arthur/IdeaProjects/parsingContentApp/files";
    private static AtomicLong recordsCount = new AtomicLong(0);
    private static AtomicLong filesCount = new AtomicLong(0);

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        String[] allPaths = new String[]
                {
                        _defaultPath + "/archives",
                        _defaultPath + "/emails",
                        _defaultPath + "/images",
                        _defaultPath + "/text"
                };
        for (String currentPath : allPaths) {
            new Thread(() -> {
                Path folderPath = Paths.get(currentPath);
                System.out.println(currentPath + "Folder now in progress");
                Map<String, List<String>> filesLines = new TreeMap<>();

                List<String> fileNames = new ArrayList<>();
                try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folderPath)) {
                    for (Path path : directoryStream) {
                        fileNames.add(path.toString());
                    }
                } catch (IOException ex) {
                    System.err.println("Error reading files");
                    ex.printStackTrace();
                }

                for (String file : fileNames) {
                    try {
                        List<String> lines = Files.readAllLines(folderPath.resolve(file));
                        filesLines.put(file, lines);
                        recordsCount.addAndGet(lines.size());
                        filesCount.incrementAndGet();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                filesLines.forEach(((String fileName, List<String> lines) ->
                {
                    System.out.println("Read");
                    System.out.println("Content of" + fileName + " is:");
                    lines.forEach(System.out::println);
                    System.out.println("-----------------------------");
                }));
            }).start();
        }
        System.out.println(("Count of files processed " + filesCount));

        long endTime = System.currentTimeMillis();

        Long duration = (endTime - startTime);

        double filesPerSec = duration.doubleValue()/ 1000 / filesCount.doubleValue();
        double recordsPerSec = duration.doubleValue() / 1000 / recordsCount.doubleValue();

        System.out.println("Files/sec : " + filesPerSec);
        System.out.println("Records/sec : " + recordsPerSec);
        System.out.print(duration + " millis");
    }

    
}
