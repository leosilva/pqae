package architecturevisualization

class ResponsibleMethod {
	
	String methodSignature
	
	static hasMany = [affectedScenarios : AnalyzedScenario, responsibleCommits : ResponsibleCommit]
	static belongsTo = AnalyzedScenario

    static constraints = {
		methodSignature nullable: true
    }
	
	static mapping = {
		datasources(["av"])
	}
	
}
