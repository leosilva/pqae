<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<!-- IMPORTAÇÕES ESPECÍFICAS DESTA PÁGINA -->
		<!-- INÍCIO JAVASCRIPT -->
		<asset:stylesheet src="methodCallGraph/callGraphVisualization.css"/>
		<asset:javascript src="events.js"/>
		<asset:javascript src="methodCallGraph/drawHTMLElement.js"/>
		<asset:javascript src="methodCallGraph/mainCallGraph.js"/>
		<!-- FIM JAVASCRIPT -->
	</head>
	<body>
		<div id="page-body" role="main" class="content body">
			<input type="hidden" name="mapAffectedNodes" id="mapAffectedNodes" value="${affectedNodes}"/>
			<g:render template="callGraphVisualization_details" model="[info: info]"></g:render>
			<g:render template="callGraphVisualization_graph"></g:render>
		</div>
	</body>
</html>
