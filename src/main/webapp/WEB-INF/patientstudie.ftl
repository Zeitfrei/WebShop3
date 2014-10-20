<html>
<head><title>Patient zu Studie hinzufuegen</title>
 
<body>
  
	<input type="button" value="Zurueck zum Menue" onclick="location.href='/menu';"> <br>
	
	<h1> Patient zu Studie hinzufuegen </h1>
		
   	<form name="studie" action="patient_zu_studie" method="post">
		<select name="studi">
	  			<#list studis as studie>
	  				<option>
	  				${studie.getName()?html}
	  				</option>
	  			</#list>
		</select>
		Name der Studie
		<br>
		<select name="patient">
	  		<#list patienten as patient>
	  			<option>
	  				${patient.getName()?html}
	  			</option>
	  		</#list>
		</select>
		Name des Patienten
		<br>
		<input type="text" name="pseudo" />
	   	Pseudonym
		<input type="hidden" name="action" value="addpatient" /> <br/>
    	<input type="submit" value="Hinzufuegen"/>
    </form>

</body>
</html>