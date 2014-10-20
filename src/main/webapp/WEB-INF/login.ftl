<html>
<head><title>Login</title>
 <h1> Login </h1>
<body>

	<#if error??>
		<p style="color:red;">${error}</p>
	</#if>
	
	

  <form name="login" action="login" method="post">
    Login: <input type="text" name="login" /> <br/>
    Passwort: <input type="text" name="password" /> <br/>
    <input type="submit" value="Login" />
  </form>
  </body>
</html>