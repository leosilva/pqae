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
		<asset:javascript src="events.js" />
		<!-- FIM JAVASCRIPT -->
	</head>
	<body>
		<div id="page-body" role="main">
			<button id="centerButton">Center</button>
			<button id="centerContentButton">Center content</button>
			<button id="zoomOutButton">Zoom Out</button>
			<button id="zoomInButton">Zoom In</button>
			<button id="zoomToFitButton">Zoom to Fit</button>
			<g:select name="tipoExibicao" optionKey="key" optionValue="value" from="['SHOW_ALL':'Mostrar tudo', 'SHOW_ATTRIBUTES':'Mostrar Atributos', 'SHOW_METHODS':'Mostrar Métodos']" noSelection="['':'Selecione...']"/>

			<input type="hidden" name="mapClasses" id="mapClasses" value="${map}"/>
			
			<div id="paper"></div>
		</div>
	</body>
</html>
