<#macro pager url page>
<!--Фримаркет не может правильно интерпретировать знаки > и <  т.к. он думает что это закрытие тега
Поэтому для вмето знака больше используется gt, а вместо знака меньше lt -->
    <#if page.getTotalPages() gt 7>
        <#assign
            totalPages = page.getTotalPages()
            pageNumber = page.getNumber() + 1

            <!--Разбиваем страницы на 5 групп-->
            head = (pageNumber > 4)?then([1,-1], [1,2,3])  <!-- Первая страница -->
            <!-- Последняя странца-->
            tail = (pageNumber < totalPages - 3)?then([-1, totalPages], [totalPages - 2, totalPages - 1, totalPages])
            <!-- Текужая страница - pageNumber-->
            <!-- 2 Страницы до текущей -->
            bodyBefore = (pageNumber > 4 && pageNumber < totalPages -1)?then ([pageNumber -2, pageNumber - 1], [])
            <!-- 2 Страницы после текущей -->
            bodyAfter = (pageNumber > 2 && pageNumber < totalPages -3)?then([pageNumber + 1, pageNumber + 2], [])
        <!--Вместо скрытых страниц - где будут отображатьс точки - в коллекции мы будем использовать значение -1 -->
        <!-- Т.е если у нас 15 стр, а мы на 7, то коллекция выглядит так-->
        <!-- [1, -1, 5, 6, 7, 8, 9, -1, 15] -->
            <!-- Соединяем все -->
            body = head + bodyBefore + (pageNumber > 3 && pageNumber < totalPages - 2)?then([pageNumber],[]) + bodyAfter + tail
        >
    <#else>
        <#assign body = 1..page.getTotalPages()>
    </#if>
    <div class="mt-3">
        <ul class="pagination">
            <li class="page-item disabled">
                <a class="page-link" href="#" tabindex="-1">Pages</a>
            </li>
            <#list body as p>
                <#if (p - 1) == page.getNumber()>
                    <li class="page-item active">
                        <a class="page-link" href="#">${p}</a>
                    </li>
                <#elseif p  == -1>
                    <li class="page-item disabled">
                        <a class="page-link" href="#">...</a>
                    </li>
                <#else>
                    <li class="page-item">
                        <a class="page-link" href="${url}?page=${p-1}&size=${page.getSize()}">${p}</a>
                    </li>
                </#if>
            </#list>
        </ul>

        <ul class="pagination">
            <li class="page-item disabled">
                <a class="page-link" href="#" tabindex="-1">Count elements on page</a>
            </li>
            <#list [5, 10, 25, 50] as c>
                <#if c == page.getSize()>
                    <li class="page-item active">
                        <a class="page-link" href="#">${c}</a>
                    </li>
                <#else>
                    <li class="page-item">
                        <a class="page-link" href="${url}?page=${page.getNumber()}&size=${c}">${c}</a>
                    </li>
                </#if>
            </#list>
        </ul>
    </div>

</#macro>