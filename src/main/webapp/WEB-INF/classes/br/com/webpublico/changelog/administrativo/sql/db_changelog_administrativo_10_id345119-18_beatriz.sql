create
or replace VIEW vw_licitacao_resultado_pncp as
select distinct quantidade_homologada,
                valor_unitario_homologado,
                round(coalesce(quantidade_homologada, 1) * valor_unitario_homologado, 2)  as valor_total_homologado,
                percentual_desconto,
                tipo_pessoaId,
                ni_fornecedor,
                nome_razao_social,
                porte_fornecedorId,
                natureza_juridica,
                codigo_pais,
                indicador_subcontratacao,
                ordem_classificacao_srp,
                data_resultado,
                itemProcessoDeCompraId,
                sequencialpncp,
                numeroLote || LPAD (numeroItem,3, '0') as numeroItem,
                numeroLote,
                idLicitacao,
                aplicacaoMargemPreferencia,
                aplicacaoBeneficioMeEpp,
                aplicacaoCriterioDesempate,
                situacaoCompraItemResultadoId
from (select distinct ipc.quantidade                                                   as quantidade_homologada,
                      item.PRECO                                                       as valor_unitario_homologado,
                      0                                                                as valor_total_homologado,
                      0                                                                as percentual_desconto,
                      case when pf.id is not null then 'PF' else 'PJ' end              as tipo_pessoaId,
                      case
                          when pf.id is not null then pf.cpf
                          else pj.CNPJ end                             as ni_fornecedor,
                      case when pf.id is not null then pf.nome else pj.RAZAOSOCIAL end as nome_razao_social,

                      case
                          when pf.id is not null then '4'
                          else
                              case
                                  when pj.TIPOEMPRESA is null then '5'
                                  when pj.TIPOEMPRESA = 'MICRO' then '1'
                                  when pj.TIPOEMPRESA = 'PEQUENA' then '2'
                                  else '3' end end                                     as porte_fornecedorId,
                      coalesce(nr.codigo, (select nr.codigo
                                           from naturezajuridica nr
                                                    inner join cadastroeconomico ce on ce.NATUREZAJURIDICA_ID = nr.id
                                                    inner join CE_SITUACAOCADASTRAL ceS on ceS.CADASTROECONOMICO_ID = ce.id
                                                    inner join situacaocadastroeconomico sitCad
                                                               on ces.SITUACAOCADASTROECONOMICO_ID = sitCad.id
                                           where ce.PESSOA_ID = pes.id
                                             and trunc(sitcad.DATAREGISTRO) = (select max(trunc(sitCad1.dataregistro))
                                                                               from situacaocadastroeconomico sitCad1
                                                                                        inner join CE_SITUACAOCADASTRAL ceS1
                                                                                                   on ceS1.SITUACAOCADASTROECONOMICO_ID = sitCad1.id
                                                                               where ceS1.CADASTROECONOMICO_ID = ce.id
                                                                                 and sitCad1.SITUACAOCADASTRAL = 'ATIVO'
                                                                               group by sitCad1.DATAREGISTRO
                                                                               order by sitCad1.DATAREGISTRO desc fetch first 1 row only)
                                               fetch first 1 row only), 8885)                                     as natureza_juridica,
                      'BRA'                                                            as codigo_pais,
                      0                                                                as indicador_subcontratacao,
                      1                                                                as ordem_classificacao_srp,
                      disp.DATADADISPENSA                                              as data_resultado,
                      ipc.id                                                           as itemProcessoDeCompraId,
                      ipc.sequencialpncp,
                      ipc.numero                                                       as numeroItem,
                      lpc.numero                                                       as numeroLote,
                      disp.id                                                          as idLicitacao,
                      0                                                                as aplicacaoMargemPreferencia,
                      0                                                                as aplicacaoBeneficioMeEpp,
                      0                                                                as aplicacaoCriterioDesempate,
                      1                                                                as situacaoCompraItemResultadoId
      from DISPENSADELICITACAO disp
               inner join FORNECEDORDISPLIC fdl on fdl.DISPENSADELICITACAO_ID = disp.id
               inner join PropostaFornecedorDispensa prop on prop.id = fdl.PROPOSTAFORNECEDORDISPENSA_ID
               inner join LOTEPROPOSTAFORNEDISP lote on lote.PROPOSTAFORNECEDORDISPENSA_ID = prop.id
               inner join ITEMPROPOSTAFORNEDISP item on item.LOTEPROPOSTAFORNEDISP_ID = lote.id
               inner join itemprocessodecompra ipc on ipc.id = item.ITEMPROCESSODECOMPRA_ID
               inner join LOTEPROCESSODECOMPRA lpc on lpc.id = ipc.LOTEPROCESSODECOMPRA_ID
               inner join pessoa pes on pes.id = fdl.PESSOA_ID
               left join pessoafisica pf on pf.id = pes.id
               left join pessoajuridica pj on pj.id = pes.id
               left join naturezajuridica nr on pj.NATUREZAJURIDICA_ID = nr.ID
      where 1 = 1
      union all
      select ipc.quantidade                                                   as quantidade_homologada,
             ipf.PRECO                                                        as valor_unitario_homologado,
             0                                                                as valor_total_homologado,
             0                                                                as percentual_desconto,
             case when pf.id is not null then 'PF' else 'PJ' end              as tipo_pessoaId,
             case
                 when pf.id is not null then pf.cpf
                 else pj.CNPJ end                             as ni_fornecedor,
             case when pf.id is not null then pf.nome else pj.RAZAOSOCIAL end as nome_razao_social,

             case
                 when pf.id is not null then '4'
                 else
                     case
                         when pj.TIPOEMPRESA is null then '5'
                         when pj.TIPOEMPRESA = 'MICRO' then '1'
                         when pj.TIPOEMPRESA = 'PEQUENA' then '2'
                         else '3' end end                                     as porte_fornecedorId,
             coalesce(nr.codigo, (select nr.codigo
                                  from naturezajuridica nr
                                           inner join cadastroeconomico ce on ce.NATUREZAJURIDICA_ID = nr.id
                                           inner join CE_SITUACAOCADASTRAL ceS on ceS.CADASTROECONOMICO_ID = ce.id
                                           inner join situacaocadastroeconomico sitCad on ces.SITUACAOCADASTROECONOMICO_ID = sitCad.id
                                  where ce.PESSOA_ID = pes.id
                                    and trunc(sitcad.DATAREGISTRO) = (select max(trunc(sitCad1.dataregistro))
                                                                      from situacaocadastroeconomico sitCad1
                                                                               inner join CE_SITUACAOCADASTRAL ceS1
                                                                                          on ceS1.SITUACAOCADASTROECONOMICO_ID = sitCad1.id
                                                                      where ceS1.CADASTROECONOMICO_ID = ce.id
                                                                        and sitCad1.SITUACAOCADASTRAL = 'ATIVO'
                                                                      group by sitCad1.DATAREGISTRO
                                                                      order by sitCad1.DATAREGISTRO desc fetch first 1 row only)
                                  order by ce.id desc fetch first 1 row only), 8885)                    as natureza_juridica,
             'BRA'                                                            as codigo_pais,
             0                                                                as indicador_subcontratacao,
             1                                                                as ordem_classificacao_srp,
             st.DATAPUBLICACAO                                                as data_resultado,
             ipc.id                                                           as itemProcessoDeCompraId,
             ipc.sequencialpncp,
             ipc.numero                                                       as numeroItem,
             lpc.numero                                                       as numeroLote,
             lic.id                                                           as idLicitacao,
             0                                                                as aplicacaoMargemPreferencia,
             0                                                                as aplicacaoBeneficioMeEpp,
             0                                                                as aplicacaoCriterioDesempate,
             1                                                                as situacaoCompraItemResultadoId
      from LICITACAO lic
               inner join LICITACAOFORNECEDOR lf on lf.LICITACAO_ID = lic.id
               inner join PROPOSTAFORNECEDOR prop on prop.LICITACAOFORNECEDOR_ID = lf.id
               inner join LOTEPROPFORNEC lote on lote.PROPOSTAFORNECEDOR_ID = prop.id
               inner join ITEMPROPFORNEC ipf on ipf.LOTEPROPOSTAFORNECEDOR_ID = lote.id
               inner join itemprocessodecompra ipc on ipc.id = ipf.ITEMPROCESSODECOMPRA_ID
               inner join LOTEPROCESSODECOMPRA lpc on lpc.id = ipc.LOTEPROCESSODECOMPRA_ID
               inner join pessoa pes on pes.id = lf.EMPRESA_ID
               left join pessoafisica pf on pf.id = pes.id
               left join pessoajuridica pj on pj.id = pes.id
               left join naturezajuridica nr on pj.NATUREZAJURIDICA_ID = nr.ID
               inner join statuslicitacao st on st.LICITACAO_ID = lic.id
          and trunc(st.DATASTATUS) =
              (select max(trunc(st1.datastatus)) from STATUSLICITACAO st1 where st1.LICITACAO_ID = lic.id)
      where lic.MODALIDADELICITACAO = 'CREDENCIAMENTO'
      union all
      select ipc.quantidade                                                   as quantidade_homologada,
             case
                 when l.tipoavaliacao = 'MAIOR_DESCONTO'
                     then case ic.tipocontrole
                              when 'VALOR' then its.preco
                              else round(its.preco - ((iplv.percentualdesconto / 100) * its.preco), 2) end
                 else iplv.valor end                                          as valor_unitario_homologado,
             0                                                                as valor_total_homologado,
             coalesce(iplv.PERCENTUALDESCONTO, 0)                             as percentual_desconto,
             case when pf.id is not null then 'PF' else 'PJ' end              as tipo_pessoaId,
             case
                 when pf.id is not null then pf.cpf
                 else pj.CNPJ end                             as ni_fornecedor,
             case when pf.id is not null then pf.nome else pj.RAZAOSOCIAL end as nome_razao_social,

             case
                 when pf.id is not null then '4'
                 else
                     case
                         when pj.TIPOEMPRESA is null then '5'
                         when pj.TIPOEMPRESA = 'MICRO' then '1'
                         when pj.TIPOEMPRESA = 'PEQUENA' then '2'
                         else '3' end end                                     as porte_fornecedorId,
             coalesce(nr.codigo,(select nr.codigo
                                 from naturezajuridica nr
                                          inner join cadastroeconomico ce on ce.NATUREZAJURIDICA_ID = nr.id
                                          inner join CE_SITUACAOCADASTRAL ceS on ceS.CADASTROECONOMICO_ID = ce.id
                                          inner join situacaocadastroeconomico sitCad on ces.SITUACAOCADASTROECONOMICO_ID = sitCad.id
                                 where ce.PESSOA_ID = pes.id
                                   and trunc(sitcad.DATAREGISTRO) = (select max(trunc(sitCad1.dataregistro))
                                                                     from situacaocadastroeconomico sitCad1
                                                                              inner join CE_SITUACAOCADASTRAL ceS1
                                                                                         on ceS1.SITUACAOCADASTROECONOMICO_ID = sitCad1.id
                                                                     where ceS1.CADASTROECONOMICO_ID = ce.id
                                                                       and sitCad1.SITUACAOCADASTRAL = 'ATIVO'
                                                                     group by sitCad1.DATAREGISTRO
                                                                     order by sitCad1.DATAREGISTRO desc fetch first 1 row only)
                                 order by ce.id desc fetch first 1 row only), 8885)                     as natureza_juridica,
             'BRA'                                                            as codigo_pais,
             0                                                                as indicador_subcontratacao,
             1                                                                as ordem_classificacao_srp,
             sfl.DATASITUACAO                                                 as data_resultado,
             ipc.id                                                           as itemProcessoDeCompraId,
             ipc.sequencialpncp,
             ipc.numero                                                       as numeroItem,
             lpc.numero                                                       as numeroLote,
             l.id                                                             as idLicitacao,
             0                                                                as aplicacaoMargemPreferencia,
             0                                                                as aplicacaoBeneficioMeEpp,
             0                                                                as aplicacaoCriterioDesempate,
             1                                                                as situacaoCompraItemResultadoId
      from itempregao ip
               inner join pregao p on p.id = ip.pregao_id
               inner join licitacao l on l.id = p.licitacao_id
               inner join itempregaolancevencedor iplv on iplv.id = ip.ITEMPREGAOLANCEVENCEDOR_ID
               inner join lancepregao lp on lp.id = iplv.lancepregao_id
               inner join PROPOSTAFORNECEDOR prop on prop.id = lp.PROPOSTAFORNECEDOR_ID
               inner join pessoa pes on pes.id = prop.FORNECEDOR_ID
               left join pessoafisica pf on pf.id = pes.id
               left join pessoajuridica pj on pj.id = pes.id
               left join naturezajuridica nr on pj.NATUREZAJURIDICA_ID = nr.ID
               inner join itpreitpro ipip on ipip.itempregao_id = ip.id
               inner join itemprocessodecompra ipc on ipc.id = ipip.itemprocessodecompra_id
               inner join LOTEPROCESSODECOMPRA lpc on lpc.id = ipc.LOTEPROCESSODECOMPRA_ID
               inner join itemsolicitacao its on its.id = ipc.itemsolicitacaomaterial_id
               inner join itemcotacao ic on ic.id = its.itemcotacao_id
               inner join licitacaofornecedor licFor on licFor.LICITACAO_ID = l.id
               inner join statusfornecedorlicitacao sfl on sfl.LICITACAOFORNECEDOR_ID = licFor.id
      where licfor.EMPRESA_ID = prop.FORNECEDOR_ID
        and sfl.TIPOSITUACAO = 'HOMOLOGADA'
      union all
      select ipc.quantidade                                                                           as quantidade_homologada,
             case
                 when l.tipoavaliacao = 'MAIOR_DESCONTO' then case ic.tipocontrole
                                                                  when 'VALOR' then its.preco
                                                                  else round(its.preco - ((iplv.percentualdesconto / 100) * its.preco), 2) end
                 else iplv.valor end                                                                  as valor_unitario_homologado,
             0                                                                                        as valor_total_homologado,
             coalesce(iplv.PERCENTUALDESCONTO, 0)                                                     as percentual_desconto,
             case when pf.id is not null then 'PF' else 'PJ' end                                      as tipo_pessoaId,
             case when pf.id is not null then pf.cpf else pj.CNPJ end as ni_fornecedor,
             case when pf.id is not null then pf.nome else pj.RAZAOSOCIAL end                         as nome_razao_social,
             case
                 when pf.id is not null then '4'
                 else case
                          when pj.TIPOEMPRESA is null then '5'
                          when pj.TIPOEMPRESA = 'MICRO' then '1'
                          when pj.TIPOEMPRESA = 'PEQUENA' then '2'
                          else '3' end end                                                            as porte_fornecedorId,
             coalesce(nr.codigo, (select nr.codigo
                                  from naturezajuridica nr
                                           inner join cadastroeconomico ce on ce.NATUREZAJURIDICA_ID = nr.id
                                           inner join CE_SITUACAOCADASTRAL ceS on ceS.CADASTROECONOMICO_ID = ce.id
                                           inner join situacaocadastroeconomico sitCad on ces.SITUACAOCADASTROECONOMICO_ID = sitCad.id
                                  where ce.PESSOA_ID = pes.id
                                    and trunc(sitcad.DATAREGISTRO) = (select max(trunc(sitCad1.dataregistro))
                                                                      from situacaocadastroeconomico sitCad1
                                                                               inner join CE_SITUACAOCADASTRAL ceS1
                                                                                          on ceS1.SITUACAOCADASTROECONOMICO_ID = sitCad1.id
                                                                      where ceS1.CADASTROECONOMICO_ID = ce.id
                                                                        and sitCad1.SITUACAOCADASTRAL = 'ATIVO'
                                                                      group by sitCad1.DATAREGISTRO
                                                                      order by sitCad1.DATAREGISTRO desc fetch first 1 row only)
                                  order by ce.id desc fetch first 1 row only), 8885)                                           as natureza_juridica,
             'BRA'                                                                                    as codigo_pais,
             0                                                                                        as indicador_subcontratacao,
             1                                                                                        as ordem_classificacao_srp,
             sfl.DATASITUACAO                                                                         as data_resultado,
             ipc.id                                                                                   as itemProcessoDeCompraId,
             ipc.sequencialpncp,
             ipc.numero                                                                               as numeroItem,
             lpc.numero                                                                               as numeroLote,
             l.id                                                                                     as idLicitacao,
             0                                                                                        as aplicacaoMargemPreferencia,
             0                                                                                        as aplicacaoBeneficioMeEpp,
             0                                                                                        as aplicacaoCriterioDesempate,
             1                                                                                        as situacaoCompraItemResultadoId
      from itempregao ip
               inner join pregao p on p.id = ip.pregao_id
               inner join licitacao l on l.id = p.licitacao_id
               inner join itempregaolancevencedor iplv on iplv.id = ip.ITEMPREGAOLANCEVENCEDOR_ID
               inner join lancepregao lp on lp.id = iplv.lancepregao_id
               inner join PROPOSTAFORNECEDOR prop on prop.id = lp.PROPOSTAFORNECEDOR_ID
               inner join pessoa pes on pes.id = prop.FORNECEDOR_ID
               left join pessoafisica pf on pf.id = pes.id
               left join pessoajuridica pj on pj.id = pes.id
               left join naturezajuridica nr on pj.NATUREZAJURIDICA_ID = nr.ID
               inner join itpreitpro ipip on ipip.itempregao_id = ip.id
               inner join itemprocessodecompra ipc on ipc.id = ipip.itemprocessodecompra_id
               inner join LOTEPROCESSODECOMPRA lpc on lpc.id = ipc.LOTEPROCESSODECOMPRA_ID
               inner join itemsolicitacao its on its.id = ipc.itemsolicitacaomaterial_id
               inner join itemcotacao ic on ic.id = its.itemcotacao_id
               inner join licitacaofornecedor licFor on licFor.LICITACAO_ID = l.id
               inner join statusfornecedorlicitacao sfl on sfl.LICITACAOFORNECEDOR_ID = licFor.id
      where licfor.EMPRESA_ID = prop.FORNECEDOR_ID
        and sfl.TIPOSITUACAO = 'HOMOLOGADA'
      union all
      select ipc.quantidade                                                                           as quantidade_homologada,
             item.valor                                                                               as valor_unitario_homologado,
             0                                                                                        as valor_total_homologado,
             coalesce(iplv.PERCENTUALDESCONTO, 0)                                                     as percentual_desconto,
             case when pf.id is not null then 'PF' else 'PJ' end                                      as tipo_pessoaId,
             case when pf.id is not null then pf.cpf else pj.CNPJ end as ni_fornecedor,
             case when pf.id is not null then pf.nome else pj.RAZAOSOCIAL end                         as nome_razao_social,
             case
                 when pf.id is not null then '4'
                 else case
                          when pj.TIPOEMPRESA is null then '5'
                          when pj.TIPOEMPRESA = 'MICRO' then '1'
                          when pj.TIPOEMPRESA = 'PEQUENA' then '2'
                          else '3' end end                                                            as porte_fornecedorId,
             coalesce(nr.codigo, (select nr.codigo
                                  from naturezajuridica nr
                                           inner join cadastroeconomico ce on ce.NATUREZAJURIDICA_ID = nr.id
                                           inner join CE_SITUACAOCADASTRAL ceS on ceS.CADASTROECONOMICO_ID = ce.id
                                           inner join situacaocadastroeconomico sitCad on ces.SITUACAOCADASTROECONOMICO_ID = sitCad.id
                                  where ce.PESSOA_ID = pes.id
                                    and trunc(sitcad.DATAREGISTRO) = (select max(trunc(sitCad1.dataregistro))
                                                                      from situacaocadastroeconomico sitCad1
                                                                               inner join CE_SITUACAOCADASTRAL ceS1
                                                                                          on ceS1.SITUACAOCADASTROECONOMICO_ID = sitCad1.id
                                                                      where ceS1.CADASTROECONOMICO_ID = ce.id
                                                                        and sitCad1.SITUACAOCADASTRAL = 'ATIVO'
                                                                      group by sitCad1.DATAREGISTRO
                                                                      order by sitCad1.DATAREGISTRO desc fetch first 1 row only)
                                  order by ce.id desc fetch first 1 row only), 8885)                                             as natureza_juridica,
             'BRA'                                                                                    as codigo_pais,
             0                                                                                        as indicador_subcontratacao,
             1                                                                                        as ordem_classificacao_srp,
             sfl.DATASITUACAO                                                                         as data_resultado,
             ipc.id                                                                                   as itemProcessoDeCompraId,
             ipc.sequencialpncp,
             ipc.numero                                                                               as numeroItem,
             lpc.numero                                                                               as numeroLote,
             l.id                                                                                     as idLicitacao,
             0                                                                                        as aplicacaoMargemPreferencia,
             0                                                                                        as aplicacaoBeneficioMeEpp,
             0                                                                                        as aplicacaoCriterioDesempate,
             1                                                                                        as situacaoCompraItemResultadoId
      from itempregao ip
               inner join pregao p on p.id = ip.pregao_id
               inner join licitacao l on l.id = p.licitacao_id
               inner join itempregaolancevencedor iplv on iplv.id = ip.ITEMPREGAOLANCEVENCEDOR_ID
               inner join lancepregao lp on lp.id = iplv.lancepregao_id
               inner join PROPOSTAFORNECEDOR prop on prop.id = lp.PROPOSTAFORNECEDOR_ID
               inner join pessoa pes on pes.id = prop.FORNECEDOR_ID
               left join pessoafisica pf on pf.id = pes.id
               left join pessoajuridica pj on pj.id = pes.id
               left join naturezajuridica nr on pj.NATUREZAJURIDICA_ID = nr.ID
               inner join ITPRELOTPRO iplp on iplp.itempregao_id = ip.id
               inner join ITEMPREGAOLOTEITEMPROCESSO item on item.ITEMPREGAOLOTEPROCESSO_ID = iplp.id
               inner join itemprocessodecompra ipc on ipc.id = item.itemprocessodecompra_id
               inner join LOTEPROCESSODECOMPRA lpc on lpc.id = ipc.LOTEPROCESSODECOMPRA_ID
               inner join itemsolicitacao its on its.id = ipc.itemsolicitacaomaterial_id
               inner join itemcotacao ic on ic.id = its.itemcotacao_id
               inner join licitacaofornecedor licFor on licFor.LICITACAO_ID = l.id
               inner join statusfornecedorlicitacao sfl on sfl.LICITACAOFORNECEDOR_ID = licFor.id
      where licfor.EMPRESA_ID = prop.FORNECEDOR_ID
        and sfl.TIPOSITUACAO = 'HOMOLOGADA'
      union all
      select ipc.quantidade                                                                           as quantidade_homologada,
             ipf.preco                                                                                as valor_unitario_homologado,
             0                                                                                        as valor_total_homologado,
             coalesce(ipf.PERCENTUALDESCONTO, 0)                                                      as percentual_desconto,
             case when pf.id is not null then 'PF' else 'PJ' end                                      as tipo_pessoaId,
             case when pf.id is not null then pf.cpf else pj.CNPJ end as ni_fornecedor,
             case when pf.id is not null then pf.nome else pj.RAZAOSOCIAL end                         as nome_razao_social,
             case
                 when pf.id is not null then '4'
                 else case
                          when pj.TIPOEMPRESA is null then '5'
                          when pj.TIPOEMPRESA = 'MICRO' then '1'
                          when pj.TIPOEMPRESA = 'PEQUENA' then '2'
                          else '3' end end                                                            as porte_fornecedorId,
             coalesce(nr.codigo,(select nr.codigo
                                 from naturezajuridica nr
                                          inner join cadastroeconomico ce on ce.NATUREZAJURIDICA_ID = nr.id
                                          inner join CE_SITUACAOCADASTRAL ceS on ceS.CADASTROECONOMICO_ID = ce.id
                                          inner join situacaocadastroeconomico sitCad on ces.SITUACAOCADASTROECONOMICO_ID = sitCad.id
                                 where ce.PESSOA_ID = pes.id
                                   and trunc(sitcad.DATAREGISTRO) = (select max(trunc(sitCad1.dataregistro))
                                                                     from situacaocadastroeconomico sitCad1
                                                                              inner join CE_SITUACAOCADASTRAL ceS1
                                                                                         on ceS1.SITUACAOCADASTROECONOMICO_ID = sitCad1.id
                                                                     where ceS1.CADASTROECONOMICO_ID = ce.id
                                                                       and sitCad1.SITUACAOCADASTRAL = 'ATIVO'
                                                                     group by sitCad1.DATAREGISTRO
                                                                     order by sitCad1.DATAREGISTRO desc fetch first 1 row only)
                                 order by ce.id desc fetch first 1 row only), 8885)                                            as natureza_juridica,
             'BRA'                                                                                    as codigo_pais,
             0                                                                                        as indicador_subcontratacao,
             1                                                                                        as ordem_classificacao_srp,
             sfl.DATASITUACAO                                                                         as data_resultado,
             ipc.id                                                                                   as itemProcessoDeCompraId,
             ipc.sequencialpncp,
             ipc.numero                                                                               as numeroItem,
             lpc.numero                                                                               as numeroLote,
             lic.id                                                                                   as idLicitacao,
             0                                                                                        as aplicacaoMargemPreferencia,
             0                                                                                        as aplicacaoBeneficioMeEpp,
             0                                                                                        as aplicacaoCriterioDesempate,
             1                                                                                        as situacaoCompraItemResultadoId
      from itemcertame ic
               inner join certame c on c.id = ic.CERTAME_ID
               inner join licitacao lic on lic.id = c.LICITACAO_ID
               inner join ITEMCERTAMEITEMPRO icip on icip.ITEMCERTAME_ID = ic.id
               inner join ITEMPROPFORNEC ipf on icip.ITEMPROPOSTAVENCEDOR_ID = ipf.id
               inner join itemprocessodecompra ipc on icip.ITEMPROCESSODECOMPRA_ID = ipc.id
               inner join LOTEPROCESSODECOMPRA lpc on lpc.id = ipc.LOTEPROCESSODECOMPRA_ID
               inner join PROPOSTAFORNECEDOR prop on prop.id = ipf.PROPOSTAFORNECEDOR_ID
               inner join pessoa pes on pes.id = prop.FORNECEDOR_ID
               left join pessoafisica pf on pf.id = pes.id
               left join pessoajuridica pj on pj.id = pes.id
               left join naturezajuridica nr on pj.NATUREZAJURIDICA_ID = nr.ID
               inner join licitacaofornecedor licFor on licFor.LICITACAO_ID = lic.id
               inner join statusfornecedorlicitacao sfl on sfl.LICITACAOFORNECEDOR_ID = licFor.id
      where ic.situacaoItemCertame = 'VENCEDOR'
        and licfor.EMPRESA_ID = prop.FORNECEDOR_ID
        and sfl.TIPOSITUACAO = 'HOMOLOGADA'
      union all
      select ipc.quantidade                                                                           as quantidade_homologada,
             ipf.preco                                                                                as valor_unitario_homologado,
             0                                                                                        as valor_total_homologado,
             coalesce(lpf.PERCENTUALDESCONTO, 0)                                                      as percentual_desconto,
             case when pf.id is not null then 'PF' else 'PJ' end                                      as tipo_pessoaId,
             case when pf.id is not null then pf.cpf else pj.CNPJ end as ni_fornecedor,
             case when pf.id is not null then pf.nome else pj.RAZAOSOCIAL end                         as nome_razao_social,
             case
                 when pf.id is not null then '4'
                 else case
                          when pj.TIPOEMPRESA is null then '5'
                          when pj.TIPOEMPRESA = 'MICRO' then '1'
                          when pj.TIPOEMPRESA = 'PEQUENA' then '2'
                          else '3' end end                                                            as porte_fornecedorId,
             coalesce(nr.codigo, (select nr.codigo
                                  from naturezajuridica nr
                                           inner join cadastroeconomico ce on ce.NATUREZAJURIDICA_ID = nr.id
                                           inner join CE_SITUACAOCADASTRAL ceS on ceS.CADASTROECONOMICO_ID = ce.id
                                           inner join situacaocadastroeconomico sitCad on ces.SITUACAOCADASTROECONOMICO_ID = sitCad.id
                                  where ce.PESSOA_ID = pes.id
                                    and trunc(sitcad.DATAREGISTRO) = (select max(trunc(sitCad1.dataregistro))
                                                                      from situacaocadastroeconomico sitCad1
                                                                               inner join CE_SITUACAOCADASTRAL ceS1
                                                                                          on ceS1.SITUACAOCADASTROECONOMICO_ID = sitCad1.id
                                                                      where ceS1.CADASTROECONOMICO_ID = ce.id
                                                                        and sitCad1.SITUACAOCADASTRAL = 'ATIVO'
                                                                      group by sitCad1.DATAREGISTRO
                                                                      order by sitCad1.DATAREGISTRO desc fetch first 1 row only)
                                  order by ce.id desc fetch first 1 row only), 8885)                                            as natureza_juridica,
             'BRA'                                                                                    as codigo_pais,
             0                                                                                        as indicador_subcontratacao,
             1                                                                                        as ordem_classificacao_srp,
             sfl.DATASITUACAO                                                                         as data_resultado,
             ipc.id                                                                                   as itemProcessoDeCompraId,
             ipc.sequencialpncp,
             ipc.numero                                                                               as numeroItem,
             lpc.numero                                                                               as numeroLote,
             lic.id                                                                                   as idLicitacao,
             0                                                                                        as aplicacaoMargemPreferencia,
             0                                                                                        as aplicacaoBeneficioMeEpp,
             0                                                                                        as aplicacaoCriterioDesempate,
             1                                                                                        as situacaoCompraItemResultadoId
      from itemcertame ic
               inner join certame c on c.id = ic.CERTAME_ID
               inner join licitacao lic on lic.id = c.LICITACAO_ID
               inner join ITEMCERTAMELOTEPRO iclp on iclp.ITEMCERTAME_ID = ic.id
               inner join LOTEPROPFORNEC lpf on lpf.id = iclp.LOTEPROPFORNECEDORVENCEDOR_ID
               inner join ITEMPROPFORNEC ipf on ipf.LOTEPROPOSTAFORNECEDOR_ID = lpf.id
               inner join itemprocessodecompra ipc on ipf.ITEMPROCESSODECOMPRA_ID = ipc.id
               inner join LOTEPROCESSODECOMPRA lpc on lpc.id = ipc.LOTEPROCESSODECOMPRA_ID
               inner join PROPOSTAFORNECEDOR prop on prop.id = ipf.PROPOSTAFORNECEDOR_ID
               inner join pessoa pes on pes.id = prop.FORNECEDOR_ID
               left join pessoafisica pf on pf.id = pes.id
               left join pessoajuridica pj on pj.id = pes.id
               left join naturezajuridica nr on pj.NATUREZAJURIDICA_ID = nr.ID
               inner join licitacaofornecedor licFor on licFor.LICITACAO_ID = lic.id
               inner join statusfornecedorlicitacao sfl on sfl.LICITACAOFORNECEDOR_ID = licFor.id
      where ic.situacaoItemCertame = 'VENCEDOR'
        and licfor.EMPRESA_ID = prop.FORNECEDOR_ID
        and sfl.TIPOSITUACAO = 'HOMOLOGADA'
      union all
      select ipc.quantidade                                                                           as quantidade_homologada,
             ipf.preco                                                                                as valor_unitario_homologado,
             0                                                                                        as valor_total_homologado,
             coalesce(ipf.PERCENTUALDESCONTO, 0)                                                      as percentual_desconto,
             case when pf.id is not null then 'PF' else 'PJ' end                                      as tipo_pessoaId,
             case when pf.id is not null then pf.cpf else pj.CNPJ end as ni_fornecedor,
             case when pf.id is not null then pf.nome else pj.RAZAOSOCIAL end                         as nome_razao_social,
             case
                 when pf.id is not null then '4'
                 else case
                          when pj.TIPOEMPRESA is null then '5'
                          when pj.TIPOEMPRESA = 'MICRO' then '1'
                          when pj.TIPOEMPRESA = 'PEQUENA' then '2'
                          else '3' end end                                                            as porte_fornecedorId,
             coalesce(nr.codigo,(select nr.codigo
                                 from naturezajuridica nr
                                          inner join cadastroeconomico ce on ce.NATUREZAJURIDICA_ID = nr.id
                                          inner join CE_SITUACAOCADASTRAL ceS on ceS.CADASTROECONOMICO_ID = ce.id
                                          inner join situacaocadastroeconomico sitCad on ces.SITUACAOCADASTROECONOMICO_ID = sitCad.id
                                 where ce.PESSOA_ID = pes.id
                                   and trunc(sitcad.DATAREGISTRO) = (select max(trunc(sitCad1.dataregistro))
                                                                     from situacaocadastroeconomico sitCad1
                                                                              inner join CE_SITUACAOCADASTRAL ceS1
                                                                                         on ceS1.SITUACAOCADASTROECONOMICO_ID = sitCad1.id
                                                                     where ceS1.CADASTROECONOMICO_ID = ce.id
                                                                       and sitCad1.SITUACAOCADASTRAL = 'ATIVO'
                                                                     group by sitCad1.DATAREGISTRO
                                                                     order by sitCad1.DATAREGISTRO desc fetch first 1 row only)
                                 order by ce.id desc fetch first 1 row only), 8885)                                            as natureza_juridica,
             'BRA'                                                                                    as codigo_pais,
             0                                                                                        as indicador_subcontratacao,
             1                                                                                        as ordem_classificacao_srp,
             sfl.DATASITUACAO                                                                         as data_resultado,
             ipc.id                                                                                   as itemProcessoDeCompraId,
             ipc.sequencialpncp,
             ipc.numero                                                                               as numeroItem,
             lpc.numero                                                                               as numeroLote,
             lic.id                                                                                   as idLicitacao,
             0                                                                                        as aplicacaoMargemPreferencia,
             0                                                                                        as aplicacaoBeneficioMeEpp,
             0                                                                                        as aplicacaoCriterioDesempate,
             1                                                                                        as situacaoCompraItemResultadoId
      from ITEMMAPACOMTECPRE imc
               inner join MAPACOMTECPREC mapa on mapa.id = imc.MAPACOMTECNICAPRECO_ID
               inner join licitacao lic on lic.id = mapa.LICITACAO_ID
               inner join ITEMMACOTECPRECITEMPROC icip on icip.ITEMMAPACOMTECNICAPRECO_ID = imc.id
               inner join ITEMPROPFORNEC ipf on icip.ITEMPROPOSTAVENCEDOR_ID = ipf.id
               inner join itemprocessodecompra ipc on ipf.ITEMPROCESSODECOMPRA_ID = ipc.id
               inner join LOTEPROCESSODECOMPRA lpc on lpc.id = ipc.LOTEPROCESSODECOMPRA_ID
               inner join PROPOSTAFORNECEDOR prop on prop.id = ipf.PROPOSTAFORNECEDOR_ID
               inner join pessoa pes on pes.id = prop.FORNECEDOR_ID
               left join pessoafisica pf on pf.id = pes.id
               left join pessoajuridica pj on pj.id = pes.id
               left join naturezajuridica nr on pj.NATUREZAJURIDICA_ID = nr.ID
               inner join licitacaofornecedor licFor on licFor.LICITACAO_ID = lic.id
               inner join statusfornecedorlicitacao sfl on sfl.LICITACAOFORNECEDOR_ID = licFor.id
      where imc.SITUACAOITEM = 'VENCEDOR'
        and licfor.EMPRESA_ID = prop.FORNECEDOR_ID
        and sfl.TIPOSITUACAO = 'HOMOLOGADA'
      union all
      select ipc.quantidade                                                                           as quantidade_homologada,
             lpf.VALOR                                                                                as valor_unitario_homologado,
             0                                                                                        as valor_total_homologado,
             coalesce(lpf.PERCENTUALDESCONTO, 0)                                                      as percentual_desconto,
             case when pf.id is not null then 'PF' else 'PJ' end                                      as tipo_pessoaId,
             case when pf.id is not null then pf.cpf else pj.CNPJ end as ni_fornecedor,
             case when pf.id is not null then pf.nome else pj.RAZAOSOCIAL end                         as nome_razao_social,
             case
                 when pf.id is not null then '4'
                 else case
                          when pj.TIPOEMPRESA is null then '5'
                          when pj.TIPOEMPRESA = 'MICRO' then '1'
                          when pj.TIPOEMPRESA = 'PEQUENA' then '2'
                          else '3' end end                                                            as porte_fornecedorId,
             coalesce(nr.codigo,(select nr.codigo
                                 from naturezajuridica nr
                                          inner join cadastroeconomico ce on ce.NATUREZAJURIDICA_ID = nr.id
                                          inner join CE_SITUACAOCADASTRAL ceS on ceS.CADASTROECONOMICO_ID = ce.id
                                          inner join situacaocadastroeconomico sitCad on ces.SITUACAOCADASTROECONOMICO_ID = sitCad.id
                                 where ce.PESSOA_ID = pes.id
                                   and trunc(sitcad.DATAREGISTRO) = (select max(trunc(sitCad1.dataregistro))
                                                                     from situacaocadastroeconomico sitCad1
                                                                              inner join CE_SITUACAOCADASTRAL ceS1
                                                                                         on ceS1.SITUACAOCADASTROECONOMICO_ID = sitCad1.id
                                                                     where ceS1.CADASTROECONOMICO_ID = ce.id
                                                                       and sitCad1.SITUACAOCADASTRAL = 'ATIVO'
                                                                     group by sitCad1.DATAREGISTRO
                                                                     order by sitCad1.DATAREGISTRO desc fetch first 1 row only)
                                 order by ce.id desc fetch first 1 row only), 8885)                                            as natureza_juridica,
             'BRA'                                                                                    as codigo_pais,
             0                                                                                        as indicador_subcontratacao,
             1                                                                                        as ordem_classificacao_srp,
             sfl.DATASITUACAO                                                                         as data_resultado,
             ipc.id                                                                                   as itemProcessoDeCompraId,
             ipc.sequencialpncp,
             ipc.numero                                                                               as numeroItem,
             lpc.numero                                                                               as numeroLote,
             lic.id                                                                                   as idLicitacao,
             0                                                                                        as aplicacaoMargemPreferencia,
             0                                                                                        as aplicacaoBeneficioMeEpp,
             0                                                                                        as aplicacaoCriterioDesempate,
             1                                                                                        as situacaoCompraItemResultadoId
      from ITEMMAPACOMTECPRE imc
               inner join MAPACOMTECPREC mapa on mapa.id = imc.MAPACOMTECNICAPRECO_ID
               inner join licitacao lic on lic.id = mapa.LICITACAO_ID
               inner join ITEMMACOTECPRECLOTEPROC iclp on iclp.ITEMMAPACOMTECNICAPRECO_ID = imc.id
               inner join LOTEPROPFORNEC lpf on lpf.id = iclp.LOTEPROPOSTAVENCEDOR_ID
               inner join ITEMPROPFORNEC ipf on ipf.LOTEPROPOSTAFORNECEDOR_ID = lpf.id
               inner join itemprocessodecompra ipc on ipf.ITEMPROCESSODECOMPRA_ID = ipc.id
               inner join LOTEPROCESSODECOMPRA lpc on lpc.id = ipc.LOTEPROCESSODECOMPRA_ID
               inner join PROPOSTAFORNECEDOR prop on prop.id = ipf.PROPOSTAFORNECEDOR_ID
               inner join pessoa pes on pes.id = prop.FORNECEDOR_ID
               left join pessoafisica pf on pf.id = pes.id
               left join pessoajuridica pj on pj.id = pes.id
               left join naturezajuridica nr on pj.NATUREZAJURIDICA_ID = nr.ID
               inner join licitacaofornecedor licFor on licFor.LICITACAO_ID = lic.id
               inner join statusfornecedorlicitacao sfl on sfl.LICITACAOFORNECEDOR_ID = licFor.id
      where imc.SITUACAOITEM = 'VENCEDOR'
        and licfor.EMPRESA_ID = prop.FORNECEDOR_ID
        and sfl.TIPOSITUACAO = 'HOMOLOGADA')
