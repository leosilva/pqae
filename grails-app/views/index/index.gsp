<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
	</head>
	<body>
		<div id="page-body" role="main" class="content body text-center">
			<g:each in="${ans}" var="an">
				<div class="box">
					<div class="box-header">
						<h3 class="box-title">
							${an.systemName}
						</h3>
					</div>
					<!-- /.box-header -->
					<div class="box-body no-padding">
						<table class="table table-striped table-responsive">
							<tbody>
								<tr>
									<th><g:message code="analyzedSystems.versionFrom" /></th>
									<th><g:message code="analyzedSystems.versionTo" /></th>
									<th><g:message code="analyzedSystems.actions" /></th>
								</tr>
								<tr>
									<td>
										${an.previousVersion}
									</td>
									<td>
										${an.nextVersion}
									</td>
									<td>
										<g:link controller="deviationScenarios" action="index"
											params="['systemName' : an.systemName, 'previousVersion' : an.previousVersion, 'nextVersion' : an.nextVersion]"
											title="${message(code: 'analyzedSystems.actions.showDeviationScenarios.title')}">
											<i class="material-icons">donut_small</i>
										</g:link>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
					<!-- /.box-body -->
				</div>
			</g:each>
		</div>
	</body>
</html>
