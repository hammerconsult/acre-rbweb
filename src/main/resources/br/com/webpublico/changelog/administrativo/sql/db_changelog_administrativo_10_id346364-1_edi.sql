create or replace view VW_CONTRATO_EMPENHO_PNCP as
select distinct (select distinct pj.cnpj
                 from orgaoentidadepncp entpncp
                          inner join orgaoentidadeunidadepncp unidpncp on unidpncp.orgaoentidadepncp_id = entpncp.id
                          inner join entidade ent on ent.id = entpncp.entidade_id
                          inner join pessoajuridica pj on pj.id = ent.pessoajuridica_id
                          inner join vwhierarquiaadministrativa vwadm
                                     on vwadm.subordinada_id = unidpncp.unidadeorganizacional_id
                 where sysdate between trunc(cont.datalancamento) and coalesce(trunc(vwadm.fimvigencia), sysdate)
                   and substr(vwadm.codigo, 0, 5) = substr(vwcont.codigo, 0, 5))                              as cnpj,
                (select distinct pj.cnpj
                 from orgaoentidadepncp entpncp
                          inner join orgaoentidadeunidadepncp unidpncp on unidpncp.orgaoentidadepncp_id = entpncp.id
                          inner join entidade ent on ent.id = entpncp.entidade_id
                          inner join pessoajuridica pj on pj.id = ent.pessoajuridica_id
                          inner join vwhierarquiaadministrativa vwadm
                                     on vwadm.subordinada_id = unidpncp.unidadeorganizacional_id
                 where sysdate between trunc(cont.datalancamento) and coalesce(trunc(vwadm.fimvigencia), sysdate)
                   and substr(vwadm.codigo, 0, 5) = substr(vwproc.codigo, 0, 5))                              as cnpjCompra,
                exproc.ano                                                                                    as anoCompra,
                coalesce(lic.idpncp, disp.idpncp)                                                             as idPncpCompra,
                coalesce(lic.sequencialidpncp, disp.sequencialidpncp)                                         as sequencialCompra,
                case when cont.contratoconcessao = 1 then 4 else 1 end                                        as tipoContratoId,
                cont.numerotermo                                                                              as numeroContratoEmpenho,
                excont.ano                                                                                    as anoContrato,
                pc.numero || '/' || exproc.ano                                                                as processo,
                1                                                                                             as categoriaProcessoId,
                case when cont.contratoconcessao = 1 then 1 else 0 end                                        as receita,
                substr(vwcont.codigo, 0, 5) || '.' || (select replace(substr(MASCARA, 7, length(MASCARA)), '#', '0')
                                                       from configuracaohierarquia
                                                       where tipohierarquiaorganizacional = 'ADMINISTRATIVA') as codigoUnidade,
                coalesce(pfcont.cpf, pjcont.cnpj)                                                             as niFornecedor,
                case when pfcont.id is not null then 'PF' else 'PJ' end                                       as tipoPessoaFornecedor,
                coalesce(pfcont.nome, pjcont.razaosocial)                                                     as nomeRazaoSocialFornecedor,
                ''                                                                                            as niFornecedorSubContratado,
                ''                                                                                            as tipoPessoaFornecedorSubContratado,
                ''                                                                                            as nomeRazaoSocialFornecedorSubContratado,
                cont.objeto                                                                                   as objetoContrato,
                ''                                                                                            as informacaoComplementar,
                cont.valortotal                                                                               as valorInicial,
                1                                                                                             as numeroParcelas,
                cont.valortotal                                                                               as valorParcela,
                cont.valortotal                                                                               as valorGlobal,
                cont.valoratualcontrato                                                                       as valorAcumulado,
                cont.iniciovigencia                                                                           as dataAssinatura,
                cont.iniciovigencia                                                                           as dataVigenciaInicio,
                cont.terminovigencia                                                                          as dataVigenciaFim,
                ''                                                                                            as identificadorCipi,
                ''                                                                                            as urlCipi,
                ar.nome                                                                                       as tituloDocumento,
                tipo.tipoanexopncp                                                                            as tipoDocumentoId,
                d.id                                                                                          as idDocumento,
                ar.id                                                                                         as idArquivo,
                'CONTRATO'                                                                                    as tipoProcesso,
                cont.id                                                                                       as id,
                pc.id                                                                                         as idProcessoCompra,
                cont.idpncp                                                                                   as idPncp,
                cont.sequencialidpncp                                                                         as sequencialIdPncp
