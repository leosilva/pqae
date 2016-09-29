package architecturevisualization

class Execution {

	Date date
	String systemName
	String systemVersion

	static hasMany = [scenarios: Scenario]

	static mapping = {
		datasources(["msrPreviousVersion", "msrNextVersion"])
		id generator: "assigned"
		version false
	}

	static constraints = {
		date nullable: true
		systemName nullable: true
		systemVersion nullable: true
	}
}
