package architecturevisualization

class ResponsibleCommit {

    String commitHash
	
	static belongsTo = ResponsibleMethod

    static constraints = {
		commitHash nullable: true
    }
	
	static mapping = {
		datasources(["av"])
	}
	
}
