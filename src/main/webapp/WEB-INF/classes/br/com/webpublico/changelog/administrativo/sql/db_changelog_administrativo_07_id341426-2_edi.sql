insert into itemcontratoitemirp (id, itemcontrato_id, itempropostafornecedor_id, itemparticipanteirp_id)
select hibernate_sequence.nextval,
       itemcontrato_id,
       itempropostafornecedor_id,
       itemparticipanteirp_id
from itemcontratoitempropfornec
where itemparticipanteirp_id is not null;
