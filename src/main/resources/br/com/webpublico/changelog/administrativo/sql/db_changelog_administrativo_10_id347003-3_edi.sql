merge into solicitacaoempenhorecdiv sol
    using (select rd.id     as id_rec_div,
                  solemp.id as id_sol_emp
           from reconhecimentodivida rd
                    inner join solicitacaoempenho solemp on solemp.reconhecimentodivida_id = rd.id) dados
    on (dados.id_sol_emp = sol.solicitacaoempenho_id)
    when not matched then
        insert (ID, solicitacaoempenho_id, reconhecimentodivida_id)
            VALUES (HIBERNATE_SEQUENCE.nextval, dados.id_sol_emp, dados.id_rec_div);
