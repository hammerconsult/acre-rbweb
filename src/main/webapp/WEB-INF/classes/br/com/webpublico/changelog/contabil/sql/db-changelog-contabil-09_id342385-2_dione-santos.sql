DECLARE
conteudoSQL CLOB := '{"consulta":"select case when ato.id is not null then ''Lei Nº '' || ato.numero || ''/'' || e.ano else '' '' end  as lei, coalesce(valordareceita, 0) as orcamento\r\nfrom loa\r\n         left join atolegal ato on loa.atolegal_id = ato.id\r\n         left join exercicio e on ato.exercicio_id = e.id\r\nwhere extract(year from (loa.dataefetivacao)) = $ANO\r\n $WHERE\r\nUNION ALL\r\nSELECT '' '' as lei, 0 as orcamento FROM DUAL WHERE NOT EXISTS (\r\n        SELECT id from loa where extract(year from (dataefetivacao)) = $ANO\r\n    )","count":"select count(1) from dual","totalRegistros":1,"fields":[{"posicao":1,"descricao":"LEI","columnName":"LEI","columnValue":"LEI","tipoAlinhamento":"CENTRO","totalizar":false,"link":null,"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":false},{"posicao":2,"descricao":"ORCAMENTO","columnName":"ORCAMENTO","columnValue":"ORCAMENTO","tipoAlinhamento":"DIREITA","totalizar":true,"link":null,"tipo":"MONETARIO","tipoEnum":null,"valorPadrao":null,"escondido":false}],"pesquisaveis":[{"descricao":"Tipo","columnName":"ato.tipoatolegal","columnValue":null,"tipo":"ENUM","operacaoPadrao":null,"tipoEnum":"br.com.webpublico.enums.TipoAtoLegal","valorPadrao":[null,"DECRETO","PORTARIA","RESOLUCAO","CIRCULAR","DESPACHO","EMENDA","PROJETO_DE_LEI","OFICIO","PARECER","PROCESSO","ATESTADO","MEMORANDO","MEDIDA_PROVISORIA","LEI_COMPLEMENTAR","LEI_ORDINARIA","LEI_ORGANICA","REGIMENTO_INTERNO","TERMO_POSSE","ORIENTACAO_TECNICA","RECOMENDACAO","INSTRUCAO_NORMATIVA","RELATORIO","AUTOGRAFO","LEGISLATIVO","CONTROLE_EXTERNO"],"tipoMultiSelect":null},{"descricao":"Ementa","columnName":"ato.nome","columnValue":null,"tipo":"STRING","operacaoPadrao":null,"tipoEnum":null,"valorPadrao":"","tipoMultiSelect":null},{"descricao":"Epígrafe","columnName":"ato.numero","columnValue":null,"tipo":"STRING","operacaoPadrao":null,"tipoEnum":null,"valorPadrao":"","tipoMultiSelect":null}],"tabs":[]}';
texto CLOB := '<div class="row">
                <div class="col-sm-12">
                    <div  class="col-sm-12 card ">
                        <h2>$LEI</h2>
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
        </div>';
BEGIN
update PAGINAPREFEITURAPORTAL set CONTEUDO = conteudoSQL where NOME = 'LOA' and CHAVE = 'loa';
update PAGINAPREFEITURAPORTAL set CONTEUDOHTML = texto where NOME = 'LOA' and CHAVE = 'loa';
END;
