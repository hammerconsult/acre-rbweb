update entradamaterial em
set em.situacao      = 'CONCLUIDA',
    em.dataconclusao = em.dataentrada
where em.id in (select efet.id
                from efetivacaolevantamentoesto efet
                         inner join entradamaterial ent on ent.id = efet.id
                where ent.id = em.id);