from contrato cont
         inner join unidadecontrato uc on uc.contrato_id = cont.id
         inner join vwhierarquiaadministrativa vwcont on uc.unidadeadministrativa_id = vwcont.subordinada_id
         inner join exercicio excont on excont.id = cont.exerciciocontrato_id
         left join pessoafisica pfcont on pfcont.id = cont.contratado_id
         left join pessoajuridica pjcont on pjcont.id = cont.contratado_id
         left join conlicitacao conlic on conlic.contrato_id = cont.id
         left join licitacao lic on lic.id = conlic.licitacao_id
         left join condispensalicitacao condisp on condisp.contrato_id = cont.id
         left join dispensadelicitacao disp on condisp.dispensadelicitacao_id = disp.id
         inner join processodecompra pc on pc.id = coalesce(lic.processodecompra_id, disp.processodecompra_id)
         inner join solicitacaomaterial sm on pc.solicitacaomaterial_id = sm.id
         inner join amparolegal al on sm.amparolegal_id = al.id
         inner join vwhierarquiaadministrativa vwproc on vwproc.subordinada_id = pc.unidadeorganizacional_id
         inner join entidade ent on vwproc.entidade_id = ent.id
         inner join pessoajuridica pjEntProc on ent.pessoajuridica_id = pjEntProc.id
         inner join exercicio exproc on exproc.id = pc.exercicio_id
         left join detentordocumentolicitacao ddl on cont.detentordocumentolicitacao_id = ddl.id
         left join documentolicitacao d on d.detentordocumentolicitacao_id = ddl.id and d.enviarpncc = 1
         left join tipodocumentoanexo tipo on d.tipodocumentoanexo_id = tipo.id
         left join arquivo ar on d.arquivo_id = ar.id
where al.leilicitacao = 'LEI_14133'
  and sysdate between vwcont.iniciovigencia and coalesce(vwcont.fimvigencia, sysdate)
  and sysdate between vwproc.iniciovigencia and coalesce(vwproc.fimvigencia, sysdate)
  and sysdate between uc.iniciovigencia and coalesce(uc.fimvigencia, sysdate)
  and cont.situacaocontrato = 'DEFERIDO'
