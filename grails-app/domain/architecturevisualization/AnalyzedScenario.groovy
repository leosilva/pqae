package architecturevisualization

class AnalyzedScenario {
	
	String name
	BigInteger broadTime
	Integer totalNodes
	Integer qtdDeviationNodes
	Integer qtdAddedNodes
	Integer qtdRemovedNodes
	Integer qtdShowingNodes
	String jsonNodesToVisualization
	
	static belongsTo = [analyzedSystem : AnalyzedSystem]

    static constraints = {
		name nullable: true
		broadTime nullable: true
		totalNodes nullable: true
		qtdDeviationNodes nullable: true
		qtdAddedNodes nullable: true
		qtdRemovedNodes nullable: true
		qtdShowingNodes nullable: true
		jsonNodesToVisualization nullable: true
    }
	
	static mapping = {
		datasources(["av"])
		jsonNodesToVisualization sqlType: 'text'
	}
	
}
