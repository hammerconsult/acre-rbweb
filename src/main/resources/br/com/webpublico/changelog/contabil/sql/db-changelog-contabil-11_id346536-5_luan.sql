insert into paginaprefeituraportal
values (hibernate_sequence.nextval, 608930532, 'HTML',
        '{"consulta":"select ppa.descricao || '' Versão '' || ppa.versao as ppa,\r\n       ''Aprovado em '' || to_char(aprovacao, ''dd/MM/yyyy HH24:mi:ss'') as aprovacao,\r\n       to_char(pe.missao) as missao,\r\n       to_char(pe.visao) as visao,\r\n       to_char(pe.valores) as valores,\r\n       ''Lei Nº '' || ato.numero || ''/'' || ex.ano as lei,\r\n       to_char(ato.arquivo_id) as idArquivo\r\nfrom ppa\r\n    inner join atolegal ato on ato.id = ppa.atolegal_id\r\n    inner join exercicio ex on ex.id = ato.exercicio_id\r\n    inner join planejamentoestrategico pe on pe.id = ppa.planejamentoestrategico_id\r\n$WHERE","count":"select count(ppa.id)\r\nfrom ppa\r\n    inner join atolegal ato on ato.id = ppa.atolegal_id\r\n    inner join exercicio ex on ex.id = ato.exercicio_id\r\n    inner join planejamentoestrategico pe on pe.id = ppa.planejamentoestrategico_id\r\n$WHERE","totalRegistros":11,"fields":[{"posicao":1,"descricao":"PPA","columnName":"PPA","columnValue":"PPA","tipoAlinhamento":"CENTRO","totalizar":false,"link":null,"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":false},{"posicao":2,"descricao":"APROVACAO","columnName":"APROVACAO","columnValue":"APROVACAO","tipoAlinhamento":"CENTRO","totalizar":false,"link":null,"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":false},{"posicao":3,"descricao":"MISSAO","columnName":"MISSAO","columnValue":"MISSAO","tipoAlinhamento":"CENTRO","totalizar":false,"link":null,"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":false},{"posicao":4,"descricao":"VISAO","columnName":"VISAO","columnValue":"VISAO","tipoAlinhamento":"CENTRO","totalizar":false,"link":null,"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":false},{"posicao":5,"descricao":"VALORES","columnName":"VALORES","columnValue":"VALORES","tipoAlinhamento":"CENTRO","totalizar":false,"link":null,"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":false},{"posicao":6,"descricao":"LEI","columnName":"LEI","columnValue":"LEI","tipoAlinhamento":"CENTRO","totalizar":false,"link":null,"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":false},{"posicao":7,"descricao":"IDARQUIVO","columnName":"IDARQUIVO","columnValue":"IDARQUIVO","tipoAlinhamento":"CENTRO","totalizar":false,"link":null,"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":false}],"pesquisaveis":[],"tabs":[],"paineis":[]}',
        'Detalhes do PPA', 'ppa-ver', 'TABELA',
        '<div class="row">
    <div class="col-md-12 col-sm-12 cnt-title">
        <div class=" col-md-12 alert alert-success">
            <h3 style="font-weight: bold">$PPA $APROVACAO</h3>
            <h3 style="font-weight: bold">
               <a target="_blank" class="btn-link"
                   href="/public/pagina/download-arquivo?$IDARQUIVO">
                    <i class="fas fa-download"></i>
                </a>
                $LEI
            </h3>
        </div>
    </div>
    <div class="col-md-4 col-sm-3 cnt-title">
        <div class="thumbnail alert alert-warning">
            <h3 style="font-weight: bold">Visão</h3>
            <h4>$VISAO</h4>
        </div>
    </div>
    <div class="col-md-4 col-sm-3 cnt-title">
        <div class="thumbnail alert alert-danger">
            <h3 style="font-weight: bold">Missão</h3>
            <h4>$MISSAO</h4>
        </div>
    </div>
    <div class="col-md-4 col-sm-3 cnt-title">
        <div class="thumbnail alert alert-info">
            <h3 style="font-weight: bold">Valores</h3>
            <h4>$VALORES</h4>
        </div>
    </div>
</div>
<div class="row" style="margin-top: 10px">
    <div class="col-md-5" style="text-align: right">
        <a href="/public/pagina/programa">
            <img src="/assets/img/contabil/loa/list.png"/>
            <h4 style="margin-top: 0px; margin-left: 15px;">Programa</h4>
        </a>
    </div>
    <div class="col-md-2"></div>
    <div class="col-md-5" style="text-align: left">
        <a href="/public/pagina/acao">
            <img src="/assets/img/contabil/loa/acao.png"/>
            <h4 style="margin-top: 0px; margin-left: 15px;">Ação</h4>
        </a>
    </div>
</div>',
        0, 1, 736067043, null, null, 'O que é o PPA?', 'https://www.youtube.com/watch?v=gnJv9dFhMdw')
