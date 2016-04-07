package architecturevisualization

import git.GitConfig
import grails.transaction.Transactional

import org.eclipse.jgit.api.CloneCommand
import org.eclipse.jgit.api.Git

@Transactional
class GitService {

	def gitConfig = new GitConfig()
	
    def clone() {
		gitConfig.clone()
	}

}
