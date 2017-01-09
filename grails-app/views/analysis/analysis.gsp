<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<asset:stylesheet src="analysis/analysis.css"/>
		<asset:javascript src="analysis/mainNewAnalysis.js" />
	</head>
	<body>
		<div id="page-body" role="main" class="content">
			<g:render template="/shared/alertMessage" />
			<div class="well well-sm"><g:message code="newAnalysis.well" /></div>
			<g:hiddenField name="ajaxURL" id="ajaxURL" value="${ajaxURL}"/>
			<div class="col-md-12" style="padding-left: 0px; padding-right: 0px;">
				<div class="box box-default">
					<div class="box-header with-border">
						<h3 class="box-title"><g:message code="newAnalysis.form.title" /></h3>
					</div>
					<!-- /.box-header -->
					<!-- form start -->
					<g:form role="form" controller="analysis" action="processAndSaveAnalysis" method="post" enctype="multipart/form-data" onsubmit="\$('.overlay').css('display', 'inherit');">
						<div class="box-body">
							<div class="form-group">
								<label for="systemName"><g:message code="newAnalysis.form.system" /></label>
							 	<g:select name="systemName" id="systemName" from="${systems}" class="form-control" noSelection="['':'']" required="true"/>
							</div>
							<div class="col-md-4" style="padding-left: 0px;">
								<div class="form-group">
									<label for="previousVersion" class="control-label"><g:message code="newAnalysis.form.previousVersion" /></label>
									<g:textField name="previousVersion" id="previousVersion" required="required" class="form-control"/>
								</div>
							</div>
							<div class="col-md-8" style="padding-right: 0px;">
								<div class="form-group">
									<label for="backupFilePreviousVersion"><g:message code="newAnalysis.form.previousBackupVersion" /></label>
								 	<g:select name="backupFilePreviousVersion" id="backupFilePreviousVersion" from="" class="form-control" noSelection="['':'']" disabled="true"/>
								</div>
							</div>
							<div class="col-md-4" style="padding-left: 0px;">
								<div class="form-group">
									<label for="nextVersion" class="control-label"><g:message code="newAnalysis.form.nextVersion" /></label>
									<g:textField name="nextVersion" id="nextVersion" required="required" class="form-control"/>
								</div>
							</div>
							<div class="col-md-8" style="padding-right: 0px;">
								<div class="form-group">
									<label for="backupFileNextVersion"><g:message code="newAnalysis.form.nextBackupVersion" /></label>
								 	<g:select name="backupFileNextVersion" id="backupFileNextVersion" from="" class="form-control" noSelection="['':'']" disabled="true"/>
								</div>
							</div>
							<div class="form-group">
								<label for="resultFileDegradedScenarios"><g:message code="newAnalysis.form.perfMiner.degradedScenarios" /></label>
								<input type="file" id="resultFileDegradedScenarios" name="resultFileDegradedScenarios" required="required" accept=".txt">
							</div>
							<div class="form-group">
								<label for="resultFileOptimizedScenarios"><g:message code="newAnalysis.form.perfMiner.optimizedScenarios" /></label>
								<input type="file" id="resultFileOptimizedScenarios" name="resultFileOptimizedScenarios" required="required" accept=".txt">
							</div>
						</div>
						<!-- /.box-body -->
	
						<div class="box-footer">
							<button type="submit" class="btn btn-primary pull-right" onclick="return validateForm();"><g:message code="default.button.submit.label" /></button>
							<button type="reset" class="btn btn-default"><g:message code="default.button.cancel.label" /></button>
						</div>
					</g:form>
					<div class="overlay" style="display: none;">
						<i class="fa fa-refresh fa-spin"></i>
						<span class="wait-message"><g:message code="newAnalysis.wait.message" /></span>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
