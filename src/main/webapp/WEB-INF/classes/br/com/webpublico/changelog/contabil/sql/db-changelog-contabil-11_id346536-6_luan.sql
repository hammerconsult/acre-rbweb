update paginaprefeituraportal
set subnome = 'O que é a LDO?',
    chavesubnome = 'https://www.youtube.com/watch?v=z0PoLMrvcjQ',
    tipopaginaportal = 'HTML',
    conteudo = '{"consulta":"select ''Lei Nº '' || ato.numero || ''/'' || exAto.ano as lei,\r\n       ato.arquivo_id as idArquivo\r\nfrom ldo\r\n    inner join exercicio ex on ex.id = ldo.exercicio_id\r\n    inner join atolegal ato on ato.id = ldo.atolegal_id\r\n    inner join exercicio exAto on exAto.id = ato.exercicio_id\r\nwhere ex.ano = $ANO\r\n$WHERE","count":"select count(1) from dual","totalRegistros":1,"fields":[{"posicao":1,"descricao":"LEI","columnName":"LEI","columnValue":"LEI","tipoAlinhamento":"CENTRO","totalizar":false,"link":null,"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":false},{"posicao":2,"descricao":"IDARQUIVO","columnName":"IDARQUIVO","columnValue":"IDARQUIVO","tipoAlinhamento":"CENTRO","totalizar":false,"link":null,"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":false}],"pesquisaveis":[],"tabs":[{"titulo":"Metas Fiscais","subTitulo":"","consulta":"select\r\n    mf.TIPOMETASFISCAIS as Tipo,\r\n    mf.RECEITATOTAL as ReceitaTotal,\r\n    mf.DESPESATOTAL as DespesaTotal,\r\n    mf.RECEITAPRIMARIA as ReceitaPrimaria,\r\n    mf.DESPESAPRIMARIA as DespesaPrimaria,\r\n    mf.RESULTADONOMINAL as ResultadoNominal,\r\n    mf.DIVIDAPUBLICACONSOLIDADA as DividaPublicaConsolidada,\r\n    mf.DIVIDAPUBLICALIQUIDA as DividaPublicaLiquida,\r\n    mf.FONTEINFORMACAO as FontedeInformacao\r\nfrom METASFISCAIS mf\r\n    inner join exercicio e on mf.EXERCICIO_ID = e.ID\r\nwhere e.ano = $ANO\r\n$WHERE","count":"select count(mf.id)\r\nfrom METASFISCAIS mf\r\n    inner join exercicio e on mf.EXERCICIO_ID = e.ID\r\nwhere e.ano = $ANO\r\n$WHERE","totalRegistros":null,"ordem":1,"pesquisaveis":[],"fields":[{"posicao":1,"descricao":"Tipo","columnName":"TIPO","columnValue":"TIPO","tipoAlinhamento":"ESQUERDA","totalizar":false,"link":null,"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":false},{"posicao":2,"descricao":"Receita Total","columnName":"RECEITATOTAL","columnValue":"RECEITATOTAL","tipoAlinhamento":"DIREITA","totalizar":true,"link":null,"tipo":"MONETARIO","tipoEnum":null,"valorPadrao":null,"escondido":false},{"posicao":3,"descricao":"Despesa Total","columnName":"DESPESATOTAL","columnValue":"DESPESATOTAL","tipoAlinhamento":"DIREITA","totalizar":true,"link":null,"tipo":"MONETARIO","tipoEnum":null,"valorPadrao":null,"escondido":false},{"posicao":4,"descricao":"Receita Primária","columnName":"RECEITAPRIMARIA","columnValue":"RECEITAPRIMARIA","tipoAlinhamento":"DIREITA","totalizar":true,"link":null,"tipo":"MONETARIO","tipoEnum":null,"valorPadrao":null,"escondido":false},{"posicao":5,"descricao":"Despesa Primária","columnName":"DESPESAPRIMARIA","columnValue":"DESPESAPRIMARIA","tipoAlinhamento":"DIREITA","totalizar":true,"link":null,"tipo":"MONETARIO","tipoEnum":null,"valorPadrao":null,"escondido":false},{"posicao":6,"descricao":"Resultado Nominal","columnName":"RESULTADONOMINAL","columnValue":"RESULTADONOMINAL","tipoAlinhamento":"DIREITA","totalizar":true,"link":null,"tipo":"MONETARIO","tipoEnum":null,"valorPadrao":null,"escondido":false},{"posicao":7,"descricao":"Dívida Pública Consolidada","columnName":"DIVIDAPUBLICACONSOLIDADA","columnValue":"DIVIDAPUBLICACONSOLIDADA","tipoAlinhamento":"DIREITA","totalizar":true,"link":null,"tipo":"MONETARIO","tipoEnum":null,"valorPadrao":null,"escondido":false},{"posicao":8,"descricao":"Dívida Pública Líquida","columnName":"DIVIDAPUBLICALIQUIDA","columnValue":"DIVIDAPUBLICALIQUIDA","tipoAlinhamento":"DIREITA","totalizar":true,"link":null,"tipo":"MONETARIO","tipoEnum":null,"valorPadrao":null,"escondido":false},{"posicao":9,"descricao":"Fonte de Informação","columnName":"FONTEDEINFORMACAO","columnValue":"FONTEDEINFORMACAO","tipoAlinhamento":"ESQUERDA","totalizar":false,"link":null,"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":false}]}],"paineis":[]}',
    conteudohtml = '<h3 style="font-weight: bold">
               <a target="_blank" class="btn-link"
                   href="/public/pagina/download-arquivo?$IDARQUIVO">
                    <i class="fas fa-download"></i>
                </a>
                $LEI
            </h3>'
