
function navigateTo(path) {
	var param = getParams();
	document.location.href = path + '?' + param;
}

function getParams() {
    var url = window.location.href;
	if (url.indexOf('#') != -1) {
      var infos = url.substring(url.indexOf('#')+1, url.length);
      if (infos.length > 20) {
    	  return infos;
      }
    }
	if (url.indexOf('?') != -1) {
		var infos = url.substring(url.indexOf('?')+1, url.length);
		if (infos.length > 20) {
			return infos;
		}
	}
	return "";
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
        return root + path + "?" + token;
    }

    return "";
}

function updateSite(sitename, email, related) {
	var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/sites/' + sitename + '?' + getParams());
      
    form.appendChild(createHiddenFiled('email', email));
    form.appendChild(createHiddenFiled('related', related));
    
    document.body.appendChild(form);
    form.submit();
}

function newService(sitename, servicename, description, related) {
	var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/sites/' + sitename + '?' + getParams());
      
    form.appendChild(createHiddenFiled('name', servicename));
    form.appendChild(createHiddenFiled('description', description));
    form.appendChild(createHiddenFiled('related', related));
    
    document.body.appendChild(form);
    form.submit();
}

function updateService(sitename, servicename, description, related) {
	var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/sites/' + sitename + '/' + servicename + '?' + getParams());
      
    form.appendChild(createHiddenFiled('description', description));
    form.appendChild(createHiddenFiled('related', related));
    
    document.body.appendChild(form);
    form.submit();
}

function newDataType(name) {
	var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/dictionaries/datatypes' + '?' + getParams());
  
    form.appendChild(createHiddenFiled('name', name));
    
    document.body.appendChild(form);
    form.submit();
}

function newUnit(name, datatype, description, related) {
	var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/dictionaries/units' + '?' + getParams());
      
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
    form.setAttribute("action", '/dictionaries/units/' + name + '?' + getParams());
      
    form.appendChild(createHiddenFiled('description', description));
    form.appendChild(createHiddenFiled('related', related));
    
    document.body.appendChild(form);
    form.submit();
}

function newAttributeType(name, description, related) {
    var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/dictionaries/attributetypes' + '?' + getParams());
      
    form.appendChild(createHiddenFiled('name', name));
    form.appendChild(createHiddenFiled('description', description));
    form.appendChild(createHiddenFiled('related', related));
    
    document.body.appendChild(form);
    form.submit();
}

function updateAttributeType(name, description, related) {
	var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/dictionaries/attributetypes/' + name + '?' + getParams());
      
    form.appendChild(createHiddenFiled('name', name));
    form.appendChild(createHiddenFiled('description', description));
    form.appendChild(createHiddenFiled('related', related));
    
    document.body.appendChild(form);
    form.submit();
}

function addUnitToAttributeType(name, unit) {
	var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/dictionaries/attributetypes/' + name + '?' + getParams());
      
    form.appendChild(createHiddenFiled('unit', unit));
    
    document.body.appendChild(form);
    form.submit();
}

function newAssetType(name, description, related) {
    var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/dictionaries/assettypes' + '?' + getParams());
      
    form.appendChild(createHiddenFiled('name', name));
    form.appendChild(createHiddenFiled('description', description));
    form.appendChild(createHiddenFiled('related', related));
    
    document.body.appendChild(form);
    form.submit();
}

function updateAssetType(name, description, related) {
	var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/dictionaries/assettypes/' + name + '?' + getParams());
      
    form.appendChild(createHiddenFiled('name', name));
    form.appendChild(createHiddenFiled('description', description));
    form.appendChild(createHiddenFiled('related', related));
    
    document.body.appendChild(form);
    form.submit();
}

function addAttributeToAssetType(name, attribute) {
	var form = document.createElement("form");
    form.setAttribute("method", "post");
    form.setAttribute("action", '/dictionaries/assettypes/' + name + '?' + getParams());
      
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

function suggest(name) {
	document.getElementById('name').value = name;
	document.getElementById('description').value = "";
	document.getElementById('related').value = "";
}

var path = getRedirrectPath();
if (path != "")
  window.location = path;
