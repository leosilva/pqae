package architecturevisualization

class AnalyzedSystem {
	
	String systemName
	String previousVersion
	String nextVersion
	AnalyzedSystemStatus analyzedSystemStatus
	
	static hasMany = [analyzedScenarios : AnalyzedScenario]

    static constraints = {
		systemName nullable: true, unique: ['previousVersion', 'nextVersion']
		previousVersion nullable: true
		nextVersion nullable: true
		analyzedSystemStatus nullable: true
    }
	
	static mapping = {
		datasources(["av"])
		analyzedScenarios lazy: false
	}
}
