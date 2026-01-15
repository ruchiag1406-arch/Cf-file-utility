<html>
	<head>
	<script src="js/pdf.js"></script>
	
	<script type="text/javascript">
	
		
    
			
			function myScript() {
			
			
			
				<% String s3Url1=(String)request.getAttribute("s3url"); %>
				var url1="<%=s3Url1%>";
				document.getElementById("frame").src = 'http://docs.google.com/gview?url=' + url1 + '&embedded=true';
				console.log('Loaded ' + url1);
				
				
				<% String str1=(String)request.getAttribute("isSign"); %>
				var s1="<%=str1%>";
				
				var signBtn = document.getElementById('signbtn');
			
				if (s1) 
				{
					if ( s1!='false') {
						console.log('1');
						signBtn.style.display = "none";
						document.getElementById("h2").innerHTML = "Hello! PDF is signed, please close the window after checking.";
					} else {
					console.log('2');
						signBtn.style.display = "block";
					}
				}else {
				console.log('3');
					signBtn.style.display = "block";
				}
				
				var canvas = document.getElementById('frame');
				var width = canvas.style.width;
				var height = canvas.style.height;
				console.log('Width is ' + width + " Height is " + height);
				console.log('Width is ' + canvas.width + " Height is " + canvas.height);
				
				var y1="<%= request.getParameter("Y")%>";
				var x1="<%= request.getParameter("X")%>";
				console.log('X1 is ' + x1 + " Y1 is " + y1);
				
				signBtn.style.top  = (height/y1);
    			signBtn.style.left = 10 ;
    			console.log('Top is ' + signBtn.style.top + " Left is " + signBtn.style.left);
    			
			}
    
	</script>
	</head>
    <body onload="myScript()">
    <h2 id='h2'>Hello! Please scroll down and use Sign Button to sign the PDF.</h2>
    	<div id="container" style="position:relative;">
        	<iframe id="frame" style="width:600px; height:500px;" frameborder="0">
</iframe>
        	<button id="signbtn" type="button" style="z-index:2; position:absolute; top:<%= request.getParameter("Y") %>%; left:<%= request.getParameter("Y") %>%" onclick="myFunction()">Sign PDF</button>
        </div>
       
        
    </body>
</html>
