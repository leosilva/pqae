package architecturevisualization

import grails.transaction.Transactional

import java.math.RoundingMode
import java.nio.file.Paths

import domain.BlamedMethod
import domain.BlamedScenario
import domain.FileBlamedSignificance

@Transactional
class PerfMinerIntegrationFilesService {

    /**
	 * Método que lê e interpreta o arquivo no formato <sistema>-<versão da análise>_pu_blamed_methods_of_<degraded ou optimized>_scenarios_significance_<data>.txt
	 * @return
	 */
	def readBlamedMethodsScenariosFile(systemName, versionFrom, versionTo) {
		def files = []
		def path = "repositories/${systemName}/${versionFrom}_to_${versionTo}/"
		def fileName
		def dir = new File(path).eachFile { file ->
			fileName = file.getName()
			def f = Paths.get(path + fileName)
			def fileBlamed = new FileBlamedSignificance(fileName: f.fileName, scenarios: [], methods: [])
			def lines = f.readLines()
			def isDegradation = false
			def (isQtdScenario, isQtdMethods, isScenarioMethodRelation) = [true, true, true]
			lines.eachWithIndex { line, index ->
				if (line.startsWith("#") && line.contains("degradation")) {
					isDegradation = true
				}
				// le e cria os objetos para os cenarios no arquivo
				if (line.isNumber() && isQtdScenario) {
					fileBlamed.qtdAffectedScenarios = (line as Integer)
					isQtdScenario = false
					((index+1)..(index+fileBlamed.qtdAffectedScenarios)).each {
						def bs = new BlamedScenario(scenarioName: lines[it], methods: [], isDegraded: isDegradation)
						fileBlamed.scenarios << bs
					}
				} else if (line.isNumber() && isQtdMethods) { // le e cria os metodos para cada cenario contido no arquivo
					fileBlamed.qtdAffectedMethods = (line as Integer)
					isQtdMethods = false
					((index+1)..(index+fileBlamed.qtdAffectedMethods)).each {
						def lineSplitted = lines[it].split(";")
						def bs = new BlamedMethod()
						bs.methodSignature = lineSplitted[0] 
						bs.previousExecutionTime = lineSplitted[1] != "null" ? (lineSplitted[1] as BigDecimal).setScale(2, RoundingMode.DOWN) : null
						bs.nextExecutionTime = (lineSplitted[2] as BigDecimal).setScale(2, RoundingMode.DOWN)
						bs.executionTimeDifference = (lineSplitted[3] as BigDecimal).setScale(2, RoundingMode.DOWN)
						bs.qtdExecutedPreviousVersion = lineSplitted[4] as Integer
						bs.qtdExecutedNextVersion = lineSplitted[5] as Integer
						fileBlamed.methods << bs
					}
				} else if (line.isNumber() && isScenarioMethodRelation) { // le e associa os metodos para cada cenario contido no arquivo
					isScenarioMethodRelation = false
					((index+1)..(index+(line as Integer))).each {
						def splitted = lines[it].split(";")
						def scenario = fileBlamed.scenarios.find { it.scenarioName == splitted[0] }
						def method = fileBlamed.methods.find { it.methodSignature == splitted[1] }
						scenario.methods << method
					}
				}
			}
			files << fileBlamed
		}
		files
	}
	
}
