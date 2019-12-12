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
		<asset:javascript src="methodCallGraph/packagesCallGraph.js"/>

		<!-- FIM JAVASCRIPT -->
	</head>
	<body>
		<div id="page-body" role="main" class="content body">
			<input type="hidden" name="mapAffectedNodes" id="mapAffectedNodes" value="${affectedNodes}"/>
			<input type="hidden" name="history" id="history" value="${history}"/>
			<g:render template="callPackageGraphVisualization_details" model="[info: info]"></g:render>
			<g:render template="callPackageGraphVisualization_history"></g:render>
			<g:render template="callPackageGraphVisualization_graph"></g:render>
		</div>
	</body>
</html>
