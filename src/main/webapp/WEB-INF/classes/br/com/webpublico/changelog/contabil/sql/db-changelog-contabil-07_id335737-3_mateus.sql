update itemprocesso set query36 = 'select COALESCE(COALESCE(SUM(saldo.credito) - SUM(COALESCE(saldoInicial.credito, 0)), 0) - COALESCE(SUM(saldo.debito) - SUM(COALESCE(saldoInicial.debito, 0)) - SUM(COALESCE(saldoInicial.credito, 0) - COALESCE(saldoInicial.debito, 0)), 0), 0) as saldofinal
from (
       select mov.GRUPOBEM_ID as grupoid,
              mov.tipoGrupo as tipoGrupo,
              mov.unidadeorganizacional_id as unidade_id,
              mov.naturezaTipoGrupoBem as naturezaParametro,
              case mov.naturezaTipoGrupoBem
              when ''ORIGINAL'' then mov.credito else mov.credito * - 1
              end as credito,
              case mov.naturezaTipoGrupoBem
              when ''ORIGINAL'' then mov.debito else mov.debito * - 1
              end as debito
       from saldogrupobemimoveis mov
         inner join GRUPOBEM gb on gb.ID = mov.GRUPOBEM_ID and gb.TIPOBEM = ''IMOVEIS''
         inner join CONFIGCONCCONTABILGRUPOBEM cfg on gb.ID = cfg.GRUPOBEM_ID
       where trunc(mov.datasaldo) = (
         select max(trunc(saldo.datasaldo)) from saldogrupobemimoveis saldo
         where saldo.GRUPOBEM_ID = mov.GRUPOBEM_ID
               and trunc(saldo.datasaldo) <= to_date(:dataFinal, ''dd/mm/yyyy'')
               and mov.TIPOGRUPO = saldo.TIPOGRUPO
               and mov.NATUREZATIPOGRUPOBEM = saldo.NATUREZATIPOGRUPOBEM
               and mov.unidadeorganizacional_id = saldo.unidadeorganizacional_id
         group by saldo.TIPOGRUPO, saldo.NATUREZATIPOGRUPOBEM)
             and cfg.configConciliacaoContabil_id = :cfgId
     ) saldo
  left join (select sld.GRUPOBEM_ID as grupoid,
               max(trunc(sld.datasaldo)),
                    sld.unidadeorganizacional_id as unidade,
                    sld.TIPOGRUPO,
                    sld.NATUREZATIPOGRUPOBEM as naturezaParametro,
                    case sld.NATUREZATIPOGRUPOBEM
                    when ''ORIGINAL'' then sum(sld.credito) else sum(sld.credito) * - 1
                    end as credito,
                    case sld.NATUREZATIPOGRUPOBEM
                    when ''ORIGINAL'' then sum(sld.debito) else sum(sld.debito) * - 1
                    end as debito
             from saldogrupobemimoveis sld
               inner join GRUPOBEM gb on gb.ID = sld.GRUPOBEM_ID and gb.TIPOBEM = ''IMOVEIS''
               inner join CONFIGCONCCONTABILGRUPOBEM cf on gb.ID = cf.GRUPOBEM_ID
             where trunc(sld.datasaldo) = (
               select max(trunc(saldo.datasaldo)) from saldogrupobemimoveis saldo
               where saldo.GRUPOBEM_ID = sld.GRUPOBEM_ID
                     and trunc(saldo.datasaldo) < TO_DATE(:dataInicial, ''DD/MM/YYYY'')
                     and sld.TIPOGRUPO = saldo.TIPOGRUPO
                     and sld.NATUREZATIPOGRUPOBEM = saldo.NATUREZATIPOGRUPOBEM
                     and sld.unidadeorganizacional_id = saldo.unidadeorganizacional_id
               group by saldo.TIPOGRUPO, saldo.NATUREZATIPOGRUPOBEM)
                   and cf.configConciliacaoContabil_id = :cfgId
             group by sld.GRUPOBEM_ID, sld.TIPOGRUPO, sld.unidadeorganizacional_id, sld.NATUREZATIPOGRUPOBEM
            )
            saldoInicial on saldoInicial.grupoid = saldo.grupoid
                            and saldoInicial.TIPOGRUPO = saldo.tipoGrupo
                            and saldoInicial.unidade = saldo.unidade_id
                            and saldoInicial.naturezaParametro = saldo.naturezaParametro
  inner join vwhierarquiaorcamentaria vw on vw.SUBORDINADA_ID = saldo.unidade_id
where to_date(:dataFinal, ''dd/mm/yyyy'') between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, to_date(:dataFinal, ''dd/mm/yyyy''))'
