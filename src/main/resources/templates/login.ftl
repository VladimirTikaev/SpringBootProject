<#import "parts/common.ftl" as c>
<#import "parts/login.ftl" as log >
<@c.page>
    ${message?ifExists}
    <@log.login "/login" false/>
</@c.page>




