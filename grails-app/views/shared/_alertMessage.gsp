<g:if test="${flash.message == true}">
	<div id="alert" class="alert ${flash.alertClass} alert-dismissable">
		<button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>
		<h4> <i class="icon fa fa-check"></i> ${flash.alertTitle} </h4>
		${flash.alertMessage}
		<script>
			setTimeout(function(){
	        	$("#alert").fadeOut()
		    }, 8000);
		</script>
	</div>
</g:if>