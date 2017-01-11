<!DOCTYPE html>
<%@page import="architecturevisualization.AnalyzedSystemStatus"%>
<html>
	<head>
		<meta name="layout" content="main"/>
		<asset:stylesheet src="inicio/inicio.css"/>
		<asset:javascript src="inicio/mainInicio.js"/>
		<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
	</head>
	<body>
		<g:hiddenField name="ajaxUri" id="ajaxUri" value="${ajaxUri}"/>
		<div id="page-body" role="main" class="content">
			<g:link controller="analysis" action="startAnalysis" class="btn btn-app" style="margin-left: 0px;" params="[targetUri : createLink(uri: '/')]">
				<i class="fa fa-plus"></i> <g:message code="sidebar.menu.navigation.newAnalysis" />
			</g:link>
			<div id="tableSystems">
				<g:render template="indexSystemsTable" />
			</div>
		</div>
	</body>
</html>
