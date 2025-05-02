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
       pj.CNPJ                                                                                   as cnpj,
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
