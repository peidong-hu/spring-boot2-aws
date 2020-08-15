package org.its.service.job;

import java.util.List;

import org.its.rest.controller.FleetController;
import org.its.rest.controller.FleetController.FleetState;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class VolumeAttachmentJob extends JobExecutionListenerSupport {
	public class Reader implements ItemReader<List<FleetState>> {


		@Override
		public List<FleetState> read() throws Exception, UnexpectedInputException,
				ParseException, NonTransientResourceException {

			
				return FleetController.fleets;
			
		}
	}
	public class Writer implements ItemWriter<List<FleetState>> {

		@Override
		public void write(List<? extends List<FleetState>> messages) throws Exception {
			 
				System.out.println("Writing the data ");
			 
		}

	}

	@Autowired
	JobBuilderFactory jobBuilderFactory;

	@Autowired
	StepBuilderFactory stepBuilderFactory;

	 

	@Autowired
	Processor processor;

	 

	@Bean(name = "volumeJob")
	public Job accountKeeperJob() {
		
		Step step = stepBuilderFactory.get("step-1").<List<FleetState>, List<FleetState>>chunk(1).reader(new Reader())
				.processor(processor).writer(new Writer()).build();

		Job job = jobBuilderFactory.get("accounting-job").incrementer(new RunIdIncrementer()).listener(this).start(step)
				.build();

		return job;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			System.out.println("BATCH JOB COMPLETED SUCCESSFULLY");
		}
	}

}
