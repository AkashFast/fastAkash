(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-7cc7deb2"],{"46fa":function(e,t,n){"use strict";var i=n("f570"),c=n.n(i);c.a},b0d8:function(e,t,n){"use strict";n.r(t);var i=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",[n("p"),n("a-card",{attrs:{title:"请选择您要使用的系统角色"}},e._l(e.roleList,(function(t){return n("a-card-grid",{key:t.id,staticStyle:{width:"25%","text-align":"center",cursor:"hand"},on:{click:function(n){return e.loadLogin(t.rid)}}},[e._v(" "+e._s(t.name)+" ")])})),1)],1)},c=[],o=n("5530"),r=n("ca00"),s=n("0fea"),a=n("7ded"),u=n("2f62"),d={data:function(){return{roleList:[],indexPage:""}},created:function(){this.loadLogin("checkRole")},methods:Object(o["a"])(Object(o["a"])({},Object(u["b"])(["Login","GenerateRoutes"])),{},{loadLogin:function(e){var t=this,n=this.Login;Object(a["b"])(e).then((function(e){Object(s["b"])("accessLogin","accessLogin","",{}).then((function(e){var i=Object(s["a"])(e);null!==i&&(i.isLogin?n(i.role.rid).then((function(e){var n=i.menu;t.$store.dispatch("GenerateRoutes",{roles:n}).then((function(){t.indexPage=i.role.index_page,t.loginSuccess(i.role.index_page)}))})).catch((function(e){return t.requestFailed(e)})):i.roleList.length>0?t.roleList=i.roleList:(t.$notification["error"]({message:"错误",description:"登陆失败「暂未获得授权信息」"}),setTimeout((function(){t.$router.push({path:"/user/login"})}),3e3)))}))}))},loginSuccess:function(){this.$router.push({path:this.indexPage}),this.$notification.success({message:"欢迎",description:"".concat(Object(r["a"])(),"，欢迎回来")})},requestFailed:function(){this.$notification["error"]({message:"错误",description:"登陆失败，请稍后再试",duration:4})}})},l=d,f=(n("46fa"),n("2877")),h=Object(f["a"])(l,i,c,!1,null,"1dcf00c8",null);t["default"]=h.exports},f570:function(e,t,n){}}]);