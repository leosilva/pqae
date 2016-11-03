package domain

class BlamedScenario {
	
	String scenarioName
	BigDecimal pValueTTest
	BigDecimal pValueUTest
	BigDecimal avgExecutionTimePreviousVersion
	BigDecimal avgExecutionTimeNextVersion
	Integer qtdExecutedPreviousVersion
	Integer qtdExecutedNextVersion
	BigDecimal executionTimeDifference
	Integer variation
	List<BlamedMethod> modifiedMethods
	List<BlamedMethod> addedMethods
	List<BlamedMethod> removedMethods
	Boolean isDegraded

}
