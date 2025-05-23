create or replace view VW_LICITACAO_PNCP as
select l.id                                                                                           as id,
       l.idpncp                                                                                       as idPncp,
       l.sequencialidpncp                                                                             as sequencialIdPncp,
       l.processodecompra_id                                                                          as idProcessoCompra,
       ex.ano                                                                                         as anoCompra,
       l.numero                                                                                       as numeroCompra,
       pc.numero || '/' || ex.ano                                                                     as numeroProcesso,
       (select distinct pj.cnpj
        from orgaoentidadepncp entpncp
                 inner join orgaoentidadeunidadepncp unidpncp on unidpncp.orgaoentidadepncp_id = entpncp.id
                 inner join entidade ent on ent.id = entpncp.entidade_id
                 inner join pessoajuridica pj on pj.id = ent.pessoajuridica_id
                 inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = unidpncp.unidadeorganizacional_id
        where sysdate between trunc(l.emitidaem) and coalesce(trunc(vwadm.fimvigencia), sysdate)
          and substr(vwadm.codigo, 0, 5) = substr(vw.codigo, 0, 5))                                   as cnpj,
       substr(vw.codigo, 0, 5) || '.' || (select replace(substr(mascara, 7, length(mascara)), '#', '0')
                                          from configuracaohierarquia
                                          where tipohierarquiaorganizacional = 'ADMINISTRATIVA')      as codigoUnidadeCompradora,
       case
           when sm.modalidadelicitacao = 'LEILAO' and (sm.tiponaturezadoprocedimento = 'ELETRONICO' or
                                                       sm.tiponaturezadoprocedimento =
                                                       'ELETRONICO_COM_REGISTRO_DE_PRECO')
               then 'LEILAO_ELETRONICO'
           when sm.modalidadelicitacao = 'LEILAO' and (sm.tiponaturezadoprocedimento != 'ELETRONICO' and
                                                       sm.tiponaturezadoprocedimento !=
                                                       'ELETRONICO_COM_REGISTRO_DE_PRECO')
               then 'LEILAO_PRESENCIAL'
           when sm.modalidadelicitacao = 'CONCORRENCIA' and (sm.tiponaturezadoprocedimento = 'ELETRONICO' or
                                                             sm.tiponaturezadoprocedimento =
                                                             'ELETRONICO_COM_REGISTRO_DE_PRECO')
               then 'CONCORRENCIA_ELETRONICA'
           when sm.modalidadelicitacao = 'CONCORRENCIA' and (sm.tiponaturezadoprocedimento != 'ELETRONICO' and
                                                             sm.tiponaturezadoprocedimento !=
                                                             'ELETRONICO_COM_REGISTRO_DE_PRECO')
               then 'CONCORRENCIA_PRESENCIAL'
           when sm.modalidadelicitacao = 'PREGAO' and (sm.tiponaturezadoprocedimento = 'ELETRONICO' or
                                                       sm.tiponaturezadoprocedimento =
                                                       'ELETRONICO_COM_REGISTRO_DE_PRECO')
               then 'PREGAO_ELETRONICO'
           when sm.modalidadelicitacao = 'PREGAO' and (sm.tiponaturezadoprocedimento != 'ELETRONICO' and
                                                       sm.tiponaturezadoprocedimento !=
                                                       'ELETRONICO_COM_REGISTRO_DE_PRECO')
               then 'PREGAO_PRESENCIAL'
           when sm.modalidadelicitacao = 'CREDENCIAMENTO'
               then 'CREDENCIAMENTO'
           end                                                                                        as modalidadeId,
       case when sm.mododisputa = 'NAO_APLICAVEL' then 'NAO_SE_APLICA' else sm.mododisputa end        as modoDisputaId,
       case
           when (sm.tiponaturezadoprocedimento = 'REGISTRO_DE_PRECOS'
               or sm.tiponaturezadoprocedimento = 'PRESENCIAL_COM_REGISTRO_DE_PRECO'
               or sm.tiponaturezadoprocedimento = 'ELETRONICO_COM_REGISTRO_DE_PRECO'
               or sm.tiponaturezadoprocedimento = 'ABERTA_COM_REGISTRO_DE_PRECO'
               or sm.tiponaturezadoprocedimento = 'FECHADA_COM_REGISTRO_DE_PRECO'
               or sm.tiponaturezadoprocedimento = 'COMBINADO_COM_REGISTRO_DE_PRECO') then 1
           else 0 end                                                                                 as srp,
       case
           when sm.modalidadelicitacao = 'DISPENSA_LICITACAO' and sm.mododisputa = 'DISPENSA_COM_DISPUTA'
               then 'AVISO_CONTRATACAO_DIRETA'
           when sm.modalidadelicitacao = 'DISPENSA_LICITACAO' and sm.mododisputa != 'DISPENSA_COM_DISPUTA'
               then 'ATO_AUTORIZA_CONTRATACAO_DIRETA'
           when sm.modalidadelicitacao = 'INEXIGIBILIDADE'
               then 'ATO_AUTORIZA_CONTRATACAO_DIRETA'
           else 'EDITAL' end                                                                          as tipoInstrumentoConvocatorioId,
       al.codigo                                                                                      as amparoLegalId,
       l.resumodoobjeto                                                                               as objetoCompra,
       ''                                                                                             as informacaoComplementar,
       l.abertaem                                                                                     as dataAberturaProposta,
       l.encerradaem                                                                                  as dataEncerramentoProposta,
       l.linkSistemaOrigem                                                                            as linkSistemaOrigem,
       sm.justificativaPresencial                                                                     as justificativaPresencial,
       ar.nome                                                                                        as tituloDocumento,
       tipo.tipoanexopncp                                                                             as tipoDocumentoId,
       d.id                                                                                           as idDocumentolicitacao,
       ar.id                                                                                          as idArquivo,
       case when sm.modalidadelicitacao = 'CREDENCIAMENTO' then 'CREDENCIAMENTO' else 'LICITACAO' end as tipoProcesso,
       'DIVULGADA_NO_PNCP'                                                                            as situacaoCompraId
