<div class="box box-default collapsed-box">
	<div class="box-header with-border text-center" data-widget="collapse">
		<div class="box-tools pull-right">
			<span class="label ${info.isDegraded ? 'label-danger' : 'label-success'}">${info.isDegraded ? message(code: "callGraphVisualization.degradation") : message(code: "callGraphVisualization.optimization")}</span>
		</div>
		<h3 class="box-title pull-left">
			<a role="button" data-widget="collapse"><g:message code="callGraphVisualization.summary"/></a>
		</h3>
		<h3 class="box-title" id="scenarioName">${info.scenarioName}</h3>
		<!-- /.box-tools -->
	</div>
	<!-- /.box-header -->
	<div class="box-body bg-gray-light">
		<h4><g:message code="callGraphVisualization.summary.description"/></h4>
		<div id="divDescriptionSystem">
			<div class="col-md-2">
				<div class="info-box">
					<div class="info-box-content" style="margin-left: 0px !important">
						<span class="info-box-text"><g:message code="callGraphVisualization.summary.system"/></span> <span
							class="info-box-number" style="font-size: 14px !important;">
							${info.system}
						</span>
					</div>
					<!-- /.info-box-content -->
				</div>
				<!-- /.info-box -->
			</div>
			<div class="col-md-2">
				<div class="info-box">
					<div class="info-box-content" style="margin-left: 0px !important">
						<span class="info-box-text"><g:message code="callGraphVisualization.summary.versions"/></span> <span
							class="info-box-number" style="font-size: 14px !important;">
							${info.versionFrom} to ${info.versionTo}
						</span>
					</div>
					<!-- /.info-box-content -->
				</div>
				<!-- /.info-box -->
			</div>
			<div class="col-md-7">
				<div class="info-box">
					<div class="info-box-content" style="margin-left: 0px !important">
						<span class="info-box-text"><g:message code="callGraphVisualization.summary.scenario"/></span> <span
							class="info-box-number" style="font-size: 14px !important;">
							${info.scenarioName}
						</span>
					</div>
					<!-- /.info-box-content -->
				</div>
				<!-- /.info-box -->
			</div>
			<div class="col-md-1">
				<div class="info-box">
					<div class="info-box-content" style="margin-left: 0px !important">
						<span class="info-box-text"><g:message code="callGraphVisualization.summary.time"/></span> <span
							class="info-box-number" style="font-size: 14px !important;">
							${info.scenarioNextTime} ms
						</span>
					</div>
					<!-- /.info-box-content -->
				</div>
				<!-- /.info-box -->
			</div>
		</div>
		<h4><g:message code="callGraphVisualization.summary.nodes"/></h4>
		<div id="divDescriptionNodes">
			<div class="col-md-2">
				<div class="info-box">
					<span class="info-box-icon bg-aqua"> <i class="fa fa-bars"></i>
					</span>
					<div class="info-box-content">
						<span class="info-box-text"><g:message code="callGraphVisualization.summary.total"/></span> <span
							class="info-box-number">
							<g:message code="callGraphVisualization.summary.total.message" args="[info.showingNodes, info.totalNodes]"/>
						</span>
					</div>
					<!-- /.info-box-content -->
				</div>
				<!-- /.info-box -->
			</div>
			<div class="col-md-2">
				<div class="info-box">
					<span class="info-box-icon optimization-color">
						<i class="ionicons ion-arrow-up-b custom-icon"></i>
					</span>
					<div class="info-box-content">
						<span class="info-box-text"><g:message code="callGraphVisualization.summary.optimized"/></span> <span
							class="info-box-number">
							${info.qtdOptimizedNodes}
						</span>
					</div>
					<!-- /.info-box-content -->
				</div>
				<!-- /.info-box -->
			</div>
			<div class="col-md-2">
				<div class="info-box">
					<span class="info-box-icon degradation-color">
						<i class="ionicons ion-arrow-down-b custom-icon"></i>
					</span>
					<div class="info-box-content">
						<span class="info-box-text"><g:message code="callGraphVisualization.summary.degraded"/></span> <span
							class="info-box-number">
							${info.qtdDegradedNodes}
						</span>
					</div>
					<!-- /.info-box-content -->
				</div>
				<!-- /.info-box -->
			</div>
			<div class="col-md-2">
				<div class="info-box">
					<span class="info-box-icon added-color"> <i class="fa fa-plus custom-icon"></i>
					</span>
					<div class="info-box-content">
						<span class="info-box-text"><g:message code="callGraphVisualization.summary.added"/></span> <span
							class="info-box-number">
							${info.qtdAddedNodes} <g:message code="callGraphVisualization.summary.added.methods"/>
						</span>
					</div>
					<!-- /.info-box-content -->
				</div>
				<!-- /.info-box -->
			</div>
			<div class="col-md-2">
				<div class="info-box">
					<span class="info-box-icon removed-color"> <i class="fa fa-minus custom-icon"></i>
					</span>
					<div class="info-box-content">
						<span class="info-box-text"><g:message code="callGraphVisualization.summary.removed"/></span> <span
							class="info-box-number">
							${info.qtdRemovedNodes} <g:message code="callGraphVisualization.summary.removed.methods"/>
						</span>
					</div>
					<!-- /.info-box-content -->
				</div>
				<!-- /.info-box -->
			</div>
		</div>
	</div>
	<!-- /.box-body -->
</div>