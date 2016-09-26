package architecturevisualization


class Project {
	
	String name
	String repositoryUrl
	RepositoryType repositoryType
	Boolean isClonned = false

    static constraints = {
		name nullable: false, blank: false
		repositoryUrl nullable: false, blank: false 
		repositoryType nullable: false, blank: false
		isClonned nullable: false, blank: false 
    }

}