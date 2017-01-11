$(document).ready(function() {
	setInterval(function(){
		$.ajax({
			method : "POST",
			url : $("#ajaxUri").val(),
			success : function(data) {
				$("#tableSystems").html(data)
			}
		})
	}, 30000);
});