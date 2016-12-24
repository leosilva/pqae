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
		<div id="page-body" role="main" class="content body text-center">
			<div id="deviationScenariosSummary" class="bg-gray-light" style="min-height: 60px; width: 100%; margin: auto auto; padding: 8px; margin-bottom: 15px;">
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
				<div class="col-md-3">
					<div class="description-aster-plot">
						<div class="description-aster-plot-content">
							<span class="info-box-text"><g:message code="showDeviationScenarios.description.versions"/></span> <span
								class="info-box-number" style="font-size: 14px !important;">
								<g:message code="showDeviationScenarios.description.versionsFromTo" args="[analyzedSystem.previousVersion, analyzedSystem.nextVersion]"/>
							</span>
						</div>
					</div>
				</div>
				<div class="col-md-2">
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
			<div id="legend" class="col-md-3 pull-right" style="padding-right: 0px; display: flex; align-items: center;">
				<div class="box box-solid box-default">
					<div class="box-body" style="font-size: 12px;">
						<p style="margin: 0; line-height: 2;"><span style="font-weight: bold;">Slice Height</span>: Execution Time</p>
						<p style="margin: 0; line-height: 2;"><span style="font-weight: bold;">Slice Width</span>: Deviation Percentage</p>
					</div>
					<!-- /.box-body -->
				</div>
				<!-- /.box -->
			</div>
		</div>
	</body>
</html>
