package architecturevisualization


class AnalyzedScenario {
	
	String name
	BigDecimal previousTime
	BigDecimal nextTime
	Integer totalNodes
	Integer qtdDeviationNodes
	Integer qtdAddedNodes
	Integer qtdRemovedNodes
	Integer qtdShowingNodes
	Date date
	BigDecimal analysisDuration
	String jsonNodesToVisualization
	Boolean isDegraded
	
	static belongsTo = [analyzedSystem : AnalyzedSystem]

    static constraints = {
		name nullable: true, unique: ['analyzedSystem']
		previousTime nullable: true
		nextTime nullable: true
		totalNodes nullable: true
		qtdDeviationNodes nullable: true
		qtdAddedNodes nullable: true
		qtdRemovedNodes nullable: true
		qtdShowingNodes nullable: true
		jsonNodesToVisualization nullable: true
		date nullable: true
		analysisDuration nullable: true
		isDegraded nullable: true
    }
	
	static mapping = {
		datasources(["av"])
		jsonNodesToVisualization sqlType: 'text'
	}
	
}
