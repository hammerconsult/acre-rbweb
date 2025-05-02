insert into doctofiscalentradacompra (id, entradacompramaterial_id, doctofiscalliquidacao_id, situacao)
select hibernate_sequence.nextval,
       ent.id,
       dd.doctofiscalliquidacao_id,
       dd.situacao
from entradacompramaterial ent
         inner join dentdocfiscliquidacao det on ent.detentdoctofiscalliquidacao_id = det.id
         inner join detentordocto dd on dd.detdoctofiscalliquidacao_id = det.id;


merge into itemdoctoitementrada idie
    using
        (select docEnt.id as doc_ent, idf.id as id_itemDoc
         from ITEMDOCTOITEMENTRADA item
                  inner join itemdoctofiscalliquidacao idf on idf.id = item.ITEMDOCTOFISCALLIQUIDACAO_ID
                  inner join doctofiscalliquidacao docto on docto.id = idf.DOCTOFISCALLIQUIDACAO_ID
                  inner join doctofiscalentradacompra docEnt on docto.ID = docEnt.DOCTOFISCALLIQUIDACAO_ID) dados
    on (dados.id_itemDoc = idie.ITEMDOCTOFISCALLIQUIDACAO_ID)
    when matched then
        update
            set idie.DOCTOFISCALENTRADACOMPRA_ID = dados.doc_ent;


update itemdoctoitementrada set quantidade = (
    select idf.quantidade from itemdoctofiscalliquidacao idf
    where idf.id = itemdoctofiscalliquidacao_id)
where quantidade is null;

