package org.its.service.job;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.its.rest.controller.FleetController;
import org.its.rest.controller.FleetController.FleetState;
import org.its.service.Ec2Service;
import org.its.service.impl.Ec2ServiceImpl;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.services.ec2.model.InstanceStateName;

@Component
public class VolumeAttachmentJob extends JobExecutionListenerSupport {
	@Autowired
	private Ec2Service ec2Service;
	public class Reader implements ItemReader<List<FleetState>> {

		@Override
		public List<FleetState> read()
				throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
			int total = FleetController.fleets.stream().filter(fl->!fl.isDryRun()).map(fsp -> fsp.getFleetParam().getNumberOfNodes()).collect(Collectors.summingInt(it->it.intValue()));
			int totalMounted = FleetController.fleets.stream().filter(fl->!fl.isDryRun()).map(fsp -> fsp.getInstancesWithAttachedVolum().size()).collect(Collectors.summingInt(it->it.intValue()));
			if (FleetController.fleets.stream().filter(fl->!fl.isDryRun())
					.filter(fs -> fs.getFleetParam().getNumberOfNodes() != fs.getInstancesWithAttachedVolum().size())
					.count() == 0 || total - totalMounted < Ec2ServiceImpl.VOLUME_JOB_TRIGGER_THRESHOLD) {
				return null;
			} else {
				long running = ec2Service.getAllFleetInstances().stream().filter(ins->ins.state().name() == InstanceStateName.RUNNING).count();
				if ( running > Ec2ServiceImpl.VOLUME_JOB_TRIGGER_THRESHOLD)
					return FleetController.fleets;
				else
					return null;
			}

		}
	}

	public class Writer implements ItemWriter<List<FleetState>> {

		@Override
		public void write(List<? extends List<FleetState>> fleetStates) throws Exception {
			fleetStates.forEach(fleetS -> {
				fleetS.forEach(fs -> {
					System.out.println("fleet volume processed: "
							+ fs.getInstancesWithAttachedVolum().stream().collect(Collectors.joining(",")));
				});

			});

		}

	}

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private SimpleJobLauncher jobLauncher;

	@Autowired
	Processor processor;

	@Bean
	public SimpleJobLauncher jobLauncher2(JobRepository jobRepository) {
		SimpleJobLauncher launcher = new SimpleJobLauncher();
		launcher.setJobRepository(jobRepository);
		return launcher;
	}

	@Bean(name = "volumeJob")
	public Job volumeJob() {

		Step step = stepBuilderFactory.get("step-1").<List<FleetState>, List<FleetState>>chunk(1).reader(new Reader())
				.processor(processor).writer(new Writer()).build();

		Job job = jobBuilderFactory.get("volume-job").incrementer(new RunIdIncrementer()).listener(this).start(step)
				.build();

		return job;
	}

	@Scheduled(cron = "*/30 * * * * *")
	public void perform() throws Exception {

		System.out.println("Job Started at :" + new Date());

		JobParameters param = new JobParametersBuilder()
				.addString("volumeJob", String.valueOf(System.currentTimeMillis())).toJobParameters();

		JobExecution execution = jobLauncher.run(volumeJob(), param);

		System.out.println("Job finished with status :" + execution.getStatus());
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			System.out.println("BATCH JOB COMPLETED SUCCESSFULLY");
		}
	}

}
