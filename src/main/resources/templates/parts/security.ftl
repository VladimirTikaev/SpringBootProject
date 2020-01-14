<#assign
<#--Проверяем наличие контектса. Если он есть, то мы можем работать с сессией -->
know = Session.SPRING_SECURITY_CONTEXT??
>

<#--Если сессия существует, то выполняем действия-->
<#if know>
    <#assign
    <#--Получаем пользователя-->
    user = Session.SPRING_SECURITY_CONTEXT.authentication.principal
    name = user.getUsername()
    isAdmin = user.isAdmin()
    currentUserId = user.getId()
    >
<#else>
    <#assign
    name = "unknown"
    isAdmin = false
    currentUserId = -1
    >
</#if>