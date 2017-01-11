<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
	</head>
	<body>
		<div id="page-body" role="main" class="content">
			<!-- Main content -->
			<section class="content">
				<div class="error-page">
					<h2 class="headline text-red" style="height: 150px;">${errorCode}</h2>
					<div class="error-content" style="height: 150px; display: flex; align-items: center;">
						<div>
							<h3>
								<i class="fa fa-warning text-red"></i> ${errorTitle}
							</h3>
							<p>
								${errorMessage}
							</p>
						</div>
					</div>
				</div>
				<!-- /.error-page -->

			</section>
			<!-- /.content -->
		</div>
	</body>
</html>