from licitacao l
         inner join exercicio ex on l.exercicio_id = ex.ID
         inner join processodecompra pc on l.processodecompra_id = pc.id
         inner join solicitacaomaterial sm on pc.solicitacaomaterial_id = sm.id
         left join vwhierarquiaadministrativa vw on vw.subordinada_id = sm.unidadeorganizacional_id
         inner join entidade ent on vw.ENTIDADE_ID = ent.ID
         inner join pessoajuridica pj on ent.PESSOAJURIDICA_ID = pj.ID
    and l.EMITIDAEM between trunc(vw.INICIOVIGENCIA) and coalesce(trunc(vw.FIMVIGENCIA), l.EMITIDAEM)
         inner join amparolegal al on sm.amparolegal_id = al.id
         left join detentordocumentolicitacao ddl on l.detentordocumentolicitacao_id = ddl.id
         left join documentolicitacao d on d.detentordocumentolicitacao_id = ddl.id and d.enviarpncc = 1
         left join tipodocumentoanexo tipo on d.tipodocumentoanexo_id = tipo.id
         left join arquivo ar on d.arquivo_id = ar.id
where al.leilicitacao = 'LEI_14133'
union all
select l.id                                                                                      as id,
       l.idpncp                                                                                  as idPncp,
       l.sequencialidpncp                                                                        as sequencialIdPncp,
       l.processodecompra_id                                                                     as idProcessoCompra,
       ex.ano                                                                                    as anoCompra,
       l.numerodadispensa                                                                        as numeroCompra,
       pc.numero || '/' || ex.ano                                                                as numeroProcesso,
       (select distinct pj.cnpj
        from orgaoentidadepncp entpncp
                 inner join orgaoentidadeunidadepncp unidpncp on unidpncp.orgaoentidadepncp_id = entpncp.id
                 inner join entidade ent on ent.id = entpncp.entidade_id
                 inner join pessoajuridica pj on pj.id = ent.pessoajuridica_id
                 inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = unidpncp.unidadeorganizacional_id
        where sysdate between trunc(l.datadadispensa) and coalesce(trunc(vwadm.fimvigencia), sysdate)
          and substr(vwadm.codigo, 0, 5) = substr(vw.codigo, 0, 5))                              as cnpj,
       substr(vw.codigo, 0, 5) || '.' || (select replace(substr(mascara, 7, length(mascara)), '#', '0')
                                          from configuracaohierarquia
                                          where tipohierarquiaorganizacional = 'ADMINISTRATIVA') as codigoUnidadeCompradora,
       sm.modalidadelicitacao                                                                    as modalidadeId,
       case when sm.mododisputa = 'NAO_APLICAVEL' then 'NAO_SE_APLICA' else sm.mododisputa end   as modoDisputaId,
       case
           when (sm.tiponaturezadoprocedimento = 'REGISTRO_DE_PRECOS'
               or sm.tiponaturezadoprocedimento = 'PRESENCIAL_COM_REGISTRO_DE_PRECO'
               or sm.tiponaturezadoprocedimento = 'ELETRONICO_COM_REGISTRO_DE_PRECO'
               or sm.tiponaturezadoprocedimento = 'ABERTA_COM_REGISTRO_DE_PRECO'
               or sm.tiponaturezadoprocedimento = 'FECHADA_COM_REGISTRO_DE_PRECO'
               or sm.tiponaturezadoprocedimento = 'COMBINADO_COM_REGISTRO_DE_PRECO') then 1
           else 0 end                                                                            as srp,
       case
           when sm.modalidadelicitacao = 'DISPENSA_LICITACAO' and sm.mododisputa = 'DISPENSA_COM_DISPUTA'
               then 'AVISO_CONTRATACAO_DIRETA'
           when sm.modalidadelicitacao = 'DISPENSA_LICITACAO' and sm.mododisputa != 'DISPENSA_COM_DISPUTA'
               then 'ATO_AUTORIZA_CONTRATACAO_DIRETA'
           when sm.modalidadelicitacao = 'INEXIGIBILIDADE'
               then 'ATO_AUTORIZA_CONTRATACAO_DIRETA'
           else 'EDITAL' end                                                                     as tipoInstrumentoConvocatorioId,
       al.codigo                                                                                 as amparoLegalId,
       l.resumodoobjeto                                                                          as objetoCompra,
       ''                                                                                        as informacaoComplementar,
       l.datadadispensa                                                                          as dataAberturaProposta,
       l.encerradaem                                                                             as dataEncerramentoProposta,
       l.linkSistemaOrigem                                                                       as linkSistemaOrigem,
       sm.justificativaPresencial                                                                as justificativaPresencial,
       ar.nome                                                                                   as tituloDocumento,
       tipo.tipoanexopncp                                                                        as tipoDocumentoId,
       d.id                                                                                      as idDocumentolicitacao,
       ar.id                                                                                     as idArquivo,
       'DISPENSA_LICITACAO_INEXIGIBILIDADE'                                                      as tipoProcesso,
       'DIVULGADA_NO_PNCP'                                                                       as situacaoCompraId
