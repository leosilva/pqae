<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<!-- IMPORTAÇÕES ESPECÍFICAS DESTA PÁGINA -->
		<!-- INÍCIO JAVASCRIPT -->
		<asset:stylesheet src="methodCallGraph/callGraphVisualization.css"/>
		<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
		<asset:javascript src="events.js"/>
		<asset:javascript src="methodCallGraph/Chart.js"/>
		<asset:javascript src="methodCallGraph/mainChart.js"/>
		<asset:javascript src="methodCallGraph/drawHTMLElement.js"/>
		<asset:javascript src="methodCallGraph/mainCallGraph.js"/>
		<!-- FIM JAVASCRIPT -->
	</head>
	<body>
		<div id="page-body" role="main" class="content body">
			<input type="hidden" name="mapAffectedNodes" id="mapAffectedNodes" value="${affectedNodes}"/>
			<input type="hidden" name="history" id="history" value="${history}"/>
			<g:render template="callGraphVisualization_details" model="[info: info]"></g:render>
			<g:render template="callGraphVisualization_history"></g:render>
			<g:render template="callGraphVisualization_graph"></g:render>
		</div>
	</body>
</html>
