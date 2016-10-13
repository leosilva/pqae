<div class="box box-default">
	<div class="box-header with-border">
		<h3 class="box-title">
			<a role="button" data-widget="collapse">Call Graph</a>
		</h3>
		<!-- /.box-tools -->
	</div>
	<!-- /.box-header -->
	<div class="box-body bg-gray-light">
		<div class="mailbox-controls text-center"
			style="width: 100%; margin-top: -10px;">
			<!-- Check all button -->
			<div class="btn-group">
				<button id="zoomOutButton" class="btn btn-default btn-sm"
					title="Zoom Out">
					<i class="fa fa-search-minus"></i>
				</button>
				<button id="zoomToFitButton" class="btn btn-default btn-sm"
					title="Zoom to Fit">
					<i class="fa fa-search"></i>
				</button>
				<button id="zoomInButton" class="btn btn-default btn-sm"
					title="Zoom In">
					<i class="fa fa-search-plus"></i>
				</button>
			</div>
		</div>
		<div
			style="width: 100%; height: 530px; overflow: scroll; border: 1px solid black; float: right;">
			<div id="paperNextVersion"></div>
		</div>
		<div class="text-center" style="width: 100%;float: left; padding-top: 10px;">
			<div style="display: inline-block; margin-left: 10px;">
				<div style="border: 1px solid black;background-color: #FFF4DF;width: 35px;height: 25px;display:inline-block;float: left;"></div>
				<span style="text-align: left; margin-left: 10px; height: 25px; display: table-cell; vertical-align: middle; padding-left: 10px;">No deviation</span>
			</div>
			<div style="display: inline-block; margin-left: 10px;">
				<div style="border: 1px solid black;background-color: #99CC99;width: 35px;height: 25px;display:inline-block;float: left;"></div>
				<span style="text-align: left; margin-left: 10px; height: 25px; display: table-cell; vertical-align: middle; padding-left: 10px;">Optimization</span>
			</div>
			<div style="display: inline-block; margin-left: 10px;">
				<div style="border: 1px solid black;background-color: #FFCCCC;width: 35px;height: 25px;display:inline-block;float: left;"></div>
				<span style="text-align: left; margin-left: 10px; height: 25px; display: table-cell; vertical-align: middle; padding-left: 10px;">Degradation</span>
			</div>
			<div style="display: inline-block; margin-left: 10px;">
				<div style="border: 1px solid black;background-color: orange;width: 35px;height: 25px;display:inline-block;float: left;"></div>
				<span style="text-align: left; margin-left: 10px; height: 25px; display: table-cell; vertical-align: middle; padding-left: 10px;">Added</span>
			</div>
		</div>
	</div>
</div>