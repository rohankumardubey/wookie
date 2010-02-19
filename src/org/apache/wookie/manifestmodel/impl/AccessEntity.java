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

package org.apache.wookie.manifestmodel.impl;

import java.net.URI;

import org.apache.wookie.manifestmodel.IAccessEntity;
import org.apache.wookie.manifestmodel.IW3CXMLConfiguration;
import org.apache.wookie.util.IRIValidator;
import org.apache.wookie.util.UnicodeUtils;
import org.jdom.Attribute;
import org.jdom.Element;
/**
 * The Access element is defined in: http://www.w3.org/TR/widgets-access/
 */
public class AccessEntity implements IAccessEntity {

	private String fOrigin;
	private boolean fSubDomains;

	public AccessEntity(){
		fOrigin = null;
		fSubDomains = false;
	}

	public AccessEntity(String uri, boolean subDomains) {
		super();
		fOrigin = uri;
		fSubDomains = subDomains;
	}

	public String getOrigin() {
		return fOrigin;
	}
	public void setOrigin(String uri) {
		fOrigin = uri;
	}
	public boolean hasSubDomains() {
		return fSubDomains;
	}
	public void setSubDomains(boolean subDomains) {
		fSubDomains = subDomains;
	}
	public String getXMLTagName() {
		return IW3CXMLConfiguration.ACCESS_ELEMENT;
	}

	public void fromXML(Element element) {	
		// Origin is required
		if (element.getAttribute(IW3CXMLConfiguration.ORIGIN_ATTRIBUTE)==null) return;
		fOrigin = UnicodeUtils.normalizeSpaces(element.getAttributeValue(IW3CXMLConfiguration.ORIGIN_ATTRIBUTE));
		if (fOrigin.equals("*")) return;
		try {
			processOrigin();
		} catch (Exception e) {
			fOrigin =  null;
			return;
		}
		// Subdomains is optional
		try {
			processSubdomains(element.getAttribute(IW3CXMLConfiguration.SUBDOMAINS_ATTRIBUTE));
		} catch (Exception e) {
			fSubDomains = false;
		}			
	}

	/**
	 * Processes an origin attribute
	 * @throws Exception if the origin attribute is not valid
	 */
	private void processOrigin() throws Exception{
		if (!IRIValidator.isValidIRI(fOrigin)) throw new Exception("origin is not a valid IRI");
		URI uri = new URI(fOrigin);
		if (uri.getHost() == null) throw new Exception("origin has no host");
		if (uri.getUserInfo()!=null) throw new Exception("origin has userinfo");
		URI processedURI = new URI(uri.getScheme(),null,uri.getHost(),uri.getPort(),null,null,null);
		fOrigin = processedURI.toString();
	}

	/**
	 * Processes a subdomains attribute
	 * @param subDomains
	 * @throws Exception if the attribute is not valid
	 */
	private void processSubdomains(Attribute attr) throws Exception{
		if (attr != null){
			String subDomains = UnicodeUtils.normalizeSpaces(attr.getValue());
			fSubDomains = Boolean.valueOf(subDomains);
		}
	}
}
