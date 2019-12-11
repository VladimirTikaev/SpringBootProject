<#import "parts/common.ftl" as c>
<#import "parts/login.ftl" as log >
<@c.page>
<div class="mb-1"> Add new User</div>
${message?ifExists}
    <@log.login "/registration" true/>
</@c.page>