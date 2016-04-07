package git

import org.eclipse.jgit.api.CloneCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.util.FileUtils;
import org.grails.plugins.jgit.JGit

class GitConfig {
	
	private final def repoPrefix = "repositories/"
	
	/**
	 * Método que realiza o clone de um repositório
	 */
	def clone() {
		verifyRootDir()
		def remoteURL = "https://github.com/pagseguro/java.git"
		File localPath = new File(repoPrefix, "pagseguro")
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
	
}
