create or replace VIEW vw_licitacao_pncp as
select l.id,
       l.IDPNCP,
       l.SEQUENCIALIDPNCP,
       l.PROCESSODECOMPRA_ID,
       ex.ANO,
       l.NUMERO,
       l.NUMERO || '/' || ex.ANO                                                               as numeroProcesso,
       pj.cnpj                                                                                 as cnpj,
       substr(vw.CODIGO, 0, 5) ||'.' ||(select replace(substr(MASCARA, 7,length(MASCARA)), '#', '0') from CONFIGURACAOHIERARQUIA where TIPOHIERARQUIAORGANIZACIONAL = 'ADMINISTRATIVA') as codigoUnidadeCompradora,
       case
           when sm.MODALIDADELICITACAO = 'LEILAO' and (sm.TIPONATUREZADOPROCEDIMENTO = 'ELETRONICO' or
                                                       sm.TIPONATUREZADOPROCEDIMENTO =
                                                       'ELETRONICO_COM_REGISTRO_DE_PRECO')
               then 'LEILAO_ELETRONICO'
           when sm.MODALIDADELICITACAO = 'LEILAO' and (sm.TIPONATUREZADOPROCEDIMENTO != 'ELETRONICO' and
                                                       sm.TIPONATUREZADOPROCEDIMENTO !=
                                                       'ELETRONICO_COM_REGISTRO_DE_PRECO')
               then 'LEILAO_PRESENCIAL'
           when sm.MODALIDADELICITACAO = 'CONCORRENCIA' and (sm.TIPONATUREZADOPROCEDIMENTO = 'ELETRONICO' or
                                                             sm.TIPONATUREZADOPROCEDIMENTO =
                                                             'ELETRONICO_COM_REGISTRO_DE_PRECO')
               then 'CONCORRENCIA_ELETRONICA'
           when sm.MODALIDADELICITACAO = 'CONCORRENCIA' and (sm.TIPONATUREZADOPROCEDIMENTO != 'ELETRONICO' and
                                                             sm.TIPONATUREZADOPROCEDIMENTO !=
                                                             'ELETRONICO_COM_REGISTRO_DE_PRECO')
               then 'CONCORRENCIA_PRESENCIAL'
           when sm.MODALIDADELICITACAO = 'PREGAO' and (sm.TIPONATUREZADOPROCEDIMENTO = 'ELETRONICO' or
                                                       sm.TIPONATUREZADOPROCEDIMENTO =
                                                       'ELETRONICO_COM_REGISTRO_DE_PRECO')
               then 'PREGAO_ELETRONICO'
           when sm.MODALIDADELICITACAO = 'PREGAO' and (sm.TIPONATUREZADOPROCEDIMENTO != 'ELETRONICO' and
                                                       sm.TIPONATUREZADOPROCEDIMENTO !=
                                                       'ELETRONICO_COM_REGISTRO_DE_PRECO')
               then 'PREGAO_PRESENCIAL'
           when sm.MODALIDADELICITACAO = 'CREDENCIAMENTO'
               then 'CREDENCIAMENTO'
           end                                                                                 as modalidade,
       case when sm.MODODISPUTA = 'NAO_APLICAVEL' then 'NAO_SE_APLICA' else sm.MODODISPUTA end as mododisputa,
       case
           when (sm.TIPONATUREZADOPROCEDIMENTO = 'REGISTRO_DE_PRECOS'
               or sm.TIPONATUREZADOPROCEDIMENTO = 'PRESENCIAL_COM_REGISTRO_DE_PRECO'
               or sm.TIPONATUREZADOPROCEDIMENTO = 'ELETRONICO_COM_REGISTRO_DE_PRECO'
               or sm.TIPONATUREZADOPROCEDIMENTO = 'ABERTA_COM_REGISTRO_DE_PRECO'
               or sm.TIPONATUREZADOPROCEDIMENTO = 'FECHADA_COM_REGISTRO_DE_PRECO'
               or sm.TIPONATUREZADOPROCEDIMENTO = 'COMBINADO_COM_REGISTRO_DE_PRECO') then 1
           else 0 end                                                                          as srp,
       case
           when sm.MODALIDADELICITACAO = 'DISPENSA_LICITACAO' and sm.MODODISPUTA = 'DISPENSA_COM_DISPUTA'
               then 'AVISO_CONTRATACAO_DIRETA'
           when sm.MODALIDADELICITACAO = 'DISPENSA_LICITACAO' and sm.MODODISPUTA != 'DISPENSA_COM_DISPUTA'
               then 'ATO_AUTORIZA_CONTRATACAO_DIRETA'
           when sm.MODALIDADELICITACAO = 'INEXIGIBILIDADE'
               then 'ATO_AUTORIZA_CONTRATACAO_DIRETA'
           else 'EDITAL' end                                                                   as instrumentoconvocatorio,
       al.CODIGO                                                                               as amparolegal,
       l.RESUMODOOBJETO,
       l.ABERTAEM,
       l.ENCERRADAEM,
       l.LINKSISTEMAORIGEM,
       sm.JUSTIFICATIVAPRESENCIAL,
       ar.NOME                                                                                 as tituloDocumento,
       tipo.TIPOANEXOPNCP                                                                      as tipoDocumento,
       d.id                                                                                    as idDocumentolicitacao,
       ar.id                                                                                   as idArquivo,
       'LICITACAO' as tipo,
       'DIVULGADA_NO_PNCP' as situacao
