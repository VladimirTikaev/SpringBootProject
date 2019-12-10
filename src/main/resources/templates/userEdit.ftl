<#import "parts/common.ftl" as c>
<@c.page>
User editor
<form action="/user" method="post">
    <input type="hidden" value="${_csrf.token}" name="_csrf">
    <input type="hidden" name="userId" value="${user.id}">
    <input type="text" name="userName" value="${user.username}">

    <#list roles as role >
    <div>
        <label><input type="checkbox" name="${role}" ${user.roles?seq_contains(role)?string("checked", "")}> ${role}</label>
    </div>

    </#list>

    <button type="submit"> Save</button>
</form>
</@c.page>