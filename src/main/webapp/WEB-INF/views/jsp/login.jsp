<%@ page contentType="text/html;charset=UTF-8" %>
<%@include file="common/taglib.jsp" %>
<html>
<head>
  <%@include file="common/head.jsp" %>
  <title>Title</title>
</head>
<body>
<h2 class="text-center">谭振辉的毕设项目</h2>
<div class="container">
  <div class="row">
    <div id="loginBox" class="col-md-4 col-md-offset-4">
      <div class="panel panel-default">
        <div class="panel-heading">
          <h4 class="text-center">用户登录</h4>
        </div>
        <div class="panel-body">
          <form class="form-horizontal" action="${basePath}/login" method="post">
            <div class="form-group" id="nameInput">
              <label for="userName" class="control-label col-sm-3">用户名</label>
              <div class="col-sm-9">
                <input type="text" class="form-control" id="userName" name="username">
                <div class="errorMsg">
                  <label for="username" class="control-label"></label>
                </div>
              </div>
            </div>
            <div class="form-group" id="pwdInput">
              <label for="userPwd" class="control-label col-sm-3">密码</label>
              <div class="col-sm-9">
                <%--通常指浏览器等软件自动跟踪用户近段期间键入的信息，如Web站点地址、表单的中信息以及搜索查询等并在键入新的信息时试图预测用户要键入的信息，并提供可能的匹配内容的功能。--%>
                <input type="password" class="form-control" id="userPwd" name="password" autocomplete="off">
                <div class="errorMsg">
                  <label for="userPwd" class="control-label"></label>
                </div>
              </div>
            </div>
            <div class="form-group">
              <input class="btn btn-primary col-sm-offset-4 col-sm-4" type="submit" value="登录">
            </div>
            <a href="${pageContext.request.contextPath}/signUp" class="btn btn-link col-md-offset-4 col-md-4">用户注册</a>
            <c:if test="${message != null}">
              <div class="alert alert-danger col-sm-12">
                <p>${message}</p>
              </div>
            </c:if>

          </form>
        </div>
      </div>
      <div>
        <a href="${basePath}/shareFile" class="btn btn-default col-md-offset-4 col-md-4">提取文件</a>
      </div>
    </div>

  </div>
</div>
<style>
  .container {
    display: table;
    height: 100%;
  }

  .row {
    display: table-cell;
    vertical-align: middle;
  }
</style>
</body>
</html>
