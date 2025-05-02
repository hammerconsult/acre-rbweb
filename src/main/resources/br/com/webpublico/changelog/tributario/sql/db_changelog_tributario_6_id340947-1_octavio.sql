delete from respostalogvalidacao rlv where log_id in (select log.id from logremessaprotesto log
                                                      inner join remessaprotesto rp on log.remessaprotesto_id = rp.id
                                                      where rp.sequencia in (231, 232, 233, 234));
delete from logremessaprotesto where remessaprotesto_id in (select rp.id from remessaprotesto rp
                                                            where rp.sequencia in (231, 232, 233, 234));
delete from parcelaremessaprotesto where remessaprotesto_id in (select rp.id from remessaprotesto rp
                                                                where rp.sequencia in (231, 232, 233, 234));
delete from remessaprotesto where sequencia in (231, 232, 233, 234);
update remessaprotesto set sequencia = 234 where sequencia = 230;
