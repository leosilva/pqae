package architecturevisualization


class AnalyzedScenario {
	
	String name
	BigDecimal previousTime
	BigDecimal nextTime
	Integer totalNodes
	Integer qtdDeviationNodes
	Integer qtdOptimizedNodes
	Integer qtdDegradedNodes
	Integer qtdAddedMethods
	Integer qtdRemovedMethods
	Integer qtdShowingNodes
	Date date
	BigDecimal analysisDuration
	String jsonNodesToVisualization
	Boolean isDegraded
	
	static belongsTo = [analyzedSystem : AnalyzedSystem]
	static hasMany = [responsibleMethods: ResponsibleMethod]

    static constraints = {
		name nullable: true, unique: ['analyzedSystem']
		previousTime nullable: true
		nextTime nullable: true
		totalNodes nullable: true
		qtdDeviationNodes nullable: true
		qtdOptimizedNodes nullable: true
		qtdDegradedNodes nullable: true
		qtdAddedMethods nullable: true
		qtdRemovedMethods nullable: true
		qtdShowingNodes nullable: true
		jsonNodesToVisualization nullable: true
		date nullable: true
		analysisDuration nullable: true
		isDegraded nullable: true
		responsibleMethods nullable: true
    }
	
	static mapping = {
		datasources(["av"])
		jsonNodesToVisualization sqlType: 'text'
		responsibleMethods cascade: 'all'
	}
	
}
