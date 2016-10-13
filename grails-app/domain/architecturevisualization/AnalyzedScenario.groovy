package architecturevisualization

import java.util.Date;

class AnalyzedScenario {
	
	String name
	BigInteger broadTime
	Integer totalNodes
	Integer qtdDeviationNodes
	Integer qtdAddedNodes
	Integer qtdRemovedNodes
	Integer qtdShowingNodes
	Date date
	String analysisDuration
	String jsonNodesToVisualization
	
	static belongsTo = [analyzedSystem : AnalyzedSystem]

    static constraints = {
		name nullable: true, unique: ['analyzedSystem']
		broadTime nullable: true
		totalNodes nullable: true
		qtdDeviationNodes nullable: true
		qtdAddedNodes nullable: true
		qtdRemovedNodes nullable: true
		qtdShowingNodes nullable: true
		jsonNodesToVisualization nullable: true
		date nullable: true
		analysisDuration nullable: true
    }
	
	static mapping = {
		datasources(["av"])
		jsonNodesToVisualization sqlType: 'text'
	}
	
}