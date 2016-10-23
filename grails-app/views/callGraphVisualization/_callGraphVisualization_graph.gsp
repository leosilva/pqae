<div class="box box-default">
	<div class="box-header with-border">
		<h3 class="box-title">
			<a role="button" data-widget="collapse"><g:message code="callGraphVisualization.callGraph"/></a>
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
					title="${message(code: 'callGraphVisualization.callGraph.zoom.out')}">
					<i class="fa fa-search-minus"></i>
				</button>
				<button id="zoomToFitButton" class="btn btn-default btn-sm"
					title="${message(code: 'callGraphVisualization.callGraph.zoom.fit')}">
					<i class="fa fa-search"></i>
				</button>
				<button id="zoomInButton" class="btn btn-default btn-sm"
					title="${message(code: 'callGraphVisualization.callGraph.zoom.in')}">
					<i class="fa fa-search-plus"></i>
				</button>
			</div>
		</div>
		<div id="evolvePaper" class="evolve-paper">
			<div id="paperNextVersion"></div>
		</div>
		<div id="evolveLegend" class="text-center evolve-legend">
			<div class="legend-block">
				<div class="legend-square legend-no-deviation"></div>
				<span class="legend-text">
					<g:message code="callGraphVisualization.legend.noDeviation"/>
				</span>
			</div>
			<div class="legend-block">
				<div class="legend-square legend-optimization"></div>
				<span class="legend-text">
					<g:message code="callGraphVisualization.legend.optimization"/>
				</span>
			</div>
			<div class="legend-block">
				<div class="legend-square legend-degradation"></div>
				<span class="legend-text">
					<g:message code="callGraphVisualization.legend.degradation"/>
				</span>
			</div>
			<div class="legend-block">
				<div class="legend-square legend-added"></div>
				<span class="legend-text">
					<g:message code="callGraphVisualization.legend.added"/>
				</span>
			</div>
		</div>
	</div>
</div>