update conlicitacao cl
set cl.participanteirp_id = (select c.participanteirp_id
                             from contrato c
                             where c.id = cl.contrato_id
                               and c.participanteirp_id is not null);
