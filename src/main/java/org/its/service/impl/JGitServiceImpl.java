package org.its.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FS;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

@Service
public class JGitServiceImpl {
	private SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
		@Override
		protected void configure(Host host, Session session) {
			session.setConfig("StrictHostKeyChecking", "no");
		}

		@Override
		protected JSch createDefaultJSch(FS fs) throws JSchException {
			String privateKeyPath = CredentialServiceImpl.getPrivateKeyPath();
			if (!privateKeyPath.isEmpty()) {
				JSch defaultJSch = super.createDefaultJSch(fs);
				defaultJSch.addIdentity(privateKeyPath);
				return defaultJSch;
			} else {
				return super.createDefaultJSch(fs);
			}
		}
	};

	private String gitUsername = CredentialServiceImpl.readSecret("gitUserName");
	private String gitPassword = CredentialServiceImpl.readSecret("gitPassword");

	// private static final String remoteUrl =
	// "https://github.com/peidong-hu/Hygieia.git";

	public String lsThroughHttp(String remoteUrl) throws GitAPIException {
		// then clone
		// System.out.println("Listing remote repository " + remoteUrl + ":" + userName
		// + ":" + password);
		UsernamePasswordCredentialsProvider httpCredentials = new UsernamePasswordCredentialsProvider(
				gitUsername.trim(), gitPassword.trim());

		final Map<String, Ref> map = Git.lsRemoteRepository().setHeads(true).setTags(true).setRemote(remoteUrl)
				.setCredentialsProvider(httpCredentials).callAsMap();

		System.out.println("As map");
		for (Map.Entry<String, Ref> entry : map.entrySet()) {
			if (entry.getKey().contains("head")) {
				System.out.println("Key: " + entry.getKey() + ", Ref: " + entry.getValue());
			}
		}

		return map.keySet().stream().filter(key -> key.contains("head")).collect(Collectors.joining("\n"))
				.replaceAll("refs/heads/", "");

	}

	public String lsThroughGit(String remoteUrl) throws GitAPIException {
		// then clone
		System.out.println("Listing remote repository " + remoteUrl);

		final Map<String, Ref> map = Git.lsRemoteRepository().setHeads(true).setTags(true).setRemote(remoteUrl)
				.setTransportConfigCallback(new TransportConfigCallback() {
					@Override
					public void configure(Transport transport) {
						SshTransport sshTransport = (SshTransport) transport;
						sshTransport.setSshSessionFactory(sshSessionFactory);
					}
				}).callAsMap();

		System.out.println("As map");
		for (Map.Entry<String, Ref> entry : map.entrySet()) {
			if (entry.getKey().contains("head")) {
				System.out.println("Key: " + entry.getKey() + ", Ref: " + entry.getValue());
			}
		}

		return map.keySet().stream().filter(key -> key.contains("head")).collect(Collectors.joining("\n"))
				.replaceAll("refs/heads/", "");

	}

	public Optional<Repository> getGitRepo(String sshRemoteGitUrl, String path) {

		FileRepositoryBuilder repoBuilder = new FileRepositoryBuilder();

		if (!Files.exists(Paths.get(path)) || !Files.exists(Paths.get(path + ".git/"))) {
			Optional<Git> git = cloneToFolder(sshRemoteGitUrl, path);
			if (git.isPresent()) {
				try {
					return Optional.of(repoBuilder.build());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}
			}
		} else {
			
			try {
				repoBuilder.setMustExist(true).setGitDir(new File(path + ".git/"));

				// Already cloned. Just need to open a repository here.

				Repository repo;

				repo = repoBuilder.build();

				if (hasAtLeastOneReference(repo)) {
					Git git = new Git(repo);
					try {
						git.pull().call();
					} catch (GitAPIException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						git.close();
					}
					git.close();
					return Optional.of(repo);
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}
		return Optional.empty();
	}

	private static boolean hasAtLeastOneReference(Repository repo) {

		try {
			for (Ref ref : repo.getRefDatabase().getRefs()) {
				if (ref.getObjectId() == null)
					continue;
				return true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return false;
	}

	public Optional<Git> cloneToFolder(String sshRemoteGitUrl, String folder) {
		Path path = Paths.get(folder);
		if (Files.exists(path)) {
			try {
				FileSystemUtils.deleteRecursively(path);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Optional.empty();
			}
		}
		try (Git git = Git.cloneRepository().setURI(sshRemoteGitUrl).setDirectory(path.toFile()).call()) {
			return Optional.of(git);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Optional.empty();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Optional.empty();
		}
	}

}
