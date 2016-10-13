<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
	</head>
	<body>
		<div id="page-body" role="main" class="content body">
			<ul>
				<g:each in="${scenarios}" var="s">
					<li>
						<g:link controller="CallGraphVisualization" action="callGraphVisualization" 
						params="[systemName: 'Jetty-Servlet', previousVersion : '9.2.6', nextVersion : '9.3.0.M1', scenarioName: s]">
						${s}</g:link>
					 </li>
				</g:each>
			</ul>
		</div>
		<script>
			$(document).ready(function() {
				$("[data-toggle='offcanvas']").click()
			});
		</script>
	</body>
</html>
