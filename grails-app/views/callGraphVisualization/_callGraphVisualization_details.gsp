<div class="box box-default collapsed-box">
	<div class="box-header with-border">
		<h3 class="box-title">
			<a role="button" data-widget="collapse">Details</a>
		</h3>
		<!-- /.box-tools -->
	</div>
	<!-- /.box-header -->
	<div class="box-body bg-gray-light">
		<h4>Description</h4>
		<div id="divDescriptionSystem">
			<div class="col-md-2">
				<div class="info-box">
					<div class="info-box-content" style="margin-left: 0px !important">
						<span class="info-box-text">System</span> <span
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
						<span class="info-box-text">Versions</span> <span
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
						<span class="info-box-text">Scenario</span> <span
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
						<span class="info-box-text">Broad Time</span> <span
							class="info-box-number" style="font-size: 14px !important;">
							${info.broadScenarioTime} ms
						</span>
					</div>
					<!-- /.info-box-content -->
				</div>
				<!-- /.info-box -->
			</div>
		</div>
		<h4>Nodes</h4>
		<div id="divDescriptionNodes">
			<div class="col-md-2">
				<div class="info-box">
					<span class="info-box-icon bg-aqua"> <i class="fa fa-bars"></i>
					</span>
					<div class="info-box-content">
						<span class="info-box-text">Total</span> <span
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
					<span class="info-box-icon bg-aqua"> <i class="fa fa-files-o"></i>
					</span>
					<div class="info-box-content">
						<span class="info-box-text">Deviation</span> <span
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
					<span class="info-box-icon bg-orange"> <i class="fa fa-plus"></i>
					</span>
					<div class="info-box-content">
						<span class="info-box-text">Added</span> <span
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
						<span class="info-box-text">Removed</span> <span
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
						<span class="info-box-text">Viewing</span> <span
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