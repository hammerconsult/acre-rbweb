insert into itemcontratoadesaoataint (id, itemcontrato_id, itempropostafornecedor_id, itemsolicitacaoexterno_id)
select hibernate_sequence.nextval,
       itemcontrato_id,
       itempropostafornecedor_id,
       itemsolicitacaoexterno_id
from itemcontratoitempropfornec
where itemsolicitacaoexterno_id is not null;
