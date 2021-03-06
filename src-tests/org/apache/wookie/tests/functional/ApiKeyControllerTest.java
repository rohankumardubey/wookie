/*
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 */

package org.apache.wookie.tests.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Functional tests for API key management
 */
public class ApiKeyControllerTest extends AbstractControllerTest {

  private static final String APIKEY_SERVICE_LOCATION_VALID = TEST_SERVER_LOCATION  + "keys";

  /**
   * Attempt to get the list of API keys without having authenticated first
   * 
   * @throws IOException
   * @throws HttpException
   */
  @Test
  public void getEntriesUnauthorized() throws HttpException, IOException {
    HttpClient client = new HttpClient();
    GetMethod get = new GetMethod(APIKEY_SERVICE_LOCATION_VALID);
    client.executeMethod(get);
    int code = get.getStatusCode();
    assertEquals(401, code);
  }

  /**
   * Get the set of API keys using default admin credentials
   * 
   * @throws IOException
   * @throws HttpException
   */
  @Test
  public void getKeys() throws HttpException, IOException {
    HttpClient client = new HttpClient();
    GetMethod get = new GetMethod(APIKEY_SERVICE_LOCATION_VALID);
    setAuthenticationCredentials(client);
    client.executeMethod(get);
    int code = get.getStatusCode();
    assertEquals(200, code);
  }

  /**
   * Add a new key
   * 
   * @throws IOException
   * @throws HttpException
   */
  @Test
  public void addKey() throws HttpException, IOException {
    
    //
    // POST a new API key
    //
    HttpClient client = new HttpClient();
    PostMethod post = new PostMethod(APIKEY_SERVICE_LOCATION_VALID);
    setAuthenticationCredentials(client);
    post.setParameter("apikey", "TEST_KEY");
    post.setParameter("email", "test@incubator.apache.org");
    client.executeMethod(post);
    int code = post.getStatusCode();
    assertEquals(201, code);
    
    //
    // Test that the set of API keys includes the one we just POSTed
    //
    client = new HttpClient();
    GetMethod get = new GetMethod(APIKEY_SERVICE_LOCATION_VALID);
    setAuthenticationCredentials(client);
    client.executeMethod(get);
    code = get.getStatusCode();
    assertEquals(200, code);
    assertTrue(get.getResponseBodyAsString().contains("TEST_KEY"));
  }

  /**
   * Remove a key
   * 
   * @throws IOException
   * @throws JDOMException
   */
  @Test
  public void removeKey() throws JDOMException, IOException {
    String id = null;

    //
    // Create a new API key
    //
    HttpClient client = new HttpClient();
    GetMethod get = new GetMethod(APIKEY_SERVICE_LOCATION_VALID);
    setAuthenticationCredentials(client);
    client.executeMethod(get);
    int code = get.getStatusCode();
    assertEquals(200, code);
    assertTrue(get.getResponseBodyAsString().contains("TEST_KEY"));

    //
    // Get the ID of the key we created
    //
    Document doc = new SAXBuilder().build(get.getResponseBodyAsStream());
    for (Object key : doc.getRootElement().getChildren()) {
      Element keyElement = (Element) key;
      if (keyElement.getAttributeValue("value").equals("TEST_KEY")) {
        id = keyElement.getAttributeValue("id");
      }
    }

    //
    // Delete the API key
    //
    client = new HttpClient();
    DeleteMethod del = new DeleteMethod(APIKEY_SERVICE_LOCATION_VALID + "/"  + id);
    setAuthenticationCredentials(client);
    client.executeMethod(del);
    code = del.getStatusCode();
    assertEquals(200, code);

    //
    // Check that the key was deleted
    //
    client = new HttpClient();
    get = new GetMethod(APIKEY_SERVICE_LOCATION_VALID);
    get.setRequestHeader("Content-type", "application/json");
    setAuthenticationCredentials(client);
    client.executeMethod(get);
    code = get.getStatusCode();
    assertEquals(200, code);
    System.out.println(get.getResponseBodyAsString());
    assertFalse(get.getResponseBodyAsString().contains("TEST_KEY"));

  }

  /**
   * Try to remove a non-existant API key
   * 
   * @throws HttpException
   * @throws IOException
   */
  @Test
  public void removeNonExistantEntry() throws HttpException, IOException {
    HttpClient client = new HttpClient();
    DeleteMethod del = new DeleteMethod(APIKEY_SERVICE_LOCATION_VALID
        + "/99999999");
    setAuthenticationCredentials(client);
    client.executeMethod(del);
    int code = del.getStatusCode();
    assertEquals(404, code);
  }

  /**
   * Create an API key with missing parameters
   * 
   * @throws IOException
   * @throws HttpException
   */
  @Test
  public void addEntryNoEmailOrValue() throws HttpException, IOException {
    HttpClient client = new HttpClient();
    PostMethod post = new PostMethod(APIKEY_SERVICE_LOCATION_VALID);
    setAuthenticationCredentials(client);
    client.executeMethod(post);
    int code = post.getStatusCode();
    assertEquals(400, code);
  }

