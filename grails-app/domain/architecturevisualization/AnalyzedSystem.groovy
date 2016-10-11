package architecturevisualization

class AnalyzedSystem {
	
	String systemName
	String previousVersion
	String nextVersion
	Date date
	String analysisDuration
	
	static hasMany = [analyzedScenarios : AnalyzedScenario]

    static constraints = {
		systemName nullable: true
		previousVersion nullable: true
		nextVersion nullable: true
		date nullable: true
		analysisDuration nullable: true
    }
	
	static mapping = {
		datasources(["av"])
	}
}
