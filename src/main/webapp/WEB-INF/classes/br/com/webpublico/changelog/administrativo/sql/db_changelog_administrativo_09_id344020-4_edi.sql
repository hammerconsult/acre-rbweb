alter table ajusteprocessocompra add substituicaoobjetocompra_id number(20);
alter table ajusteprocessocompraitem add substituicaoobjetocompra_id number(20);

insert into ajusteprocessocompra (id, numero, tipoajuste, datalancamento, usuariosistema_id,
                                  historicoprocesso, idprocesso, tipoprocesso, substituicaoobjetocompra_id)
select hibernate_sequence.nextval,
       numero,
       'SUBSTITUIR_OBJETO_COMPRA',
       datasubstituicao,
       usuariosistema_id,
       historico,
       idmovimento,
       origemsubstituicao,
       id
from substituicaoobjetocompra;


insert into ajusteprocessocompraitem (id, objetocompra_id,  objetocomprapara_id, numeroitem, especificacaode,
                                      especificacaopara, unidademedidade_id, unidademedidapara_id, quantidadede,
                                      quantidadepara, substituicaoobjetocompra_id)
select hibernate_sequence.nextval,
       item.objetocomprade_id,
       item.objetocomprapara_id,
       item.numeroitem,
       item.especificacaode,
       item.especificacaopara,
       item.unidademedidade_id,
       item.unidademedidapara_id,
       item.quantidadede,
       item.quantidadepara,
       item.substituicaoobjetocompra_id
from substituicaoobjetocompraitem item;


merge into ajusteprocessocompraitem item
    using
        (select aj.id                          as id_ajuste,
                aj.substituicaoobjetocompra_id as id_sub_obj
         from ajusteprocessocompra aj
         where aj.tipoajuste = 'SUBSTITUIR_OBJETO_COMPRA') dados
    on (dados.id_sub_obj = item.substituicaoobjetocompra_id)
    when matched then
        update
            set item.ajusteprocessocompra_id = dados.id_ajuste;

alter table ajusteprocessocompra drop column substituicaoobjetocompra_id;
alter table ajusteprocessocompraitem drop column substituicaoobjetocompra_id;
