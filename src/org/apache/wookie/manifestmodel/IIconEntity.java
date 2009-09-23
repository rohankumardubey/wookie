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

package org.apache.wookie.manifestmodel;
/**
 * @author Paul Sharples
 * @version $Id: IIconEntity.java,v 1.3 2009-09-02 18:37:31 scottwilson Exp $
 */
public interface IIconEntity extends IManifestModelBase {
		
	String getSrc();

	void setSrc(String src);

	int getHeight();

	void setHeight(int height);

	int getWidth();

	void setWidth(int width);

}