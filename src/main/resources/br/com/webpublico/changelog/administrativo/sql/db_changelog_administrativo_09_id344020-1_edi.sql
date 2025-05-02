update ajusteprocessocompra set tipoajuste = 'INCLUIR_PROPOSTA_FORNECEDOR' where tipoajuste = 'NOVA_PROPOSTA';
update ajusteprocessocompra set tipoajuste = 'EDITAR_PROPOSTA_FORNECEDOR' where tipoajuste = 'ALTERAR_PROPOSTA';
update ajusteprocessocompra set tipoajuste = 'INCLUIR_FORNECEDOR' where tipoajuste = 'NOVO_PARTICIPANTE';
update ajusteprocessocompra set tipoajuste = 'EDITAR_FORNECEDOR' where tipoajuste = 'ALTERAR_PARTICIPANTE';
update ajusteprocessocompra set tipoajuste = 'SUBSTITUIR_FORNECEDOR' where tipoajuste = 'SUBSTITUIR_PARTICIPANTE';

update ajusteprocessocompra aj
set aj.tipoprocesso      = 'LICITACAO',
    aj.idprocesso        = (select distinct lf.licitacao_id
                            from licitacaofornecedor lf
                            where lf.id = aj.licitacaofornecedor_id),
    aj.processocompra_id = (select distinct lic.processodecompra_id
                            from licitacaofornecedor lf
                                     inner join licitacao lic on lic.id = lf.licitacao_id
                            where lf.id = aj.licitacaofornecedor_id)
where idprocesso is null;

insert into ajusteprocessocompra (id, numero, tipoajuste, datalancamento, usuariosistema_id, historico, idprocesso,
                                  processocompra_id, tipoprocesso)
select hibernate_sequence.nextval,
       numero,
       'SUBSTITUIR_CONTROLE_ITEM',
       datalancamento,
       usuariosistema_id,
       to_char(substrb(historicoprocesso, 0,3000)),
       licitacao_id,
       (select distinct lic.processodecompra_id
        from licitacao lic
        where lic.id = licitacao_id),
       tipoprocesso
from alteracaoitemprocesso
where tipoalteracaoitem = 'TIPO_CONTROLE'
  and tipoprocesso = 'LICITACAO';
