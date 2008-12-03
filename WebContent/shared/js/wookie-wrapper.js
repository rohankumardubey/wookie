/*
 * Widget object
 */
var Widget = {	
	
	instanceid_key : null,	
	proxyUrl : null,	
	// this should be assigned by the calling JS app
	onSharedUpdate : null,
	// this should be assigned by the calling JS app
	onLocked : null,
	// this should be assigned by the calling JS app
	onUnlocked : null,

	init : function(){	
		/*
		 * This page url will be called with e.g. idkey=4j45j345jl353n5lfg09cw03f05
		 * so grab that key to use as authentication against the server.
		 * Also get the proxy address and store it.
		 */
		var query = window.location.search.substring(1);
		var pairs = query.split("&");
		for (var i=0;i<pairs.length;i++){
			var pos = pairs[i].indexOf('=');
			if (pos >= 0){				
				var argname = pairs[i].substring(0,pos);
				if(argname=="idkey"){
					// This gets the id_key and assigns it to instanceid_key.
					this.instanceid_key = pairs[i].substring(pos+1);
				}
				if(argname=="proxy"){
					this.proxyUrl = pairs[i].substring(pos+1);
				}
			}
		}
		// this line tells DWR to use call backs 
		// (i.e. will call onsharedupdate() when an event is received for shared data
		dwr.engine.setActiveReverseAjax(true);	
	},
	
	setPreferenceForKey : function(wName, wValue){
		WidgetImpl.setPreferenceForKey(this.instanceid_key, wName, wValue);	
	},

	preferenceForKey : function(wName, callBackFunction){
		WidgetImpl.preferenceForKey(this.instanceid_key, wName, callBackFunction);
	},
	
	setSharedDataForKey : function(wName, wValue){
		WidgetImpl.setSharedDataForKey(this.instanceid_key, wName, wValue);
	},
	
	sharedDataForKey : function(wName, callBackFunction){
		WidgetImpl.sharedDataForKey(this.instanceid_key, wName, callBackFunction)
	},
	
	appendSharedDataForKey : function(wName, wValue){
		WidgetImpl.appendSharedDataForKey(this.instanceid_key, wName, wValue)		
	},	
	
	lock : function(){
		WidgetImpl.lock(this.instanceid_key);
	},
	
	unlock : function(){
		WidgetImpl.unlock(this.instanceid_key);
	},
	
	hide : function(){
		WidgetImpl.hide(this.instanceid_key);
	},
	
	show : function(){
		WidgetImpl.show(this.instanceid_key);
	},
	
	openURL : function(url){
		window.open(url);
	},
	
	getInstanceKey : function(){
		return this.instanceid_key;
	},
	
	getProxyUrl : function(){
		return this.proxyUrl;
	},
	
	proxify : function(url){
		return proxyUrl + "?instanceid_key=" + instanceid_key + "&url=" + url
		
	}
}
// very important !
Widget.init();

/*
 * Language helper object
 */
var LanguageHelper = {

	currentLanguage : "en",
	supportedLanguages : new Array("bu","en","fr","nl"),
	lCallbackFunctName : null, // This is a callback function to be called once the language is changed in a widget
	
	
	init : function(){
		Widget.preferenceForKey("Locale", this.setDefaultLanguageFromPrefs);
	},
	
	setDefaultLanguageFromPrefs : function(res){
		if(res != "null"){
				this.currentLanguage = res;
		}
	},

	getLangOpts : function(callbackFunct) {
		var langOptionStr = "<select name=\"select_lang\" id=\"select_lang\" onchange=\"LanguageHelper.doLanguageUpdate(this.options[this.selectedIndex].value);\">" ;
		var optStr; 
		for (alang in this.supportedLanguages){
			if(this.supportedLanguages[alang] == this.currentLanguage){
				optStr = "<option value=\"" + this.supportedLanguages[alang] + "\" selected>" + this.supportedLanguages[alang] + "</option>";
			}
			else{
				optStr = "<option value=\"" + this.supportedLanguages[alang] + "\">" + this.supportedLanguages[alang] + "</option>";
			}
			langOptionStr += optStr;
		}
		langOptionStr += "</select>";
		lCallbackFunctName = callbackFunct;
		return langOptionStr;
	},

	getLocalizedString : function(key) {
		try {
			var evalString = this.currentLanguage + "_" + "localizedStrings['"+key+"'];";		
			var ret = eval(evalString);
			if (ret === undefined)
				ret = key;
			return ret;
		}
		catch (ex) {
		}
		return key;
	},

	doLanguageUpdate : function(la){
		this.currentLanguage = la;
		Widget.setPreferenceForKey("Locale", this.currentLanguage);
		lCallbackFunctName();
	}

}
LanguageHelper.init();

/*
 *  Browser sniffer
 */
