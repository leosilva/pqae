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
			def isDegradation = fileName.contains("degraded") ? true : false
			def (isQtdScenario, isQtdMethods, isScenarioMethodRelation) = [true, true, true]
			lines.eachWithIndex { line, index ->
				if (line.startsWith("#") && line.contains("Members") && line.contains("scenario")) {
					fileBlamed = readScenario(fileBlamed, lines, index, isDegradation)
				}
			}
			files << fileBlamed
		}
		files
	}
	
	/**
	 * Método que faz a leitura do cenário e dos seus respectivos métodos.
	 * @param fileBlamed
	 * @param lines
	 * @param index
	 * @param isDegradation
	 * @return
	 */
	def readScenario(fileBlamed, lines, index, isDegradation) {
		def scenarioLine = lines[index+2]?.split(";")
		def blamedScenario = new BlamedScenario(
			scenarioName: scenarioLine[0],
			pValueTTest: (scenarioLine[1] as BigDecimal).setScale(2, RoundingMode.DOWN),
			pValueUTest: (scenarioLine[2] as BigDecimal).setScale(2, RoundingMode.DOWN),
			avgExecutionTimePreviousVersion: (scenarioLine[3] as BigDecimal).setScale(2, RoundingMode.DOWN),
			avgExecutionTimeNextVersion: (scenarioLine[4] as BigDecimal).setScale(2, RoundingMode.DOWN),
			qtdExecutedPreviousVersion: scenarioLine[5] as Integer,
			qtdExecutedNextVersion: scenarioLine[6] as Integer,
			executionTimeDifference: (scenarioLine[7] as BigDecimal).setScale(2, RoundingMode.DOWN),
			variation: scenarioLine[8] as Integer,
			modifiedMethods: [],
			addedMethods: [],
			removedMethods: [],
			isDegraded: isDegradation)
		
		// verifica os métodos com variação de desempenho
		index += 3
		def qtdMembersWithDeviation = lines[index] as Integer
		if (qtdMembersWithDeviation > 0) {
			index += 2
			blamedScenario = readMethods(blamedScenario, lines, index, qtdMembersWithDeviation, "modifiedMethods")
		}
		
		// verifica os métodos adicionados
		index = index + (qtdMembersWithDeviation ?: 1) + 1
		def qtdAddedMethods = lines[index] as Integer
		if (qtdAddedMethods > 0) {
			index += 2
			blamedScenario = readMethods(blamedScenario, lines, index, qtdAddedMethods, "addedMethods")
		}
		
		// verifica os métodos removidos
		index = index + (qtdAddedMethods ?: 1) + 1
		def qtdRemovedMethods = lines[index] as Integer
		if (qtdRemovedMethods > 0) {
			index += 2
			blamedScenario = readMethods(blamedScenario, lines, index, qtdRemovedMethods, "removedMethods")
		}
		
		fileBlamed.scenarios << blamedScenario
		fileBlamed
	}
	
	/**
	 * Método que cria um objeto do tipo BlamedMethod, tanto para métodos modificados, adicionados ou removidos.
	 * @param blamedScenario
	 * @param lines
	 * @param index
	 * @param qtdMembers
	 * @param methods
	 * @return
	 */
	def readMethods(blamedScenario, lines, index, qtdMembers, methods) {
		def startRange = index
		((startRange)..<(startRange + qtdMembers)).each {
			def methodLine = lines[it]?.split(";")
			def blamedMethod = new BlamedMethod(methodSignature: methodLine[0])
			if (methods == "modifiedMethods") {
				blamedMethod.pValueTTest = (methodLine[1] as BigDecimal).setScale(2, RoundingMode.DOWN)
				blamedMethod.pValueUTest = (methodLine[2] as BigDecimal).setScale(2, RoundingMode.DOWN)
				blamedMethod.avgExecutionTimePreviousVersion = (methodLine[3] as BigDecimal).setScale(2, RoundingMode.DOWN)
				blamedMethod.avgExecutionTimeNextVersion = (methodLine[4] as BigDecimal).setScale(2, RoundingMode.DOWN)
				blamedMethod.qtdExecutedPreviousVersion = methodLine[5] as Integer
				blamedMethod.qtdExecutedNextVersion = methodLine[6] as Integer
				blamedMethod.executionTimeDifference = (methodLine[7] as BigDecimal).setScale(2, RoundingMode.DOWN)
				blamedMethod.variation = methodLine[8] as Integer
				blamedMethod.isModified = true
			} else if (methods == "addedMethods") {
				blamedMethod.avgExecutionTimeNextVersion = (methodLine[1] as BigDecimal).setScale(2, RoundingMode.DOWN)
				blamedMethod.qtdExecutedNextVersion = methodLine[2] as Integer
				blamedMethod.isAdded = true
			} else if (methods == "removedMethods") {
				blamedMethod.avgExecutionTimePreviousVersion = (methodLine[1] as BigDecimal).setScale(2, RoundingMode.DOWN)
				blamedMethod.qtdExecutedPreviousVersion = methodLine[2] as Integer
				blamedMethod.isRemoved = true
			}
			blamedScenario."${methods}" << blamedMethod
		}
		blamedScenario
	}
	
}
