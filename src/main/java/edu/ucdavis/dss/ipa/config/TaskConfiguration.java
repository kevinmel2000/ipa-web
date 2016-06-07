package edu.ucdavis.dss.ipa.config;

import java.util.concurrent.Executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import edu.ucdavis.dss.ipa.exceptions.handlers.AsyncExceptionHandler;

@Configuration
@ComponentScan(basePackages = {"edu.ucdavis.dss.ipa.tasks"})
public class TaskConfiguration implements AsyncConfigurer, SchedulingConfigurer {
	private static final Logger log = LogManager.getLogger();
	private static final Logger schedulingLogger = LogManager.getLogger(log.getName() + ".[scheduling]");

	@Bean
	public ThreadPoolTaskScheduler taskScheduler()
	{
		log.info("Setting up thread pool task scheduler with 5 threads.");
		
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(10);
		scheduler.setThreadNamePrefix("ipa-task-");
		scheduler.setAwaitTerminationSeconds(5);
		scheduler.setWaitForTasksToCompleteOnShutdown(false);
		scheduler.setErrorHandler(t -> schedulingLogger.error(
				"Unknown error occurred while executing task.", t
				));
		scheduler.setRejectedExecutionHandler(
				(r, e) -> schedulingLogger.error(
						"Execution of task {} was rejected for unknown reasons.", r
						)
				);
		return scheduler;
	}

	@Override
	public Executor getAsyncExecutor()
	{
		Executor executor = this.taskScheduler();
		log.info("Configuring asynchronous method executor {}.", executor);
		return executor;
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar registrar)
	{
		TaskScheduler scheduler = this.taskScheduler();
		log.info("Configuring scheduled method executor {}.", scheduler);
		registrar.setTaskScheduler(scheduler);
	}
	
	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new AsyncExceptionHandler();
	}
}
