@import hexlet.code.dto.UrlsIndexPage
@import hexlet.code.util.NamedRoutes
@import hexlet.code.repository.UrlCheckRepository

@param UrlsIndexPage page

@template.layout.main(
    page=page,
    content=@`
        <div class="row">
            <div class="col-12 col-md-10 col-lg-8 mx-auto border rounded-3 bg-light p-5">
                <h1>Сайты</h1>
                <table class="table table-bordered table-hover mt-3" data-test="urls">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th width="*">Имя</th>
                            <th>Дата создания</th>
                            <th>Код ответа</th>
                        </tr>
                    </thead>
                    <tbody>
                        @for(var url : page.getUrls())
                            <tr>
                                <td>${url.getId()}</td>
                                <td><a href="${NamedRoutes.urlsRoot(url.getId())}">${url.getName()}</a></td>
                                <td>
                                    @if(page.getChecks().get(url.getId()) != null)
                                        ${page.getChecks().get(url.getId()).getCreatedTimeFormatted("ru")}
                                    @endif
                                </td>
                                <td>
                                    @if(page.getChecks().get(url.getId()) != null)
                                        ${page.getChecks().get(url.getId()).getStatusCode()}
                                    @endif
                                </td>
                            </tr>
                        @endfor
                    </tbody>
                </table>
            </div>
        </div>
    `
)