var BrowserDetect = {
		init: function () {
			this.browser = this.searchString(this.dataBrowser) || "An unknown browser";
			this.version = this.searchVersion(navigator.userAgent)
				|| this.searchVersion(navigator.appVersion)
				|| "an unknown version";
			this.OS = this.searchString(this.dataOS) || "an unknown OS";		
		},
		isBrowser: function (bName) {
			if(this.browser==bName){
				return true;
			}
			else{
				return false;
			}
		},
		searchString: function (data) {
			for (var i=0;i<data.length;i++)	{
				var dataString = data[i].string;
				var dataProp = data[i].prop;
				this.versionSearchString = data[i].versionSearch || data[i].identity;
				if (dataString) {
					if (dataString.indexOf(data[i].subString) != -1)
						return data[i].identity;
				}
				else if (dataProp)
					return data[i].identity;
			}
		},
		searchVersion: function (dataString) {
			var index = dataString.indexOf(this.versionSearchString);
			if (index == -1) return;
			return parseFloat(dataString.substring(index+this.versionSearchString.length+1));
		},
		dataBrowser: [
			{ 	string: navigator.userAgent,
				subString: "OmniWeb",
				versionSearch: "OmniWeb/",
				identity: "OmniWeb"
			},
			{
				string: navigator.vendor,
				subString: "Apple",
				identity: "Safari"
			},
			{
				prop: window.opera,
				identity: "Opera"
			},
			{
				string: navigator.vendor,
				subString: "iCab",
				identity: "iCab"
			},
			{
				string: navigator.vendor,
				subString: "KDE",
				identity: "Konqueror"
			},
			{
				string: navigator.userAgent,
				subString: "Firefox",
				identity: "Firefox"
			},
			{
				string: navigator.vendor,
				subString: "Camino",
				identity: "Camino"
			},
			{		// for newer Netscapes (6+)
				string: navigator.userAgent,
				subString: "Netscape",
				identity: "Netscape"
			},
			{
				string: navigator.userAgent,
				subString: "MSIE",
				identity: "Explorer",
				versionSearch: "MSIE"
			},
			{
				string: navigator.userAgent,
				subString: "Gecko",
				identity: "Mozilla",
				versionSearch: "rv"
			},
			{ 		// for older Netscapes (4-)
				string: navigator.userAgent,
				subString: "Mozilla",
				identity: "Netscape",
				versionSearch: "Mozilla"
			}
		],
		dataOS : [
			{
				string: navigator.platform,
				subString: "Win",
				identity: "Windows"
			},
			{
				string: navigator.platform,
				subString: "Mac",
				identity: "Mac"
			},
			{
				string: navigator.platform,
				subString: "Linux",
				identity: "Linux"
			}
		]

	};
BrowserDetect.init();


/*
 * Replace text function - overloaded on strings
 */
 String.prototype.replaceAll = function(strTarget, strSubString){
	var strText = this;
	var intIndexOfMatch = strText.indexOf( strTarget );
  
	// Keep looping while an instance of the target string
	// still exists in the string.
	while (intIndexOfMatch != -1){
		// Relace out the current instance.
		strText = strText.replace( strTarget, strSubString )
		// Get the index of any next matching substring.
		intIndexOfMatch = strText.indexOf( strTarget );
	}
  
	// Return the updated string with ALL the target strings
	// replaced out with the new substring.
	return( strText );
 }

 
 /*
  * Utility class
  */
 var WidgetUtil = {
		 
	 generate3DigitRandomNumber : function(){
	 	return Math.floor((Math.random() * 900) + 100);
 	 },
		 
	 generateRandomHexColor : function(){
	 			colors = new Array(14)
				colors[0]="0"
				colors[1]="1"
				colors[2]="2"
				colors[3]="3"
				colors[4]="4"
				colors[5]="5"
				colors[5]="6"
				colors[6]="7"
				colors[7]="8"
				colors[8]="9"
				colors[9]="a"
				colors[10]="b"
				colors[11]="c"
				colors[12]="d"
				colors[13]="e"
				colors[14]="f"

				digit = new Array(5)
				color=""
				for (i=0;i<6;i++){
					digit[i]=colors[Math.round(Math.random()*14)]
					color = color+digit[i]
				}
				return color;
			},
			
			findObj : function (n, d) {
				var p,i,x; if(!d) d=document;
				if((p=n.indexOf("?"))>0&&parent.frames.length) {
					d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);
				}
				if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++)
					x=d.forms[i][n];
					for(i=0;!x&&d.layers&&i<d.layers.length;i++)
						x=findObj(n,d.layers[i].document);
					if(!x && document.getElementById) x=document.getElementById(n); return x;
			},
			
			roundNumber : function (num, dec) {
				var result = Math.round(num*Math.pow(10,dec))/Math.pow(10,dec);
				return result;
			},
			
			onReturn : function(event, sendMessage){
				dwr.util.onReturn(event, sendMessage)
			},
			
			getValue : function(fName){				
				return dwr.util.getValue(fName);
			},
		
			setValue : function(fName, fValue, options){				
				dwr.util.setValue(fName, fValue, options);
			},
		
			escapeHtml : function(text){
				return dwr.util.escapeHtml(text);
			},
			
			byId : function(id){
				return dwr.util.byId(id);
			}
 }
 
/*
 * simple debug window
 */
var DebugHelper = {
		
	winLength : 800,
	winHeight : 200,
	debugLog : "",
	
	debug : function(p){
		result = this.cleanInput(p);
		this.debugLog+="<tr><td>";
		this.debugLog+=result;
		this.debugLog+="<hr/></td></tr>";
		this.writeConsole(this.debugLog);	
	},

	cleanInput : function(p){
		removedOpenBracket = p.replaceAll( "<", "&lt;" )
		removedClosedBracket = removedOpenBracket.replaceAll( ">", "&gt;" )
		result = removedClosedBracket;
		return result;
	},

	writeConsole : function(content) {
		 consoleRef=window.open('','myconsole',
		  'width=' + this.winLength + ',height=' + this.winHeight
		   +',menubar=0'
		   +',toolbar=1'
		   +',status=0'
		   +',scrollbars=1'
		   +',resizable=1')	
		   
		 consoleRef.document.writeln(
		  '<html><head>'		  		  
		 +'<title>Debug</title></head>'
		 +'<body bgcolor=white><table width=' + this.winLength + '>'
		 +content+'<table><a name="bottom"></a></body></html>'
		 )
		 consoleRef.onLoad = consoleRef.scrollTo(0,999999);
		 consoleRef.document.close()
	}

}


