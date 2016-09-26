package architecturevisualization

import git.GitConfig
import grails.transaction.Transactional

import org.eclipse.jgit.api.errors.JGitInternalException
import org.eclipse.jgit.errors.NoRemoteRepositoryException

@Transactional
class GitService {

	def gitConfig = new GitConfig()
	
    def clone(project) throws NoRemoteRepositoryException, JGitInternalException {
		gitConfig.clone(project)
	}
	
	def branchList() {
		gitConfig.branchList()
	}
	
	def tagList() {
		gitConfig.tagList()
	}

}
