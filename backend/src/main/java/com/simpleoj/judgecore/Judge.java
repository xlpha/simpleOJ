package com.simpleoj.judgecore;

import com.simpleoj.judgecore.languageconfig.LanguageConfig;
import com.simpleoj.models.db.Problem;
import com.simpleoj.models.db.Submission;
import com.simpleoj.models.db.TestCase;
import com.simpleoj.repositories.SubmissionRepository;
import org.json.JSONObject;
import org.springframework.util.FileCopyUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;

public class Judge {
    private Submission submission;
    private Problem problem;
    private List<TestCase> testCases;
    private LanguageConfig languageConfig;

    private String cwd;
    private final String baseDir;
    private final String containerCwd;
    private final String judgeCoreRunnerPath;


    public Judge(Submission submission, Problem problem, List<TestCase> testCases, LanguageConfig languageConfig,
                 String baseDir, String containerCwd, String runnerPath) {
        this.submission = submission;
        this.problem = problem;
        this.testCases = testCases;
        this.languageConfig = languageConfig;
        this.baseDir = baseDir;
        this.containerCwd = containerCwd;
        this.judgeCoreRunnerPath = runnerPath;
    }

    private void saveFile() throws IOException {
        String filename = languageConfig.getSaveFileName();
        Files.write(Paths.get(cwd, filename), submission.getCode().getBytes());
    }

    private boolean compile() {
        String compileCmd = languageConfig.getCompileCmd();
        String imageName = languageConfig.getDockerImageName();
        if (compileCmd == null) return true;    // 有些语言不需要编译，比如Python
        List<String> cmdList = Arrays.asList("docker", "run", "--rm", "-v", cwd + ":" + containerCwd, "-w", containerCwd,
                imageName, "/bin/sh", "-c", compileCmd);

        System.out.println("compile cmd: " + cmdList.toString());
        ProcessBuilder processBuilder = new ProcessBuilder(cmdList);

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
                if (errMsg.length() > 500) break;
                isError = true;
            }
            if (isError) {
                submission.setStatus(Submission.STATUS_CompileError);
                submission.setError(errMsg.toString());
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public int run() throws RuntimeException, IOException {
        int timeLimit = problem.getRuntimeLimit();
        int memoryLimit = problem.getMemoryLimit() * 1024 * 1000;
        String imageName = languageConfig.getDockerImageName();
        int maxTimeUsed = -1;
        int maxMemoryUsed = -1;
        for (int i = 0; i < testCases.size(); i++) {
            String input = testCases.get(i).getInput();
            String expected = testCases.get(i).getExpected();
            String inputFileName = i + "-in.txt";
            String outputFileName = i + "-out.txt";
            String expectedFileName = i + "-expected.txt";

            Files.write(Paths.get(cwd, inputFileName), input.getBytes());
            Files.write(Paths.get(cwd, expectedFileName), expected.getBytes());

            String runCmd = String.format("./judgeCore '%s' %d %d %s %s",
                    languageConfig.getRunCmd(), timeLimit, memoryLimit, inputFileName, outputFileName);
            List<String> cmdList = Arrays.asList("docker", "run", "--rm", "-v", cwd + ":" + containerCwd,
                    "-w", containerCwd, imageName, "/bin/sh", "-c", runCmd);
            System.out.println("run cmd: " + cmdList.toString());
            ProcessBuilder processBuilder = new ProcessBuilder(cmdList);

            Process process = processBuilder.start();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                while (true) {
                    String line = br.readLine();
                    if (line == null) break;
                    sb.append(line);
                }
                String resultStr = sb.toString();
                if (resultStr.isEmpty()) return Submission.STATUS_SERVER_INTERNAL_ERROR;
                JSONObject result = new JSONObject(resultStr);
                int status = result.getInt("status");
                int timeUsed = result.getInt("timeUsed");
                int memoryUsed = result.getInt("memoryUsed");
                if (status == Submission.STATUS_Accept) {
                    String output = Files.readString(Paths.get(cwd, outputFileName));
                    if (!output.trim().equals(expected.trim())) {
                        return Submission.STATUS_WrongAnswer;
                    } else {
                        if (timeUsed > maxTimeUsed) maxTimeUsed = timeUsed;
                        if (memoryUsed > maxMemoryUsed) maxMemoryUsed = memoryUsed;
                    }
                } else {
                    return status;
                }
            }
        }
        submission.setRuntime((long) maxTimeUsed);
        submission.setMemory((long) maxMemoryUsed);
        return Submission.STATUS_Accept;
    }

    public void process(SubmissionRepository submissionRepository) {
        submission.setStatus(Submission.STATUS_RUNNING);
        submissionRepository.save(submission);

        cwd = baseDir + File.separator + submission.getId();
        new File(cwd).mkdirs();
        try {
            FileCopyUtils.copy(new File(judgeCoreRunnerPath), new File(cwd, "judgeCore"));
            changeFilePermission(new File(cwd, "judgeCore"));
            saveFile();
        } catch (IOException e) {
            e.printStackTrace();
            submission.setStatus(Submission.STATUS_SERVER_INTERNAL_ERROR);
            submissionRepository.save(submission);
            return;
        }

        boolean ok = compile();
        if (!ok) {
            submission.setStatus(Submission.STATUS_CompileError);
            submissionRepository.save(submission);
            return;
        }
        int status = Submission.STATUS_SERVER_INTERNAL_ERROR;
        try {
            status = run();
        } catch (IOException e) {
            e.printStackTrace();
        }
        submission.setStatus(status);
        submissionRepository.save(submission);
    }

    private void changeFilePermission(File dirFile) throws IOException {
        Set<PosixFilePermission> perms = new HashSet<>();
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);
        perms.add(PosixFilePermission.OWNER_EXECUTE);
        perms.add(PosixFilePermission.GROUP_READ);
        perms.add(PosixFilePermission.GROUP_WRITE);
        perms.add(PosixFilePermission.GROUP_EXECUTE);
        Path path = Paths.get(dirFile.getAbsolutePath());
        try {
            Files.setPosixFilePermissions(path, perms);
        } catch (UnsupportedOperationException e) {
        }
    }
}