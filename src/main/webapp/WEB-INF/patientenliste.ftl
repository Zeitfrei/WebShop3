<html>
<head><title>Hospital Meenu</title>
 
<body>

	<input type="button" value="Zurück zum Menü" onclick="location.href='/menu';"> <br>

 	<h1> Patientenliste </h1>
 	
 	<form name="liste" id="liste_id" action="patientenliste" method="post">
	 			<td>ID</td> <td>Name</td> Datum <br>
		 		<#list patients as patient>
		 			<a href="#" onclick=clickLink(${patient.getId()})>${patient.getId()?html} ${patient.getName()?html} ${patient.getGeburtstag()?html}</a> 
		 			<br>
		 		</#list>
		 		<input type="hidden" name="_id" id="_id" value="" />
	</form>
	
</body>
<script>
function clickLink(a) {
	document.getElementById('_id').value = a;
	document.getElementById('liste_id').submit();
	
}
</script>
</html>