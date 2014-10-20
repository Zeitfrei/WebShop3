<html>
<head><title>Apotheke</title>
 
<body>
  
	<input type="button" value="Zurück zum Menü" onclick="location.href='/menu';"> <br>
	
	<#if error??>
	<p style="color:red;">${error}</p>
	</#if>
	
	<h1> Apotheke </h1>
	
	<#if (place)??>
	<#assign posi = place>
	<#else>
	<#assign posi = 0>
	</#if>
	
	<form name="apotheke1" action="apotheke" method="post">
	Medikament:
		<select name="med" id="dropdown"  >
	  			<#list meds as medikament>
	  				<option value="${medikament.getPzn()?html}">
	  				${medikament.getName()?html}  ${medikament.getLagerbestand()?html}
	  				</option>
	  			</#list>
		</select>
		<br>
    Anzahl: <input type="text" name="anzahl" /> <br/>
    <input type="hidden" id="lastpzn" name="lastpzn" value="" />
    <input type="hidden" name="action" value="remove" /> <br/>
    <input type="submit" value="Entnehmen" />
   </form>
</body>
<script>
onload = function() {
	document.getElementById('dropdown').selectedIndex = ${posi};
}

</script>
</html>