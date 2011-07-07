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

package org.apache.wookie.feature;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import org.apache.wookie.feature.wave.WaveAPIImpl;
import org.apache.wookie.w3c.util.IRIValidator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Installs and manages Features. 
 * 
 * Features are loaded from the DEFAULT_FEATURE_FOLDER, looking for files named feature.xml.
 * 
 * Feature.xml files consist of a <feature> element with child <name> <script> and <stylesheet> elements.
 * 
 * The <name> element contains the Feature name IRI; the <script> and <stylesheet> elements should contain a src attribute
 * with the filename of the resource relative to the feature folder (e.g. "myfile.js")
 */
public class Features {
  
  /*
   * The current features installed
   */
  private static ArrayList<IFeature> features;

  static Logger _logger = Logger.getLogger(Features.class.getName());

  /*
   * The default folder name for features 
   */
  public static final File DEFAULT_FEATURE_FOLDER = new File("features");
  
  /*
   * The folder where deployed features live
   */
  private static File featuresFolder;
  
  public static File getFeaturesFolder(){
    if (featuresFolder == null) return DEFAULT_FEATURE_FOLDER;
    return featuresFolder;
  }
  
  /**
   * Get the currently installed features
   * @return a List of IFeature objects
   */
  public static List<IFeature> getFeatures(){
    return features;
  }
  
  /**
   * Get an array of names of installed features
   * @return an array of names as Strings
   */
  public static String[] getFeatureNames(){
    ArrayList<String> featureNames = new ArrayList<String>();
    for (IFeature feature: features){
      featureNames.add(feature.getName());
    }
    return featureNames.toArray(new String[featureNames.size()]);
  }
  
  /**
   * Explicitly load any "special" features
   */
  public static void loadDefaultFeatures(){
    features.add(new WaveAPIImpl());
  }

  /**
   * Loads features from the default feature folder in the current servlet context
   * @param context the current servlet context
   */
  public static void loadFeatures(ServletContext context){
    
    // Clear any existing installed features
    features = new ArrayList<IFeature>();
    
    // Load defaults
    loadDefaultFeatures();
    
    // Load features from file
    loadFeatures(DEFAULT_FEATURE_FOLDER, context.getContextPath() + "/" + DEFAULT_FEATURE_FOLDER + "/");
  }
  
  public static void loadFeatures(File theFeaturesFolder, String basePath){
    featuresFolder = theFeaturesFolder;
    
    if (features == null) features = new ArrayList<IFeature>();
    
    // Iterate over child folders of the /features folder
    for (File folder: featuresFolder.listFiles()){

      // If the folder contains a feature.xml file, parse it and create a Feature object
      if (folder.isDirectory()){
        File featureXml = new File(folder.getPath()+"/feature.xml");
        if (featureXml.exists() && featureXml.canRead()){
          try {
            // Create a base path for resources using the current servlet context and default feature folder 
            String path = "/wookie/features/" + folder.getName();
            // Load the feature and add it to the features collection
            Feature feature = loadFeature(featureXml, path);
            feature.setFolder(folder.getPath());
            features.add(feature);
            _logger.info("Installed feature:"+feature.getName());   
          } catch (Exception e) {
            _logger.error("Error installing feature:"+e.getMessage());
          }
        }
      }
    }
  }

  /**
   * Load a feature.xml file
   * @param featureFile the feature.xml file to load
   * @param basePath the base path (e.g. /wookie/features) to prepend to any resources
   * @return an IFeature implementation
   * @throws Exception
   */
  private static Feature loadFeature(File featureFile, String basePath) throws Exception{
    // Parse the XML
    Document doc;
    doc = new SAXBuilder().build(featureFile);

    String name = doc.getRootElement().getChild("name").getText();
    @SuppressWarnings("unchecked")
    List<Element> scriptElements = doc.getRootElement().getChildren("script");
    @SuppressWarnings("unchecked")
    List<Element> stylesheetElements = doc.getRootElement().getChildren("stylesheet");

    // Is the feature name a valid IRI?
    if (!IRIValidator.isValidIRI(name)){
      throw new Exception("Invalid feature: name is not a valid IRI");            
    }
    // Construct arrays for scripts and stylesheet URLs
    String[] scripts = new String[doc.getRootElement().getChildren("script").size()];
    for (int i=0;i<scriptElements.size();i++){
      scripts[i] = basePath + "/" + scriptElements.get(i).getAttributeValue("src");
    }
    String[] stylesheets = new String[doc.getRootElement().getChildren("stylesheet").size()];
    for (int i=0;i<stylesheetElements.size();i++){
      stylesheets[i] =  basePath + "/" + stylesheetElements.get(i).getAttributeValue("src");
    }
    // Create a Feature object and return it
    Feature feature = new Feature(name, scripts, stylesheets);
    
    // Set the "flatten" flag if set
    if (doc.getRootElement().getAttributeValue("flatten")!=null){
      if (doc.getRootElement().getAttributeValue("flatten").equals("true")){
        ((Feature)feature).setFlattenOnExport(true);        
      }
    }
    
    return feature;
  }

}