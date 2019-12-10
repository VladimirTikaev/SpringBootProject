<#import "parts/common.ftl" as c>
<#import "parts/login.ftl" as log >
<@c.page>
Add new User
${message?ifExists}
    <@log.login "/registration"/>
</@c.page>