/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wookie.tests.conformance;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.apache.wookie.tests.functional.AbstractControllerTest;
import org.apache.wookie.tests.helpers.WidgetUploader;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Abstract superclass for conformance tests including utility functions
 * for working with widgets and outputting results. This is used for generating
 * HTML output for "eyeball tests" where the developer wants to scan the results
 * of instantiating widgets, for example the W3C Test Cases which present a green
 * "PASS" or red "FAIL" output based on runtime behaviour.
 * 
 * FIXME there is some duplication in the methods here that could be rationalized.
 */
public abstract class AbstractFunctionalConformanceTest extends
AbstractControllerTest {

	protected static String html = "";

	@BeforeClass
	public static void setup(){
		html = "<html><body>";
	}

	@AfterClass
	public static void finish(){
		html += "</body></html>";
		System.out.println(html);
	}

	//// Utility methods
	
	/**
	 * Execute the test for the given widget; upload the widget,
	 * create an instance of it, obtain the URL for it, generate 
	 * an iframe for it, and add it to the HTML output for the test
	 * 
	 * @param widget the widget to test
	 */
	protected static void doTest(String widget){
		String url;
		try {
			url = getWidgetUrl(widget);
			html += "<iframe src=\""+url+"\"></iframe>";
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create an output <iframe> for an instance 
	 * of the specified widget
	 * @param widgetId the widget to instantiate and output
	 */
	protected static void outputInstance(String widgetId){
		String url;
		String response = instantiateWidget(widgetId);
		url = getStartFile(response);
		html += "<iframe src=\""+url+"\"></iframe>";
	}

	/**
	 * Upload a widget, create an instance of it, and return the URL for the instance
	 * 
	 * @param widgetfname the widget to upload
	 * @return the URL to access an instance of the uploaded widget
	 * @throws IOException
	 */
	protected static String getWidgetUrl(String widgetfname) throws IOException{
		WidgetUploader.uploadWidget(widgetfname);
		Element widget = WidgetUploader.getLastWidget();
		String response = instantiateWidget(widget);
		return getStartFile(response);
	}

	/**
	 * Create an instance of the specified widget
	 * @param widget the widget to create an instance of
	 * @return the id of the widget instance that was created
	 */
	protected static String instantiateWidget(Element widget){
		return instantiateWidget(widget.getAttributeValue("identifier"));	
	}

	/**
	 * Create an instance of the specified widget
	 * @param identifier the identifier of the widget to create an instance of
	 * @return the XML representation of the widget instance as a String
	 */
	protected static String instantiateWidget(String identifier){
		String response = null;
		//
		// instantiate widget and parse results
		//
		try {
			HttpClient client = new HttpClient();
			PostMethod post = new PostMethod(TEST_INSTANCES_SERVICE_URL_VALID);
			post.setQueryString("api_key="+API_KEY_VALID+"&widgetid="+identifier+"&userid=test&shareddatakey=test");
			client.executeMethod(post);
			response = IOUtils.toString(post.getResponseBodyAsStream());
			post.releaseConnection();
		}
		catch (Exception e) {
			fail("failed to instantiate widget");
		}
		return response;		
	}

	/**
	 * Get the URL of a widget instance from the XML representation
	 * @param response the XML returned from a request to create a new widget instance
	 * @return the URL of the instance, as a String
	 */
	protected static String getStartFile(String response){
		SAXBuilder builder = new SAXBuilder();
		Reader in = new StringReader(response);
		Document doc;
		try {
			doc = builder.build(in);
		} catch (Exception e) {
			return null;
		} 
		return doc.getRootElement().getChild("url").getText();
	}

}
