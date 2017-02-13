<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<asset:javascript src="d3.v3.js"/>
		<asset:javascript src="responsibleMethods/mainResponsibleMethods.js"/>
		<asset:stylesheet src="responsibleMethods/responsibleMethods.css"/>
	</head>
	<body>
		<input type="hidden" name="jsonResponsibleMethods" id="jsonResponsibleMethods" value="${responsibleMethods}"/>
		<input type="hidden" name="urlsMap" id="urlsMap" value="${urlsMap}"/>
		<div id="page-body" role="main" class="content body">
			<div id="svgResponsibleMethods">
			</div>
		</div>
	</body>
</html>
