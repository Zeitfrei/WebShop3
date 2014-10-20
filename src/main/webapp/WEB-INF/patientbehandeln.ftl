<html>
<body>
	<input type="button" value="Zurück zur Patientenliste" onclick="location.href='/patientenliste';"> <br>
	
	<input type="hidden" name="_id" id="_id" value="" />
	
	<#if error??>
	<p style="color:red;">${error}</p>
	</#if>
	

	<#if (place_dia)??>
	<#assign posi = place_dia>
	<#else>
	<#assign posi = 0>
	</#if>
	
	<h1> Behandlung </h1>
	<p>${patient.getName()?html} ${patient.getGeburtstag()?html}</p>
	<br>
	<table class="datatable">
		<tr>
			<th> Datum </th><th> Diagnose </th><th> Medikation </th><th> Notizen </th>
		</tr>
		<#list behandlungen as behandlung>
		<tr>
			<td> ${behandlung.getDatum()} </td><td> ${behandlung.getKrankheit()?html} </td><td> ${behandlung.getMedikamente()?html} </td> <td> ${behandlung.getNotizen()?html} </td>
		</tr>
		</#list>
	</table>
	
	<br>
	
	<form name ="behandeln1" id="behandeln1" action="patientbehandeln" method="post">
	Diagnose: 	
				<select name="dia" id="dia">
					<#list krankheiten as krankheit>
						<option value="${krankheit.getName()?html}">${krankheit.getName()?html}</option>
					</#list>
				</select>
				<input type="submit" value="Medikament suchen" onclick=clickLink("searchmed") />
				
				<br>
	Behandlung mit:
				<select name="med">
					<#list medikamente as medikament>
						<option value="${medikament.getPzn()?html}">${medikament.getName()?html}</option>
					</#list>
				</select>
				<br>
	Datum:
				<input type="text" id="date_txt" name="datum"  /> <br>
	Notizen <br>
		<textarea rows="4" cols="44" name="notizen" id="textarea1" > </textarea>
				<br>
				<input type="submit" value="Weitere Diagnose" onclick=clickLink("adddia") /> <input type="submit" value="Abschließen" onclick=clickLink("sub") />
				<input type="hidden" name="whatbutton" id="whatbutton" value="" />	
	</form>
</body>
<script>

onload = function() {
	document.getElementById('dia').selectedIndex = ${posi};
}

function clickLink(a) {
	document.getElementById('whatbutton').value = a;
}
</script>
</html>