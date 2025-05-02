update sociedadecadastroeconomico soc set soc.cadastroeconomico_id = (select cesoc.cadastroeconomico_id from ce_sociedade cesoc where cesoc.sociedadecadastroeconomico_id = soc.id)
