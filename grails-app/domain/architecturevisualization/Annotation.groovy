package architecturevisualization

class Annotation {

	String type
	String name
	Long limitTime
	Double failureRate

	static hasMany = [nodes: Node]
	
	static belongsTo = Node

	static mapping = {
		datasources(["msrPreviousVersion", "msrNextVersion"])
		id generator: "assigned"
		version false
	}

	static constraints = {
		type maxSize: 31
		name nullable: true
		limitTime nullable: true
		failureRate nullable: true, scale: 17
	}
}
