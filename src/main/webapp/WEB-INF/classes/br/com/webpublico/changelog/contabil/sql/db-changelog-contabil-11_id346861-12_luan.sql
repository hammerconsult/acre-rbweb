insert into PAGINAPREFEITURAPORTAL
values (hibernate_sequence.nextval, 608930532, 'HTML',
        '{"consulta":"select * from dual $WHERE","count":"select 1 from dual","totalRegistros":null,"fields":[],"pesquisaveis":[],"tabs":[],"paineis":[]}',
        'Legislação do Portal ', 'legislacao-portal', 'GRID',
        '<div>
    <a href="https://www.planalto.gov.br/ccivil_03/_ato2011-2014/2011/lei/l12527.htm" target="_blank">
        Lei Federal n° 12.527, de 18 de novembro de 2011 – Lei de Acesso à Informação (LAI). Disponível.
    </a><br/><br/>
    <a href="https://www.planalto.gov.br/ccivil_03/_ato2015-2018/2018/lei/l13709.htm" target="_blank">
        Lei n° 13.079, de 14 de agosto de 2018 – Lei Geral de Proteção de Dados (LGPD).
    </a><br/><br/>
    <a href="https://www.planalto.gov.br/ccivil_03/_ato2019-2022/2021/lei/l14129.htm" target="_blank">
        Lei n° 14.129, de 29 de março de 2021 – Governo Digital.
    </a><br/><br/>
    <a href="http://portalcgm.riobranco.ac.gov.br/lai/wp-content/uploads/images/ARQUIVOS/principal/LEI-1915_DISPOE_SOBRE_O_ACESSO_A_INF._LF_12.527-2011.pdf" target="_blank">
        Lei Municipal n° 1.915, de 25 de junho de 2012 – Dispõe sobre o acesso à informação e a aplicação da Lei Federal nº 12.527, de 18 de novembro de 2011, no âmbito do Poder Executivo do Município de Rio Branco e dá outras providências.
    </a><br/><br/>
    <a href="https://www.riobranco.ac.leg.br/leis/legislacao-municipal/2020/LeiMunicipaln2.391de30dedezembrode2020.PDF" target="_blank">
        Lei Municipal n° 2.391, de 30 de dezembro de 2020 - Dispõe sobre a criação do Programa de Integridade e Compliance da Administração Pública Municipal e dá outras providências.
    </a><br/><br/>
    <a href="http://portalcgm.riobranco.ac.gov.br/portal/wp-content/uploads/2015/02/DECRETO-N%C2%BA-1.381-DE-27-DE-MAIO-DE-2010.docx" target="_blank">
        Decreto Municipal n° 1.381/2010 - Dispõe sobre a divulgação dos dados e informações pelos órgãos e entidades da administração pública municipal, por meio da Rede Mundial de Computadores, e dá outras providências.
    </a><br/><br/>
    <a href="http://portalcgm.riobranco.ac.gov.br/lai/wp-content/uploads/2015/02/DECRETO-N%C2%BA-3.556-DE-15-DE-MAIO-DE-2012.pdf" target="_blank">
        Decreto Municipal n° 3.556/2012 – Dispõe sobre os procedimentos para acesso à informação e a aplicação da Lei Federal n°12.527, de 18 de novembro de 2011, no âmbito do Poder Executivo do Município de Rio Branco.
    </a><br/><br/>
</div>', 1, 1, (select id from moduloprefeituraportal where modulo = 'LEGISLACAO'), null, null, null, null, null);

insert into paginaprefeituraportal
values (hibernate_sequence.nextval, 608930532, 'SQL',
        '{"consulta":"select nome, chave, ''height: 100px;'' as style, ''menu-strip-branco'' as classediv \r\nfrom paginaprefeituraportal \r\nwhere modulo_id = (select id from moduloprefeituraportal where modulo = ''CONTROLE_INTERNO'') \r\n$WHERE \r\norder by ordem","count":"select count(id) from paginaprefeituraportal where modulo_id = (select id from moduloprefeituraportal where modulo = ''CONTROLE_INTERNO'') $WHERE","totalRegistros":1,"fields":[{"posicao":1,"descricao":"Nome","columnName":"NOME","columnValue":"NOME","tipoAlinhamento":"CENTRO","totalizar":false,"link":{"chaveDestino":"CHAVE","columnOrigem":"NOME","columnDestino":""},"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":false},{"posicao":2,"descricao":"CHAVE","columnName":"CHAVE","columnValue":"CHAVE","tipoAlinhamento":"CENTRO","totalizar":false,"link":null,"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":true},{"posicao":3,"descricao":"STYLE","columnName":"STYLE","columnValue":"STYLE","tipoAlinhamento":"CENTRO","totalizar":false,"link":null,"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":true},{"posicao":4,"descricao":"CLASSEDIV","columnName":"CLASSEDIV","columnValue":"CLASSEDIV","tipoAlinhamento":"CENTRO","totalizar":false,"link":null,"tipo":"STRING","tipoEnum":null,"valorPadrao":null,"escondido":true}],"pesquisaveis":[],"tabs":[],"paineis":[]}',
        'Controle Interno', 'controle-interno', 'TABELA_UNICA',
        '<div></div>', 1, 1, (select id from moduloprefeituraportal where modulo = 'PORTAL'), null, null, null, null, null);