  /**
   * Try to create a new API key that duplicates an existing one
   * 
   * @throws IOException
   * @throws HttpException
   */
  @Test
  public void addDuplicateEntry() throws HttpException, IOException {
    //
    // Create an API key
    //
    HttpClient client = new HttpClient();
    PostMethod post = new PostMethod(APIKEY_SERVICE_LOCATION_VALID);
    setAuthenticationCredentials(client);
    post.setParameter("apikey", "DUPLICATION_TEST");
    post.setParameter("email", "test@127.0.0.1");
    client.executeMethod(post);
    int code = post.getStatusCode();
    assertEquals(201, code);
    
    //
    // Replay the POST
    //
    client.executeMethod(post);
    code = post.getStatusCode();
    assertEquals(409, code);
  }

  /**
   * Complex test for migrating an API key. This test is disabled as there are
   * issues implementing this functionality - for now, migration has been
   * disabled in the REST API.
   * 
   * @throws IOException
   * @throws HttpException
   * @throws JDOMException
   */
  @Test
  @Ignore
  public void migrateAPIKey() throws HttpException, IOException, JDOMException {

    String keyId = null;
    
    //
    // Create a new key
    //
    HttpClient client = new HttpClient();
    PostMethod post = new PostMethod(APIKEY_SERVICE_LOCATION_VALID);
    setAuthenticationCredentials(client);
    post.setParameter("apikey", "MIGRATION_TEST_KEY_1");
    post.setParameter("email", "test@incubator.apache.org");
    client.executeMethod(post);
    int code = post.getStatusCode();
    assertEquals(201, code);
    
    //
    // Get the ID
    //
    GetMethod get = new GetMethod(APIKEY_SERVICE_LOCATION_VALID);
    setAuthenticationCredentials(client);
    client.executeMethod(get);
    Document doc = new SAXBuilder().build(get.getResponseBodyAsStream());
    for (Object key : doc.getRootElement().getChildren()) {
      Element keyElement = (Element) key;
      if (keyElement.getAttributeValue("value").equals("MIGRATION_TEST_KEY_1")) {
        keyId = keyElement.getAttributeValue("id");
      }
    }

    //
    // Create a widget instance
    //
    String instance_id_key = null;
    client = new HttpClient();
    post = new PostMethod(TEST_INSTANCES_SERVICE_URL_VALID);
    post.setQueryString("api_key=MIGRATION_TEST_KEY_1&widgetid="
        + WIDGET_ID_VALID + "&userid=test&shareddatakey=migration_test");
    client.executeMethod(post);
    code = post.getStatusCode();
    String response = post.getResponseBodyAsString();
    instance_id_key = post.getResponseBodyAsString().substring(
        response.indexOf("<identifier>") + 12,
        response.indexOf("</identifier>"));
    assertEquals(201, code);
    post.releaseConnection();

    //
    // Set participant
    //
    client = new HttpClient();
    post = new PostMethod(TEST_PARTICIPANTS_SERVICE_URL_VALID);
    post.setQueryString("api_key=MIGRATION_TEST_KEY_1&widgetid="
        + WIDGET_ID_VALID
        + "&userid=test&shareddatakey=migration_test&participant_id=1&participant_display_name=bob&participant_thumbnail_url=http://www.test.org");
    client.executeMethod(post);
    code = post.getStatusCode();
    assertEquals(201, code);
    post.releaseConnection();

    //
    // Migrate key
    //
    client = new HttpClient();
    PutMethod put = new PutMethod(APIKEY_SERVICE_LOCATION_VALID + "/" + keyId);
    put.setQueryString("apikey=MIGRATION_TEST_KEY_2&email=test@127.0.0.1");
    setAuthenticationCredentials(client);
    client.executeMethod(put);
    code = put.getStatusCode();
    assertEquals(200, code);
    put.releaseConnection();

    //
    // Get instance again using the new key - should be 200 not 201
    //
    client = new HttpClient();
    post = new PostMethod(TEST_INSTANCES_SERVICE_URL_VALID);
    post.setQueryString("api_key=MIGRATION_TEST_KEY_2&widgetid="
        + WIDGET_ID_VALID + "&userid=test&shareddatakey=migration_test");
    client.executeMethod(post);
    code = post.getStatusCode();
    assertEquals(200, code);
    post.releaseConnection();

    //
    // Get participant
    //
    client = new HttpClient();
    get = new GetMethod(TEST_PARTICIPANTS_SERVICE_URL_VALID);
    get.setQueryString("api_key=MIGRATION_TEST_KEY_2&id_key=" + instance_id_key);
    client.executeMethod(get);
    code = get.getStatusCode();
    assertEquals(200, code);
    response = get.getResponseBodyAsString();
    assertTrue(response
        .contains("<participant id=\"1\" display_name=\"bob\" thumbnail_url=\"http://www.test.org\" />"));
    get.releaseConnection();

  }
}
