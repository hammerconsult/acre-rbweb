update lotacaofuncional set finalvigencia = to_date('31/12/2023', 'dd/mm/yyyy') where id in
 (select l.id as id_lotacao
 from lotacaofuncional l
          inner join horariocontratofp h2 on h2.id = l.horariocontratofp_id
          inner join horariodetrabalho h3 on h3.id = h2.horariodetrabalho_id
          inner join vinculofp v on v.id = l.vinculofp_id
          inner join contratofp c on c.id = v.id
          inner join hierarquiaorganizacional ho on ho.subordinada_id = l.unidadeorganizacional_id
          inner join unidadeorganizacional u on u.id = l.unidadeorganizacional_id
          inner join matriculafp m on m.id = v.matriculafp_id
          inner join pessoafisica pf on pf.id = m.pessoa_id
 where not exists(select 1 from hierarquiaorganizacional h
                  where h.subordinada_id = l.unidadeorganizacional_id
                    and current_date between h.iniciovigencia and coalesce(h.fimvigencia, current_date))
   and ho.id = (select max(h1.id) from hierarquiaorganizacional h1 where h1.subordinada_id = l.unidadeorganizacional_id)
   and to_date('30/03/2024', 'dd/mm/yyyy') between v.iniciovigencia and coalesce(v.finalvigencia, current_date)
   and to_date('30/03/2024', 'dd/mm/yyyy') between l.iniciovigencia and coalesce(l.finalvigencia, current_date)
   and ho.tipohierarquiaorganizacional = 'ADMINISTRATIVA' );

update horariocontratofp h set h.finalvigencia = to_date('31/12/2023', 'dd/mm/yyyy') where id in
(select h2.id
     from lotacaofuncional l
              inner join horariocontratofp h2 on h2.id = l.horariocontratofp_id
              inner join horariodetrabalho h3 on h3.id = h2.horariodetrabalho_id
              inner join vinculofp v on v.id = l.vinculofp_id
              inner join contratofp c on c.id = v.id
              inner join hierarquiaorganizacional ho on ho.subordinada_id = l.unidadeorganizacional_id
              inner join unidadeorganizacional u on u.id = l.unidadeorganizacional_id
              inner join matriculafp m on m.id = v.matriculafp_id
              inner join pessoafisica pf on pf.id = m.pessoa_id
     where not exists(select 1 from hierarquiaorganizacional h
                      where h.subordinada_id = l.unidadeorganizacional_id
                        and current_date between h.iniciovigencia and coalesce(h.fimvigencia, current_date))
       and ho.id = (select max(h1.id) from hierarquiaorganizacional h1 where h1.subordinada_id = l.unidadeorganizacional_id)
       and to_date('30/03/2024', 'dd/mm/yyyy') between v.iniciovigencia and coalesce(v.finalvigencia, current_date)
       and to_date('31/12/2023', 'dd/mm/yyyy') between l.iniciovigencia and coalesce(l.finalvigencia, current_date)
       and ho.tipohierarquiaorganizacional = 'ADMINISTRATIVA' );
