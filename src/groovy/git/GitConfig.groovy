package git

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand.ListMode
import org.eclipse.jgit.api.errors.JGitInternalException
import org.eclipse.jgit.errors.NoRemoteRepositoryException
import org.eclipse.jgit.lib.Ref

class GitConfig {
	
	private final def repoPrefix = "repositories/"
	
	/**
	 * Método que realiza o clone de um repositório
	 */
	def clone(project) throws NoRemoteRepositoryException, JGitInternalException {
		verifyRootDir()
		def remoteURL = project.repositoryUrl
		def projectName = project.name.toLowerCase().replace(' ', '_')
		File localPath = new File(repoPrefix, projectName)
		localPath.mkdir()

		println "Cloning from $remoteURL to $localPath"
		
		Git result = Git.cloneRepository().setURI(remoteURL).setDirectory(localPath).call()
		println "Having repository: ${result.getRepository().getDirectory()}"
	}
	
	/**
	 * Método que cria a pasta raiz onde ficam os repositórios clonados, se não existir.
	 * @return
	 */
	private def verifyRootDir() {
		def file = new File(repoPrefix)
		if (!file.exists()) {
			file.mkdir()
		}
	}
	
	def branchList() {
		File localPath = new File(repoPrefix, "pagseguro")
		Git git = Git.open(localPath)
		
		List<Ref> bList = git.branchList().setListMode(ListMode.REMOTE).call()
		bList
	}
	
	def tagList() {
		File localPath = new File(repoPrefix, "pagseguro")
		Git git = Git.open(localPath)
		
		List<Ref> tList = git.tagList().call()
		tList
	}
	
}
