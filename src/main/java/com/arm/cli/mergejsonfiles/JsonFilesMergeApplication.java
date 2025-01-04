package com.arm.cli.mergejsonfiles;

import com.arm.cli.mergejsonfiles.cli.CliExecutor;
import com.arm.cli.mergejsonfiles.cli.OptionParser;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import static java.lang.System.exit;

/**
 * Spring boot application class to start service
 */
@SpringBootApplication
public class JsonFilesMergeApplication implements ApplicationRunner {
    private final ApplicationContext applicationContext;
    private final CliExecutor cliExecutor;

    public JsonFilesMergeApplication(final ApplicationContext applicationContext,
                                     final CliExecutor cliExecutor) {
        this.applicationContext = applicationContext;
        this.cliExecutor = cliExecutor;
    }

    /**
     * Main method to start program.
     *
     * @param args program arguments.
     */
    public static void main(final String... args) {
        SpringApplication.run(JsonFilesMergeApplication.class, args);
    }

    /**
     * Application runner method.
     *
     * @param args {@link ApplicationArguments} program arguments.
     */
    @Override
    public void run(final ApplicationArguments args) {
        terminateApplication(() -> cliExecutor
                .execute(new OptionParser(args))
                .getValue());
    }

    /**
     * Terminates application according to status.
     *
     * @param exitCodeGenerator status code.
     */
    private void terminateApplication(final ExitCodeGenerator exitCodeGenerator) {
        exit(SpringApplication.exit(applicationContext, exitCodeGenerator));
    }
}
