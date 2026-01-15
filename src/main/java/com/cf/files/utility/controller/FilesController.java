package com.cf.files.utility.controller;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.cf.files.utility.model.PdfCord;
import com.cf.files.utility.model.PdfSignRequest;
import com.cf.files.utility.model.ResponseModel;
import com.cf.files.utility.service.PdfMergeService;
import com.cf.files.utility.service.PdfSignService;
import com.cf.files.utility.service.S3SignService;
import com.cf.files.utility.util.EncodingUtil;
import com.cf.files.utility.util.FetchingUtil;
import com.cf.files.utility.util.PdfUtility;
import com.cf.files.utility.util.SfConnUtil;

@Controller
public class FilesController {

	private Logger logObj = LoggerFactory.getLogger("FilesController");

	@PostMapping("/merge")
	public ResponseModel mergeFiles(HttpServletRequest request, HttpServletResponse response) {

		System.out.println("Calling PDF Merge Service");
		ResponseModel resModel = new ResponseModel();

		try {
			PdfMergeService pdfMergeService = new PdfMergeService();
			pdfMergeService.service(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resModel;
	}

	@PostMapping("/generateFile")
	public ModelAndView generateFilePost(HttpServletRequest request, HttpServletResponse response, ModelMap model) {

		String uri = request.getRequestURI();
		System.out.println("Post URI - " + uri);
		
		String requestData = null;
		try {

			
			requestData = request.getReader().lines().collect(Collectors.joining());
			if (requestData != null && requestData.length() > 10) {
				// requestData = requestData.replaceAll("imgText=", "");
				requestData = EncodingUtil.getDecodedStr(requestData);
				System.out.println("RD " + requestData);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String recordId = null; //request.getParameter("recordId");
		
		String startStr="recordId=";
		String stopStr = "&";
		

		try {
			int srIdx = requestData.indexOf(startStr);
			int stIdx = requestData.indexOf(stopStr, srIdx);
			
			System.out.println("Start " + srIdx + " Stop " + stIdx);
			
			if ( srIdx >= 0 && stIdx > 0 ) {
				recordId = requestData.substring(srIdx+startStr.length(), stIdx);
			}
		} catch (Exception e) {
			System.out.println("Exception while extracing signData " );
		}
		
	
		//String recordId = request.getParameter("recordId");
		System.out.println("First RecordId is " + recordId);
		
		String org = null; //request.getParameter("org");
		
		try {
			startStr="org=";
			stopStr = "&";
			
			int srIdx = requestData.indexOf(startStr);
			int stIdx = requestData.indexOf(stopStr, srIdx);
			
			System.out.println("Start " + srIdx + " Stop " + stIdx);
			
			if ( srIdx >= 0 && stIdx > 0 ) {
				org = requestData.substring(srIdx+startStr.length(), stIdx);
			}
		} catch (Exception e) {
			System.out.println("Exception while extracing signData " );
		}

		//String org = request.getParameter("org");
		System.out.println("Org is " + org);
		
		String fileUrl1 = null; // request.getParameter("fileUrl");
		
		try {
			
			startStr="fileUrl=";
			stopStr = "&";
			
			
			int srIdx = requestData.indexOf(startStr);
			int stIdx = requestData.indexOf(stopStr, srIdx);
			
			System.out.println("Start " + srIdx + " Stop " + stIdx);
			
			if ( srIdx >= 0 && stIdx > 0 ) {
				fileUrl1 = requestData.substring(srIdx+startStr.length(), stIdx);
			} else {
				fileUrl1 = requestData.substring(srIdx+startStr.length());
			}
			
			
			
		} catch (Exception e) {
			System.out.println("Exception while extracing signData " );
		}

		//String fileUrl1 = request.getParameter("fileUrl");
		System.out.println("FileUrl is " + fileUrl1);

		
		
		System.out.println("Expired - Auto generate externally");
		String fileUrl = new S3SignService().generateFileUrl(fileUrl1, recordId, org);
		System.out.println("fileUrl");

		// response.sendRedirect(fileUrl);

		RedirectView view = new RedirectView(fileUrl, false);
		view.setExposeModelAttributes(false);
		return new ModelAndView(view);

	}

	@GetMapping("/generateFile")
	public ModelAndView generateFileGet(HttpServletRequest request, HttpServletResponse response, ModelMap model) {

		String uri = request.getRequestURI();
		System.out.println("Get URI - " + uri);

		String recId = request.getParameter("recordId");
		System.out.println("First RecordId is " + recId);

		byte[] decodedBytes = Base64.getUrlDecoder().decode(recId);
		String recordId = new String(decodedBytes);
//		recordId = recId;
		System.out.println("Decoded RecordId is " + recordId);

		String org = request.getParameter("org");
		System.out.println("Org is " + org);

		String userId = request.getParameter("userId");
		System.out.println("User Id is " + userId);
		if (userId != null && !userId.isEmpty()) {
			decodedBytes = Base64.getUrlDecoder().decode(userId);
			userId = new String(decodedBytes);
			System.out.println("Decoded userId is " + userId);
		}

		String orgId = request.getParameter("orgId");
		System.out.println("Org Id is " + orgId);
		if (orgId != null && !orgId.isEmpty()) {
			/*
			 * decodedBytes = Base64.getUrlDecoder().decode(orgId); orgId = new
			 * String(decodedBytes);
			 */

			if (orgId.length() > 15) {
				orgId = orgId.substring(0, orgId.length() - 3);
			}
			System.out.println("Decoded orgId is " + orgId);
		}

		PdfSignRequest pdfSignRequest = SfConnUtil.getFileUrlFromSf(recordId, org, userId, orgId);

//		String fileUrl = "https://s3connector-feb2019.s3.amazonaws.com/root/Field_Ticket__c/a0U1k0000021tGJEAY/1623142947141/17119-FT-0008-SENT6859560874078862831.pdf?AWSAccessKeyId=AKIAIOWW4BYT5PDTZMYA&Expires=1627623672&Signature=P4EieENEP3oJDXQsAFZmfWIn4Qs%3D";
//		String fileUrl = "https://s3connector-feb2019.s3.amazonaws.com/root/Field_Ticket__c/a0U1k0000021tGJEAY/1623142947141/17119-FT-0008-SENT6859560874078862831.pdf?AWSAccessKeyId=AKIAIOWW4BYT5PDTZMYA&Expires=1629966071&Signature=E5ZGWM7wjhePS6iKgPeddWKk61A%3D";
//		String fileUrl = "https://s3connector-feb2019.s3.amazonaws.com/root/Account/0011k00000lW5dIAAS/arrow-1624348429096.png";

		boolean expired = false;
		S3SignService s3SignService = new S3SignService();
		try {
			expired = s3SignService.checkExpiryNew(pdfSignRequest);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String fileUrl1 = pdfSignRequest.getAmazonS3Url();
		System.out.println("Url2 is " + fileUrl1);

		if (expired) {

			if (userId != null && !userId.isEmpty()) {

				System.out.println("Expired - Auto generate for internal user");

				String fileUrl = s3SignService.generateFileUrl(fileUrl1, recordId, orgId);
				System.out.println("fileUrl");
				try {
					// response.sendRedirect(fileUrl);
					// return new ModelAndView("redirect:" + fileUrl1);
					fileUrl1 = fileUrl;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {

				// response.getWriter().print("Expired");
				System.out.println("Expired - External User");

				try {
					/*
					 * RequestDispatcher dispatcher = request.getRequestDispatcher("genSign.jsp");
					 * request.setAttribute("recordId", recordId); request.setAttribute("org", org);
					 * request.setAttribute("fileUrl", fileUrl1); dispatcher.forward( request,
					 * response );
					 */

					model.addAttribute("recordId", recordId);
					model.addAttribute("org", orgId);
					//model.addAttribute("orgId", orgId);
					model.addAttribute("fileUrl", fileUrl1);
					return new ModelAndView("forward:/genSign.jsp", model);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Forward Dispatch Ex is " + e);
				}

			}

		} else {
			System.out.println("Not Expired " + fileUrl1);
			try {
				// response.sendRedirect(fileUrl1);
				// model.addAttribute("attribute", "redirectWithRedirectPrefix");
				// return new ModelAndView("redirect:" + fileUrl1);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// return resModel;

		RedirectView view = new RedirectView(fileUrl1, false);
		view.setExposeModelAttributes(false);
		return new ModelAndView(view);

	}

	public static final String SRC = "src/main/webapp/unsigned.pdf";
	public static final String DEST = "src/main/webapp/signed.pdf";

	@PostMapping(value = "/sign")
	//@PostMapping(value = "/sign", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	protected ModelAndView signFilePost(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
		String uri = request.getRequestURI();
		System.out.println("Post URI - " + uri);

		String requestData = null;
		try {

			
			requestData = request.getReader().lines().collect(Collectors.joining());
			if (requestData != null && requestData.length() > 10) {
				// requestData = requestData.replaceAll("imgText=", "");
				requestData = EncodingUtil.getDecodedStr(requestData);
				System.out.println("RD " + requestData);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String signData = null; //signPostReq.getImgText();
		
		String startStr="imgText=";
		String stopStr = "&";
		

		try {
			int srIdx = requestData.indexOf(startStr);
			int stIdx = requestData.indexOf(stopStr, srIdx);
			
			System.out.println("Start " + srIdx + " Stop " + stIdx);
			
			if ( srIdx >= 0 && stIdx > 0 ) {
				signData = requestData.substring(srIdx+startStr.length(), stIdx);
			}
		} catch (Exception e) {
			System.out.println("Exception while extracing signData " );
		}
		
		// String signData = request.getParameter("imgText");
		// String orgId = request.getParameter("orgId");

		System.out.println("Sign data is " + signData);

		if (signData != null) {

			System.out.println("Sign Image is there");

			//String jsonStr = request.getParameter("data");
			String jsonStr = null; //signPostReq.getData();
			
			startStr="&data=";
			stopStr = "]&";
			
			try {
				
				int srIdx = requestData.indexOf(startStr);
				int stIdx = requestData.indexOf(stopStr, srIdx);
				
				System.out.println("Start " + srIdx + " Stop " + stIdx);
				
				if ( srIdx >= 0 && stIdx > 0 ) {
					jsonStr = requestData.substring(srIdx+startStr.length(), stIdx+1);
				} else {
					jsonStr = requestData.substring(srIdx+startStr.length());
				}
				
			} catch (Exception e) {
				System.out.println("Exception while extracing jsonStr");
			}
			
			System.out.println("jsonStr is " + jsonStr);

			try {

				JSONArray jsonArr = new JSONArray(jsonStr);
				boolean signed = new PdfSignService().processSignReq(jsonArr, signData);

				if (signed) {
					/*
					 * RequestDispatcher dispatcher = request.getRequestDispatcher("showPdf.jsp");
					 * request.setAttribute("isSign", "true"); // request.setAttribute("org",
					 * "org"); if ( jsonStr!=null ) {
					 * request.setAttribute("json",jsonArr.toString());
					 * System.out.println("Json 2 is " + jsonArr.toString()); } dispatcher.forward(
					 * request, response );
					 */

					model.addAttribute("isSign", "true");
					if (jsonStr != null) {
						model.addAttribute("json", jsonArr.toString());
						System.out.println("Json 2 is " + jsonArr.toString());
					}
//					model.addAttribute("org", org);
//					model.addAttribute("orgId", orgId);
					return new ModelAndView("forward:/showPdf.jsp", model);

				} else {
					/*
					 * RequestDispatcher dispatcher = request.getRequestDispatcher("showPdf.jsp");
					 * request.setAttribute("isSign", "false"); // request.setAttribute("org",
					 * "org"); if ( jsonStr!=null ) {
					 * request.setAttribute("json",jsonArr.toString());
					 * System.out.println("Json 3 is " + jsonArr.toString()); } dispatcher.forward(
					 * request, response );
					 */

					model.addAttribute("isSign", "false");
					if (jsonStr != null) {
						model.addAttribute("json", jsonArr.toString());
						System.out.println("Json 3 is " + jsonArr.toString());
					}
//					model.addAttribute("org", org);
//					model.addAttribute("orgId", orgId);
					return new ModelAndView("forward:/showPdf.jsp", model);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			System.out.println("No Service for this url path");
		}

		return null;
	}

	@GetMapping("/sign")
	protected ModelAndView signFileGet(HttpServletRequest request, HttpServletResponse response, ModelMap model) {

		String uri = request.getRequestURI();
		System.out.println("Get URI - " + uri);

		String recordId = request.getParameter("recordId");
		System.out.println("First RecordId is " + recordId);

		// String org = request.getParameter("org");
		// System.out.println("Org Id is " + org);

		String org = request.getParameter("orgId");
		System.out.println("OrgId is " + org);

		String signData = request.getParameter("imgText");
		System.out.println("Data is " + signData);

		if (signData != null && !signData.isEmpty()) {

			String jsonStr = request.getParameter("data");

			/* Fix added for presigned url (also added in app.js) */
			Map params = request.getParameterMap();
			Iterator it = params.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				// System.out.println("Key is " + key);
				if (key.startsWith("X-")) {
					String value = ((String[]) params.get(key))[0];
					jsonStr += '&' + key + '=' + value;
				}
			}
			System.out.println("jsonStr is " + jsonStr);

			try {

				JSONArray jsonArr = new JSONArray(jsonStr);
				boolean signed = new PdfSignService().processSignReq(jsonArr, signData);

				if (signed) {
					/*
					 * RequestDispatcher dispatcher = request.getRequestDispatcher("showPdf.jsp");
					 * request.setAttribute("isSign", "true"); // request.setAttribute("org",
					 * "org"); if ( jsonStr!=null ) { request.setAttribute("json",
					 * jsonArr.toString()); System.out.println("Json 2 is " + jsonArr.toString()); }
					 * dispatcher.forward( request, response );
					 */

					model.addAttribute("isSign", "true");
					if (jsonStr != null) {
						model.addAttribute("json", jsonArr.toString());
						System.out.println("Json 2 is " + jsonArr.toString());
					}
//					model.addAttribute("org", org);
//					model.addAttribute("orgId", orgId);
					return new ModelAndView("forward:/showPdf.jsp", model);

				} else {
					/*
					 * RequestDispatcher dispatcher = request.getRequestDispatcher("showPdf.jsp");
					 * request.setAttribute("isSign", "false"); // request.setAttribute("org",
					 * "org"); if ( jsonStr!=null ) { request.setAttribute("json",
					 * jsonArr.toString()); System.out.println("Json 3 is " + jsonArr.toString()); }
					 * dispatcher.forward( request, response );
					 */

					model.addAttribute("isSign", "false");
					if (jsonStr != null) {
						model.addAttribute("json", jsonArr.toString());
						System.out.println("Json 3 is " + jsonArr.toString());
					}
//					model.addAttribute("org", org);
//					model.addAttribute("orgId", orgId);
					return new ModelAndView("forward:/showPdf.jsp", model);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {

			String s3url = null;
			JSONArray jsonArr = new JSONArray();
			File useFile = null;
			PdfSignRequest pdfReq = null;
			PdfCord pdfCord = new PdfCord();
			boolean signed = true;

			if (recordId.contains(",")) {

				String[] recArr = recordId.split(",");

				int i = 0;

				while (i < recArr.length) {

					JSONObject jsonObj = new JSONObject();

					pdfReq = SfConnUtil.getUrlFromSf(recArr[i], org);
					s3url = pdfReq.getAmazonS3Url();
//					System.out.println("S3 URL is " + s3url);

					try {

						File tempFile = File.createTempFile("src/main/webapp/unsigned", ".pdf");
						useFile = new File("src/main/webapp/" + tempFile.getName());

						InputStream in = null;
						try {
							in = FetchingUtil.getPDFFile(s3url);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						FileUtils.copyInputStreamToFile(in, useFile);
						in.close();

					} catch (Exception e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}

					try {
//						PdfUtility.applyCoord( getServletContext().getRealPath(SRC) );
						pdfCord = PdfUtility.applyCoord(useFile.getAbsolutePath());
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						jsonObj.put("recordId", recArr[i]);
						jsonObj.put("org", org);
						// jsonObj.put("orgId", orgId);

						String nam = pdfReq.getSigName();
						nam = Base64.getEncoder().encodeToString(nam.getBytes(StandardCharsets.UTF_8));
						// nam = nam.replaceAll("'", "%s1%");
						jsonObj.put("sigName", nam);
						String comments = pdfReq.getComments();
						comments = Base64.getEncoder().encodeToString(comments.getBytes(StandardCharsets.UTF_8));
						// comments = comments.replaceAll("'", "%s1%");
						jsonObj.put("comments", comments);
						jsonObj.put("s3Url", s3url);
						jsonObj.put("fileName", useFile.getName());
						jsonObj.put("isNew", "false");
						if ("Signed".equals(pdfReq.getStatus()) || "Declined to Sign".equals(pdfReq.getStatus())) {
							jsonObj.put("isSign", "true");
						} else {
							jsonObj.put("isSign", "false");
							signed = false;
						}

						jsonObj.put("s1", pdfCord.getxSign());
						jsonObj.put("s2", pdfCord.getySign());
						jsonObj.put("n1", pdfCord.getxName());
						jsonObj.put("n2", pdfCord.getyName());
						jsonObj.put("d1", pdfCord.getxDate());
						jsonObj.put("d2", pdfCord.getyDate());
						jsonObj.put("c1", pdfCord.getxComm());
						jsonObj.put("c2", pdfCord.getyComm());
						jsonObj.put("page", pdfCord.getPage() - 1);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					jsonArr.put(jsonObj);
					i++;

				}

			} else {

				pdfReq = SfConnUtil.getUrlFromSf(recordId, org);
				s3url = pdfReq.getAmazonS3Url();
				System.out.println("S3 URL is " + s3url);

				try {
					File tempFile = File.createTempFile("src/main/webapp/unsigned", ".pdf");

					useFile = new File("src/main/webapp/" + tempFile.getName());

					InputStream in = null;
					try {
						in = FetchingUtil.getPDFFile(s3url);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					FileUtils.copyInputStreamToFile(in, useFile);
					in.close();

				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				try {
//					PdfUtility.applyCoord( getServletContext().getRealPath(SRC) );
					pdfCord = PdfUtility.applyCoord(useFile.getAbsolutePath());
				} catch (Exception e) {
					e.printStackTrace();
				}

				JSONObject jsonObj = new JSONObject();
				try {
					jsonObj.put("recordId", recordId);
					jsonObj.put("org", org);
					// jsonObj.put("orgId", orgId);

					String nam = pdfReq.getSigName();
					nam = Base64.getEncoder().encodeToString(nam.getBytes(StandardCharsets.UTF_8));
					// nam = nam.replaceAll("'", "%s1%");
					jsonObj.put("sigName", nam);
					String comments = pdfReq.getComments();
//					comments = "Testing's Comments1\n Testing's Comments2\n Testing's Comments3";
					comments = Base64.getEncoder().encodeToString(comments.getBytes(StandardCharsets.UTF_8));
					// comments = comments.replaceAll("'", "%s1%");
					jsonObj.put("comments", comments);

					jsonObj.put("s3Url", s3url);
					jsonObj.put("fileName", useFile.getName());
					jsonObj.put("isNew", "false");
					if ("Signed".equals(pdfReq.getStatus()) || "Declined to Sign".equals(pdfReq.getStatus())) {
						jsonObj.put("isSign", "true");
					} else {
						jsonObj.put("isSign", "false");
						signed = false;
					}

					jsonObj.put("s1", pdfCord.getxSign());
					jsonObj.put("s2", pdfCord.getySign());
					jsonObj.put("s3", pdfCord.getpSign());
					
					jsonObj.put("n1", pdfCord.getxName());
					jsonObj.put("n2", pdfCord.getyName());
					jsonObj.put("n3", pdfCord.getpName());
					
					jsonObj.put("d1", pdfCord.getxDate());
					jsonObj.put("d2", pdfCord.getyDate());
					jsonObj.put("d3", pdfCord.getpDate());
					
					jsonObj.put("c1", pdfCord.getxComm());
					jsonObj.put("c2", pdfCord.getyComm());
					jsonObj.put("c3", pdfCord.getpComm());
					
					jsonObj.put("page", pdfCord.getPage() - 1);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				jsonArr.put(jsonObj);
			}

			if (signed) {
				/*
				 * RequestDispatcher dispatcher =
				 * request.getRequestDispatcher("showPdf.jsp?page=" + PdfUtility.page);
				 * request.setAttribute("isSign", "true"); request.setAttribute("org", org); if
				 * (jsonArr != null) { request.setAttribute("json", jsonArr.toString());
				 * System.out.println("Json 3 is " + jsonArr.toString()); }
				 * dispatcher.forward(request, response);
				 */

				model.addAttribute("isSign", "true");
				if (jsonArr != null) {
					model.addAttribute("json", jsonArr.toString());
					System.out.println("Json 3 is " + jsonArr.toString());
				}
				model.addAttribute("org", org);
				// model.addAttribute("orgId", orgId);
				return new ModelAndView("forward:/showPdf.jsp", model);

			} else {
				/*
				 * RequestDispatcher dispatcher = request.getRequestDispatcher("showPdf.jsp");
				 * request.setAttribute("isSign", "false"); request.setAttribute("org", org); if
				 * (jsonArr != null) { request.setAttribute("json", jsonArr.toString());
				 * System.out.println("Json 3 is " + jsonArr.toString()); }
				 * dispatcher.forward(request, response);
				 */

				model.addAttribute("isSign", "false");
				if (jsonArr != null) {
					model.addAttribute("json", jsonArr.toString());
					System.out.println("Json 3 is " + jsonArr.toString());
				}
				model.addAttribute("org", org);
				// model.addAttribute("orgId", orgId);
				return new ModelAndView("forward:/showPdf.jsp", model);
			}

		}

		return null;
	}

}