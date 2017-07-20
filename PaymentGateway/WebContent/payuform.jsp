<%@ page import="java.util.*" %>
<%@ page import="java.security.*" %>

<html>
<script type="text/javascript">
  function success() {
    var form = document.forms['transaction'];
    // stuff some value into the form field
   form.elements['status'].value='success';
   form.action='PayUSuccess.jsp';
   form.submit();
 }
  
  function failure() {
	    var form = document.forms['transaction'];
	    // stuff some value into the form field
	   form.elements['status'].value='failure';
	   form.action='PayUFailure.jsp';
	   form.submit();
	 }
</script>
<body> 
 <form name="transaction" method="post" action="">
  <table>
        <tr>
          <td>Transaction Id: </td>
          <td><input type="number" name="txnid" value=""/></td>
        </tr>
        <tr>
          <td><input type="hidden" name="status" value=""/></td>
        </tr>
        </table>
</form>


 

<button onClick="success();">Success</button>
<button onClick="failure();">Failure</button>

</body>
</html>