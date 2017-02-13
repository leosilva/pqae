<div class="box box-default">
	<div class="box-header with-border">
		<h3 class="box-title"><g:message code="uploadABackup.form.title" /></h3>
	</div>
	<!-- /.box-header -->
	<!-- form start -->
	<g:form role="form" controller="config" action="saveBackupUpload" method="post" enctype="multipart/form-data">
		<g:hiddenField name="typeahead" id="typeahead" value="${typeahead}"/>
		<div class="box-body">
			<div class="form-group">
				<label for="systemName"><g:message code="uploadABackup.form.system" /></label>
				<input type="text" class="form-control typeahead" id="systemName" name="systemName" autocomplete="off" data-provide="typeahead" required="required">
			</div>
			<div class="form-group">
				<label for="backupFile"><g:message code="uploadABackup.form.backupFile" /></label>
				<input type="file" id="backupFile" name="backupFile" required="required" accept=".backup">
			</div>
		</div>
		<!-- /.box-body -->

		<div class="box-footer">
			<button type="submit" class="btn btn-primary pull-right" onclick="return validateForm();"><g:message code="default.button.submit.label" /></button>
			<button type="reset" class="btn btn-default"><g:message code="default.button.cancel.label" /></button>
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
	</div>
</div>