<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<asset:javascript src="d3.v3.js"/>
		<asset:stylesheet src="responsibleMethods/responsibleMethods.css"/>
		<asset:javascript src="responsibleMethods/mainResponsibleMethods.js"/>
	</head>
	<body>
		<input type="hidden" name="jsonResponsibleMethods" id="jsonResponsibleMethods" value="${responsibleMethods}"/>
		<input type="hidden" name="urlsMap" id="urlsMap" value="${urlsMap}"/>
		<div id="page-body" role="main" class="content body">
			<div id="deviationScenariosSummary" class="bg-gray-light deviationScenariosSummary text-center">
				<div class="col-md-3">
					<div class="description-aster-plot">
						<div class="description-aster-plot-content">
							<span class="info-box-text"><g:message code="callGraphVisualization.summary.system"/></span> <span
								class="info-box-number" style="font-size: 14px !important;">
								${systemName}
							</span>
						</div>
					</div>
				</div>
				<div class="col-md-3">
					<div class="description-aster-plot">
						<div class="description-aster-plot-content">
							<span class="info-box-text"><g:message code="showDeviationScenarios.description.versions"/></span> <span
								class="info-box-number" style="font-size: 14px !important;">
								<g:message code="showDeviationScenarios.description.versionsFromTo" args="[previousVersion, nextVersion]"/>
							</span>
						</div>
					</div>
				</div>
				<div class="col-md-3">
					<div class="description-aster-plot">
						<div class="description-aster-plot-content">
							<span class="info-box-text">
								<g:message code="showDeviationScenarios.description.quantityOfScenarios"/>
							</span> <span
								class="info-box-number" style="font-size: 14px !important;">
								${totalScenarios} <small style=font-weight:normal;font-size:12px;>(total)</small> / ${degradedScenarios} <small style=font-weight:normal;font-size:12px;>(degraded)</small> / ${optimizedScenarios} <small style=font-weight:normal;font-size:12px;>(optimized)</small>
							</span>
						</div>
					</div>
				</div>
				<div class="col-md-3">
					<div class="description-aster-plot">
						<div class="description-aster-plot-content">
							<span class="info-box-text"><g:message code="responsibleMethods.caption.quantityOfMethods" /></span> <span
								class="info-box-number" style="font-size: 14px !important;">
								${totalMethods}
							</span>
						</div>
					</div>
				</div>
			</div>
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
								<div class="legend-square legend-degradation"></div>
								<span class="legend-text">
									<g:message code="responsibleMethods.caption.scenario.degraded"/>
								</span>
							</div>
							<div class="legend-block">
								<div class="legend-square legend-optimization"></div>
								<span class="legend-text">
									<g:message code="responsibleMethods.caption.scenario.optimized"/>
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