from dispensadelicitacao l
         inner join exercicio ex on l.exerciciodadispensa_id = ex.ID
         inner join processodecompra pc on l.processodecompra_id = pc.id
         inner join solicitacaomaterial sm on pc.solicitacaomaterial_id = sm.id
         inner join vwhierarquiaadministrativa vw on vw.subordinada_id = sm.unidadeorganizacional_id
    and l.datadadispensa between trunc(vw.iniciovigencia) and coalesce(trunc(vw.fimvigencia), l.datadadispensa)
         inner join amparolegal al on sm.amparolegal_id = al.id
         left join detentordocumentolicitacao ddl on l.detentordocumentolicitacao_id = ddl.id
         left join documentolicitacao d on d.detentordocumentolicitacao_id = ddl.id and d.enviarpncc = 1
         left join tipodocumentoanexo tipo on d.tipodocumentoanexo_id = tipo.id
         left join arquivo ar on d.arquivo_id = ar.id
where al.leilicitacao = 'LEI_14133';

delete
from DATABASECHANGELOG
where id = '346299-21';



create or replace view VW_ATA_REGISTROPRECO_PNCP as
select ata.id                                                                                    as idAta,
       ata.sequencialidpncp                                                                      as sequencialIdPncp,
       ata.idpncp                                                                                as idPncp,
       ata.NUMERO                                                                                as numeroAtaRegistro,
       ex.ano                                                                                    as anoCompra,
       extract(year from ata.DATAINICIO)                                                         as anoAta,
       ata.DATAASSINATURA                                                                        as dataAssinatura,
       ata.DATAINICIO                                                                            as dataVigenciaInicio,
       ata.DATAVENCIMENTO                                                                        as dataVigenciaFim,
       (select distinct pj.cnpj
        from orgaoentidadepncp entpncp
                 inner join orgaoentidadeunidadepncp unidpncp on unidpncp.orgaoentidadepncp_id = entpncp.id
                 inner join entidade ent on ent.id = entpncp.entidade_id
                 inner join pessoajuridica pj on pj.id = ent.pessoajuridica_id
                 inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = unidpncp.unidadeorganizacional_id
        where sysdate between trunc(ata.datainicio) and coalesce(trunc(vwadm.fimvigencia), sysdate)
          and substr(vwadm.codigo, 0, 5) = substr(vw.CODIGO, 0, 5))                                                                                   as cnpj,
       lic.SEQUENCIALIDPNCP                                                                      as sequencialCompra,
       ar.NOME                                                                                   as tituloDocumento,
       tipo.TIPOANEXOPNCP                                                                        as tipoDocumento,
       d.id                                                                                      as idDocumentolicitacao,
       ar.id                                                                                     as idArquivo,
       substr(vw.CODIGO, 0, 5) || '.' || (select replace(substr(MASCARA, 7, length(MASCARA)), '#', '0')
                                          from CONFIGURACAOHIERARQUIA
                                          where TIPOHIERARQUIAORGANIZACIONAL = 'ADMINISTRATIVA') as codigoUnidadeCompradora
