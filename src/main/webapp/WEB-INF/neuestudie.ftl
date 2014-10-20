<html>
<head><title>Neue Studie</title>
 
<body>
  
	<input type="button" value="Zurück zum Menü" onclick="location.href='/menu';"> <br>
	
	<h1> Neue Studie </h1>
		
   	<form name="studie" action="neue_studie" method="post">
		<input type="text" name="sName" /> Name der Studie
	   	<br>
   		<select name="pharma">
			<#list pharmas as pharmafirma>
	  			<option>
	  			${pharmafirma.getName()?html}
	  			</option>
	  		</#list>
		</select>
		Durchführende Pharmafirma
		<br>
		<select name="studi">
	  			<#list studis as studie>
	  				<option>
	  				${studie.getName()?html}
	  				</option>
	  			</#list>
		</select>
		Ausschlusskriterien
		<br>
		<input type="hidden" name="action" value="add" /> <br/>
    	<input type="submit" value="Anlegen"/>
    </form>

</body>
</html>