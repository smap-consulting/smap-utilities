/*
 * Copyright (C) 2013 Smap Consulting
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


public class SubmitResults {
	
	
    public boolean sendFile(Main parent, String hostname, String instanceFilePath, String status, 
    		String user, String password, boolean encrypted, String newIdent) {	

    	boolean submit_status = false;
    	File tempFile = null;
    	
    	// XSLT if ident needs to be changed
		final String changeIdXSLT = 
				"<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">" +
						"<xsl:param name=\"surveyId\"/>" +
						"<xsl:template match=\"@*|node()\">" +
							"<xsl:copy>" +
								"<xsl:apply-templates select=\"@*|node()\"/>" +
							"</xsl:copy>" +
						"</xsl:template>" +
						"<xsl:template match=\"@id\">" +
							"<xsl:attribute name=\"id\">" +
							"<xsl:value-of select=\"$surveyId\"/>" +
 					  		"</xsl:attribute>" +
						"</xsl:template>" +
						"</xsl:stylesheet>";
    	
        //FileBody fb = null;
        ContentType ct = null;
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        
    	CredentialsProvider credsProvider = new BasicCredentialsProvider();
        String urlString = null;
        HttpHost targetHost = null;
        if(encrypted) {
           	urlString = "https://" + hostname + "/submission";
        	targetHost = new HttpHost(hostname, 443, "https");
        	parent.appendToStatus("	Using https");
            credsProvider.setCredentials(
                    new AuthScope(hostname, 443, "smap", "digest"),
                    new UsernamePasswordCredentials(user, password));
        } else {
        	urlString = "http://" + hostname + "/submission";
        	targetHost = new HttpHost(hostname, 80, "http");
        	parent.appendToStatus("	Using http (not encrypted)");
            credsProvider.setCredentials(
                    new AuthScope(hostname, 80, "smap", "digest"),
                    new UsernamePasswordCredentials(user, password));
        }
    	
    	CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
    	
	    // get instance file
	    File instanceFile = new File(instanceFilePath);	
	
	    if (!instanceFile.exists()) {
	    	parent.appendToStatus("	Error: Submission file " + instanceFilePath + " does not exist");
	    } else {
	
	        HttpPost req = new HttpPost(URI.create(urlString));
	        //req.setHeader("form_status", status);						// smap add form_status header
	        
	        tempFile = populateRequest(parent, status, instanceFilePath, req, changeIdXSLT, ct, entityBuilder, newIdent);
	        
		    // find all files in parent directory
	        /*
		    File[] allFiles = instanceFile.getParentFile().listFiles();
		
		    // add media files ignoring invisible files and the submission file
		    List<File> files = new ArrayList<File>();
		    for (File f : allFiles) {
		        String fileName = f.getName();
		        if (!fileName.startsWith(".") && !fileName.equals(instanceFile.getName())) {	// ignore invisible files and instance xml file    
		        	files.add(f);
		        }
		    }
		    */
		    
		    // add the submission file first...

	        /*
	        ct = ContentType.create("text/xml");
            //fb = new FileBody(instanceFile, ct);
            entity.addBinaryBody("xml_submission_file", instanceFile, ct, instanceFile.getPath());
            //entity.addPart("xml_submission_file", fb);
		*/

	        /*
		    for (int j = 0; j < files.size(); j++) {
		    	

	            File f = files.get(j);
	            String fileName = f.getName();
	            int idx = fileName.lastIndexOf(".");
	            String extension = "";
	            if (idx != -1) {
	                extension = fileName.substring(idx + 1);
	            }
	
	            // we will be processing every one of these, so
	            // we only need to deal with the content type determination...
	            if (extension.equals("xml")) {
	            	ct = ContentType.create("text/xml");
	            } else if (extension.equals("jpg")) {
	            	ct = ContentType.create("image/jpeg");
	            } else if (extension.equals("3gp")) {
	            	ct = ContentType.create("video/3gp");
	            } else if (extension.equals("3ga")) {
	            	ct = ContentType.create("audio/3ga");
	            } else if (extension.equals("mp4")) {
	            	ct = ContentType.create("video/mp4");
	            } else if (extension.equals("m4a")) {
	              	ct = ContentType.create("audio/m4a");
	            }else if (extension.equals("csv")) {
	            	ct = ContentType.create("text/csv");
	            } else if (f.getName().endsWith(".amr")) {
	            	ct = ContentType.create("audio/amr");
	            } else if (extension.equals("xls")) {
	            	ct = ContentType.create("application/vnd.ms-excel");
	            }  else {
	            	ct = ContentType.create("application/octet-stream");
	            	parent.appendToStatus("	Info: unrecognised content type for extension " + extension);
	               
	            }
		
	            //fb = new FileBody(f, ct);
	            //entity.addPart(f.getName(), fb);
	            entity.addBinaryBody(f.getName(), f, ct, f.getName());
		           
		        parent.appendToStatus("	Info: added file " + f.getName());

		    }
		    */
		    
	        //req.setEntity(entity.build());
			
	        // prepare response and return uploaded
	        HttpResponse response = null;
	        try {
	        	
	            // Create AuthCache instance
	            AuthCache authCache = new BasicAuthCache();
	            // Generate DIGEST scheme object, initialize it and add it to the local
	            // auth cache
	            DigestScheme digestAuth = new DigestScheme();
	            // Suppose we already know the realm name
	            digestAuth.overrideParamter("realm", "smap");
	            // Suppose we already know the expected nonce value
	            digestAuth.overrideParamter("nonce", "whatever");
	            authCache.put(targetHost, digestAuth);
	        	
	            // Add AuthCache to the execution context
	            HttpClientContext localContext = HttpClientContext.create();
	            localContext.setAuthCache(authCache);
	        	
	        	parent.appendToStatus("	Info: submitting to: " + req.getURI().toString());
	            response = httpclient.execute(targetHost, req, localContext);
	            int responseCode = response.getStatusLine().getStatusCode();
	           	
				try {
					// have to read the stream in order to reuse the connection
					InputStream is = response.getEntity().getContent();
					// read to end of stream...
					final long count = 1024L;
					while (is.skip(count) == count)
						;
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
	    		
	            // verify that the response was a 201 or 202.
	            // If it wasn't, the submission has failed.
	        	parent.appendToStatus("	Info: Response code: " + responseCode + " : " + response.getStatusLine().getReasonPhrase());
	            if (responseCode != HttpStatus.SC_CREATED && responseCode != HttpStatus.SC_ACCEPTED) {      
	                parent.appendToStatus("	Error: upload failed: ");
	            } else {
	            	submit_status = true;
	            }
	        } catch (Exception e) {
	            e.printStackTrace();    
	            parent.appendToStatus("	Error: Generic Exception. " + e.toString());
	        }
	    }
	
	    try {
	    	httpclient.close();
	    } catch (Exception e) {
	    	
	    } finally {
	    	
	    }
	    
	    if(tempFile != null) {
    		tempFile.delete();
    	}
	    
	    return submit_status;
	}
    
    
	private File populateRequest(final Main parent, String formStatus, String filePath, HttpPost req, 
			final String changeIdXSLT,
			ContentType ct,
			MultipartEntityBuilder entityBuilder, final String newIdent) {
	    
		File ammendedFile = null;
		
		final File instanceFile = new File(filePath);	    
	
    	if(formStatus != null) {
    		System.out.println("Setting form status in header: " + formStatus);
    		req.setHeader("form_status", formStatus);						// smap add form_status header
    	} else {
    		System.out.println("Form Status null");
    	}
    	
    	if(newIdent != null) {
		    // Transform the survey ID
    		try {
		        System.out.println("Transformaing Instance file: " + instanceFile);
		        PipedInputStream in = new PipedInputStream();
		        final PipedOutputStream outStream = new PipedOutputStream(in);
		        new Thread(
		          new Runnable(){
		            public void run(){
				        try {
				        	InputStream xslStream = new ByteArrayInputStream(changeIdXSLT.getBytes("UTF-8"));
			            	Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xslStream));
							StreamSource source = new StreamSource(instanceFile);
							StreamResult out = new StreamResult(outStream);
							transformer.setParameter("surveyId", newIdent);
				        	transformer.transform(source, out);
				        	outStream.close();
				        } catch (TransformerConfigurationException e1) {
				        	parent.appendToStatus("Error changing ident: " + e1.toString());
						} catch (TransformerFactoryConfigurationError e1) {
							parent.appendToStatus("Error changing ident: " + e1.toString());
						} catch (TransformerException e) {
							parent.appendToStatus("Error changing ident: " + e.toString());
						} catch (IOException e) {
							parent.appendToStatus("Error changing ident: " + e.toString());
						}
		            }
		          }
		        ).start();
		        System.out.println("Saving stream to file");
		        ammendedFile = saveStreamTemp(in);
    		} catch (Exception e) {
        		parent.appendToStatus("Error changing ident: " + e.toString());
        	}
	    }
    	
        
        /*
         * Add submission file as file body, hence save to temporary file first
         */  
        if(newIdent == null) {
        	ct = ContentType.create("text/xml");
        	entityBuilder.addBinaryBody("xml_submission_file", instanceFile, ct, instanceFile.getPath());
        } else {  
        	FileBody fb = new FileBody(ammendedFile);
        	entityBuilder.addPart("xml_submission_file", fb);	
        }
	
        parent.appendToStatus("Instance file path: " + instanceFile.getPath());

	    /*
	     *  find all files referenced by the survey
	     *  Temporarily check to see if the parent directory is "uploadedSurveys". If it is
	     *   then we will need to scan the submission file to get the list of attachments to 
	     *   send. 
	     *  Alternatively this is a newly submitted survey stored in its own directory just as
	     *  surveys are stored on the phone.  We can then just add all the surveys that are in 
	     *  the same directory as the submission file.
	     */
	    File[] allFiles = instanceFile.getParentFile().listFiles();
		
	    // add media files ignoring invisible files and the submission file
	    List<File> files = new ArrayList<File>();
	    for (File f : allFiles) {
	        String fileName = f.getName();
	        if (!fileName.startsWith(".") && !fileName.equals(instanceFile.getName())) {	// ignore invisible files and instance xml file    
	        	files.add(f);
	        }
	    }
	    
	    
	    for (int j = 0; j < files.size(); j++) {	

            File f = files.get(j);
            String fileName = f.getName();
            ct = ContentType.create(getContentType(parent, fileName));
            FileBody fba = new FileBody(f, ct, fileName);
	        entityBuilder.addPart(fileName, fba);
	
	    }
	    
        req.setEntity(entityBuilder.build());
	    
	    return ammendedFile;
	}
	
	private File saveStreamTemp(InputStream in) throws IOException {

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File file = new File("upload" + timeStamp);
	    FileUtils.copyInputStreamToFile(in, file);

	    return file;
	}
	
	public static String getContentType(Main parent, String filename) {
		
		String ct = null;
		String extension = "";
		int idx = filename.lastIndexOf('.');
		if(idx > 0) {
			extension = filename.substring(idx+1);
		}
		
		
	  if (extension.equals("xml")) {
		  ct = "text/xml";
	  } else if (extension.equals("jpg") || extension.equals("jpeg") || extension.equals("jpe")) {
		  ct = "image/jpeg";
	  } else if (extension.equals("png")) {
		ct = "image/png";
	  } else if (extension.equals("3gp")) {
		  ct = "video/3gp";
	  } else if (extension.equals("3ga")) {
		  ct = "audio/3ga";
	  } else if (extension.equals("mp2") || extension.equals("mp3") || extension.equals("mpga")) {
		  ct = "audio/mpeg";
	  } else if (extension.equals("mpeg") || extension.equals("mpg") || extension.equals("mpe")) {
		  ct = "video/mpeg";
	  } else if (extension.equals("qt") || extension.equals("mov")) {
		  ct = "video/quicktime";
	  } else if (extension.equals("mp4") || extension.equals("m4p")) {
		  ct = "video/mp4";
	  } else if (extension.equals("avi")) {
		  ct = "video/x-msvideo";
	  } else if (extension.equals("movie")) {
		  ct = "video/x-sgi-movie";
	  } else if (extension.equals("m4a")) {
		  ct = "audio/m4a";
	  } else if (extension.equals("csv")) {
		  ct = "text/csv";
	  } else if (extension.equals("amr")) {
		  ct = "audio/amr";
	  } else if (extension.equals("xls")) {
		  ct = "application/vnd.ms-excel";
	  }  else {
		  ct = "application/octet-stream";
		  parent.appendToStatus("	Info: unrecognised content type for extension " + extension);           
	  }
		
		return ct;
	}

}

