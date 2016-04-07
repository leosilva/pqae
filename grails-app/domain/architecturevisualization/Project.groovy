package architecturevisualization

class Project {
	
	String name
	String repositoryUrl
	RepositoryType repositoryType

    static constraints = {
		name nullable: false, blank: false
		repositoryUrl nullable: false, blank: false 
		repositoryType nullable: false, blank: false 
    }

}