from AtaRegistroPreco ata
         inner join licitacao lic on ata.LICITACAO_ID = lic.ID
         inner join exercicio ex on lic.EXERCICIO_ID = ex.ID
         inner join processodecompra pc on lic.processodecompra_id = pc.id
         inner join solicitacaomaterial sm on pc.solicitacaomaterial_id = sm.id
         left join VWHIERARQUIAADMINISTRATIVA vw on vw.SUBORDINADA_ID = sm.UNIDADEORGANIZACIONAL_ID and
                                                    ata.DATAINICIO between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, ata.DATAINICIO)
         inner join entidade ent on vw.ENTIDADE_ID = ent.ID
         inner join pessoajuridica pj on ent.PESSOAJURIDICA_ID = pj.ID
         inner join amparolegal al on sm.amparolegal_id = al.id

         left join detentordocumentolicitacao ddl on ata.detentordocumentolicitacao_id = ddl.id
         left join documentolicitacao d on d.detentordocumentolicitacao_id = ddl.id and d.ENVIARPNCC = 1
         left join TIPODOCUMENTOANEXO tipo on d.TIPODOCUMENTOANEXO_ID = tipo.ID
         left join arquivo ar on d.ARQUIVO_ID = ar.ID
where al.leilicitacao = 'LEI_14133'
