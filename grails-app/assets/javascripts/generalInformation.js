/*
* Este arquivo define variáveis globais básicas a serem usadas pelos outros scripts.
* Contém:
* 	- Conteúdo dos Popovers
* 	- Textos para o gráfico de desempenho dos cenários
*/
	
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
	
var helpPopoverContent = "<p>Double-click a <b>deviation</b> or <b>added</b> node to highlight the call path from root node to it.</p><p>Double-click a <b>non-deviation</b> node to reset the highlight.</p>"
	
	
/*
* TEXTOS PARA O GRÁFICO DE HISTÓRICO DE DESEMPENHO DOS CENÁRIOS
*/
	
var graphTitle = "Execution Time History"
var yAxisTitle = "Execution Time (ms)"
var xAxisTitle = "Versions"