union all
select distinct(select distinct pj.cnpj
                from orgaoentidadepncp entpncp
                         inner join orgaoentidadeunidadepncp unidpncp on unidpncp.orgaoentidadepncp_id = entpncp.id
                         inner join entidade ent on ent.id = entpncp.entidade_id
                         inner join pessoajuridica pj on pj.id = ent.pessoajuridica_id
                         inner join vwhierarquiaadministrativa vwadm
                                    on vwadm.subordinada_id = unidpncp.unidadeorganizacional_id
                where sysdate between trunc(emp.dataempenho) and coalesce(trunc(vwadm.fimvigencia), sysdate)
                  and substr(vwadm.codigo, 0, 5) = substr(vwemp.codigo, 0, 5))                              as cnpj,
               (select distinct pj.cnpj
                from orgaoentidadepncp entpncp
                         inner join orgaoentidadeunidadepncp unidpncp on unidpncp.orgaoentidadepncp_id = entpncp.id
                         inner join entidade ent on ent.id = entpncp.entidade_id
                         inner join pessoajuridica pj on pj.id = ent.pessoajuridica_id
                         inner join vwhierarquiaadministrativa vwadm
                                    on vwadm.subordinada_id = unidpncp.unidadeorganizacional_id
                where sysdate between trunc(emp.dataempenho) and coalesce(trunc(vwadm.fimvigencia), sysdate)
                  and substr(vwadm.codigo, 0, 5) = substr(vwproc.codigo, 0, 5))                             as cnpjCompra,
               exproc.ano                                                                                   as anoCompra,
               coalesce(lic.idpncp, disp.idpncp)                                                            as idPncpCompra,
               coalesce(lic.sequencialidpncp, disp.sequencialidpncp)                                        as sequencialCompra,
               7                                                                                            as tipoContratoId,
               emp.numero                                                                                   as numeroContratoEmpenho,
               ex.ano                                                                                       as anoContrato,
               pc.numero || '/' || exproc.ano                                                               as processo,
               1                                                                                            as categoriaProcessoId,
               0                                                                                            as receita,
               substr(vwemp.codigo, 0, 5) || '.' || (select replace(substr(MASCARA, 7, length(MASCARA)), '#', '0')
                                                     from configuracaohierarquia
                                                     where tipohierarquiaorganizacional = 'ADMINISTRATIVA') as codigoUnidade,
               coalesce(pfEmp.cpf, pjEmp.cnpj)                                                              as niFornecedor,
               case when pfEmp.id is not null then 'PF' else 'PJ' end                                       as tipoPessoaFornecedor,
               coalesce(pfEmp.nome, pjEmp.razaosocial)                                                      as nomeRazaoSocialFornecedor,
               ''                                                                                           as niFornecedorSubContratado,
               ''                                                                                           as tipoPessoaFornecedorSubContratado,
               ''                                                                                           as nomeRazaoSocialFornecedorSubContratado,
               emp.complementohistorico                                                                     as objetoContrato,
               ''                                                                                           as informacaoComplementar,
               emp.valor                                                                                    as valorInicial,
               1                                                                                            as numeroParcelas,
               emp.valor                                                                                    as valorParcela,
               emp.valor                                                                                    as valorGlobal,
               emp.valor                                                                                    as valorAcumulado,
               emp.dataempenho                                                                              as dataAssinatura,
               emp.dataempenho                                                                              as dataVigenciaInicio,
               to_date('31/12/' || (extract(year from emp.dataempenho)),
                       'dd/MM/yyyy')                                                                        as dataVigenciaFim,
               ''                                                                                           as identificadorCipi,
               ''                                                                                           as urlCipi,
               ar.nome                                                                                      as tituloDocumento,
               tipo.tipoanexopncp                                                                           as tipoDocumentoId,
               d.id                                                                                         as idDocumento,
               ar.id                                                                                        as idArquivo,
               'EMPENHO'                                                                                    as tipoProcesso,
               emp.id                                                                                       as id,
               pc.id                                                                                        as idProcessoCompra,
               exemp.idpncp                                                                                 as idPncp,
               exemp.sequencialidpncp                                                                       as sequencialIdPncp
from execucaoprocesso exec
         inner join execucaoprocessoempenho exemp on exemp.execucaoprocesso_id = exec.id
    inner join empenho emp on emp.id = exemp.empenho_id
    inner join vwhierarquiaadministrativa vwemp on vwemp.subordinada_id = emp.unidadeorganizacionaladm_id
    inner join exercicio ex on ex.id = emp.exercicio_id
    left join execucaoprocessoata exata on exata.execucaoprocesso_id = exec.id
    left join ataregistropreco ata on ata.id = exata.ataregistropreco_id
    left join licitacao lic on lic.id = ata.licitacao_id
    left join execucaoprocessodispensa exdisp on exdisp.execucaoprocesso_id = exec.id
    left join dispensadelicitacao disp on disp.id = exdisp.dispensalicitacao_id
    left join pessoafisica pfEmp on pfEmp.id = emp.fornecedor_id
    left join pessoajuridica pjEmp on pjEmp.id = emp.fornecedor_id
    inner join processodecompra pc on pc.id = coalesce(lic.processodecompra_id, disp.processodecompra_id)
    inner join solicitacaomaterial sm on pc.solicitacaomaterial_id = sm.id
    inner join amparolegal al on sm.amparolegal_id = al.id
    inner join vwhierarquiaadministrativa vwproc on vwproc.subordinada_id = pc.unidadeorganizacional_id
    inner join entidade ent on vwproc.entidade_id = ent.id
    inner join pessoajuridica pjEntProc on ent.pessoajuridica_id = pjEntProc.id
    inner join exercicio exproc on exproc.id = pc.exercicio_id
    left join detentordocumentolicitacao ddl on exec.detentordocumentolicitacao_id = ddl.id
    left join documentolicitacao d on d.detentordocumentolicitacao_id = ddl.id and d.enviarpncc = 1
    left join tipodocumentoanexo tipo on d.tipodocumentoanexo_id = tipo.id
    left join arquivo ar on d.arquivo_id = ar.id
where al.leilicitacao = 'LEI_14133'
  and sysdate between vwemp.iniciovigencia and coalesce(vwemp.fimvigencia, sysdate)
  and sysdate between vwproc.iniciovigencia and coalesce(vwproc.fimvigencia, sysdate)
