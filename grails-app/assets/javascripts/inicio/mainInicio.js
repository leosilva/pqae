$(document).ready(function() {
	refreshSystemStatus(15000);
});


function refreshSystemStatus(interval) {
	setTimeout(function(){
		$.ajax({
			method : "POST",
			url : $("#ajaxUri").val(),
			success : function(data) {
				$("#tableSystems").html(data)
				if (data.includes("PENDING")) {
					refreshSystemStatus(15000)
				} else if (data.includes("COMPLETED")) {
					refreshSystemStatus(60000)
				}
			}
		})
	}, interval);	
}