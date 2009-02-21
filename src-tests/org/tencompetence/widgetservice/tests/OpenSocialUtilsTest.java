package org.tencompetence.widgetservice.tests;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.tencompetence.widgetservice.beans.Widget;
import org.tencompetence.widgetservice.beans.WidgetInstance;
import org.tencompetence.widgetservice.util.opensocial.OpenSocialUtils;

public class OpenSocialUtilsTest extends TestCase {
	
	private WidgetInstance INSTANCE;
	private WidgetInstance INSTANCE_NO_WIDGET;
	private WidgetInstance INSTANCE_NOT_SAVED;
	private WidgetInstance INSTANCE_NO_USER_ID;
	
	@Before public void setUp(){
		Widget widget = new Widget();
		widget.setUrl("http://getwookie.org/test/index.html");
		widget.setGuid("http://getwookie.org/test");
		widget.setId(1);
		
		INSTANCE = new WidgetInstance();
		INSTANCE.setWidget(widget);
		INSTANCE.setId(1);
		INSTANCE.setIdKey("xhKEoiff/4ltxSuuBmPjjxBx5hw.eq.");
		INSTANCE.setUserId("scott");

		INSTANCE_NO_WIDGET = new WidgetInstance();
		INSTANCE_NO_WIDGET.setId(1);
		INSTANCE_NO_WIDGET.setIdKey("xhKEoiff/4ltxSuuBmPjjxBx5hw.eq.");
		INSTANCE_NO_WIDGET.setUserId("scott");
		
		INSTANCE_NOT_SAVED = new WidgetInstance();
		INSTANCE_NOT_SAVED.setWidget(widget);
		//INSTANCE_NOT_SAVED.setIdKey("xhKEoiff/4ltxSuuBmPjjxBx5hw.eq.");
		INSTANCE_NOT_SAVED.setUserId("scott");
		
		INSTANCE_NO_USER_ID = new WidgetInstance();
		INSTANCE_NO_USER_ID.setWidget(widget);
		INSTANCE_NO_USER_ID.setId(1);
		INSTANCE_NO_USER_ID.setIdKey("xhKEoiff/4ltxSuuBmPjjxBx5hw.eq.");
		
	}

	@Test
	public void testCreatePlainToken() {
		try {
			String token = OpenSocialUtils.createPlainToken(INSTANCE);
			assertNotNull(token);
			assertFalse(token.equals(""));
			System.out.println(token);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Couldn't create plaintext token");
		}
	}
	
    @Test(expected=Exception.class)
    public void testCreatePlainTokenNoWidget() throws Exception{
    	try {
			String token = OpenSocialUtils.createPlainToken(INSTANCE_NO_WIDGET);
            // Uh-oh! No exception was thrown so we 
            // better make this test fail!
            fail("should've thrown an exception!");
        } catch (Exception expected) {
            // this is exactly what we were expecting so 
            // let's just ignore it and let the test pass
        }
    }
    
    @Test(expected=Exception.class)
    public void testCreatePlainTokenNoUserId()
            throws Exception {
        try {
			String token = OpenSocialUtils.createPlainToken(INSTANCE_NO_USER_ID);
            // Uh-oh! No exception was thrown so we 
            // better make this test fail!
            fail("should've thrown an exception!");
        } catch (Exception expected) {
            // this is exactly what we were expecting so 
            // let's just ignore it and let the test pass
        }
    }
    
    @Test(expected=Exception.class)
    public void testCreatePlainTokenNoId()
            throws Exception {
        try {
			String token = OpenSocialUtils.createPlainToken(INSTANCE_NOT_SAVED);
            // Uh-oh! No exception was thrown so we 
            // better make this test fail!
            fail("should've thrown an exception!");
        } catch (Exception expected) {
            // this is exactly what we were expecting so 
            // let's just ignore it and let the test pass
        }
    }

	@Test
	public void testCreateEncryptedToken() {
		try {
			String token = OpenSocialUtils.createEncryptedToken(INSTANCE,"UNSECURED_TOKEN_KEY");
			assertNotNull(token);
			assertFalse(token.equals(""));
			System.out.println(token);
		} catch (Exception e) {
			fail("Couldn't create encrypted token");
		}
	}
	
    @Test(expected=Exception.class)
    public void testCreateEncryptedTokenNoWidget() throws Exception{
    	try {
			String token = OpenSocialUtils.createEncryptedToken(INSTANCE_NO_WIDGET,"UNSECURED_TOKEN_KEY");
            // Uh-oh! No exception was thrown so we 
            // better make this test fail!
            fail("should've thrown an exception!");
        } catch (Exception expected) {
            // this is exactly what we were expecting so 
            // let's just ignore it and let the test pass
        }
    }
    
    @Test(expected=Exception.class)
    public void testCreateEncryptedTokenNoUserId()
            throws Exception {
        try {
			String token = OpenSocialUtils.createEncryptedToken(INSTANCE_NO_USER_ID,"UNSECURED_TOKEN_KEY");
            // Uh-oh! No exception was thrown so we 
            // better make this test fail!
            fail("should've thrown an exception!");
        } catch (Exception expected) {
            // this is exactly what we were expecting so 
            // let's just ignore it and let the test pass
        }
    }
    
    @Test(expected=Exception.class)
    public void testCreateEncryptedTokenNoId()
            throws Exception {
        try {
			String token = OpenSocialUtils.createEncryptedToken(INSTANCE_NOT_SAVED,"UNSECURED_TOKEN_KEY");
            // Uh-oh! No exception was thrown so we 
            // better make this test fail!
            fail("should've thrown an exception!");
        } catch (Exception expected) {
            // this is exactly what we were expecting so 
            // let's just ignore it and let the test pass
        }
    }

}
