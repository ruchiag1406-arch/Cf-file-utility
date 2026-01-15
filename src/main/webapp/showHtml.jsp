<html>
	<head>
	<title>PDF Utility - Please Sign PDF</title>
	<script src="js/pdf.js"></script>
	
	<style>
		.divCont {
  			position:relative;
		}
		.divCanvas {
  			border: 2px solid black !important; 
  			margin-bottom: 2px;
  			direction: ltr;
		}
		.divButton {
  			z-index:2; 
  			position:absolute;
  			display: none;
		}
		.divText {
  			z-index:2; 
  			position:absolute;
  			display: none;
		}
	</style>
	
	<script type="text/javascript">
    	// If absolute URL from the remote server is provided, configure the CORS
			// header on that server.
			//var url = 'https://raw.githubusercontent.com/mozilla/pdf.js/ba2edeae/examples/learning/helloworld.pdf';
			//var url = '/js/PdfSignExm.pdf';
				
			<% String isSig=(String)request.getAttribute("isSign"); %>
			var isSign="<%=isSig%>";
			
			<% String or=(String)request.getAttribute("org"); %>
			var org="<%=or%>";
			
			<% String jsonSt=(String)request.getAttribute("json"); %>
			var jsonStr='<%=jsonSt%>';
			
			var jsonArr = JSON.parse(jsonStr);
			
			// Loaded via <script> tag, create shortcut to access PDF.js exports.
			var pdfjsLib = window['pdfjs-dist/build/pdf'];
			
			// The workerSrc property shall be specified.
			pdfjsLib.GlobalWorkerOptions.workerSrc = 'js/pdf.worker.js';
			
			//var otherIdx=0;
			//var oPdfIdx=0;
			//var oPageIdx=1;
			
			function doWork(text, data) {
				  console.log(text + data + text);
			}
			
			 for(var pdfIdx = 0; pdfIdx < jsonArr.length; pdfIdx++) {
				 
				 var jsonObj = jsonArr[pdfIdx];
				 console.log(jsonObj.fileName);
				 
				 
				// Asynchronous download of PDF
					var loadingTask = pdfjsLib.getDocument(jsonObj.fileName);
					loadingTask.promise.then(function(pdf) {
					//console.log('PDF Loading Pages ' + pdf.numPages);
					   
					  var divCan = document.createElement('div');
					  divCan.classList.add("divCont");
					  var mainDiv = document.getElementById('mainDiv');
					  mainDiv.appendChild(divCan);
					  
					  //oPageIdx = 1;
					  for(var pageIdx = 1; pageIdx <= pdf.numPages; pageIdx++) {
						  
						pdf.getPage(pageIdx).then(function(page) {
							
					    	console.log('Page loaded Loop ' + pageIdx + pdfIdx);
					    	console.log('Page data 1 is ' + page._pageIndex);
					    	//console.log('Page data 2 is ' + page._transport._params.url);
					    	var filename = page._transport._params.url.split('/').pop();
					    	//console.log('Page data 3 is ' + filename);
					    	var jObj = jsonArr[filename];
					    	var jIdx=0;
					    	
					    	for(var idx = 0; idx < jsonArr.length; idx++) {
		                		
		                		var jsonObj = jsonArr[idx];
		                		if ( jsonObj.fileName == filename ) {
		                			console.log('Yes Page data 4 is ' + jsonObj.fileName);
		                			jObj=jsonObj;
		                			jIdx = idx;
		                		}
		       				 	console.log(jsonObj.recordId);
					    	}
					    	//console.log('Page data 4 is ' + jObj.recordId);
					    	//console.log('Page data 5 is ' + page.pageNumber);
					    	
					    	/* if ( page._pageIndex == jObj.page ) {
					    		console.log('Yes');
					    	} else {
					    		console.log('No');
					    	} */
					    	
					    	var scale = 1.5;
					    	var viewport = page.getViewport({scale: scale});
					
					    	// Prepare canvas using PDF page dimensions
					    	var canvas = document.createElement('canvas');
					    	canvas.classList.add("divCanvas");
					    
					    	var context = canvas.getContext('2d');
					    	canvas.height = viewport.height;
					    	canvas.width = viewport.width;
					    	divCan.appendChild(canvas);
					    	
					    	//console.log('oPdfIdx 1 is  '+oPdfIdx);
					    	//console.log('oPageIdx 1 is  '+oPageIdx);
					    	
					    	
					    	//if ( oPageIdx == 1 ) {
					    	if ( page._pageIndex == jObj.page ) {	
					    		console.log('here 1');
					    		var input = document.createElement("input");
						    	//input.id = 'nameTxt' + oPdfIdx;
						    	input.id = 'nameTxt' + jIdx;
						    	input.classList.add("divText");
				                input.type = "text";
				                input.value = jObj.sigName;
				                
				                //console.log('here 2');
				                var commArea = document.createElement("textarea");
				                commArea.id = 'commArea' + jIdx;
				                commArea.classList.add("divText");
				                commArea.maxLength = "120";
				                commArea.cols = "25";
				                commArea.rows = "2";
				                input.value = jObj.comments;
					    	
				                //console.log('here 3');
				                var button = document.createElement("button");
				                button.id = 'signBtn' + jIdx;
				                button.name = jIdx;
				                //button.name = oPdfIdx;
				                button.innerHTML = "Sign PDF";   
				                button.classList.add("divButton");
				                button.onclick = function() {
				                	
				                	//console.log('Record Clicked is ' + button.name);
									
									
				                	for(var idx = 0; idx < jsonArr.length; idx++) {
				                		
				                		var jsonObj = jsonArr[idx];
				       				 	console.log(jsonObj.recordId);
				       				 	
				       				 	if ( button.name == idx ) {
				       				 		console.log('Record Signed is ' + jsonObj.recordId);
											jsonObj.isSign = "true"; 
											jsonObj.isNew = "true";
											
											var nametxt = document.getElementById('nameTxt' + idx);
											jsonObj.sigName = nametxt.value;
										
											var commArea = document.getElementById('commArea' + idx);
											jsonObj.comments = commArea.value;
				       				 	}
				       				 	
										
				                		
				                	}
				                	var params = '?data=' + JSON.stringify(jsonArr);
				    				window.location.href='/signPdf.html' + params;
				                };
				                
				                //console.log('here 3');
				                var decBtn = document.createElement("button");
				                decBtn.id = 'decBtn' + jIdx;
				                decBtn.name = 'decBtn' + jIdx;;
				                //button.name = oPdfIdx;
				                decBtn.innerHTML = "Decline";   
				                decBtn.classList.add("divButton");
				                decBtn.onclick = function() {
				                	
				                	//console.log('Record Clicked is ' + button.name);
									
									
				                	for(var idx = 0; idx < jsonArr.length; idx++) {
				                		
				                		var jsonObj = jsonArr[idx];
				       				 	console.log(jsonObj.recordId);
				       				 	
				       				 	if ( decBtn.name == ('decBtn' + idx) ) {
				       				 		console.log('Record Signed is ' + jsonObj.recordId);
											jsonObj.isSign = "true"; 
											jsonObj.isNew = "true";
											
											var nametxt = document.getElementById('nameTxt' + idx);
											jsonObj.sigName = nametxt.value;
										
											var commArea = document.getElementById('commArea' + idx);
											jsonObj.comments = commArea.value;
				       				 	}
				       				 	
										
				                		
				                	}
				                	var params = '?data=' + JSON.stringify(jsonArr) + '&imgText=DeclinedtoSign';
				    				window.location.href='/sign' + params;
				                };

				      
				                //canvas.id = 'canvas' + oPdfIdx;
				                canvas.id = 'canvas' + jIdx;
				                console.log('Canvas is 2 ' + 'canvas' + jIdx);
						    	divCan.appendChild(button);
						    	divCan.appendChild(decBtn);
						    	divCan.appendChild(input);
						    	divCan.appendChild(commArea);
						    	 console.log('Canvas is 3 ' + 'canvas' + jIdx);
						    	//oPageIdx = 2;
						    	//oPdfIdx++;
					    	}
					    	//console.log('PAgeIdx 2 is  '+jIdx);
					    	
					
					    	// Render PDF page into canvas context
					    	var renderContext = {
					      		canvasContext: context,
					      		viewport: viewport
					    	};
					    	var renderTask = page.render(renderContext);
					    	renderTask.promise.then(function () {
					    		
					    		console.log('Page rendered Loop' + jIdx);
								
					    		//if ( otherIdx<=oPdfIdx ) {
					    		if (isSign == 'false') {
					    			
					    			var signStr  = jObj.isSign;
					    			console.log(' Signed 1 is ' + signStr);
					    			//console.log(' Signed 2 is ' + otherIdx);
					    			//console.log(' Signed 3 is ' + jIdx);
					    			
					    			//console.log('Page rendered Loop Inside' + jIdx);
					    			var renCanvas = document.getElementById('canvas' + jIdx);
					    			console.log('Canvas is 1 ' + 'canvas' + jIdx);
						    		
					    			if ( renCanvas ) {
					    				
					    				if ( signStr == "false" ) {
					    					
					    					var signBtn = document.getElementById('signBtn' + jIdx);
					    					var decBtn = document.getElementById('decBtn' + jIdx);
								    		s1 = jObj.s1;
								    		s2 = jObj.s2;
								    		if ( s1 == 2) {
								    			signBtn.style.display = "none";
								    			decBtn.style.display = "none";
								    		} else {
								    			signBtn.style.display = "block";
								    			decBtn.style.display = "block";
								    		}
								    		console.log('S1 is ' + s1 + " S2 is " + s2);
									      	signBtn.style.top  = ((renCanvas.height*s2)/100);
											signBtn.style.left = ((renCanvas.width*s1)/100);
											console.log(' Yeah Loop Top is ' + signBtn.style.top + " Left is " + signBtn.style.left);
											
											decBtn.style.top  = ((renCanvas.height*s2)/100);
											decBtn.style.left = ((renCanvas.width*s1)/100) + signBtn.offsetWidth + 5 ;
											
											var nametxt = document.getElementById('nameTxt' + jIdx);
											n1 = jObj.n1;
								    		n2 = jObj.n2;
								    		if ( n1 == 2) {
								    			nametxt.style.display = "none";
								    		} else {
								    			nametxt.style.display = "block";
								    		}
								    		console.log('N1 is ' + n1 + " N2 is " + n2);
											nametxt.style.top  = ((renCanvas.height*n2)/100);
											nametxt.style.left = ((renCanvas.width*n1)/100);
											//console.log(' Yeah Loop Top is ' + nametxt.style.top + " Left is " + nametxt.style.left);
											
											var commArea = document.getElementById('commArea' + jIdx);
											c1 = jObj.c1;
								    		c2 = jObj.c2;
								    		if ( c1 == 2) {
								    			commArea.style.display = "none";
								    		} else {
								    			commArea.style.display = "block";
								    		}
								    		console.log('C1 is ' + c1 + " C2 is " + c2);
								    		commArea.style.top  = ((renCanvas.height*c2)/100);
								    		commArea.style.left = ((renCanvas.width*c1)/100);
											//console.log(' Yeah Loop Top is ' + commArea.style.top + " Left is " + commArea.style.left);
					    					
					    				} 
					    				
					    			}
					    			
						    		
									console.log('Page rendered Loop Inside End' + jIdx);
					    		} else {
					    			document.title = 'PDF Utility - PDF is Signed';
					    		}
								//}
					    			
					    		//console.log('Page rendered Loop End' + jIdx);
					    		//otherIdx++;
					    		
					    	});
					  	});
						
					}
					
					
					  
					  
					  
					}, function (reason) {
					  // PDF loading error
					  console.error('Error PDF ' + reason);
					});
				 
			 }
			
			
			function myFunction() {
				var params = '?fName=' + url + '&sName=' + nametxt.value + '&recId=' + recordId + '&org=' + org + '&url=' + surl;  
				window.location.href='/signPdf.html' + params;
			}
			function myScript() {
				console.log('Body Loaded');
			}
    
	</script>
	</head>
    <body onload="myScript()">
		<div id="mainDiv">
			<div id="img-div" style="width: 100%;"><img src="giphy.gif" alt="Loading" title="Loading" style="display: block; margin: 0 auto;"/></div>
		</div>
    </body>
</html>