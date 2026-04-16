@import hexlet.code.dto.RootPage
@param RootPage page

@template.layout.main(
    page=page,
    content=@`
        <div class="row">
            <div class="col-12 col-md-10 col-lg-8 mx-auto border rounded-3 bg-light p-5">
                <h1 class="display-3">Анализатор страниц</h1>
                <p class="lead">Бесплатно проверяйте сайты на SEO пригодность</p>
                <form action="/urls" method="post" class="row">
                    <div class="col-8">
                        <label for="url-name" class="visually-hidden">Url для проверки</label>
                        <input type="text" name="url" id="url-name" value="${page.getUrl()}" class="form-control form-control-lg" placeholder="https://www.example.com/"/>
                    </div>
                    <div class="col-2">
                        <button type="submit" class="btn btn-primary btn-lg ms-3 px-5 text-uppercase mx-3">Проверить</button>
                    </div>
                </form>
            </div>
        </div>
    `
)
