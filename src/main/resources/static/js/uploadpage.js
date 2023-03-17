var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
function deleteCheckedList(){
    var $positions = document.querySelectorAll(".position-checkbox:checked");
    const positions = [];
    for (let i = 0; i < $positions.length; i++) {
        positions.push($positions[i].value)
    }
     $.ajax({
     url: '/deletefile',
     contentType: 'application/json',
     method: 'POST',
     data: JSON.stringify({positions : positions}),
     beforeSend: function(xhr) {
        xhr.setRequestHeader(header, token);
     },
     error: function(request, status, error) {
     console.log("code:" + request.status + "\n" + "message:" + request.responseText + "\n" + "error:" + error);
     }, complete: function() {
        window.location.reload();
     }
     });
}

$(document).ready(function() {
	$("#cbx_chkAll").click(function() {
		if($("#cbx_chkAll").is(":checked")) $("input[name=delete-filename]").prop("checked", true);
		else $("input[name=delete-filename]").prop("checked", false);
	});

	$("input[name=chk]").click(function() {
		var total = $("input[name=delete-filename]").length;
		var checked = $("input[name=delete-filename]:checked").length;

		if(total != checked) $("#cbx_chkAll").prop("checked", false);
		else $("#cbx_chkAll").prop("checked", true);
	});
});