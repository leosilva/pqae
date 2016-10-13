package domain



/**
 * Classe que representa o arquivo no formato: <sistema>-<versão da análise>_pu_blamed_methods_of_<degraded ou optimized>_scenarios_significance_<data>.txt
 * @author leosilva
 *
 */
class FileBlamedSignificance {
	
	String fileName
	Integer qtdAffectedScenarios
	Integer qtdAffectedMethods
	List<BlamedScenario> scenarios
	List<BlamedMethod> methods
	Boolean isDegradation
	
}
