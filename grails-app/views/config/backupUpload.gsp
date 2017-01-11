<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<asset:javascript src="bootstrap3-typeahead.js" />
		<asset:javascript src="backupUpload/mainBackupUpload.js" />
	</head>
	<body>
		<div id="page-body" role="main" class="content">
			<g:render template="/shared/alertMessage" />
			<div class="well well-sm"><g:message code="uploadABackup.well" /></div>
			<div class="col-md-6" style="padding-left: 0px;">
				<g:render template="uploadABackupForm" />
			</div>
			<div class="col-md-6" style="padding-right: 0px;">
				<g:render template="backupsUploaded" />
			</div>
			<!-- /.box -->
		</div>
	</body>
</html>
