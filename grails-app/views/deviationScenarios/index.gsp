<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<asset:stylesheet src="asterPlot/asterPlot.css"/>
		<asset:stylesheet src="methodCallGraph/callGraphVisualization.css"/>
		<asset:javascript src="d3.v3.js"/>
		<asset:javascript src="d3.tip.v0.6.3.js"/>
		<asset:javascript src="asterPlot/mainAsterPlot.js"/>
	</head>
	<body>
		<input type="hidden" name="jsonScenarios" id="jsonScenarios" value="${scenarios}"/>
		<div id="page-body" role="main" class="content body">
			<div id="deviationScenariosSummary" class="bg-gray-light deviationScenariosSummary text-center">
				<div class="col-md-3">
					<div class="description-aster-plot">
						<div class="description-aster-plot-content">
							<span class="info-box-text"><g:message code="callGraphVisualization.summary.system"/></span> <span
								class="info-box-number" style="font-size: 14px !important;">
								${analyzedSystem.systemName}
							</span>
						</div>
					</div>
				</div>
				<div class="col-md-3" style="width: 23.6%;">
					<div class="description-aster-plot">
						<div class="description-aster-plot-content">
							<span class="info-box-text"><g:message code="showDeviationScenarios.description.versions"/></span> <span
								class="info-box-number" style="font-size: 14px !important;">
								<g:message code="showDeviationScenarios.description.versionsFromTo" args="[analyzedSystem.previousVersion, analyzedSystem.nextVersion]"/>
							</span>
						</div>
					</div>
				</div>
				<div class="col-md-2" style="width: 18%;">
					<div class="description-aster-plot">
						<div class="description-aster-plot-content">
							<span class="info-box-text"><g:message code="showDeviationScenarios.description.quantityOfScenarios"/></span> <span
								class="info-box-number" style="font-size: 14px !important;">
								${analyzedSystem.analyzedScenarios.size()}
							</span>
						</div>
					</div>
				</div>
				<div class="col-md-2">
					<div class="description-aster-plot">
						<div class="description-aster-plot-content">
							<span class="info-box-text"><g:message code="showDeviationScenarios.description.degradedScenarios"/></span> <span
								class="info-box-number" style="font-size: 14px !important;">
								${analyzedSystem.analyzedScenarios.count { it.isDegraded == true }}
							</span>
						</div>
					</div>
				</div>
				<div class="col-md-2">
					<div class="description-aster-plot">
						<div class="description-aster-plot-content">
							<span class="info-box-text"><g:message code="showDeviationScenarios.description.optimizedScenarios"/></span> <span
								class="info-box-number" style="font-size: 14px !important;">
								${analyzedSystem.analyzedScenarios.count { it.isDegraded == false }}
							</span>
						</div>
					</div>
				</div>
			</div>
			<div id="svgAsterPlot" class="text-center"></div>
			<div id="legendBox" class="col-md-2 pull-right">
					<div id="legendBoxChildDiv" class="box box-default box-solid">
						<div id="legendBoxHeader" class="box-header with-border text-center" data-widget="collapse" style="padding: 5px;">
							<h3 class="box-title" style="font-size: 14px;"><g:message code="callGraphVisualization.caption.title"/></h3>
						</div>
						<!-- /.box-header -->
						<div class="box-body" style="font-size: 12px;">
							<div id="evolveLegend" class="evolve-legend">
								<div class="legend-block">
									<div class="legend-title">
										<g:message code="showDeviationScenarios.legend.sliceHeight" />
									</div>
									<span class="legend-text">
										<g:message code="showDeviationScenarios.legend.sliceHeight.definition" />
									</span>
								</div>
								<div class="legend-block">
									<div class="legend-title">
										<g:message code="showDeviationScenarios.legend.sliceWidth" />
									</div>
									<span class="legend-text">
										<g:message code="showDeviationScenarios.legend.sliceWidth.definition" />
									</span>
								</div>
								<div class="legend-block">
									<div class="legend-square legend-degradation"></div>
									<span class="legend-text">
										<g:message code="callGraphVisualization.caption.degradation"/>
									</span>
								</div>
								<div class="legend-block">
									<div class="legend-square legend-optimization"></div>
									<span class="legend-text">
										<g:message code="callGraphVisualization.caption.optimization"/>
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
