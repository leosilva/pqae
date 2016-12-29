<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<asset:javascript src="bootstrap3-typeahead.js" />
	</head>
	<body>
		<div id="page-body" role="main" class="content">
			<g:render template="/shared/alertMessage" />
			<div class="well well-sm"><g:message code="uploadABackup.well" /></div>
			<div class="col-md-6" style="padding-left: 0px;">
				<div class="box box-default">
					<div class="box-header with-border">
						<h3 class="box-title"><g:message code="uploadABackup.form.title" /></h3>
					</div>
					<!-- /.box-header -->
					<!-- form start -->
					<g:form role="form" controller="config" action="saveBackupUpload" method="post" enctype="multipart/form-data" onsubmit="\$('.overlay').css('display', 'inherit');">
						<g:hiddenField name="typeahead" id="typeahead" value="${typeahead}"/>
						<div class="box-body">
							<div class="form-group">
								<label for="systemName"><g:message code="uploadABackup.form.system" /></label>
								<input type="text" class="form-control typeahead" id="systemName" name="systemName" autocomplete="off" data-provide="typeahead" required="required">
							</div>
							<div class="form-group">
								<label for="backupFile"><g:message code="uploadABackup.form.backupFile" /></label>
								<input type="file" id="backupFile" name="backupFile" required="required">
							</div>
						</div>
						<!-- /.box-body -->
	
						<div class="box-footer">
							<button type="submit" class="btn btn-primary"><g:message code="uploadABackup.form.button.submit" /></button>
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
			</div>
			<div class="col-md-6" style="padding-right: 0px;">
				<div class="box box-default">
					<div class="box-header with-border">
						<h3 class="box-title"><g:message code="uploadABackup.backupsUploaded" /></h3>
					</div>
					<!-- /.box-header -->
					<div class="box-body">
						<div class="box-group" id="accordion">
							<g:each in="${mapSystems}" var="ms">
								<div class="panel box box-primary">
									<div class="box-header with-border">
										<h4 class="box-title">
											<a data-toggle="collapse" data-parent="#accordion"
												href="#collapse${ms.getKey()}" aria-expanded="false" class="collapsed">
												${ms.getKey()} - ${ms.getValue().size()} file(s)
											</a>
										</h4>
									</div>
									<div id="collapse${ms.getKey()}" class="panel-collapse collapse"
										aria-expanded="false" style="height: 0px;">
										<div id="boxBody${ms.getKey()}" class="box-body" style="max-height: 200px; overflow-y: scroll;">
											<table class="table table-striped table-responsive">
												<tbody>
													<tr>
														<th><g:message code="uploadABackup.backupsUploaded.fileName" /></th>
														<th>Size</th>
														<th><g:message code="uploadABackup.backupsUploaded.actions" /></th>
													</tr>
													<g:each in="${ms.getValue()}" var="b">
														<tr>
															<td>
																${b.getKey()}
															</td>
															<td>
																<g:formatNumber number="${b.getValue()["size"] / 1024 / 1024}" type="number" maxFractionDigits="2" /> MB
															</td>
															<td>
																<g:link controller="config" action="deleteBackupFile" params="[file : b.getKey(), systemName : ms.getKey()]" onclick="return confirm('Are you sure?');" title="${message(code: 'uploadABackup.delete')}">
																	<i class="fa fa-trash" aria-hidden="true"></i>
																</g:link>
																&nbsp;
																<g:link controller="config" action="downloadFile" params="[file : b.value]" title="${message(code: 'uploadABackup.download')}">
																	<i class="fa fa-download" aria-hidden="true"></i>
																</g:link>
															</td>
														</tr>
													</g:each>
												</tbody>
											</table>
										</div>
									</div>
								</div>
							</g:each>
						</div>
					</div>
					<div class="overlay" style="display: none;">
						<i class="fa fa-refresh fa-spin"></i>
					</div>
				</div>
				<!-- /.box-body -->
			</div>
			<!-- /.box -->
		</div>
	</body>
</html>
