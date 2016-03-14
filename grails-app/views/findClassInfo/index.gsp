<html>
	<head>
		<meta name="layout" content="main"/>
		<script type="text/javascript" src="${createLink(uri : '/')}assets/relations.json"></script>
		<!-- IMPORTAÇÕES ESPECÍFICAS DESTA PÁGINA -->
		<!-- INÍCIO JAVASCRIPT -->
		<asset:javascript src="drawUMLDiagram.js" />
		<asset:javascript src="drawRelations.js" />
		<asset:javascript src="drawClass.js" />
		<asset:javascript src="drawEnum.js" />
		<asset:javascript src="drawInterface.js" />
		<asset:javascript src="drawElement.js" />
		<asset:javascript src="main.js"/>
		<!-- FIM JAVASCRIPT -->
	</head>
	<body>
		<div id="page-body" role="main">
			Página index do controlador FindClassInfo. Use a ferramenta do desenvolvedor para ver o retorno em JSON.<br><br>

			<button id="centerButton">Center</button>
			<button id="centerContentButton">Center content</button>
			<button id="zoomOutButton">Zoom Out</button>
			<button id="zoomInButton">Zoom In</button>
			<button id="zoomToFitButton">Zoom to Fit</button>

			<input type="hidden" name="mapClasses" id="mapClasses" value="${map}"/>
			<div id="paper"></div>
		</div>
	</body>
</html>
