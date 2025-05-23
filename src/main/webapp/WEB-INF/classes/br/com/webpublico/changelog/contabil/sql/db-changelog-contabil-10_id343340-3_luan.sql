update itemprocesso set query23 = 'select data,
       grupoMaterial,
       unidade,
       lancamento,
       tipoestoque,
       operacao,
       valor,
       idOrigem
from(
        select b.databem as data,
               b.grupomaterial_id as grupoMaterial,
               b.unidadeorganizacional_id as unidade,
               b.tipolancamento as lancamento,
               b.tipoestoque as tipoestoque,
               b.operacoesbensestoque as operacao,
               b.valor as valor,
               b.id as idOrigem
        from bensestoque b
                 inner join grupomaterial g on b.grupomaterial_id = g.id
        where trunc(b.databem) between to_date(:dataInicial, ''dd/MM/yyyy'') and to_date(:dataFinal, ''dd/MM/yyyy'')
            $Filtros
        union all
        select l.dataliquidacao as data,
            cf.grupomaterial_id as grupoMaterial,
            l.unidadeorganizacional_id as unidade,
            ''NORMAL'' as lancamento,
            cf.tipoestoque as tipoestoque,
            ''AQUISICAO_BENS_ESTOQUE'' as operacao,
            d.valor as valor,
            l.id as idOrigem
        from liquidacao l
            inner join desdobramento d on l.id = d.liquidacao_id
            inner join contadespesa cd on d.conta_id = cd.id
            inner join configgrupomaterial cf on cf.contadespesa_id = cd.id
            inner join grupomaterial g on cf.grupomaterial_id = g.id
        where cd.tipocontadespesa = ''BEM_ESTOQUE''   and l.transportado = 0
          and trunc(l.dataliquidacao) between cf.iniciovigencia and coalesce(cf.fimvigencia, trunc(l.dataliquidacao))
          and trunc(l.dataliquidacao) between to_date(:dataInicial, ''dd/MM/yyyy'') and to_date(:dataFinal, ''dd/MM/yyyy'')
            $Filtros
        union all
        select le.dataestorno as data,
            cf.grupomaterial_id as grupoMaterial,
            le.unidadeorganizacional_id as unidade,
            ''ESTORNO'' as lancamento,
            cf.tipoestoque as tipoestoque,
            ''AQUISICAO_BENS_ESTOQUE'' as operacao,
            d.valor as valor,
            le.id as idOrigem
        from liquidacaoestorno le
            inner join desdobramentoliqestorno d on le.id = d.liquidacaoestorno_id
            inner join contadespesa cd on d.conta_id = cd.id
            inner join configgrupomaterial cf on cf.contadespesa_id = cd.id
            inner join grupomaterial g on cf.grupomaterial_id = g.id
        where cd.tipocontadespesa = ''BEM_ESTOQUE''
          and trunc(le.dataestorno) between cf.iniciovigencia and coalesce(cf.fimvigencia, trunc(le.dataestorno) )
          and trunc(le.dataestorno) between to_date(:dataInicial, ''dd/MM/yyyy'') and to_date(:dataFinal, ''dd/MM/yyyy'')
            $Filtros
        union all
        select b.DATATRANSFERENCIA as data,
            b.GRUPOMATERIAL_ID as grupobem,
            b.UNIDADEORGORIGEM_ID as unidade,
            b.tipolancamento as lancamento,
            b.TIPOESTOQUEORIGEM as tipogrupo,
            b.OPERACAOORIGEM as operacao,
            b.valor as valor,
            b.id as idOrigem
        from TRANSFBENSESTOQUE b
            inner join grupomaterial g on b.GRUPOMATERIAL_ID = g.id
        where trunc(b.DATATRANSFERENCIA) between to_date(:dataInicial, ''dd/MM/yyyy'') and to_date(:dataFinal, ''dd/MM/yyyy'')
            $Filtros
        union all
        select b.DATATRANSFERENCIA as data,
            b.GRUPOMATERIALDESTINO_ID as grupobem,
            b.unidadeOrgDestino_ID as unidade,
            b.tipolancamento as lancamento,
            b.TIPOESTOQUEDESTINO as tipogrupo,
            b.OPERACAODESTINO as operacao,
            b.valor as valor,
            b.id as idOrigem
        from TRANSFBENSESTOQUE b
            inner join grupomaterial g on b.GRUPOMATERIALDESTINO_ID = g.id
        where trunc(b.DATATRANSFERENCIA) between to_date(:dataInicial, ''dd/MM/yyyy'') and to_date(:dataFinal, ''dd/MM/yyyy'')
            $Filtros
    ) dados
order by data'
