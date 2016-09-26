<!DOCTYPE html>
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
		<asset:javascript src="mainDiagram.js"/>
		<asset:javascript src="events.js" />
		<asset:stylesheet href="select2.css"/>
		<asset:javascript src="select2.full.js"/>
		<!-- FIM JAVASCRIPT -->
	</head>
	<body>
		<div class="box box-default">
			<div class="box-body no-padding">
				<div class="mailbox-controls text-center">
					<!-- Check all button -->
					<div class="btn-group">
						<button id="zoomOutButton" class="btn btn-default btn-sm">
							<i class="fa fa-search-minus"></i>
						</button>
						<button id="zoomToFitButton" class="btn btn-default btn-sm">
							<i class="fa fa-search"></i>
						</button>
						<button id="zoomInButton" class="btn btn-default btn-sm">
							<i class="fa fa-search-plus"></i>
						</button>
					</div>
					<!-- /.btn-group -->
					<g:select name="tipoExibicao" optionKey="key" optionValue="value" class="form-control select2"
						from="['SHOW_ALL':'Mostrar tudo', 'SHOW_ATTRIBUTES':'Mostrar Atributos', 'SHOW_METHODS':'Mostrar Métodos']"
						noSelection="['':'Selecione...']" style="width: 150px;" />
				</div>
			</div>
		</div>
		<div id="page-body" role="main">
			<input type="hidden" name="mapClasses" id="mapClasses" value="${map}"/>
			<div id="paper"></div>
		</div>
		<script>
			$(document).ready(function() {
				$(".select2").select2({
					minimumResultsForSearch: Infinity
				})
				$("[data-toggle='offcanvas']").click()			
			});
		</script>
	</body>
</html>
