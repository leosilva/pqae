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
			<div id="svgAsterPlot" class="text-center">
				<div id="legend" class="col-md-3 pull-right" style="padding-right: 0px; display: flex; align-items: center;">
					<div class="box box-solid box-default">
						<div class="box-body" style="font-size: 12px;">
							<dl class="dl-horizontal" style="margin-bottom: 0px;">
								<dt style="width: 110px;">Slice Height</dt>
								<dd style="margin-left: 130px;">Execution Time</dd>
								<dt style="width: 110px;">Slice Width</dt>
								<dd style="margin-left: 130px;">Deviation Percentage</dd>
							</dl>
						</div>
						<!-- /.box-body -->
					</div>
					<!-- /.box -->
				</div>
			</div>
		</div>
	</body>
</html>
