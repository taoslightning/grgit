package org.ajoberstar.grgit.fixtures

import org.ajoberstar.grgit.Commit
import org.ajoberstar.grgit.service.RepositoryService
import org.ajoberstar.grgit.util.JGitUtil
import org.eclipse.jgit.api.ListBranchCommand.ListMode
import org.eclipse.jgit.transport.RemoteConfig

final class GitTestUtil {
	private GitTestUtil() {
		throw new AssertionError('Cannot instantiate this class.')
	}

	static File repoFile(RepositoryService grgit, String path, boolean makeDirs = true) {
		def file = new File(grgit.repository.rootDir, path)
		if (makeDirs) file.parentFile.mkdirs()
		return file
	}

	static List branches(RepositoryService grgit, boolean trim = true) {
		return grgit.repository.git.branchList().with {
			listMode = ListMode.ALL
			delegate.call()
		}.collect { trim ? it.name - 'refs/heads/' : it.name }
	}

	static List remoteBranches(RepositoryService grgit) {
		return grgit.repository.git.branchList().with {
			listMode = ListMode.REMOTE
			delegate.call()
		}.collect { it.name - 'refs/remotes/origin/' }
	}

	static List tags(RepositoryService grgit) {
		return grgit.repository.git.tagList().call().collect {
			it.name - 'refs/tags/'
		}
	}

	static List remotes(RepositoryService grgit) {
		def jgitConfig = grgit.repository.git.repo.config
		return RemoteConfig.getAllRemoteConfigs(jgitConfig).collect { it.name}
	}

	static Commit resolve(RepositoryService grgit, String revstr) {
		return JGitUtil.resolveCommit(grgit.repository, revstr)
	}

	static void configure(RepositoryService grgit, Closure closure) {
		def config = grgit.repository.git.repo.config
		config.with(closure)
		config.save()
	}
}
