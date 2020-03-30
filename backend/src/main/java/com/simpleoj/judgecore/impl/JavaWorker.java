package com.simpleoj.judgecore.impl;

import com.simpleoj.judgecore.IWorker;
import com.simpleoj.models.db.Problem;
import com.simpleoj.models.db.Submission;
import com.simpleoj.models.db.TestCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class JavaWorker implements IWorker {
    private static String containerCwd = "/tmp/oj";

    @Override
    public void save(String cwd, Submission submission) throws IOException {
        Files.write(Paths.get(cwd, "Main.java"), submission.getCode().getBytes());
    }

    @Override
    public boolean compile(String cwd, Submission submission) {
        String cmd = String.format("docker run --rm -i -v %s:%s -w %s openjdk:8 /bin/sh -c \"javac Main.java\"",
                cwd, containerCwd, containerCwd);
        ProcessBuilder processBuilder = new ProcessBuilder(cmd.split(" "));

        Process process = null;
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            StringBuilder errMsg = new StringBuilder();
            String line;
            boolean isError = false;
            while ((line = br.readLine()) != null) {
                errMsg.append(line);
                isError = true;
            }
            if (isError) {
                submission.setStatus(Submission.STATUS_CE);
                submission.setError(errMsg.toString());
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void run(String cwd, Problem problem, List<TestCase> testCases, Submission submission) throws RuntimeException, IOException {
        String cmd = String.format("docker run --rm -i -v %s:%s -w %s openjdk:8 /bin/sh -c \"java Main\"",
                cwd, containerCwd, containerCwd);
        for (int i = 0; i < testCases.size(); i++) {
            String input = testCases.get(i).getInput();
            String expected = testCases.get(i).getExpected();
            String inputFileName = i + "-in.txt";
            String outputFileName = i + "-out.txt";
            String expectedFileName = i + "-expected.txt";
            File outputFile = new File(cwd, outputFileName);

            Files.write(Paths.get(cwd, inputFileName), input.getBytes());
            Files.write(Paths.get(cwd, expectedFileName), expected.getBytes());
            outputFile.createNewFile();

            ProcessBuilder processBuilder = new ProcessBuilder(cmd.split(" "));
            processBuilder.redirectInput(new File(cwd, inputFileName));
            processBuilder.redirectOutput(outputFile);

            Process process = null;

            process = processBuilder.start();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                StringBuilder errMsg = new StringBuilder();
                String line;
                boolean isError = false;
                while ((line = br.readLine()) != null) {
                    errMsg.append(line);
                    isError = true;
                }
                if (isError) {
                    submission.setStatus(Submission.STATUS_RE);
                    submission.setError(errMsg.toString());
                }
            }

            String output = Files.readString(Paths.get(cwd, outputFileName));
            if (output.trim().equals(expected.trim())) {
                submission.setStatus(Submission.STATUS_AC);
            } else {
                submission.setStatus(Submission.STATUS_WA);
            }
        }
    }
}
