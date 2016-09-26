package architecturevisualization

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class Events implements Serializable {

	Integer id
	String eventName
	Date createdAt
	String issueNumber
	String project

	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append id
		builder.append eventName
		builder.append createdAt
		builder.append issueNumber
		builder.append project
		builder.toHashCode()
	}

	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append id, other.id
		builder.append eventName, other.eventName
		builder.append createdAt, other.createdAt
		builder.append issueNumber, other.issueNumber
		builder.append project, other.project
		builder.isEquals()
	}

	static mapping = {
		datasources(["DEFAULT", "msrNextVersion"])
		id composite: ["id", "eventName", "createdAt", "issueNumber", "project"]
		version false
	}

	static constraints = {
		id nullable: true
		eventName nullable: true, maxSize: 100
		createdAt nullable: true
		issueNumber nullable: true, maxSize: 100
		project nullable: true, maxSize: 100
	}
}
