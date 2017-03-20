/*
* Este arquivo define variáveis globais básicas a serem usadas pelos outros scripts.
* Contém:
* 	- Conteúdo dos Popovers
* 	- Textos para o gráfico de desempenho dos cenários
*/

//ENDEREÇO DOS REPOSITÓRIOS DE COMMITS DOS SISTEMAS ANALISADOS (O IDEAL É QUE ISSO MUDE E SEJA FORNECIDO PELO PERFMINER)
var githubCommitAddressJettyServlet = "https://github.com/eclipse/jetty.project/commit/"
var githubCommitAddressVRaptor = "https://github.com/caelum/vraptor4/commit/"
var githubCommitsAddress = "" // variavel definida de acordo com o nome do sistema
	
//CONTEÚDO DOS POPOVERS
var popoverNoDetails = "No details."
var popoverAddedNodes = "Added nodes"
var popoverPackage = "Package"
var popoverParameters = "Parameters"
var popoverNoParameters = "No parameters."
var popoverPotenciallyCausedDeviation = "This node potentially caused the performance deviation."
var popoverTotalTime = "Total time"
var popoverSelfTime = "Self time"
var popoverDeviation = "Deviation"
var popoverCommits = "Commits"
	
var helpPopoverContent = "<p>Double-click a <b>deviation</b> or <b>added</b> node to highlight the call path from root node to it.</p><p>Double-click a <b>non-deviation</b> node to reset the highlight.</p>"
	
	
/*
* TEXTOS PARA O GRÁFICO DE HISTÓRICO DE DESEMPENHO DOS CENÁRIOS
*/
	
var graphTitle = "Execution Time History"
var yAxisTitle = "Execution Time (ms)"
var xAxisTitle = "Versions"

/*
* TEXTO CONTEÚDO DO HELP DA LEGENDA DO GRAFO DE CHAMADAS
*/
	
var perfArrowMessage = "<p>Each arrow represents up to 25% of deviation:</p>"
	
var perfOptOneArrow = "<p>"
perfOptOneArrow += "<span class='legend-square-arrow'>"
perfOptOneArrow += "<i class='ionicons ion-arrow-up-b optimization arrow'></i>"
perfOptOneArrow += "</span>"
perfOptOneArrow += "<span class='legend-description-arrow'>until 25% of performance optimization</span>"
perfOptOneArrow += "</p>"

var perfOptTwoArrows = "<p>"
perfOptTwoArrows += "<span class='legend-square-arrow'>"
perfOptTwoArrows += "<i class='ionicons ion-arrow-up-b optimization arrow' style='position: absolute; top: 7px;'></i>"
perfOptTwoArrows += "<i class='ionicons ion-arrow-up-b optimization arrow' style='position: absolute; top: 18px;'></i>"
perfOptTwoArrows += "</span>"
perfOptTwoArrows += "<span class='legend-description-arrow'>from 25% to 50% of performance optimization</span>"
perfOptTwoArrows += "</p>"
	
var perfOptThreeArrows = "<p>"
perfOptThreeArrows += "<span class='legend-square-arrow'>"
perfOptThreeArrows += "<i class='ionicons ion-arrow-up-b optimization arrow' style='position: absolute; top: 3px;'></i>"
perfOptThreeArrows += "<i class='ionicons ion-arrow-up-b optimization arrow' style='position: absolute; top: 14px;'></i>"
perfOptThreeArrows += "<i class='ionicons ion-arrow-up-b optimization arrow' style='position: absolute; top: 25px;'></i>"
perfOptThreeArrows += "</span>"
perfOptThreeArrows += "<span class='legend-description-arrow'>from 50% to 75% of performance optimization</span>"
perfOptThreeArrows += "</p>"
	
var perfOptFourArrows = "<p>"
perfOptFourArrows += "<span class='legend-square-arrow'>"
perfOptFourArrows += "<i class='ionicons ion-arrow-up-b optimization arrow' style='position: absolute; top: 0px;'></i>"
perfOptFourArrows += "<i class='ionicons ion-arrow-up-b optimization arrow' style='position: absolute; top: 10px;'></i>"
perfOptFourArrows += "<i class='ionicons ion-arrow-up-b optimization arrow' style='position: absolute; top: 20px;'></i>"
perfOptFourArrows += "<i class='ionicons ion-arrow-up-b optimization arrow' style='position: absolute; top: 30px;'></i>"
perfOptFourArrows += "</span>"
perfOptFourArrows += "<span class='legend-description-arrow' style='margin-bottom: 10px;'>more than 75% of performance optimization</span>"
perfOptFourArrows += "</p>"
	
	
var perfDegOneArrow = "<p>"
perfDegOneArrow += "<span class='legend-square-arrow'>"
perfDegOneArrow += "<i class='ionicons ion-arrow-down-b degradation arrow'></i>"
perfDegOneArrow += "</span>"
perfDegOneArrow += "<span class='legend-description-arrow'>until 25% of performance degradation</span>"
perfDegOneArrow += "</p>"

var perfDegTwoArrows = "<p>"
perfDegTwoArrows += "<span class='legend-square-arrow'>"
perfDegTwoArrows += "<i class='ionicons ion-arrow-down-b degradation arrow' style='position: absolute; top: 7px;'></i>"
perfDegTwoArrows += "<i class='ionicons ion-arrow-down-b degradation arrow' style='position: absolute; top: 18px;'></i>"
perfDegTwoArrows += "</span>"
perfDegTwoArrows += "<span class='legend-description-arrow'>from 25% to 50% of performance degradation</span>"
perfDegTwoArrows += "</p>"
	
var perfDegThreeArrows = "<p>"
perfDegThreeArrows += "<span class='legend-square-arrow'>"
perfDegThreeArrows += "<i class='ionicons ion-arrow-down-b degradation arrow' style='position: absolute; top: 3px;'></i>"
perfDegThreeArrows += "<i class='ionicons ion-arrow-down-b degradation arrow' style='position: absolute; top: 14px;'></i>"
perfDegThreeArrows += "<i class='ionicons ion-arrow-down-b degradation arrow' style='position: absolute; top: 25px;'></i>"
perfDegThreeArrows += "</span>"
perfDegThreeArrows += "<span class='legend-description-arrow'>from 50% to 75% of performance degradation</span>"
perfDegThreeArrows += "</p>"
	
var perfDegFourArrows = "<p>"
perfDegFourArrows += "<span class='legend-square-arrow'>"
perfDegFourArrows += "<i class='ionicons ion-arrow-down-b degradation arrow' style='position: absolute; top: 0px;'></i>"
perfDegFourArrows += "<i class='ionicons ion-arrow-down-b degradation arrow' style='position: absolute; top: 10px;'></i>"
perfDegFourArrows += "<i class='ionicons ion-arrow-down-b degradation arrow' style='position: absolute; top: 20px;'></i>"
perfDegFourArrows += "<i class='ionicons ion-arrow-down-b degradation arrow' style='position: absolute; top: 30px;'></i>"
perfDegFourArrows += "</span>"
perfDegFourArrows += "<span class='legend-description-arrow' style='margin-bottom: 10px;'>more than 75% of performance degradation</span>"	