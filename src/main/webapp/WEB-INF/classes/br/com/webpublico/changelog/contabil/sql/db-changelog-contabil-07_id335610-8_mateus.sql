update itemprocesso set query33 = 'select COALESCE(COALESCE(SUM(saldo.credito) - SUM(COALESCE(saldoInicial.credito, 0)), 0) - COALESCE(SUM(saldo.debito) - SUM(COALESCE(saldoInicial.debito, 0)) - SUM(COALESCE(saldoInicial.credito, 0) - COALESCE(saldoInicial.debito, 0)), 0), 0) as saldofinal
from (
       select mov.grupomaterial_id as grupoid,
              mov.tipoestoque as tipoestoque,
              mov.unidadeorganizacional_id as unidade_id,
              mov.naturezatipogrupomaterial as naturezaParametro,
              case mov.naturezatipogrupomaterial
              when ''ORIGINAL'' then mov.credito else mov.credito * - 1
              end as credito,
              case mov.naturezatipogrupomaterial
              when ''ORIGINAL'' then mov.debito else mov.debito * - 1
              end as debito
       from saldogrupomaterial mov
         inner join CONFIGCONCCONTABILNATTPGM cfg on mov.naturezatipogrupomaterial = cfg.naturezaTipoGrupoMaterial
       where trunc(mov.datasaldo) = (
         select max(trunc(saldo.datasaldo)) from saldogrupomaterial saldo
         where saldo.grupomaterial_id = mov.grupomaterial_id
               and trunc(saldo.datasaldo) <= to_date(:dataFinal, ''dd/mm/yyyy'')
               and mov.tipoestoque = saldo.tipoestoque
               and mov.naturezatipogrupomaterial = saldo.naturezatipogrupomaterial
               and mov.unidadeorganizacional_id = saldo.unidadeorganizacional_id
         group by saldo.tipoestoque, saldo.naturezatipogrupomaterial)
          and cfg.configConciliacaoContabil_id = :cfgId
     ) saldo
  left join (select sld.grupomaterial_id as grupoid,
               max(trunc(sld.datasaldo)),
                    sld.unidadeorganizacional_id as unidade,
               sld.tipoestoque,
                    sld.naturezatipogrupomaterial as naturezaParametro,
                    case sld.naturezatipogrupomaterial
                    when ''ORIGINAL'' then sum(sld.credito) else sum(sld.credito) * - 1
                    end as credito,
                    case sld.naturezatipogrupomaterial
                    when ''ORIGINAL'' then sum(sld.debito) else sum(sld.debito) * - 1
                    end as debito
             from saldogrupomaterial sld
               inner join CONFIGCONCCONTABILNATTPGM cf on sld.naturezatipogrupomaterial = cf.naturezaTipoGrupoMaterial
             where trunc(sld.datasaldo) = (
               select max(trunc(saldo.datasaldo)) from saldogrupomaterial saldo
               where saldo.grupomaterial_id = sld.grupomaterial_id
                     and trunc(saldo.datasaldo) < TO_DATE(:dataInicial, ''DD/MM/YYYY'')
                     and sld.tipoestoque = saldo.tipoestoque
                     and sld.naturezatipogrupomaterial = saldo.naturezatipogrupomaterial
                     and sld.unidadeorganizacional_id = saldo.unidadeorganizacional_id
               group by saldo.tipoestoque, saldo.naturezatipogrupomaterial)
                  and cf.configConciliacaoContabil_id = :cfgId
             group by sld.grupomaterial_id, sld.tipoestoque, sld.unidadeorganizacional_id, sld.naturezatipogrupomaterial
            )
            saldoInicial on saldoInicial.grupoid = saldo.grupoid
                            and saldoInicial.tipoestoque = saldo.tipoestoque
                            and saldoInicial.unidade = saldo.unidade_id
                            and saldoInicial.naturezaParametro = saldo.naturezaParametro
  inner join vwhierarquiaorcamentaria vw on vw.SUBORDINADA_ID = saldo.unidade_id
where to_date(:dataFinal, ''dd/mm/yyyy'') between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, to_date(:dataFinal, ''dd/mm/yyyy''))'