from licitacao l
         left join exercicio ex on l.EXERCICIO_ID = ex.ID
         inner join processodecompra pc on l.processodecompra_id = pc.id
         inner join solicitacaomaterial sm on pc.solicitacaomaterial_id = sm.id
         left join VWHIERARQUIAADMINISTRATIVA vw on vw.SUBORDINADA_ID = sm.UNIDADEORGANIZACIONAL_ID
         inner join entidade ent on vw.ENTIDADE_ID = ent.ID
         inner join pessoajuridica pj on ent.PESSOAJURIDICA_ID = pj.ID
    and l.EMITIDAEM between trunc(vw.INICIOVIGENCIA) and coalesce(trunc(vw.FIMVIGENCIA), l.EMITIDAEM)
         inner join amparolegal al on sm.amparolegal_id = al.id
         left join detentordocumentolicitacao ddl on l.detentordocumentolicitacao_id = ddl.id
         left join documentolicitacao d on d.detentordocumentolicitacao_id = ddl.id and d.ENVIARPNCC = 1
         left join TIPODOCUMENTOANEXO tipo on d.TIPODOCUMENTOANEXO_ID = tipo.ID
         left join arquivo ar on d.ARQUIVO_ID = ar.ID
where al.leilicitacao = 'LEI_14133'
union all
select l.id,
       l.IDPNCP,
       l.SEQUENCIALIDPNCP,
       l.PROCESSODECOMPRA_ID,
       ex.ANO,
       l.NUMERODADISPENSA,
       l.NUMERODADISPENSA || '/' || ex.ANO                                                               as numeroProcesso,
       pj.cnpj                                                                                 as cnpj,
       substr(vw.CODIGO, 0, 5) ||'.' ||(select replace(substr(MASCARA, 7,length(MASCARA)), '#', '0') from CONFIGURACAOHIERARQUIA where TIPOHIERARQUIAORGANIZACIONAL = 'ADMINISTRATIVA') as codigoUnidadeCompradora,
       sm.MODALIDADELICITACAO as modalidade,
       case when sm.MODODISPUTA = 'NAO_APLICAVEL' then 'NAO_SE_APLICA' else sm.MODODISPUTA end as mododisputa,
       case
           when (sm.TIPONATUREZADOPROCEDIMENTO = 'REGISTRO_DE_PRECOS'
               or sm.TIPONATUREZADOPROCEDIMENTO = 'PRESENCIAL_COM_REGISTRO_DE_PRECO'
               or sm.TIPONATUREZADOPROCEDIMENTO = 'ELETRONICO_COM_REGISTRO_DE_PRECO'
               or sm.TIPONATUREZADOPROCEDIMENTO = 'ABERTA_COM_REGISTRO_DE_PRECO'
               or sm.TIPONATUREZADOPROCEDIMENTO = 'FECHADA_COM_REGISTRO_DE_PRECO'
               or sm.TIPONATUREZADOPROCEDIMENTO = 'COMBINADO_COM_REGISTRO_DE_PRECO') then 1
           else 0 end                                                                          as srp,
       case
           when sm.MODALIDADELICITACAO = 'DISPENSA_LICITACAO' and sm.MODODISPUTA = 'DISPENSA_COM_DISPUTA'
               then 'AVISO_CONTRATACAO_DIRETA'
           when sm.MODALIDADELICITACAO = 'DISPENSA_LICITACAO' and sm.MODODISPUTA != 'DISPENSA_COM_DISPUTA'
               then 'ATO_AUTORIZA_CONTRATACAO_DIRETA'
           when sm.MODALIDADELICITACAO = 'INEXIGIBILIDADE'
               then 'ATO_AUTORIZA_CONTRATACAO_DIRETA'
           else 'EDITAL' end                                                                   as instrumentoconvocatorio,
       al.CODIGO                                                                               as amparolegal,
       l.RESUMODOOBJETO,
       l.DATADADISPENSA,
       l.ENCERRADAEM,
       l.LINKSISTEMAORIGEM,
       sm.JUSTIFICATIVAPRESENCIAL,
       ar.NOME                                                                                 as tituloDocumento,
       tipo.TIPOANEXOPNCP                                                                      as tipoDocumento,
       d.id                                                                                    as idDocumentolicitacao,
       ar.id                                                                                   as idArquivo,
       'DISPENSA' as tipo,
       'DIVULGADA_NO_PNCP' as situacao
from DISPENSADELICITACAO l
         left join exercicio ex on l.EXERCICIODADISPENSA_ID = ex.ID
         inner join processodecompra pc on l.processodecompra_id = pc.id
         inner join solicitacaomaterial sm on pc.solicitacaomaterial_id = sm.id
         left join VWHIERARQUIAADMINISTRATIVA vw on vw.SUBORDINADA_ID = sm.UNIDADEORGANIZACIONAL_ID
         inner join entidade ent on vw.ENTIDADE_ID = ent.ID
         inner join pessoajuridica pj on ent.PESSOAJURIDICA_ID = pj.ID
    and l.DATADADISPENSA between trunc(vw.INICIOVIGENCIA) and coalesce(trunc(vw.FIMVIGENCIA), l.DATADADISPENSA)
         inner join amparolegal al on sm.amparolegal_id = al.id
         left join detentordocumentolicitacao ddl on l.detentordocumentolicitacao_id = ddl.id
         left join documentolicitacao d on d.detentordocumentolicitacao_id = ddl.id and d.ENVIARPNCC = 1
         left join TIPODOCUMENTOANEXO tipo on d.TIPODOCUMENTOANEXO_ID = tipo.ID
         left join arquivo ar on d.ARQUIVO_ID = ar.ID
where al.leilicitacao = 'LEI_14133'