where chave = 'ldo';
update paginaprefeituraportal
set subnome = 'O que é a LOA?',
    chavesubnome = 'https://www.youtube.com/watch?v=CP0Jz3qA9G8',
    conteudo = '{"consulta":"select ''Lei Nº '' || ato.numero || ''/'' || e.ano as lei, coalesce(valordareceita, 0) as orcamento, ato.arquivo_id as idArquivo\r\nfrom loa\r\n         inner join atolegal ato on loa.atolegal_id = ato.id\r\n         inner join exercicio e on ato.exercicio_id = e.id\r\nwhere extract(year from (loa.dataefetivacao)) = $ANO\r\n    $WHERE","count":"select count(1) from dual","totalRegistros":1,"fields":[{"posicao":1,"descricao":"LEI","columnName":"LEI","columnValue":"LEI","tipoAlinhamento":"CENTRO","totalizar":false,"link":null,"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":false},{"posicao":2,"descricao":"ORCAMENTO","columnName":"ORCAMENTO","columnValue":"ORCAMENTO","tipoAlinhamento":"DIREITA","totalizar":false,"link":null,"tipo":"MONETARIO","tipoEnum":null,"valorPadrao":null,"escondido":false},{"posicao":3,"descricao":"IDARQUIVO","columnName":"IDARQUIVO","columnValue":"IDARQUIVO","tipoAlinhamento":"CENTRO","totalizar":false,"link":null,"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":false}],"pesquisaveis":[{"descricao":"Tipo","columnName":"ato.tipoatolegal","columnValue":null,"tipo":"ENUM","operacaoPadrao":null,"tipoEnum":"br.com.webpublico.enums.TipoAtoLegal","valorPadrao":[null,"DECRETO","PORTARIA","RESOLUCAO","CIRCULAR","DESPACHO","EMENDA","PROJETO_DE_LEI","OFICIO","PARECER","PROCESSO","ATESTADO","MEMORANDO","MEDIDA_PROVISORIA","LEI_COMPLEMENTAR","LEI_ORDINARIA","LEI_ORGANICA","REGIMENTO_INTERNO","TERMO_POSSE","ORIENTACAO_TECNICA","RECOMENDACAO","INSTRUCAO_NORMATIVA","RELATORIO","AUTOGRAFO","LEGISLATIVO","CONTROLE_EXTERNO"],"tipoMultiSelect":null},{"descricao":"Ementa","columnName":"ato.nome","columnValue":null,"tipo":"STRING","operacaoPadrao":null,"tipoEnum":null,"valorPadrao":"","tipoMultiSelect":null},{"descricao":"Epígrafe","columnName":"ato.numero","columnValue":null,"tipo":"STRING","operacaoPadrao":null,"tipoEnum":null,"valorPadrao":"","tipoMultiSelect":null}],"tabs":[],"paineis":[]}',
    conteudohtml = '<div class="row">
                <div class="col-sm-12">
                    <div  class="col-sm-12 card ">
                        <h2>
                            <a target="_blank" class="btn-link"
                              href="/public/pagina/download-arquivo?$IDARQUIVO">
                                 <i class="fas fa-download"></i>
                             </a>
                             $LEI
                         </h2>
                        <div class="col-sm-6 bg-success" style="margin-bottom: 10px;">
                            <div class="card-left" style="padding-top: 5px; padding-bottom: 5px;">
                                <h4>Orçamento Geral do Município: $ORCAMENTO</h4>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-sm-12">
                    <div class="col-sm-4">
                        <a href="/public/pagina/unidade">
                           <img src="/assets/img/contabil/loa/unidade.png"/>
                        <h4 style="margin-top: 0px; margin-left: 15px;">Unidades</h4>
                    </a>
                </div>
                <div class="col-sm-4">
                    <a href="/public/pagina/planejamento-despesa">
                        <img src="/assets/img/contabil/loa/despesa.png"/>
                        <h4 style="margin-top: 0px; margin-left: 15px;">Despesa</h4>
                    </a>
                </div>
                <div class="col-sm-4">
                    <a href="/public/pagina/planejamento-receita">
                        <img src="/assets/img/contabil/loa/receita.png"/>
                        <h4 style="margin-top: 0px; margin-left: 15px;">Receita</h4>
                    </a>
                </div>
            </div>
            <div class="col-sm-12">
                <div class="col-sm-4">
                        <a href="/public/pagina/fonte-de-recurso">
                        <img src="/assets/img/contabil/loa/fonte.png"/>
                        <h4 style="margin-top: 0px; margin-left: 15px;">Fonte</h4>
                    </a>
                </div>
                <div class="col-sm-4">
                    <a href="/public/pagina/programa">
                        <img src="/assets/img/contabil/loa/list.png"/>
                        <h4 style="margin-top: 0px; margin-left: 15px;">Programa</h4>
                    </a>
                </div>
                <div class="col-sm-4">
                    <a href="/public/pagina/acao">
                        <img src="/assets/img/contabil/loa/acao.png"/>
                        <h4 style="margin-top: 0px; margin-left: 15px;">Ação</h4>
                    </a>
                </div>
            </div>
        </div>'
where chave = 'loa';
