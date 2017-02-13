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
			<div id="svgResponsibleMethods"></div>
			<div id="legendBox" class="col-md-2">
				<div id="legendBoxChildDiv" class="box box-default box-solid collapsed-box">
					<div id="legendBoxHeader" class="box-header with-border text-center" data-widget="collapse" style="padding: 5px;">
						<h3 class="box-title" style="font-size: 14px;"><g:message code="callGraphVisualization.caption.title"/></h3>
					</div>
					<!-- /.box-header -->
					<div class="box-body" style="display: none;">
						<div id="evolveLegend" class="evolve-legend">
							<div class="legend-block">
								<div class="legend-square legend-method"></div>
								<span class="legend-text">
									<g:message code="responsibleMethods.caption.method"/>
								</span>
							</div>
							<div class="legend-block">
								<div class="legend-square legend-scenario"></div>
								<span class="legend-text">
									<g:message code="responsibleMethods.caption.scenario"/>
								</span>
							</div>
						</div>
					</div>
					<!-- /.box-body -->
				</div>
				<!-- /.box -->
			</div>
		</div>
	</body>
</html>
