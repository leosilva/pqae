<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<asset:stylesheet src="analysis/analysis.css"/>
		<asset:javascript src="bootstrap3-typeahead.js" />
		<asset:javascript src="analysis/mainNewAnalysis.js" />
	</head>
	<body>
		<div id="page-body" role="main" class="content">
			<g:render template="/shared/alertMessage" />
			<div class="well well-sm"><g:message code="newAnalysis.well" /></div>
			<g:hiddenField name="rootURL" id="rootURL" value="${rootURL}"/>
			<g:hiddenField name="ajaxURL" id="ajaxURL" value="${ajaxURL}"/>
			<g:hiddenField name="typeahead" id="typeahead" value="${typeahead}"/>
			<div class="col-md-12" style="padding-left: 0px; padding-right: 0px;">
				<div class="box box-default">
					<div class="box-header with-border">
						<h3 class="box-title"><g:message code="newAnalysis.form.title" /></h3>
					</div>
					<!-- /.box-header -->
					<!-- form start -->
					<g:form role="form" controller="analysis" action="processAndSaveAnalysis" method="post" enctype="multipart/form-data">
						<g:hiddenField name="targetUri" id="targetUri" value="${createLink(uri: '/')}"/>
						<div class="box-body">
							<div class="form-group">
								<label for="systemName"><g:message code="newAnalysis.form.system" /></label>
							 	<%--<g:select name="systemName" id="systemName" from="${systems}" class="form-control" noSelection="['':'']" value="${params?.systemName}" required="true"/>
							--%><input type="text" class="form-control typeahead" id="systemName" name="systemName" autocomplete="off" data-provide="typeahead" required="required">
							</div>
							<div class="form-group">
								<div class="col-md-4" style="padding-left: 0px;">
									<label for="previousVersion" class="control-label"><g:message code="newAnalysis.form.previousVersion" /></label>
									<g:textField name="previousVersion" id="previousVersion" required="required" class="form-control" value="${params?.previousVersion}"/>
								</div>
							</div>
							<div class="col-md-8" style="padding-right: 0px;">
								<label for="backupFilePreviousVersion" class="control-label"><g:message code="newAnalysis.form.previousBackupVersion" /></label>
								<input type="file" id="backupFilePreviousVersion" name="backupFilePreviousVersion" required="required" accept=".backup" class="form-control">
							</div>
							<div class="form-group">
								<div class="col-md-4" style="padding-left: 0px;">
									<label for="nextVersion" class="control-label"><g:message code="newAnalysis.form.nextVersion" /></label>
									<g:textField name="nextVersion" id="nextVersion" required="required" class="form-control" value="${params?.nextVersion}"/>
								</div>
							</div>
							<div class="col-md-8" style="padding-right: 0px;">
								<label for="backupFileNextVersion" class="control-label"><g:message code="newAnalysis.form.nextBackupVersion" /></label>
								<input type="file" id="backupFileNextVersion" name="backupFileNextVersion" required="required" accept=".backup" class="form-control">
							</div>
							<div class="form-group">
								<label for="resultFileDegradedScenarios"><g:message code="newAnalysis.form.perfMiner.degradedScenarios" /></label>
								<input type="file" id="resultFileDegradedScenarios" name="resultFileDegradedScenarios" accept=".txt" value="${params?.resultFileDegradedScenarios}">
							</div>
							<div class="form-group">
								<label for="resultFileOptimizedScenarios"><g:message code="newAnalysis.form.perfMiner.optimizedScenarios" /></label>
								<input type="file" id="resultFileOptimizedScenarios" name="resultFileOptimizedScenarios" accept=".txt" value="${params?.resultFileOptimizedScenarios}">
							</div>
						</div>
						<!-- /.box-body -->
	
						<div class="box-footer">
							<button type="submit" class="btn btn-primary pull-right" onclick="return validateForm();"><g:message code="default.button.submit.label" /></button>
							<button type="button" class="btn btn-default" onclick="resetForm();"><g:message code="default.button.cancel.label" /></button>
						</div>
						<script>
							$(document).ready(function() {
								var src = $("#typeahead").val().replace("[", "").replace("]", "").split(",")
								$.each(src, function(key, value) {
									value = value.replace(" ", "")
								})
								$("#systemName").typeahead({source : src})
							});
						</script>
					</g:form>
					<div class="overlay" style="display: none;">
						<i class="fa fa-refresh fa-spin"></i>
						<span class="wait-message text-center" style="display: none;"><g:message code="newAnalysis.wait.message" args="[rootURL]"/></span>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>