package domain

class BlamedMethod {
	
	String methodSignature
	BigDecimal previousExecutionTime
	BigDecimal nextExecutionTime
	BigDecimal executionTimeDifference
	Integer qtdExecutedPreviousVersion
	Integer qtdExecutedNextVersion

}
