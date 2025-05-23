update reajusteaplicado reajuste
set reajuste.exercicioreferencia_id = (select exer.id
                                       from exercicio exer
                                       where exer.ano = (select (ex.ano - 1)
                                                         from reajusteaplicado reaj
                                                                  inner join exercicio ex on reaj.exercicio_id = ex.id
                                                         where reaj.id = reajuste.id)
                                         and rownum = 1)
where reajuste.exercicioreferencia_id is null;
