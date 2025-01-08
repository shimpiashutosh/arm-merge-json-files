package com.arm.cli.mergejsonfiles.config;

import com.arm.cli.mergejsonfiles.cli.CliExecutor;
import com.arm.cli.mergejsonfiles.service.DefaultMergeFilesService;
import com.arm.cli.mergejsonfiles.service.IMergeFilesService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bean config class to define application beans.
 */
@Configuration
public class JsonFilesMergeConfig {

    /**
     * @return {@link DefaultMergeFilesService} bean.
     */
    @Bean
    public IMergeFilesService mergeFilesService() {
        return new DefaultMergeFilesService();
    }

    /**
     * @param mergeFilesService bean of type {@link IMergeFilesService}
     *
     * @return {@link CliExecutor} bean.
     */
    @Bean
    public CliExecutor cliExecutor(final IMergeFilesService mergeFilesService) {
        return new CliExecutor(mergeFilesService);
    }
}
