update entradamaterial em set em.unidadeorganizacional_id = (select ei.unidadeorganizacional_id from ENTRADAINCORPORACAO ei where ei.id = em.id)
