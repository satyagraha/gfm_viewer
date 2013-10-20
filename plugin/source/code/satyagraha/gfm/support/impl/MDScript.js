
function getDocumentScrollTop() 
{
   var res = document.body.scrollTop || document.documentElement.scrollTop || window.pageYOffset || 0;
   // alert(res);
   return res;
}

function setDocumentScrollTop(ypos) 
{
	window.scrollTo(0, ypos);
}
