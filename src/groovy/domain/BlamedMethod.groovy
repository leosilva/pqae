package domain

import architecturevisualization.ResponsibleCommit;

class BlamedMethod {
	
	String methodSignature
	BigDecimal pValueTTest
	BigDecimal pValueUTest
	BigDecimal avgExecutionTimePreviousVersion
	BigDecimal avgExecutionTimeNextVersion
	Integer qtdExecutedPreviousVersion
	Integer qtdExecutedNextVersion
	BigDecimal executionTimeDifference
	Integer variation
	Boolean isModified = false
	Boolean isAdded = false
	Boolean isRemoved = false
	
	List<Commit> commits = []

}
