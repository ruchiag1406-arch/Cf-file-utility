<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">

<title>Generate Link Page</title>
<style>

	.main {
		background-color: rgb(233, 232, 232);
	}
	.ex-1 {
		background-color: rgb(233, 232, 232);
	}
	
	.ex-2 {
		background-color: rgb(49, 49, 219);
		display: block;
		justify-content: center;
		padding: 20px;
		text-align: center;
		color: white;
		font-size: small;
		font-family: Arial, Helvetica, sans-serif;
	}
	
	.ex-4 {
		text-align: center;
	}
	
	.ex-5 {
		background-color: rgb(228, 228, 228);
	}
	
	.vertical-line {
		display: inline-block;
		border-left: 1px solid #ccc;
		margin: 0 10px;
		height: 125px;
	}
	
	.ex-6 {
		width: 100%;
	}
	
	.img1 {
		display: block;
		margin-left: auto;
		margin-right: auto;
		/* width: 50%; */
	}
	
	.ex-7 {
		text-align: center;
		text-decoration-color: grey;
		font-family: Arial, Helvetica, sans-serif;
		font-display: center;
	}
	
	.ex-8 {
		background-color: grey;
		color: rgb(218, 216, 216);
		padding: 10px;
		text-align: right;
	}
	
	#span {
		align-items: right;
	}
	
	.button1 {
		background-color: #FEBE10;
		padding: 3px 6px;,
		cursor: pointer;
	}
</style>
<!-- 
<style>
.first {
	background-color: blueviolet;
	display: flex;
}

.img1 {
	padding: 10px;
}

.first_one {
	display: flex;
	color: white;
	justify-content: left;
}

.first_two {
	display: flex;
	color: white;
	justify-content: right;
}

.second {
	background-color: orangered;
	display: flex;
	justify-content: center;
	color: white;
	font-family: Arial, Helvetica, sans-serif;
	font-size: medium;
}

.third {
	display: flex;
	background-color: gainsboro;
	justify-content: center;
}

.Fourth {
	display: flex;
	width: 100%;
	background-color: gainsboro;
}

.fourth_one {
	display: flex-col;
	justify-content: left;
	background-color: gainsboro;
	padding: 50px;
	width: 40%
}

.para {
	color: grey;
}

.vertical-line {
	display: inline-block;
	border-left: 1px solid black;
	height: 275px;
	padding: 60px;
}

.fourth_two {
	display: flex-col;
	justify-content: right;
	width: 40%;
	background-color: gainsboro;
	padding: 50px;
}

.Fifth {
	display: flex;
	width: 100%;
	background-color: grey;
}

.fifth_one {
	display: flex;
	background-color: grey;
	color: white;
	font-size: medium;
}

.fifth_two {
	display: flex;
	background-color: grey;
	color: gainsboro;
}
</style>
 -->

<script type="text/javascript">
    	// If absolute URL from the remote server is provided, configure the CORS
			// header on that server.
			//var url = 'https://raw.githubusercontent.com/mozilla/pdf.js/ba2edeae/examples/learning/helloworld.pdf';
			//var url = '/js/PdfSignExm.pdf';
			function onLoad() {
				
				<%String recId = (String) request.getAttribute("recordId");%>
				var recordId="<%=recId%>";
				document.getElementById('recordId').value = recordId;
				
				<%String or = (String) request.getAttribute("org");%>
				var org="<%=or%>";
				document.getElementById('org').value = org;
				
				<%String fUrl = (String) request.getAttribute("fileUrl");%>
				var fileUrl="<%=fUrl%>";
				
				document.getElementById('fileUrl').value = fileUrl;
				console.log('FileUrl ' + fileUrl);

			}
</script>
</head>

<body onload="onLoad()">
	<div class="main">
	<div class="ex-2">
			<div class="ex-3">
				<p>This link has expired. Generate new link to continue.</p>
			</div>
	</div>
	
	<div class="ex-1">
	<!-- <div>
		<p>
			<h2>
				<b>To continue, generate a new link of this document</b>
			</h2>
		</p>
	</div> -->
	<BR>
	</div>
	
	<div class="ex-4">
		<form id="form1" action="/generateFile" method="post">
			<button type="submit" class="button1">GENERATE NEW LINK</button>
			<input type="text" style="display: none;" name="recordId"
				id="recordId" value="text" />
			<input type="text" style="display: none;" name="org" id="org"
				value="text" /> 
			<input type="text" style="display: none;" name="fileUrl" id="fileUrl"
				value="text" /> 
		</form>
	</div>
	
	<BR>
	
	<div class="ex-5">	 
		<div class="ex-6">
			<img class="img1" src="image/left image.jpg" alt="Generate Document">
		</div>
	</div>
			
	<div class="ex-7">		
		<div class="ex-8">
			<p>		
				<h2></h2>
			</p>
			<div class="ex-9">
				<BR>
				<!-- <h3>English(US) Terms Of Use | Support | Feedback | Privacy Policy</h3> -->
			</div>
		</div>
	</div>
	</div>
	
		<!--
	<div class="first">
    	<div class="first_one">
            <img class="img1" src="image/logo.png" alt="logo pic of construcforce">
        </div>
	</div>
	
   	<div class="second">
    	<p>This Link From Your Email has Expired.</p>
	</div>
    
    <div class="third">
    	<p><h3><b> To Continue Request a new Link or login to your Salesforce Account </b></h3></p>
    </div>
    
    <form id="form1" action="/generateFile" method="post">
    <div class="Fourth">
        <div class="fourth_one">
    		<img src="image/left image.jpg" alt="documents">
			<div>
    			&nbsp; &nbsp;
    			<button type="submit" class="button1">GENERATE NEW LINK</button>
			</div>
			<div class="para">
    			<p><h6>ConstrucForce will generate a fresh link for your File</h6></p>
			</div>
		</div>

		<span class="vertical-line"></span>

		<div class="fourth_two">
    		<img src="image/right image.jpg" alt="birds images">
			<div>
    			&nbsp;&nbsp;
    			<button type="button" class="button2">LOG IN SALESFORCE</button>
			</div>
		</div>
	</div>
	<input type="text" style="display: none;" name="recordId" id="recordId" value="text" />
	<input type="text" style="display: none;" name="org" id="org" value="text" /> 
	<input type="text" style="display: none;" name="fileUrl" id="fileUrl" value="text" /> 
	</form>

	<div class="fifth">
    	<div class="fifth_one">
        	<p> Powered By <i><b>Construcforce</b></i></p>
    	</div>
    	<div class="fifth_two">
        	<p>English(US)  Terms Of Use | Support | Feedback | Privacy Policy</p><br><br>
        	<p>Copyright@2020 Construcforce.All Rights Reserved</p>
    	</div>
	</div>
	-->
<!--<H2>File Path is Invalid or Expired, Click below to regenerate
			again!</H2>
		<input type="text" style="display: none;" name="recordId" id="recordId" value="text" />
		<input type="text" style="display: none;" name="org" id="org" value="text" /> 
		<input type="text" style="display: none;" name="fileUrl" id="fileUrl" value="text" /> 
		<input type="submit" value="regenerate url" /> --></body>

		</html>