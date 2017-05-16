
function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires="+ d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
} 

function getRedirrectPath() {
    var url = window.location.href;
    var callIdx = url.indexOf('/callback');
    var pathIdx = url.indexOf('?path=');
    var tokenIdx = url.indexOf('#id_token=');
    
    if (callIdx != -1 && pathIdx != -1 && tokenIdx != -1) {       
        var root = url.substring(0, callIdx);     
        var path = url.substring(pathIdx+6, tokenIdx);
        var token = url.substring(tokenIdx+1);
        setCookie('octoken', token, 1)
        return root + path;
    }

    return "";
}

function logout() {
	setCookie('octoken', '', 0);
	window.location=window.location.href;
}

function newSite(name, email, related, latitude, longitude, city, region, countryCode, country, wiki) {
	
	var namevalid = /^[a-zA-Z0-9]+$/;
	if(! name.match(namevalid))
	{
		alert("Invalid name: only letters and numbers are allowed");
		return;
	}
	
	var emailvalid = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	if(! email.match(emailvalid))
	{
		alert("Invalid email address");
		return;
	}
	
	document.getElementById('createsitebtn').innerHTML = 'Please wait...'
	
	var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/sites/');
      
    form.appendChild(createHiddenFiled('name', name));
    form.appendChild(createHiddenFiled('email', email));
    form.appendChild(createHiddenFiled('related', related));
    form.appendChild(createHiddenFiled('latitude', latitude));
    form.appendChild(createHiddenFiled('longitude', longitude));
    form.appendChild(createHiddenFiled('city', city));
    form.appendChild(createHiddenFiled('region', region));
    form.appendChild(createHiddenFiled('countryCode', countryCode));
    form.appendChild(createHiddenFiled('country', country));
    form.appendChild(createHiddenFiled('wiki', wiki));

    document.body.appendChild(form);
    form.submit();
}

function updateSite(sitename, email, related) {
	var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/sites/' + sitename);
      
    form.appendChild(createHiddenFiled('email', email));
    form.appendChild(createHiddenFiled('related', related));
    
    document.body.appendChild(form);
    form.submit();
}

function newService(sitename, servicename, description, related) {
	var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/sites/' + sitename);
      
    form.appendChild(createHiddenFiled('name', servicename));
    form.appendChild(createHiddenFiled('description', description));
    form.appendChild(createHiddenFiled('related', related));
    
    document.body.appendChild(form);
    form.submit();
}

function addSiteManager(sitename, sub) {
	var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/sites/' + sitename + '/createmanager');
      
    form.appendChild(createHiddenFiled('sub', sub));
    
    document.body.appendChild(form);
    form.submit();
}

function removeSiteManager(idx) {
	var sitename = document.getElementById('elementname').innerHTML;
	var sub = document.getElementById('managers').children[idx].children[0].innerHTML;
	
	var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/sites/' + sitename + '/deletemanager');
      
    form.appendChild(createHiddenFiled('sub', sub));
    
    document.body.appendChild(form);
    form.submit();
}

function addServiceManager(sitename, servicename, sub) {
	var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/sites/' + sitename + '/' + servicename + '/createmanager');
      
    form.appendChild(createHiddenFiled('sub', sub));
    
    document.body.appendChild(form);
    form.submit();
}

function removeServiceManager(idx) {
	var sitename = document.getElementById('sitename').innerHTML;
	var servicename = document.getElementById('elementname').innerHTML; 
	var sub = document.getElementById('managers').children[idx].children[0].innerHTML;
	
	var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/sites/' + sitename + '/' + servicename + '/deletemanager');
      
    form.appendChild(createHiddenFiled('sub', sub));
    
    document.body.appendChild(form);
    form.submit();
}

function updateService(sitename, servicename, description, related) {
	var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/sites/' + sitename + '/' + servicename);
      
    form.appendChild(createHiddenFiled('description', description));
    form.appendChild(createHiddenFiled('related', related));
    
    document.body.appendChild(form);
    form.submit();
}

function newDataType(name) {
	var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/dictionaries/datatypes');
  
    form.appendChild(createHiddenFiled('name', name));
    
    document.body.appendChild(form);
    form.submit();
}

function newUnit(name, datatype, description, related) {
	var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/dictionaries/units');
      
    form.appendChild(createHiddenFiled('name', name));
    form.appendChild(createHiddenFiled('datatype', datatype));
    form.appendChild(createHiddenFiled('description', description));
    form.appendChild(createHiddenFiled('related', related));
    
    document.body.appendChild(form);
    form.submit();
}

function updateUnit(name, description, related) {
	var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/dictionaries/units/' + name);
      
    form.appendChild(createHiddenFiled('description', description));
    form.appendChild(createHiddenFiled('related', related));
    
    document.body.appendChild(form);
    form.submit();
}

function newAttributeType(name, description, related) {
    var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/dictionaries/attributetypes');
      
    form.appendChild(createHiddenFiled('name', name));
    form.appendChild(createHiddenFiled('description', description));
    form.appendChild(createHiddenFiled('related', related));
    
    document.body.appendChild(form);
    form.submit();
}

function updateAttributeType(name, description, related) {
	var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/dictionaries/attributetypes/' + name);
      
    form.appendChild(createHiddenFiled('name', name));
    form.appendChild(createHiddenFiled('description', description));
    form.appendChild(createHiddenFiled('related', related));
    
    document.body.appendChild(form);
    form.submit();
}

function addUnitToAttributeType(name, unit) {
	var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/dictionaries/attributetypes/' + name);
      
    form.appendChild(createHiddenFiled('unit', unit));
    
    document.body.appendChild(form);
    form.submit();
}

function newAssetType(name, description, related) {
    var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/dictionaries/assettypes');
      
    form.appendChild(createHiddenFiled('name', name));
    form.appendChild(createHiddenFiled('description', description));
    form.appendChild(createHiddenFiled('related', related));
    
    document.body.appendChild(form);
    form.submit();
}

function updateAssetType(name, description, related) {
	var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/dictionaries/assettypes/' + name);
      
    form.appendChild(createHiddenFiled('name', name));
    form.appendChild(createHiddenFiled('description', description));
    form.appendChild(createHiddenFiled('related', related));
    
    document.body.appendChild(form);
    form.submit();
}

function addAttributeToAssetType(name, attribute) {
	var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/dictionaries/assettypes/' + name);
      
    form.appendChild(createHiddenFiled('attribute', attribute));
    
    document.body.appendChild(form);
    form.submit();
}

function createHiddenFiled(name, value) {
	var field = document.createElement("input");
	field.setAttribute("type", "hidden");
	field.setAttribute("name", name);
	field.setAttribute("value", value);
    return field;
}

function activateEditMode() {
	var editElem = document.getElementsByClassName("editmode");
	var inlineEditElem = document.getElementsByClassName("inlineeditmode");
	var displayElem = document.getElementsByClassName("displaymode");

	for (var i = 0; i < editElem.length; i++) {
		editElem[i].style.display="block";
	}
	
	for (var i = 0; i < displayElem.length; i++) {
		displayElem[i].style.display="none";
	}
	
	for (var i = 0; i < inlineEditElem.length; i++) {
		inlineEditElem[i].style.display="inline";
	}
}

function suggest(idx) {	
	document.getElementById('name').value = document.getElementById('suggestions').children[idx].children[0].innerHTML;
	document.getElementById('description').value = "";
	document.getElementById('related').value = "";
}

var path = getRedirrectPath();
if (path != "")
  window.location = path;
