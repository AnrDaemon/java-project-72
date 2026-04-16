@import hexlet.code.dto.UrlsShowPage
@import hexlet.code.util.NamedRoutes
@import hexlet.code.repository.BaseRepository
@param UrlsShowPage page

@template.layout.main(
    page=page,
    content=@`
        <div class="row">
            <div class="col-12 col-md-10 col-lg-8 mx-auto border rounded-3 bg-light p-5">
                <h1>Сайт: ${page.getUrl().getName()}</h1>
                <table class="table table-bordered table-hover mt-3" data-test="url">
                    <tbody>
                        <tr>
                            <td>ID</td>
                            <td>${page.getUrl().getId()}</td>
                        </tr>
                        <tr>
                            <td>Имя</td>
                            <td>${page.getUrl().getName()}</td>
                        </tr>
                        <tr>
                            <td>Дата создания</td>
                            <td>${page.getUrl().getCreatedTimeFormatted("ru")}</td>
                        </tr>
                    </tbody>
                </table>
                <h2 class="mt-5">Проверки</h2>
                <form action="${NamedRoutes.urlsUrlCheck(page.getUrl().getId())}" method="post">
                    <button type="submit" class="btn btn-primary">Запустить проверку</button>
                </form>
                <table class="table table-bordered table-hover mt-3" data-test="checks">
                    <thead>
                        <tr class="text-nowrap">
                            <th class="col-1">ID</th>
                            <th class="col-1">Код ответа</th>
                            <th>h1</th>
                            <th>title</th>
                            <th>description</th>
                            <th class="col-2">Дата проверки</th>
                        </tr>
                    </thead>
                    <tbody>
                        @if(page.getChecks() != null)
                            @for(var check : page.getChecks())
                                <tr>
                                    <td>${check.getId()}</td>
                                    <td>${check.getStatusCode()}</td>
                                    <td>${check.getH1()}</td>
                                    <td>${check.getTitle()}</td>
                                    <td>${check.getDescription()}</td>
                                    <td>${check.getCreatedTimeFormatted("ru")}</td>
                                </tr>
                            @endfor
                        @endif
                    </tbody>
                </table>
            </div>
        </div>
    `
)
