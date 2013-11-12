//>>built
define("dojox/mobile/app/_FormWidget",["dijit","dojo","dojox","dojo/require!dojo/window,dijit/_WidgetBase,dijit/focus"],function(e,b,d){b.provide("dojox.mobile.app._FormWidget");b.experimental("dojox.mobile.app._FormWidget");b.require("dojo.window");b.require("dijit._WidgetBase");b.require("dijit.focus");b.declare("dojox.mobile.app._FormWidget",e._WidgetBase,{name:"",alt:"",value:"",type:"text",disabled:!1,intermediateChanges:!1,scrollOnFocus:!1,attributeMap:b.delegate(e._WidgetBase.prototype.attributeMap,
{value:"focusNode",id:"focusNode",alt:"focusNode",title:"focusNode"}),postMixInProperties:function(){this.nameAttrSetting=this.name?'name\x3d"'+this.name.replace(/'/g,"\x26quot;")+'"':"";this.inherited(arguments)},postCreate:function(){this.inherited(arguments);this.connect(this.domNode,"onmousedown","_onMouseDown")},_setDisabledAttr:function(a){this.disabled=a;b.attr(this.focusNode,"disabled",a);this.valueNode&&b.attr(this.valueNode,"disabled",a)},_onFocus:function(a){this.scrollOnFocus&&b.window.scrollIntoView(this.domNode);
this.inherited(arguments)},isFocusable:function(){return!this.disabled&&!this.readOnly&&this.focusNode&&"none"!=b.style(this.domNode,"display")},focus:function(){this.focusNode.focus()},compare:function(a,b){return"number"==typeof a&&"number"==typeof b?isNaN(a)&&isNaN(b)?0:a-b:a>b?1:a<b?-1:0},onChange:function(a){},_onChangeActive:!1,_handleOnChange:function(a,c){this._lastValue=a;if(void 0==this._lastValueReported&&(null===c||!this._onChangeActive))this._resetValue=this._lastValueReported=a;if((this.intermediateChanges||
c||void 0===c)&&(typeof a!=typeof this._lastValueReported||0!=this.compare(a,this._lastValueReported)))this._lastValueReported=a,this._onChangeActive&&(this._onChangeHandle&&clearTimeout(this._onChangeHandle),this._onChangeHandle=setTimeout(b.hitch(this,function(){this._onChangeHandle=null;this.onChange(a)}),0))},create:function(){this.inherited(arguments);this._onChangeActive=!0},destroy:function(){this._onChangeHandle&&(clearTimeout(this._onChangeHandle),this.onChange(this._lastValueReported));
this.inherited(arguments)},_onMouseDown:function(a){if(this.isFocusable())var c=this.connect(b.body(),"onmouseup",function(){this.isFocusable()&&this.focus();this.disconnect(c)})},selectInputText:function(a,c,f){var d=b.global;a=b.byId(a);isNaN(c)&&(c=0);isNaN(f)&&(f=a.value?a.value.length:0);e.focus(a);d.getSelection&&a.setSelectionRange&&a.setSelectionRange(c,f)}});b.declare("dojox.mobile.app._FormValueWidget",d.mobile.app._FormWidget,{readOnly:!1,attributeMap:b.delegate(d.mobile.app._FormWidget.prototype.attributeMap,
{value:"",readOnly:"focusNode"}),_setReadOnlyAttr:function(a){this.readOnly=a;b.attr(this.focusNode,"readOnly",a)},postCreate:function(){this.inherited(arguments);void 0===this._resetValue&&(this._resetValue=this.value)},_setValueAttr:function(a,b){this.value=a;this._handleOnChange(a,b)},_getValueAttr:function(){return this._lastValue},undo:function(){this._setValueAttr(this._lastValueReported,!1)},reset:function(){this._hasBeenBlurred=!1;this._setValueAttr(this._resetValue,!0)}})});
//@ sourceMappingURL=_FormWidget.js.map