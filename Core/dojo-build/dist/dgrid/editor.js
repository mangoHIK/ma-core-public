//>>built
define("dgrid/editor","dojo/_base/kernel dojo/_base/lang dojo/_base/array dojo/_base/Deferred dojo/on dojo/aspect dojo/has dojo/query ./Grid put-selector/put dojo/_base/sniff".split(" "),function(C,v,D,w,l,h,s,E,x,q){function t(a,c){a.value=c;if("radio"==a.type||"checkbox"==a.type)a.checked=a.defaultChecked=!!c}function y(a,c){if("number"==typeof c)a=isNaN(a)?a:parseFloat(a);else if("boolean"==typeof c)a="true"==a?!0:"false"==a?!1:a;else if(c instanceof Date){var b=new Date(a);a=isNaN(b.getTime())?
a:b}return a}function F(a,c,b,f,e){var d,g,k;if((b&&b.valueOf())!=(f&&f.valueOf())&&(d=a.cell(c),g=d.row,k=d.column,k.field&&g))if(d={grid:a,cell:d,rowId:g.id,oldValue:b,value:f,bubbles:!0,cancelable:!0},e&&e.type&&(d.parentType=e.type),l.emit(c,"dgrid-datachange",d))a.updateDirty?(a.updateDirty(g.id,k.field,f),k.autoSave&&setTimeout(function(){a._trackError("save")},0)):g.data[k.field]=f;else{var m;(m=c.widget)?(m._dgridIgnoreChange=!0,m.set("value",b),setTimeout(function(){m._dgridIgnoreChange=
!1},0)):(m=c.input)&&t(m,b);return b}return f}function u(a,c,b,f){var e;if(!b.isValid||b.isValid())if(f=F(a,(b.domNode||b).parentNode,n?p:b._dgridLastValue,"function"==typeof b.get?y(b.get("value")):y(b["checkbox"==b.type||"radio"==b.type?"checked":"value"]),f),n?p=f:b._dgridLastValue=f,"radio"===b.type&&b.name&&!c.editOn&&c.field)for(e in f=a.row(b),E("input[type\x3dradio][name\x3d"+b.name+"]",a.contentNode).forEach(function(d){var g=a.row(d);d!==b&&d._dgridLastValue&&(d._dgridLastValue=!1,a.updateDirty?
a.updateDirty(g.id,c.field,!1):g.data[c.field]=!1)}),a.dirty)f.id!==e&&a.dirty[e][c.field]&&a.updateDirty(e,c.field,!1)}function z(a){var c=a.editor,b=a.editOn,f=a.grid,e="string"!=typeof c,d,g,k;d=a.editorArgs||{};"function"==typeof d&&(d=d.call(f,a));if(e)g=new c(d),e=g.focusNode||g.domNode,e.className+=" dgrid-input",g.connect(g,b?"onBlur":"onChange",function(){g._dgridIgnoreChange||u(f,a,this,{type:"widget"})});else if(k=function(b){var c=b.target;"_dgridLastValue"in c&&-1<c.className.indexOf("dgrid-input")&&
u(f,a,c,b)},a.grid._hasInputListener||(f._hasInputListener=!0,f.on("change",function(a){k(a)})),g=e=q(("textarea"==c?"textarea":"input[type\x3d"+c+"]")+".dgrid-input",v.mixin({name:a.field,tabIndex:isNaN(a.tabIndex)?-1:a.tabIndex},d)),9>s("ie")||s("ie")&&s("quirks"))"radio"==c||"checkbox"==c?l(g,"click",function(a){k(a)}):l(g,"change",function(a){k(a)});l(e,"mousedown",function(a){a.stopPropagation()});return g}function G(a,c){var b=z(a),f=a.grid,e=b.domNode||b,d=b.focusNode||e,g=b.domNode?function(){b.set("value",
b._dgridLastValue)}:function(){t(b,b._dgridLastValue);u(a.grid,a,b)};l(d,"keydown",function(c){c=c.keyCode||c.which;27==c?(g(),p=b._dgridLastValue,d.blur()):13==c&&!1!==a.dismissOnEnter&&d.blur()});(a._editorBlurHandle=l.pausable(b,"blur",function(){var c=e.parentNode,g=c.children.length-1,d={alreadyHooked:!0},h=f.cell(e);l.emit(h.element,"dgrid-editor-hide",{grid:f,cell:h,column:a,editor:b,bubbles:!0,cancelable:!1});a._editorBlurHandle.pause();c.removeChild(e);for(q(h.element,"!dgrid-cell-editing");g--;)q(c.firstChild,
"!");x.appendIfNode(c,a.renderCell(a.grid.row(c).data,p,c,r?v.delegate(d,r):d));n=p=r=null})).pause();return b}function A(a,c,b,f){var e=a.domNode,d=c.grid;e||t(a,f);b.innerHTML="";q(b,".dgrid-cell-editing");q(b,a.domNode||a);e&&(a._started||a.startup(),a._dgridIgnoreChange=!0,a.set("value",f),setTimeout(function(){a._dgridIgnoreChange=!1},0));a._dgridLastValue=f;n&&(p=f,l.emit(b,"dgrid-editor-show",{grid:d,cell:d.cell(b),column:c,editor:a,bubbles:!0,cancelable:!1}))}function B(a){var c,b,f,e,d,g;
a.column||(a=this.cell(a));if(!a||!a.element)return null;c=a.column;e=c.field;b=a.element.contents||a.element;if(d=c.editorInstance){if(n!=b&&(!c.canEdit||c.canEdit(a.row.data,f)))return n=b,a=a.row,f=(f=this.dirty&&this.dirty[a.id])&&e in f?f[e]:c.get?c.get(a.data):a.data[e],A(c.editorInstance,c,b,f),g=new w,setTimeout(function(){d.focus&&d.focus();c._editorBlurHandle&&c._editorBlurHandle.resume();g.resolve(d)},0),g.promise}else if(c.editor&&(d=b.widget||b.input))return g=new w,d.focus&&d.focus(),
g.resolve(d),g.promise;return null}var n,p,r;return function(a,c,b){var f=a.renderCell||x.defaultRenderCell,e=[],d;a||(a={});a.editor=c=c||a.editor||"text";a.editOn=b=b||a.editOn;d="string"!=typeof c;a.widgetArgs&&(C.deprecated("column.widgetArgs","use column.editorArgs instead","dgrid 0.4"),a.editorArgs=a.widgetArgs);h.after(a,"init",b?function(){var b=a.grid;b.edit||(b.edit=B);a.editorInstance=G(a,f)}:function(){var b=a.grid;b.edit||(b.edit=B);d&&e.push(h.before(b,"removeRow",function(c){(c=(c=
b.cell(c,a.id).element)&&(c.contents||c).widget)&&c.destroyRecursive()}))});h.after(a,"destroy",function(){D.forEach(e,function(a){a.remove()});a._editorBlurHandle&&a._editorBlurHandle.remove();b&&d&&a.editorInstance.destroyRecursive()});a.renderCell=b?function(c,d,e,h){if(!h||!h.alreadyHooked)l("TD"==e.tagName?e:e.parentNode,b,function(){r=h;a.grid.edit(this)});return f.call(a,c,d,e,h)}:function(b,c,e,h){if(!a.canEdit||a.canEdit(b,c))b=z(a),A(b,a,e,c),e[d?"widget":"input"]=b;else return f.call(a,
b,c,e,h)};return a}});
//@ sourceMappingURL=editor.js.map