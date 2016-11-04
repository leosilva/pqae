<div class="box box-default collapsed-box">
	<div class="box-header with-border" data-widget="collapse">
		<div class="box-tools pull-right">
			<span class="label ${info.isDegraded ? 'label-danger' : 'label-success'}">${info.isDegraded ? message(code: "callGraphVisualization.degradation") : message(code: "callGraphVisualization.optimization")}</span>
		</div>
		<h3 class="box-title">
			<a role="button" data-widget="collapse"><g:message code="callGraphVisualization.details"/></a>
		</h3>
		<!-- /.box-tools -->
	</div>
	<!-- /.box-header -->
	<div class="box-body bg-gray-light">
		<h4><g:message code="callGraphVisualization.details.description"/></h4>
		<div id="divDescriptionSystem">
			<div class="col-md-2">
				<div class="info-box">
					<div class="info-box-content" style="margin-left: 0px !important">
						<span class="info-box-text"><g:message code="callGraphVisualization.details.system"/></span> <span
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
						<span class="info-box-text"><g:message code="callGraphVisualization.details.versions"/></span> <span
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
						<span class="info-box-text"><g:message code="callGraphVisualization.details.scenario"/></span> <span
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
						<span class="info-box-text"><g:message code="callGraphVisualization.details.time"/></span> <span
							class="info-box-number" style="font-size: 14px !important;">
							${info.scenarioNextTime} ms
						</span>
					</div>
					<!-- /.info-box-content -->
				</div>
				<!-- /.info-box -->
			</div>
		</div>
		<h4><g:message code="callGraphVisualization.details.nodes"/></h4>
		<div id="divDescriptionNodes">
			<div class="col-md-2">
				<div class="info-box">
					<span class="info-box-icon bg-aqua"> <i class="fa fa-bars"></i>
					</span>
					<div class="info-box-content">
						<span class="info-box-text"><g:message code="callGraphVisualization.details.total"/></span> <span
							class="info-box-number">
							${info.totalNodes}
						</span>
					</div>
					<!-- /.info-box-content -->
				</div>
				<!-- /.info-box -->
			</div>
			<div class="col-md-2">
				<div class="info-box">
					<span class="info-box-icon bg-yellow"> <i class="ion ion-speedometer"></i>
					</span>
					<div class="info-box-content">
						<span class="info-box-text"><g:message code="callGraphVisualization.details.deviation"/></span> <span
							class="info-box-number">
							${info.deviationNodes}
						</span>
					</div>
					<!-- /.info-box-content -->
				</div>
				<!-- /.info-box -->
			</div>
			<div class="col-md-2">
				<div class="info-box">
					<span class="info-box-icon bg-light-salmon"> <i class="fa fa-plus"></i>
					</span>
					<div class="info-box-content">
						<span class="info-box-text"><g:message code="callGraphVisualization.details.added"/></span> <span
							class="info-box-number">
							${info.addedNodes}
						</span>
					</div>
					<!-- /.info-box-content -->
				</div>
				<!-- /.info-box -->
			</div>
			<div class="col-md-2">
				<div class="info-box">
					<span class="info-box-icon bg-red"> <i class="fa fa-minus"></i>
					</span>
					<div class="info-box-content">
						<span class="info-box-text"><g:message code="callGraphVisualization.details.removed"/></span> <span
							class="info-box-number">
							${info.removedNodes}
						</span>
					</div>
					<!-- /.info-box-content -->
				</div>
				<!-- /.info-box -->
			</div>
			<div class="col-md-2">
				<div class="info-box">
					<span class="info-box-icon bg-aqua"> <i class="fa fa-eye"></i>
					</span>
					<div class="info-box-content">
						<span class="info-box-text"><g:message code="callGraphVisualization.details.viewing"/></span> <span
							class="info-box-number">
							${info.showingNodes}
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