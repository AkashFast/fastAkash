(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-671ea726"],{a905:function(t,e,a){"use strict";a.r(e);var n=function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{attrs:{title:!1}},[a("a-layout",{attrs:{id:"components-layout-demo-responsive"}},[a("a-layout-sider",{staticStyle:{"background-color":"#FFFFFF"},attrs:{breakpoint:"lg",width:"25%","collapsed-width":"0"}},[a("a-layout-header",{style:{color:"white",textAlign:"center",background:"rgba(0, 0, 0, 0.65)",padding:0}}),a("div",{style:{padding:"24px",background:"#fff",minHeight:"360px"}},[a("a-tree",{staticStyle:{height:"650px",overflow:"auto"},attrs:{checkable:"","tree-data":t.treeData,showLine:!0},on:{select:t.treeNodeClick},scopedSlots:t._u([{key:"title0010",fn:function(){return[a("span",{staticStyle:{color:"#1890ff"}},[t._v("sss")])]},proxy:!0}])})],1)],1),a("a-layout",[a("a-layout-header",{style:{color:"white",textAlign:"center",background:"rgba(0, 0, 0, 0.65)",marginLeft:"10px",padding:0}},[t._v(" "+t._s(t.menuData.name||"新建菜单")+" ")]),a("a-layout-content",{style:{marginLeft:"10px"}},[a("div",{style:{padding:"24px",background:"#fff",minHeight:"360px"}},[a("a-form-model",{attrs:{layout:"horizontal",model:t.menuData,"label-col":{span:3},"wrapper-col":{span:18}}},[a("a-form-model-item",{attrs:{label:"是否父级"}},[a("a-radio-group",{model:{value:t.menuData.is_parent,callback:function(e){t.$set(t.menuData,"is_parent",e)},expression:"menuData.is_parent"}},[a("a-radio-button",{attrs:{value:"1"}},[t._v("是")]),a("a-radio-button",{attrs:{value:"0"}},[t._v("否")])],1)],1),a("a-form-model-item",{attrs:{label:"父级节点"}},[a("a-select",{staticStyle:{width:"200px"},model:{value:t.menuData.pid,callback:function(e){t.$set(t.menuData,"pid",e)},expression:"menuData.pid"}},[a("a-select-option",{attrs:{value:"-1"}},[t._v(t._s(t.systemName))]),t._l(t.parentNode,(function(e){return a("a-select-option",{key:e.id},[t._v(t._s(e.name))])}))],2)],1),a("a-form-model-item",{attrs:{label:"菜单名称"}},[a("a-input",{attrs:{placeholder:"请输入菜单名称"},model:{value:t.menuData.name,callback:function(e){t.$set(t.menuData,"name",e)},expression:"menuData.name"}})],1),a("a-form-model-item",{attrs:{label:"菜单备注"}},[a("a-input",{attrs:{placeholder:"请输入菜单备注"},model:{value:t.menuData.note,callback:function(e){t.$set(t.menuData,"note",e)},expression:"menuData.note"}})],1),a("a-form-model-item",{attrs:{label:"阶次序列"}},[a("a-input",{attrs:{type:"number",placeholder:"请输入当前菜单的排序值"},model:{value:t.menuData.order_number,callback:function(e){t.$set(t.menuData,"order_number",e)},expression:"menuData.order_number"}})],1),a("a-form-model-item",{attrs:{label:"是否锁定"}},[a("a-radio-group",{model:{value:t.menuData.is_lock,callback:function(e){t.$set(t.menuData,"is_lock",e)},expression:"menuData.is_lock"}},[a("a-radio-button",{attrs:{value:"1"}},[t._v("是")]),a("a-radio-button",{attrs:{value:"0"}},[t._v("否")])],1)],1),a("a-form-model-item",{attrs:{label:"路由标识"}},[a("a-input",{attrs:{placeholder:"请输入路由code标识"},model:{value:t.menuData.code,callback:function(e){t.$set(t.menuData,"code",e)},expression:"menuData.code"}})],1),a("a-form-model-item",{attrs:{label:"路由图标"}},[a("a-input",{attrs:{placeholder:"请输入路由icon名称"},model:{value:t.menuData.icon,callback:function(e){t.$set(t.menuData,"icon",e)},expression:"menuData.icon"}})],1),a("a-form-model-item",{attrs:{label:"路由路径"}},[a("a-input",{attrs:{placeholder:"请输入路由path路径"},model:{value:t.menuData.path,callback:function(e){t.$set(t.menuData,"path",e)},expression:"menuData.path"}})],1),a("a-form-model-item",{attrs:{label:"组件地址"}},[a("a-input",{attrs:{placeholder:"请输入路由component组件地址"},model:{value:t.menuData.component,callback:function(e){t.$set(t.menuData,"component",e)},expression:"menuData.component"}})],1),a("a-form-model-item",{attrs:{label:"指定页面"}},[a("a-input",{attrs:{placeholder:"请输入当前路由指定打开的页面「配置跳转/首页」"},model:{value:t.menuData.redirect_page,callback:function(e){t.$set(t.menuData,"redirect_page",e)},expression:"menuData.redirect_page"}})],1),a("a-form-item",{attrs:{"label-col":{span:4},"wrapper-col":{span:30}}},[a("a-popconfirm",{attrs:{placement:"topLeft","ok-text":"确认","cancel-text":"取消"},on:{confirm:t.onSave},scopedSlots:t._u([{key:"title",fn:function(){return[a("p",[t._v("是否确认"+t._s(t.menuData.id?"保存":"新增")+"当前菜单数据?")])]},proxy:!0}])},[a("a-button",{staticStyle:{width:"30%"},attrs:{type:"primary"}},[t._v(t._s(t.menuData.id?"保存":"新增"))])],1),a("a-button",{style:"width: 30%;margin-left: 5%;",attrs:{type:"primary"},on:{click:t.resetData}},[t._v("重置数据")]),t.menuData.id?a("a-popconfirm",{attrs:{placement:"topLeft","ok-text":"提交","cancel-text":"返回",disabled:void 0===t.menuData.id||"1"===t.menuData.is_lock},on:{confirm:function(e){return t.deleteDataNode(t.menuData)}},scopedSlots:t._u([{key:"title",fn:function(){return[a("p",[t._v("是否确定移除当前菜单？")])]},proxy:!0}],null,!1,3275399760)},[a("a-button",{staticStyle:{width:"30%","margin-left":"5%"},attrs:{type:"danger"}},[t._v("删除菜单")])],1):t._e()],1)],1)],1)])],1),a("a-layout",{style:{width:"16%"}},[a("a-layout-header",{style:{color:"white",textAlign:"center",background:"rgba(0, 0, 0, 0.65)",marginLeft:"10px",padding:0}},[t._v("数据源设定")]),a("a-layout-content",{style:{marginLeft:"10px"}},[a("div",{style:{padding:"24px",background:"#fff",minHeight:"360px"}},[a("a-button",{staticStyle:{float:"right",width:"100px"},attrs:{disabled:void 0===t.menuData.id,type:"primary"},on:{click:function(e){return t.addData()}}},[t._v("新增数据源")]),a("br"),a("br"),a("a-table",{attrs:{rowKey:"id",columns:t.columns,"data-source":t.data,loading:t.loading},scopedSlots:t._u([{key:"name",fn:function(e){return a("a",{},[t._v(t._s(e))])}},{key:"expandedRowRender",fn:function(e){return[a("a-form-model",{attrs:{layout:t.form.layout,model:e}},[a("a-form-model-item",{attrs:{label:"禁用字段"}},[t._v(t._s(e.filedBan))]),a("br"),a("a-form-model-item",{attrs:{label:"禁用方法"}},[t._v(t._s(e.methods))]),a("br")],1)]}},{key:"action",fn:function(e,n){return[a("a-popconfirm",{attrs:{placement:"topLeft","ok-text":"提交","cancel-text":"返回"},on:{confirm:function(e){return t.deleteData(n)}},scopedSlots:t._u([{key:"title",fn:function(){return[a("p",[t._v("是否确定移除当前数据源？")])]},proxy:!0}],null,!0)},[a("a-button",{attrs:{type:"danger"}},[t._v("移除数据源")])],1)]}}])})],1)])],1)],1),a("a-drawer",{attrs:{width:"650",title:"数据源管理",placement:"right",closable:!1,visible:t.visible},on:{close:t.onClose}},[a("a-form-model",{attrs:{layout:"horizontal",model:t.drawerData,"label-col":{span:3},"wrapper-col":{span:18}}},[a("a-form-model-item",{attrs:{label:"数据类型"}},[a("a-select",{on:{change:t.changeData},model:{value:t.drawerData.type,callback:function(e){t.$set(t.drawerData,"type",e)},expression:"drawerData.type"}},[a("a-select-option",{attrs:{value:"-1"}},[t._v("请选择数据类型")]),a("a-select-option",{attrs:{value:"0"}},[t._v("数据表")]),a("a-select-option",{attrs:{value:"1"}},[t._v("逻辑方法")]),a("a-select-option",{attrs:{value:"2"}},[t._v("数据引擎")])],1)],1),a("br"),a("a-form-model-item",{attrs:{label:"数据源"}},[a("a-select",{model:{value:t.drawerData.tid,callback:function(e){t.$set(t.drawerData,"tid",e)},expression:"drawerData.tid"}},[a("a-select-option",{attrs:{value:""}},[t._v("请选择数据源")]),t._l(t.sourceData,(function(e){return a("a-select-option",{key:e.id},[t._v(t._s(e.name))])}))],2)],1),a("br"),a("a-form-model-item",{attrs:{label:"字段禁用"}},[a("a-input",{attrs:{placeholder:"请输入禁用字段,多个字段间用,隔开"},model:{value:t.drawerData.filedBan,callback:function(e){t.$set(t.drawerData,"filedBan",e)},expression:"drawerData.filedBan"}})],1),a("br"),a("a-form-model-item",{attrs:{label:"方法禁用"}},[a("a-input",{attrs:{placeholder:"请输入禁用方法名称,多个方法名称间用,隔开"},model:{value:t.drawerData.methods,callback:function(e){t.$set(t.drawerData,"methods",e)},expression:"drawerData.methods"}})],1)],1),a("div",{style:{position:"absolute",bottom:0,width:"100%",borderTop:"1px solid #e8e8e8",padding:"10px 16px",textAlign:"right",left:0,background:"#fff",borderRadius:"0 0 4px 4px"}},[a("a-popconfirm",{attrs:{placement:"topRight","ok-text":"提交","cancel-text":"返回"},on:{confirm:t.onSaveData},scopedSlots:t._u([{key:"title",fn:function(){return[a("p",[t._v(t._s(t.confirm))])]},proxy:!0}])},[a("a-button",{attrs:{type:"primary"}},[t._v("提交保存")])],1),a("a-button",{staticStyle:{marginLeft:"8px"},on:{click:t.onClose}},[t._v("返回")])],1)],1)],1)},o=[],r=(a("b0c0"),{data:function(){return{drawerData:{},visible:!1,sourceData:[],confirm:"我已确认并核实当前信息准确无误",treeData:[],menuData:{is_parent:"0",is_lock:"0",pid:"-1"},parentNode:[],systemName:"Fast·Akash",form:{layout:"inline"},data:[],pageNo:0,pageSize:10,scrollX:650,columns:[{title:"数据源名称",dataIndex:"name",key:"name",align:"center"},{title:"类型",dataIndex:"type",key:"type",align:"center",customRender:function(t,e,a){return 0===t?"数据表":1===t?"逻辑方法":"数据引擎"}},{title:"操作",align:"center",dataIndex:"action",scopedSlots:{customRender:"action"}}],loading:!1}},created:function(){this.getTree(),this.getNodeParent()},methods:{changeData:function(t){var e=this,a={type:t};this.executeUnify("menuData","getSourceData","",a).then((function(t){var a=e.dataClean(t);e.sourceData=a}))},onClose:function(){this.visible=!1,this.getCurrentAccessData(this.menuData.id)},addData:function(){this.drawerData={tid:"",type:"-1"},this.visible=!0},resetData:function(){this.sourceData=[],this.data=[],this.menuData={is_parent:"0",is_lock:"0",pid:"-1"}},onSaveData:function(){var t=this;this.drawerData.mid=this.menuData.id,this.executeUnify("menuData","bindMenuData","",this.drawerData).then((function(e){var a=t.dataClean(e);null!==a&&(t.success("数据源新增成功"),t.onClose())}))},deleteData:function(t){var e=this,a={mid:t.mid,id:t.id};this.executeUnify("menuData","removeBind","",a).then((function(a){var n=e.dataClean(a);null!==n&&(e.success("数据源已移除成功"),e.getCurrentAccessData(t.mid))}))},deleteDataNode:function(t){var e=this,a={version:parseInt(t.version)+1,id:t.id};this.executeUnify("menu","deleteMenuNode","",a).then((function(t){var a=e.dataClean(t);null!==a&&(e.success("菜单已移除成功"),e.getTree())}))},getCurrentAccessData:function(t){var e=this,a={mid:t};this.executeUnify("menuData","getCurrentAccessData","",a).then((function(t){var a=e.dataClean(t);e.data=a}))},treeNodeClick:function(t,e){var a=this,n=e.node.dataRef.id;if(-1!==n){var o={id:n};this.executeUnify("menu","getMenuNodeData","",o).then((function(t){var e=a.dataClean(t);e.is_parent=null===e.is_parent?"0":e.is_parent+"",e.is_lock=null===e.is_lock?"0":e.is_lock+"",a.menuData=e})),this.getCurrentAccessData(n)}},onSave:function(){var t=this,e=this.menuData.id;void 0===e?this.executeUnify("menu","addMenuNode","",this.menuData).then((function(e){var a=t.dataClean(e);null!==a&&(t.success("菜单：「"+t.menuData.name+"」新增成功"),t.getTree())})):(this.menuData.version=parseInt(this.menuData.version)+1,this.executeUnify("menu","updateMenuNode","",this.menuData).then((function(e){var a=t.dataClean(e);null!==a&&(t.success("菜单：「"+t.menuData.name+"」数据更新成功"),t.getTree())})))},getNodeParent:function(){var t=this;this.executeUnify("menu","getParentNode","",{}).then((function(e){var a=t.dataClean(e);t.parentNode=a}))},getTree:function(){var t=this,e={systemName:this.systemName};this.executeUnify("menu","getRootMenuTree","",e).then((function(e){var a=t.dataClean(e);t.treeData=a}))}}}),i=r,l=(a("bf47"),a("2877")),s=Object(l["a"])(i,n,o,!1,null,null,null);e["default"]=s.exports},b83e:function(t,e,a){},bf47:function(t,e,a){"use strict";var n=a("b83e"),o=a.n(n);o.a}}]);